package ubc.cosc322.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.xml.validation.ValidatorHandler;

import ubc.cosc322.WebsocketClient;
import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;
import ubc.cosc322.messages.MCTSUpdate;
import ubc.cosc322.search.heuristics.DeadMonarchHeuristic;
import ubc.cosc322.search.heuristics.FastTerritoryHeursitic;
import ubc.cosc322.search.heuristics.Heuristic;

class UCT {
    public static double uctValue(
        double totalVisit, double nodeWinScore, double nodeVisit) {
        if (nodeVisit == 0) {
            return Double.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit) 
          + 1 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static SearchNode findBestNodeWithUCT(SearchNode node) {
        double parentVisit = node.board.numVisit.get();
        return Collections.max(
          node.getChildren(),
          Comparator.comparing(c -> uctValue(parentVisit, 
            c.board.numWins.get(), c.board.numVisit.get())));
    }
}

class SimulationWorker extends Thread {
    SearchNode root;
    long endTime;
    MonteCarloTreeSearch mcts;

    Heuristic heuristic = new FastTerritoryHeursitic();
    DeadMonarchHeuristic monarchHeuristic = new DeadMonarchHeuristic();

    public void run(){
        while(System.nanoTime() < endTime) {

            SearchNode exploreNode = mcts.selectPromisingNode(root);
            double playoutResult = mcts.simulateRandomPlayout(exploreNode, heuristic, monarchHeuristic);

            mcts.backPropogation(exploreNode, playoutResult);
        }
    }

    public SimulationWorker(SearchNode root, long endTime, MonteCarloTreeSearch mcts) {
        this.root = root;
        this.endTime = endTime;
        this.mcts = mcts;
    }
  }

public class MonteCarloTreeSearch {
    public SearchNode root;
    public boolean isWhite;
    public int playouts = 0;
    private double queenHeur = 0;

    double MAX_TIME = 29.5;
    public WebsocketClient ws = null;

    public ArrayList<MCTSUpdate> updates = new ArrayList<>();

    //CONFIG
    final double MAX_TIME_CONFIG = 29.5;
    final double NUM_THREADS = Runtime.getRuntime().availableProcessors();

    public MonteCarloTreeSearch(SearchNode root) {
        this.root = root;
    }

    public void performSearch(boolean isBot) {
        if(isBot) {
            MAX_TIME = MAX_TIME_CONFIG - 1;
        }
        performSearch();
    }

    public void performSearch() {
        this.queenHeur = (new DeadMonarchHeuristic()).calc(root.board);

        root.setChildren();

        long start = System.nanoTime();
        long end = start + ((long)(MAX_TIME * TimeUnit.SECONDS.toNanos(1)));
        playouts = 0;

        ArrayList<SimulationWorker> workers = new ArrayList<>();

        for(int i = 0; i < NUM_THREADS; i++)
        {
            SimulationWorker worker = new SimulationWorker(root, end, this);
            worker.start();
            workers.add(worker);
        }

        boolean alive = true;
        while(alive)
        {
            alive = false;
            for(Thread t: workers) {
                if(t.isAlive()) alive = true;
            }
        }
    }

    public SearchNode findNextMove() {
        if (root.board.checkStatus() != 0.5)
        {
            return root;
        }

        root.board.numVisit.set(0);
        root.board.numWins.set(0);
        performSearch();

        SearchNode winnerNode = null;
        for(SearchNode child: root.children)
        {
            Queen q = child.getQueen();
            Arrow ar = child.getArrow();
            for(MCTSUpdate update : updates) {
                if(
                    update.move.friendly == q.friendly &&
                    update.move.col == q.col &&
                    update.move.row == q.row &&
                    update.move.prev_col == q.prev_col &&
                    update.move.prev_row == q.prev_row && 
                    update.arrow.row == ar.row &&
                    update.arrow.col == ar.col
                ) {
                    child.board.updateMCTS(update.wins, update.visits);
                    root.board.updateMCTS(update.wins, update.visits);
                }
            }

            playouts += child.board.numVisit.get();

            if(winnerNode == null || child.board.getScore() > winnerNode.board.getScore())
                winnerNode = child;

            //addLearningData(child);
        }
        //addLearningData(root);
        
        updates.clear();
        printStatus(winnerNode);

        return winnerNode;
    }

    private void printStatus(SearchNode node) {

        double pct = node.board.numWins.get() / node.board.numVisit.get();
        final int total_bars = 10;
        int left_bars = (int)Math.round(pct * 10);
        String innerStr = "#".repeat(Math.min(left_bars, 10)) + " ".repeat(Math.max(10 - left_bars, 0));
        
        String statusString = (isWhite?"WHITE\t":"BLACK\t") + Math.round(pct * 1000) / 10. + "%\t[" + innerStr + "]";

        System.out.println();
        System.out.println(statusString);
        System.out.println();

        if(this.ws != null) {
            ws.log("\n" + statusString + "\n" + playouts + " playouts\n");
        }

        ws.updateMCTS();
    }

    private void addLearningData(SearchNode node) {
        //ReinforcementLearning.getInstance().addData(node.board.board, node.board.numWins, node.board.numVisit);
    }
    
    public void moveQueen(Queen qpos, Arrow apos) {
        root.moveQueen(qpos, apos);
    }

    public SearchNode selectPromisingNode(SearchNode parent) {
        SearchNode node = parent;
        while(node.getChildren().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    public void backPropogation(SearchNode nodeToExplore, double result) {
        SearchNode tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.board.numVisit.addAndGet(1);
            tempNode.board.numWins.addAndGet(result);

            tempNode = tempNode.getParent();
        }
    }

    public double simulateRandomPlayout(SearchNode node, Heuristic heuristic, DeadMonarchHeuristic monarchHeuristic) {
        SearchNode tempNode = new SearchNode(ChildGenerator.cloneState(node.board));
        GameState tempState = tempNode.board;

        double status = tempState.checkStatus();
        double deathHeuristic = monarchHeuristic.calc(tempState);
        /*if(deathHeuristic - queenHeur < 0) {
            return 0;
        }*/

        boolean isBlunder = false;
        boolean approvedBlockade = deathHeuristic - queenHeur > 0 && !monarchHeuristic.isBlunder(tempState);

        int plies = 0;
        while (status == 0.5 && plies < 11) {
            tempState.randomPlay();
            status = tempState.checkStatus();
            plies++;
        }
        if(status == 0.5) {
            double h = heuristic.calc(tempState);
            if(approvedBlockade) {
                return 0.85 * h + 0.15;
            }
            else if(isBlunder) {
                return 0.6 * h;
            }
            else {
                return h;
            }
        }
        return status;
    }
}   


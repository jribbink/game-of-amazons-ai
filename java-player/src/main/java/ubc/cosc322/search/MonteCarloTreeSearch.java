package ubc.cosc322.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ubc.cosc322.WebsocketClient;
import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;
import ubc.cosc322.messages.MCTSUpdate;

class UCT {
    public static double uctValue(
      int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Double.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit) 
          + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static SearchNode findBestNodeWithUCT(SearchNode node) {
        int parentVisit = node.board.numVisit.get();
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

    public void run(){
        while(System.nanoTime() < endTime) {

            SearchNode exploreNode = mcts.selectPromisingNode(root);
            int playoutResult = mcts.simulateRandomPlayout(exploreNode);

            mcts.backPropogation(exploreNode, (playoutResult == 1));
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

    double MAX_TIME = 5;
    public WebsocketClient ws = null;

    public ArrayList<MCTSUpdate> updates = new ArrayList<>();

    //CONFIG
    final double MAX_TIME_CONFIG = 5;
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
        if (root.board.checkStatus() != 0)
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
                    root.board.numVisit.addAndGet(update.visits);
                    root.board.numWins.addAndGet(update.wins);
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

        double pct = (double)node.board.numWins.get() / node.board.numVisit.get();
        final int total_bars = 10;
        int left_bars = (int)Math.round(pct * 10);
        String innerStr = "#".repeat(left_bars) + " ".repeat(10 - left_bars);
        
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

    public void backPropogation(SearchNode nodeToExplore, boolean win) {
        SearchNode tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.board.numVisit.incrementAndGet();
            if (win) {
                tempNode.board.numWins.incrementAndGet();
            } else {
                boolean test = false;
            }

            tempNode = tempNode.getParent();
        }
    }

    public int simulateRandomPlayout(SearchNode node) {
        SearchNode tempNode = new SearchNode(ChildGenerator.cloneState(node.board));
        GameState tempState = tempNode.board;

        int status = tempState.checkStatus();

        while (status == 0) {
            tempState.randomPlay();
            status = tempState.checkStatus();
        }
        return status;
    }
}   


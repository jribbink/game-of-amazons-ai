package ubc.cosc322.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;

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
        int parentVisit = node.board.numVisit;
        return Collections.max(
          node.getChildren(),
          Comparator.comparing(c -> uctValue(parentVisit, 
            c.board.numWins, c.board.numVisit)));
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

            mcts.backPropogation(exploreNode, (playoutResult == -1));
            mcts.playouts++;
        }
    }

    public SimulationWorker(SearchNode root, long endTime, MonteCarloTreeSearch mcts) {
        this.root = root;
        this.endTime = endTime;
        this.mcts = mcts;
    }
  }

public class MonteCarloTreeSearch {
    SearchNode root;
    public boolean isWhite;
    public int playouts = 0;
    
    //CONFIG
    final double MAX_TIME = 15;

    public MonteCarloTreeSearch(SearchNode root) {
        this.root = root;
    }

    public SearchNode findNextMove() {
        long start = System.nanoTime();
        long end = start + ((long)MAX_TIME * TimeUnit.SECONDS.toNanos(1));
        playouts = 0;

        if (root.board.checkStatus() == 0)
        {
            root.setChildren();
        }
        else
        {
            return root;
        }

        ArrayList<SimulationWorker> workers = new ArrayList<>();

        for(int i = 0; i < 12; i++)
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

        SearchNode winnerNode = null;
        for(SearchNode child: root.children)
        {
            if(winnerNode == null || child.board.getScore() > winnerNode.board.getScore())
                winnerNode = child;

            //addLearningData(child);
        }
        //addLearningData(root);

        printStatus(winnerNode);
        return winnerNode;
    }

    private void printStatus(SearchNode node) {

        double pct = (double)node.board.numWins / node.board.numVisit;
        final int total_bars = 10;
        int left_bars = (int)Math.round(pct * 10);
        String innerStr = "#".repeat(left_bars) + " ".repeat(10 - left_bars);
        
        System.out.println();
        System.out.println((isWhite?"WHITE\t":"BLACK\t") + Math.round(pct * 1000) / 10. + "%\t[" + innerStr + "]");
        System.out.println();
    }

    private void addLearningData(SearchNode node) {
        //ReinforcementLearning.getInstance().addData(node.board.board, node.board.numWins, node.board.numVisit);
    }
    
    public void moveQueen(Queen qpos, Arrow apos) {
        root.board.moveQueen(qpos, apos);
        root.children.clear();
    }

    public SearchNode selectPromisingNode(SearchNode parent) {
        SearchNode node = parent;
        while(node.getChildren().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    public void backPropogation(SearchNode nodeToExplore, boolean ourTurn) {
        SearchNode tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.board.numVisit++;
            if (tempNode.board.ourTurn == ourTurn) {
                tempNode.board.numWins++;
            }

            tempNode = tempNode.getParent();
        }
    }

    public int simulateRandomPlayout(SearchNode node) {
        SearchNode tempNode = new SearchNode(ChildGenerator.cloneState(node.board));
        GameState tempState = tempNode.board;

        int status = tempState.checkStatus();
        
        if (status == -1) {
            tempNode.getParent().board.numWins = Integer.MIN_VALUE;
            return status;
        }
        while (status == 0) {
            tempState.randomPlay();
            status = tempState.checkStatus();
        }
        return status;
    }
}   


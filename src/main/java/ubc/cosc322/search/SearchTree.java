package ubc.cosc322.search;

import java.util.ArrayList;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;

public class SearchTree {
    SearchNode root;
    RelativeTerrritoryHeuristic relHeur = new RelativeTerrritoryHeuristic();

    private ArrayList<SearchNode> frontier = new ArrayList();
    private int depth;

    private void expandFrontier() {
        ArrayList<SearchNode> f = new ArrayList<>();

        if(depth == 0)
        {
            root.setChildren(true);
            f.addAll(root.getChildren());
        }
        else {
            for(SearchNode node: frontier)
            {
                node.setChildren(depth % 2 == 0);
                f.addAll(node.getChildren());
            }
        }

        // TODO: POSSIBLE NEED TO DEEP COPY, UNCERTAIN
        frontier.clear();
        for(SearchNode node: f)
        {
            frontier.add(node);
        }
        depth++;
    }

    private void trimFrontier() {
        int avg = 0;
        for(SearchNode s:frontier){
            int val = relHeur.calc(s.board);
            s.setHeuristic(val);
            avg += val;
        }

        if(!frontier.isEmpty())
            avg /= frontier.size();

        ArrayList<SearchNode> delList = new ArrayList<>();
        for(SearchNode s:frontier){
            if(relHeur.calc(s.board) < avg) {
                delList.add(s);
            }
        }

        frontier.removeAll(delList);
    }

    public SearchTree(SearchNode node) {
        this.root = node;
    }

    public SearchNode performMove() {
        // Need to generate frontier in advance
        // depth will always equal 0 currently
        expandFrontier();
        trimFrontier();

        alphaBetaMinimax(root, getDepth(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        SearchNode bestMove = getBestMove();
        moveQueen(bestMove.getQueen(), bestMove.getArrow());
        return bestMove;
    }

    public void moveQueen(Queen qpos, Arrow apos) {
        root.board.addArrow(apos);

        if (qpos.friendly) {
            for (Queen Q1 : root.board.friendlies) {
                if (Q1.row == qpos.prev_row && Q1.col == qpos.prev_col) {
                    Q1.moveQueen(qpos.row, qpos.col);
                }
            }
        } else {
            for (Queen Q1 : root.board.enemies) {
                if (Q1.row == qpos.prev_row && Q1.col == qpos.prev_col) {
                    Q1.moveQueen(qpos.row, qpos.col);
                }
            }
        }
        root.board.rebaseBoard();
    }

    public int getDepth() {
        SearchNode node = this.root;
        int depth = 0;
        while (node != null) {
            if (node.getChildren().size() == 0) {
                break;
            } else {
                node = node.getChildren().get(0);
            }
            depth++;
        }

        return depth;
    }

    // https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
    private int alphaBetaMinimax(SearchNode node, int depth, int alpha, int beta, boolean maxPlayer) {
        // If Depth is 0 or node is a terminal node
        if (depth == 0 || node.getChildren().size() == 0) {
            int heuristic = relHeur.calc(node.board);
            node.setHeuristic(heuristic);
            return heuristic;
        }

        if (maxPlayer) {
            int value = Integer.MIN_VALUE;
            for (SearchNode child : node.getChildren()) {
                value = Math.max(value, alphaBetaMinimax(child, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);

                if (alpha >= beta)
                    break;
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (SearchNode child : node.getChildren()) {
                value = Math.min(value, alphaBetaMinimax(child, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);

                if (beta <= alpha)
                    break;
            }
            return value;
        }
    }

    private SearchNode getBestMove() {
        SearchNode best = null;
        int max = Integer.MIN_VALUE;

        for (SearchNode child : root.getChildren()) {
            if (child.getHeuristic() > max) {
                max = child.getHeuristic();
                best = child;
            }
        }

        if (best == null) {
            System.out.println("error has occured, goal state reached?");
        }

        return best;
    }
}

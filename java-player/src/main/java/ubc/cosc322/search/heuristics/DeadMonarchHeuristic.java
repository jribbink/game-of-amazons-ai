package ubc.cosc322.search.heuristics;

import java.util.ArrayList;

import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Queen;
import ubc.cosc322.search.SearchNode;

public class DeadMonarchHeuristic extends Heuristic {
    GameState state;

    public double calc(GameState state) {
        this.state = state;

        return getLiveQueens(true) - (double)getLiveQueens(false);
    }

    private int getLiveQueens(boolean friendly) {
        Queen[] queens;
        if(friendly) queens = state.friendlies;
        else queens = state.enemies;

        int count = 4;
        for(Queen q: queens) {
            if(queenWillDie(q)) {
                count--;
            }
        }

        return count;
    }

    private boolean queenWillDie(Queen q) {
        ArrayList<Queen> visited = new ArrayList<>();
        ArrayList<Queen> frontier = new ArrayList<>();

        frontier.addAll(state.getMoves(q));

        boolean oneMove = frontier.size() <= 1;

        // Could be looped starting here
        ArrayList<Queen> newFrontier = new ArrayList<>();

        for(Queen move: frontier) {
            ArrayList<Queen> moves = state.getMoves(move);
            moves.removeAll(visited);

            newFrontier.addAll(moves);
        }
        
        frontier = newFrontier;
        visited.addAll(frontier);
        //loop woudl end here
        

        return frontier.isEmpty() && oneMove;
    }

    public boolean isBlunder(GameState state) {
        SearchNode node = new SearchNode(state);
        node.setChildren();

        double firstHeur = calc(state);

        for(SearchNode child: node.getChildren()) {
            if(calc(child.board) < firstHeur) return true;
        }
        return false;
    }
}
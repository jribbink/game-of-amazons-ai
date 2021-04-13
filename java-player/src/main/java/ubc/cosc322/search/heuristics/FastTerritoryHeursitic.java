package ubc.cosc322.search.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Queen;

class ShortestMove {
    int friend = Integer.MAX_VALUE;
    int enemy = Integer.MAX_VALUE;
}

public class FastTerritoryHeursitic extends Heuristic {
    GameState state;
    ShortestMove distances[][] = new ShortestMove[10][10];

    public double calc(GameState state) {
        this.state = state;

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                distances[i][j] = new ShortestMove();
            }
        }

        setDistances(true);
        setDistances(false);

        double sum = 0;
        int cnt = 0;

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(distances[i][j].friend < Integer.MAX_VALUE || distances[i][j].enemy < Integer.MAX_VALUE)
                {
                    sum += localEvaluation(distances[i][j]);
                    cnt++;
                }
            }
        }

        return sum / cnt;
    }

    private double localEvaluation(ShortestMove dist) {
        return 0.5 + Math.pow(0.5, dist.friend) - Math.pow(0.5, dist.enemy);
    }

    private void setDistances(boolean friendly) {
        Queen[] queens;
        if(friendly) queens = state.friendlies;
        else queens = state.enemies;

        ArrayList<Queen> frontier = new ArrayList<>();
        int numMoves = 1;

        frontier.addAll(new ArrayList<>(Arrays.asList(queens)));

        while(frontier.size() > 0) {
            ArrayList<Queen> newFrontier = new ArrayList<>();
            for(Queen q: frontier) {
                ArrayList<Queen> moves = state.getMoves(q);
                Iterator<Queen> iter = moves.iterator();

                while(iter.hasNext()) {
                    Queen move = iter.next();

                    if(
                        distances[move.row][move.col].friend < numMoves ||
                        (distances[move.row][move.col].friend == numMoves && friendly) ||
                        distances[move.row][move.col].enemy < numMoves ||
                        (distances[move.row][move.col].enemy == numMoves && !friendly)
                    ) {
                        iter.remove();
                    } else {
                        if(friendly)
                            distances[move.row][move.col].friend = numMoves;
                        else
                            distances[move.row][move.col].enemy = numMoves;
                    }
                }

                newFrontier.addAll(moves);
            }

            numMoves++;
            frontier = newFrontier;
        }
    }
}

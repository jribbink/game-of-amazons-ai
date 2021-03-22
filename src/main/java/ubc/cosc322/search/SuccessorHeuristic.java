package ubc.cosc322.search;

import java.util.ArrayList;

import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;

public class SuccessorHeuristic {
    public ArrayList<SearchNode> getSuccessors(GameState state, boolean ourMove) {
        Queen[] queens;
        ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

        if (!ourMove) {
            queens = state.enemies;
        }
         else {
            queens = state.friendlies; 
        }

        for (int i = 0; i < queens.length; i++) {
            for(Queen move: state.getMoves(queens[i])) {
                GameState tempState = cloneState(state);

                if(ourMove)
                    tempState.friendlies[i] = move;
                else
                    tempState.enemies[i] = move;

                tempState.rebaseBoard();

                ArrayList<Arrow> arrowMoves = tempState.getArrowMoves(move.col, move.row);
                for(Arrow arrow: arrowMoves)
                {
                    GameState newState = cloneState(tempState);
                    newState.addArrow(arrow);

                    SearchNode node = new SearchNode(newState, move, arrow, 0);
                    successors.add(node);
                }
            }
        }

        return successors;
    }

    private GameState cloneState(GameState state) {
        Queen[] newFriendlies = new Queen[4];
        for(int i = 0; i < 4; i++)
        {
            newFriendlies[i] = new Queen(state.friendlies[i].row, state.friendlies[i].col, state.friendlies[i].friendly);
            newFriendlies[i].prev_col = state.friendlies[i].prev_col;
            newFriendlies[i].prev_row = state.friendlies[i].prev_row;
        }

        Queen[] newEnemies = new Queen[4];
        for(int i = 0; i < 4; i++)
        {
            newEnemies[i] = new Queen(state.enemies[i].row, state.enemies[i].col, state.enemies[i].friendly);
            newEnemies[i].prev_col = state.enemies[i].prev_col;
            newEnemies[i].prev_row = state.enemies[i].prev_row;
        }

        ArrayList<Arrow> arrowCopy = new ArrayList<Arrow>();
        for(Arrow arrow: state.arrows) {
            arrowCopy.add(arrow);
        }

        return new GameState(newFriendlies, newEnemies, arrowCopy);
    }
}

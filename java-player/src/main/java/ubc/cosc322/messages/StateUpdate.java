package ubc.cosc322.messages;

import java.util.ArrayList;

import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;

public class StateUpdate extends BaseMessage {
    public int[][] state;
    public boolean isWhite;
    public boolean ourTurn;

    public StateUpdate(GameState state, boolean isWhite) {
        message_type = "state-update";
        this.isWhite = isWhite;
        this.ourTurn = state.ourTurn;
        this.state = new int[10][10];
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(state.board[i][j] instanceof Queen && ((Queen)state.board[i][j]).friendly == isWhite)
                    this.state[i][j] = 1;
                else if(state.board[i][j] instanceof Queen && ((Queen)state.board[i][j]).friendly != isWhite)
                    this.state[i][j] = 2;
                else if(state.board[i][j] instanceof Arrow)
                    this.state[i][j] = 3;
                else
                    this.state[i][j] = 0;
            }
        }
    }

    public GameState toGameState() {
        ArrayList<Queen> enemies = new ArrayList<Queen>();
        ArrayList<Queen> friendlies = new ArrayList<Queen>();
        ArrayList<Arrow> arrows = new ArrayList<Arrow>();

        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if(state[i][j] == 1) {
                    if(isWhite) {
                        friendlies.add(new Queen(i, j, true));
                    } else {
                        enemies.add(new Queen(i, j, false));
                    }
                } else if(state[i][j] == 2) {
                    if(!isWhite) {
                        friendlies.add(new Queen(i, j, true));
                    } else {
                        enemies.add(new Queen(i, j, false));
                    }
                } else if(state[i][j] == 3) {
                    arrows.add(new Arrow(i, j));
                }
            }
        }

        GameState s = new GameState(friendlies.toArray(new Queen[0]), enemies.toArray(new Queen[0]), arrows, ourTurn);
        return s;
    }
}

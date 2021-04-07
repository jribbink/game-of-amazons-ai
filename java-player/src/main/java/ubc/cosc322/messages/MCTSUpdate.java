package ubc.cosc322.messages;

import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;

public class MCTSUpdate {
    public Queen move;
    public Arrow arrow;
    public int visits;
    public int wins;

    public MCTSUpdate(Queen move, Arrow arrow, int visits, int wins) {
        this.move = move;
        this.arrow = arrow;
        this.visits = visits;
        this.wins = wins;
    }
}

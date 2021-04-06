package ubc.cosc322.board.tiles;

import java.util.ArrayList;

public class Arrow extends BoardTile {
    public Arrow(int row, int col) {
        super(row, col);
    }

    public ArrayList<Integer> currentPos() {
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(this.row + 1);
        pos.add(this.col + 1);
        return pos;
    }
    
}

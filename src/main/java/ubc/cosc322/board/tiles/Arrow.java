package ubc.cosc322.board.tiles;

import java.util.ArrayList;

public class Arrow extends BoardTile {
    public Arrow(int row, int col) {
        super(row, col);
    }

    public ArrayList<Integer> currentPos() {
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(this.row);
        pos.add(this.col);
        return pos;
    }
    
}

package ubc.cosc322.board.tiles;

import java.util.ArrayList;

public class Queen extends BoardTile {
    public int prev_row;
    public int prev_col;
    public boolean friendly;

    public Queen(int row, int col) {
        super(row, col);
        this.row = row;
        this.col = col;
    }

    public Queen(int row, int col, boolean friendly) {
        super(row, col);
        prev_row = row;
        prev_col = col;

        this.friendly = friendly;
    }

    public void moveQueen(int row, int col) {
        this.prev_row = this.row;
        this.prev_col = this.col;
        this.row = row;
        this.col = col;
    }

    public ArrayList<Integer> oldPosition() {
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(this.prev_row);
        pos.add(this.prev_col);
        return pos;
    }

    public ArrayList<Integer> currentPos() {
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(this.row);
        pos.add(this.col);
        return pos;
    }
}

package ubc.cosc322.search;

import java.util.LinkedList;
import java.util.Queue;

import ubc.cosc322.board.tiles.BoardTile;
import ubc.cosc322.board.GameBoard;
import ubc.cosc322.board.tiles.Queen;

public class RelativeTerrritoryHeuristic {
    private int friendlyTiles = 0;
    private int enemyTiles = 0;

    BoardTile[][] board;

    public int calc(GameBoard board) {
        this.board = board.board;

        friendlyTiles = 0;
        enemyTiles = 0;

        for(int i = 0; i <= 9; i++) {
            for(int j = 0; j <= 9; j++) {
                if(board.board[i][j] == null) {
                    findNearbyQueen(i, j);
                }
            }
        }

        return friendlyTiles - enemyTiles;
    }

    private void findNearbyQueen(int row, int col) {
        boolean[][] checked = new boolean[10][10];
        checked[row][col] = true;
        boolean isFound = false;

        Queue<BoardTile> q = getAccessibleQueens(row, col, checked);

        //No valid moves
        if(q.size() == 0) {
            return;
        }

        while(!q.isEmpty()) {
            BoardTile tile = q.poll();

            // If tile contains a queen
            if(board[tile.row][tile.col] instanceof Queen) {
                boolean isFriendly = ((Queen)board[tile.row][tile.col]).friendly;
                //TODO: FINISH CONTESTED CHECK
                //boolean contested = false;

                if(isFriendly) {
                    friendlyTiles++;
                } else {
                    enemyTiles++;
                }

                return;
            }
        }
    }

    private Queue<BoardTile> getAccessibleQueens(int row, int col, boolean[][] checked)
    {
        Queue<BoardTile> moves = new LinkedList<BoardTile>();

        // Get all moves left
        for(int i = 1; col - i >= 0; i++)
        {
            if(checked[row][col - i] == false)
                moves.add(new Queen(row, col - i));
            if(board[row][col - i] != null)
                break;
        }

        // Get all moves right
        for(int i = 1; col + i <= 9; i++)
        {
            if(checked[row][col + i] == false)
                moves.add(new Queen(row, col - i));
            if(board[row][col + i] != null)
                break;
        }

        // Get all moves up
        for(int i = 1; row - i > 0; i++)
        {
            if(checked[row - i][col] == false)
                moves.add(new Queen(row - i, col));
            if(board[row - i][col] == null)
                break;
        }

        // Get all moves down
        for(int i = 1; row + i <= 9; i++)
        {
            if(checked[row + i][col] == false)
                moves.add(new Queen(row + i, col));
            if(board[row + i][col] == null)
                break;
        }

        // Get all moves diag left/up
        for(int i = 1; col - i >= 0 && row - i >= 0; i++)
        {
            if(checked[row - i][col - i] == false)
                moves.add(new Queen(row - i, col - i));
            if(board[row - i][col - i] == null)
                break;
        }

        // Get all moves diag left/down
        for(int i = 1; col - i >= 0 && row + i <= 9; i++)
        {
            if(checked[row + i][col - i] == false)
                moves.add(new Queen(row + i, col - i));
            if(board[row + i][col - i] == null)
                break;
        }

        // Get all moves diag right/down
        for(int i = 1; col + i <= 9 && row + i <= 9; i++)
        {
            if(checked[row + i][col + i] == false)
                moves.add(new Queen(row + i, col + i));
            if(board[row + i][col + i] == null)
                break;
        }

        // Get all moves diag right/up
        for(int i = 1; col + i <= 9 && row - i >= 0; i++)
        {
            if(checked[row + i][col + i] == false)
                moves.add(new Queen(row - i, col + i));
            if(board[row + i][col + i] == null)
                break;
        }

        return moves;
    }
}
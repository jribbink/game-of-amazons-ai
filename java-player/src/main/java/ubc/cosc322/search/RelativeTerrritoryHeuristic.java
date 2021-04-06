package ubc.cosc322.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ubc.cosc322.board.tiles.BoardTile;
import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Queen;

public class RelativeTerrritoryHeuristic {
    private int friendlyTiles = 0;
    private int enemyTiles = 0;

    BoardTile[][] board;

    GameState state;

    public int calc(GameState board) {
        this.board = board.board;
        this.state = board;

        if(state.isGoal()) {
            return Integer.MAX_VALUE;
        }

        friendlyTiles = 0;
        enemyTiles = 0;

        for(int i = 0; i < 8; i++) {
            if(i < 4) {
                friendlyTiles += findTotalPaths(board.friendlies[i]);
            } else {
                enemyTiles += findTotalPaths(board.enemies[i - 4]);
            }
        }

        return friendlyTiles - enemyTiles;
    }

    public int findTotalPaths(Queen q) {
        int sum = 0;
        ArrayList<Queen> firstMoves = state.getMoves(q);
        
        for(Queen move: firstMoves) {
            boolean contested = false;
            for(Queen qq: getAccessibleQueens(move.row, move.col))
            {
                if(qq.friendly != q.friendly) contested = true;
            }
            ArrayList<Queen> secondMoves = state.getMoves(move);

            //Simply sum += secondMoves.size() seems to be a better heuristic :(
            sum += secondMoves.size();
        }
        return sum;
    }

    /*private void findNearbyQueen(int row, int col) {
        boolean[][] checked = new boolean[10][10];
        checked[row][col] = true;
        boolean isFound = false;

        ArrayList<BoardTile> q = getAccessibleQueens(row, col, checked);

        //No valid moves
        if(q.size() == 0) {
            return;
        }

        for(int i = 0; i < q.size(); i++) {
            BoardTile tile = q.get(i);

            // If tile contains a queen
            if(board[tile.row][tile.col] instanceof Queen) {
                boolean isFriendly = ((Queen)board[tile.row][tile.col]).friendly;

                boolean contested = false;
                for(int j = 0; j < q.size(); j++) {
                    BoardTile contest = q.get(j);
                    if(board[contest.row][contest.col] instanceof Queen && ((Queen)contest).friendly != isFriendly) {
                        contested = true;
                        break;
                    }
                }

                if(!contested) {
                    if(isFriendly) {
                        friendlyTiles ++;
                    } else {
                        enemyTiles ++;
                    }
                }

                return;
            }
            /*else
            {
                ArrayList<BoardTile> q2 = getAccessibleQueens(tile.row, tile.col, checked);
                for(int x = 0; x < q2.size(); x++) {
                    BoardTile t2 = q2.get(x);
                    if(board[t2.row][t2.col] instanceof Queen) {
                        boolean f2 = ((Queen)board[t2.row][t2.col]).friendly;
    
                        boolean c2 = false;
                        for(int j = 0; j < q2.size(); j++) {
                            BoardTile contest = q2.get(j);
                            if(board[contest.row][contest.col] instanceof Queen && ((Queen)contest).friendly != f2) {
                                c2 = true;
                                break;
                            }
                        }

                        if(!c2) {
                            if(f2) {
                                friendlyTiles++;
                            } else {
                                 enemyTiles++;
                            }
                        }
                    }
                }
            }*/
        /*}
    }*/

    private ArrayList<Queen> getAccessibleQueens(int row, int col)
    {
        ArrayList<Queen> moves = new ArrayList<Queen>();

        // Get all moves left
        for(int i = 1; col - i >= 0; i++)
        {
            if(board[row][col - i] instanceof Queen)
                moves.add((Queen)board[row][col - i]);
            if(board[row][col - i] != null)
                break;
        }

        // Get all moves right
        for(int i = 1; col + i <= 9; i++)
        {
            if(board[row][col + i] instanceof Queen)
                moves.add((Queen)board[row][col + i]);
            if(board[row][col + i] != null)
                break;
        }

        // Get all moves up
        for(int i = 1; row - i > 0; i++)
        {
            if(board[row - i][col] instanceof Queen)
                moves.add((Queen)board[row - i][col]);
            if(board[row - i][col] == null)
                break;
        }

        // Get all moves down
        for(int i = 1; row + i <= 9; i++)
        {
            if(board[row + i][col] instanceof Queen)
                moves.add((Queen)board[row + i][col]);
            if(board[row + i][col] == null)
                break;
        }

        // Get all moves diag left/up
        for(int i = 1; col - i >= 0 && row - i >= 0; i++)
        {
            if(board[row - i][col - i] instanceof Queen)
                moves.add((Queen)board[row - i][col - i]);
            if(board[row - i][col - i] == null)
                break;
        }

        // Get all moves diag left/down
        for(int i = 1; col - i >= 0 && row + i <= 9; i++)
        {
            if(board[row + i][col - i] instanceof Queen)
                moves.add((Queen)board[row + i][col - i]);
            if(board[row + i][col - i] == null)
                break;
        }

        // Get all moves diag right/down
        for(int i = 1; col + i <= 9 && row + i <= 9; i++)
        {
            if(board[row + i][col + i] instanceof Queen)
                moves.add((Queen)board[row + i][col + i]);
            if(board[row + i][col + i] == null)
                break;
        }

        // Get all moves diag right/up
        for(int i = 1; col + i <= 9 && row - i >= 0; i++)
        {
            if(board[row - i][col + i] instanceof Queen)
                moves.add((Queen)board[row - i][col + i]);
            if(board[row - i][col + i] == null)
                break;
        }

        return moves;
    }
}
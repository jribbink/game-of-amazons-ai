package ubc.cosc322.board;

import java.util.ArrayList;
import java.util.Arrays;

import ubc.cosc322.board.tiles.*;

public class GameBoard {
    public BoardTile[][] board = new BoardTile[10][10];

    public Queen[] friendlies;
    public Queen[] enemies;
    public ArrayList<Arrow> arrows = new ArrayList<>();

    public ArrayList<Queen> moves = new ArrayList<>();

    public GameBoard(boolean isWhite) {
        // If we start first (are black)
        if(!isWhite) {
            friendlies = new Queen[] { new Queen(6, 0, true), new Queen(6, 9, true), new Queen(9, 3, true), new Queen(9, 6, true) };
            enemies = new Queen[] { new Queen(0, 3, false), new Queen(0, 6, false), new Queen(3, 0, false), new Queen(3, 9, false) };
        }
        else
        {
            friendlies = new Queen[] { new Queen(0, 3, true), new Queen(0, 6, true), new Queen(3, 0, true), new Queen(3, 9, true) };
            enemies = new Queen[] { new Queen(6, 0, false), new Queen(6, 9, false), new Queen(9, 3, false), new Queen(9, 6, false) };
        }

        // Will populate the board object based on friendlies and enemies
        rebaseBoard();

        // Update possible queen moves
        updateMoves();
    }

    public GameBoard(Queen[] friendlies, Queen[] enemies, ArrayList<Arrow> arrows)
    {
        this.friendlies = friendlies;
        this.enemies = enemies;
        this.arrows = arrows;

        this.rebaseBoard();
    }

    // Add an arrow
    public void addArrow(Arrow arrow) {
        arrows.add(arrow);
        rebaseBoard();
    }

    // Move objects from their arraylists to main board
    public void rebaseBoard() {
        // Clear board
        for(int i = 0; i < board.length; i++)
            Arrays.fill(board[i], null);

        for(Queen enemy: enemies) {
            if(enemy != null) {
                board[enemy.row][enemy.col] = enemy;
            }
        }
        
        for(Queen friendly: friendlies) {
            if(friendly != null) {
                board[friendly.row][friendly.col] = friendly;
            }
        }

        for(Arrow arrow: arrows) {
            if(arrow != null) {
                board[arrow.row][arrow.col] = arrow;
            }
        }
    }

    // Update possible queen moves
    public void updateMoves() {
        //Clear previous array of moves
        moves.clear();

        // Add all legal moves for each queen
        for(Queen friendly: friendlies)
            moves.addAll(getMoves(friendly));
    }

    // Get all possible moves for queen
    public ArrayList<Queen> getMoves(Queen q) {
        ArrayList<Queen> moves = new ArrayList<Queen>();

        // Get all moves left
        for(int i = 1; q.col - i >= 0; i++)
        {
            if(board[q.row][q.col - i] == null) {
                Queen move = new Queen(q.row, q.col - i);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        // Get all moves right
        for(int i = 1; q.col + i <= 9; i++)
        {
            if(board[q.row][q.col + i] == null)
            {
                Queen move = new Queen(q.row, q.col + i);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        // Get all moves up
        for(int i = 1; q.row - i > 0; i++)
        {
            if(board[q.row - i][q.col] == null)
            {
                Queen move = new Queen(q.row - i, q.col);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        // Get all moves down
        for(int i = 1; q.row + i <= 9; i++)
        {
            if(board[q.row + i][q.col] == null)
            {
                Queen move = new Queen(q.row + i, q.col);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        // Get all moves diag left/up
        for(int i = 1; q.col - i >= 0 && q.row - i >= 0; i++)
        {
            if(board[q.row - i][q.col - i] == null)
            {
                Queen move = new Queen(q.row - i, q.col - i);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        // Get all moves diag left/down
        for(int i = 1; q.col - i >= 0 && q.row + i <= 9; i++)
        {
            if(board[q.row + i][q.col - i] == null)
            {
                Queen move = new Queen(q.row + i, q.col - i);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        // Get all moves diag right/down
        for(int i = 1; q.col + i <= 9 && q.row + i <= 9; i++)
        {
            if(board[q.row + i][q.col + i] == null)
            {
                Queen move = new Queen(q.row + i, q.col + i);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        // Get all moves diag right/up
        for(int i = 1; q.col + i <= 9 && q.row - i >= 0; i++)
        {
            if(board[q.row + i][q.col + i] == null)
            {
                Queen move = new Queen(q.row - i, q.col + i);
                move.prev_col = q.col;
                move.prev_row = q.row;
                moves.add(move);
            }
            else
                break;
        }

        return moves;
    }

    public ArrayList<Arrow> getArrowMoves(int col, int row) {
        ArrayList<Arrow> moves = new ArrayList<>();

        // Get all moves left
        for(int i = 1; col - i >= 0; i++)
        {
            if(board[row][col - i] == null)
                moves.add(new Arrow(row, col - i));
            else
                break;
        }

        // Get all moves right
        for(int i = 1; col + i <= 9; i++)
        {
            if(board[row][col + i] == null)
                moves.add(new Arrow(row, col + i));
            else
                break;
        }

        // Get all moves up
        for(int i = 1; row - i > 0; i++)
        {
            if(board[row - i][col] == null)
                moves.add(new Arrow(row - i, col));
            else
                break;
        }

        // Get all moves down
        for(int i = 1; row + i <= 9; i++)
        {
            if(board[row + i][col] == null)
                moves.add(new Arrow(row + i, col));
            else
                break;
        }

        // Get all moves diag left/up
        for(int i = 1; col - i >= 0 && row - i >= 0; i++)
        {
            if(board[row - i][col - i] == null)
                moves.add(new Arrow(row - i, col - i));
            else
                break;
        }

        // Get all moves diag left/down
        for(int i = 1; col - i >= 0 && row + i <= 9; i++)
        {
            if(board[row + i][col - i] == null)
                moves.add(new Arrow(row + i, col - i));
            else
                break;
        }

        // Get all moves diag right/down
        for(int i = 1; col + i <= 9 && row + i <= 9; i++)
        {
            if(board[row + i][col + i] == null)
                moves.add(new Arrow(row + i, col + i));
            else
                break;
        }

        // Get all moves diag right/up
        for(int i = 1; col + i <= 9 && row - i >= 0; i++)
        {
            if(board[row + i][col + i] == null)
                moves.add(new Arrow(row - i, col + i));
            else
                break;
        }

        return moves;
    }
}

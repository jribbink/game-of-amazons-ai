package ubc.cosc322.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import ubc.cosc322.board.tiles.*;
import ubc.cosc322.search.ChildGenerator;
import ubc.cosc322.search.SearchNode;

public class GameState {
    public BoardTile[][] board = new BoardTile[10][10];

    public Queen[] friendlies;
    public Queen[] enemies;
    public ArrayList<Arrow> arrows = new ArrayList<>();

    public ArrayList<Queen> moves = new ArrayList<>();

    public boolean ourTurn = true;

    ChildGenerator childGen = new ChildGenerator();

    // MONTE CARLO
    public AtomicInteger numVisit = new AtomicInteger(0);
    public AtomicInteger numWins = new AtomicInteger(0);

    public GameState(boolean isWhite) {
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

        ourTurn = !isWhite;

        // Will populate the board object based on friendlies and enemies
        rebaseBoard();

        // Update possible queen moves
        updateMoves();
    }

    public void updateMCTS(int wins, int visits)
    {
        numWins.addAndGet(wins);
        numVisit.addAndGet(visits);
    }

    public GameState(Queen[] friendlies, Queen[] enemies, ArrayList<Arrow> arrows, boolean ourTurn)
    {
        this.friendlies = friendlies;
        this.enemies = enemies;
        this.arrows = arrows;
        this.ourTurn = ourTurn;

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
        Queen[] queens = (ourTurn)?friendlies:enemies;
        for(Queen q: queens)
            moves.addAll(getMoves(q));
    }

    public void moveQueen(Queen qpos, Arrow apos) {
        addArrow(apos);

        if (qpos.friendly) {
            for (Queen Q1 : friendlies) {
                if (Q1.row == qpos.prev_row && Q1.col == qpos.prev_col) {
                    Q1.moveQueen(qpos.row, qpos.col);
                }
            }
        } else {
            for (Queen Q1 : enemies) {
                if (Q1.row == qpos.prev_row && Q1.col == qpos.prev_col) {
                    Q1.moveQueen(qpos.row, qpos.col);
                }
            }
        }
        rebaseBoard();
        ourTurn = !ourTurn;
    }

    public void randomPlay() {
        /*ArrayList<SearchNode> possibleMoves = childGen.getChildren(this);

        if(possibleMoves.size() == 0)
        {
            int status = checkStatus();
            System.out.print("");

            possibleMoves = childGen.getChildren(this);

        }

        SearchNode move = possibleMoves.get((int)(Math.random() * possibleMoves.size()));*/

        updateMoves();
        Queen move = moves.get((int)(Math.random() * moves.size()));
        ArrayList<Arrow> arrows = getArrowMoves(move.row, move.col, move.prev_row, move.prev_col);
        Arrow arrow = arrows.get((int)(Math.random() * arrows.size()));
        move.friendly = ourTurn;

        moveQueen(move, arrow);
    }

    // Check if opponent has been defeated :)
    public boolean isGoal() {
        return isFriendlyGoal();
    }

    public boolean isFriendlyGoal() {
        ArrayList<Queen> m = new ArrayList<>();
        for(Queen enemy: enemies)
            m.addAll(getMoves(enemy));
        
        return m.isEmpty();
    }

    public boolean isEnemyGoal() {
        ArrayList<Queen> m = new ArrayList<>();
        for(Queen friend: friendlies)
            m.addAll(getMoves(friend));
        
        return m.isEmpty();
    }

    public int checkStatus() {
        return (isFriendlyGoal())?1:(isEnemyGoal())?-1:0;
    }

    public double getScore() {
        int visit = numVisit.get();
        return visit!=0?((double)numWins.get() / visit):0;
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
        for(int i = 1; q.row - i >= 0; i++)
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
            if(board[q.row - i][q.col + i] == null)
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

    public ArrayList<Arrow> getArrowMoves(int row, int col, int old_row, int old_col) {
        ArrayList<Arrow> moves = new ArrayList<>();

        // Get all moves left
        for(int i = 1; col - i >= 0; i++)
        {
            if(board[row][col - i] == null || (row == old_row && col - i == old_col))
                moves.add(new Arrow(row, col - i));
            else
                break;
        }

        // Get all moves right
        for(int i = 1; col + i <= 9; i++)
        {
            if(board[row][col + i] == null || (row == old_row && col + i == old_col))
                moves.add(new Arrow(row, col + i));
            else
                break;
        }

        // Get all moves down
        for(int i = 1; row - i >= 0; i++)
        {
            if(board[row - i][col] == null || (row - i == old_row && col == old_col))
                moves.add(new Arrow(row - i, col));
            else
                break;
        }

        // Get all moves up
        for(int i = 1; row + i <= 9; i++)
        {
            if(board[row + i][col] == null || (row + i == old_row && col == old_col))
                moves.add(new Arrow(row + i, col));
            else
                break;
        }

        // Get all moves diag left/up
        for(int i = 1; col - i >= 0 && row - i >= 0; i++)
        {
            if(board[row - i][col - i] == null || (row - i == old_row && col - i == old_col))
                moves.add(new Arrow(row - i, col - i));
            else
                break;
        }

        // Get all moves diag left/down
        for(int i = 1; col - i >= 0 && row + i <= 9; i++)
        {
            if(board[row + i][col - i] == null || (row + i == old_row && col - i == old_col))
                moves.add(new Arrow(row + i, col - i));
            else
                break;
        }

        // Get all moves diag right/down
        for(int i = 1; col + i <= 9 && row + i <= 9; i++)
        {
            if(board[row + i][col + i] == null || (row + i == old_row && col + i == old_col))
                moves.add(new Arrow(row + i, col + i));
            else
                break;
        }

        // Get all moves diag right/up
        for(int i = 1; col + i <= 9 && row - i >= 0; i++)
        {
            if(board[row - i][col + i] == null || (row - i == old_row && col + i == old_col))
                moves.add(new Arrow(row - i, col + i));
            else
                break;
        }

        return moves;
    }
}

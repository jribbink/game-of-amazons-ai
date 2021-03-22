package ubc.cosc322.search;
import java.util.*; 
import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;

public class SearchNode {
    private Queen queen;
    private Arrow arrow;
    private int heuristic;
    private ChildGenerator childGen = new ChildGenerator();
    private ArrayList<SearchNode> children = new ArrayList<SearchNode>();
   
    // public GameState GameState;

    GameState board;
    public SearchNode(GameState board, Queen queen, Arrow arrow, int heuristic) {
        this.board = board;
        this.queen = queen;
        this.arrow = arrow;
        this.heuristic = heuristic;
    }


    public SearchNode(GameState board) {
        this.board = board;
    }

    public Queen getQueen(){
        return queen;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public void setChildren(boolean ourTurn) {
        children = childGen.getChildren(board, ourTurn);
    }

    public ArrayList<SearchNode> getChildren() {
        return children;
    }
}

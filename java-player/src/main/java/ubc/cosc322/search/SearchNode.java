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
    public ArrayList<SearchNode> children = new ArrayList<SearchNode>();

    public SearchNode parent;
   
    // public GameState GameState;

    public GameState board;
    
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

    public void setChildren() {
        children = childGen.getChildren(board, this);
    }

    public ArrayList<SearchNode> getChildren() {
        return children;
    }

    public void setParent(SearchNode parent) {
        this.parent = parent;
    }

    public SearchNode getParent() {
        return parent;
    }

    public void moveQueen(Queen qpos, Arrow apos) {
        board.moveQueen(qpos, apos);
        children.clear();
    }

    public static SearchNode max(SearchNode n1, SearchNode n2) {
        if(n2 == null || (n1 != null && n1.getHeuristic() > n2.getHeuristic())) return n1;
        else return n2;
    }

    public static SearchNode min(SearchNode n1, SearchNode n2) {
        if(n2 == null || (n1 != null && n1.getHeuristic() > n2.getHeuristic())) return n1;
        else return n2;
    }

    public SearchNode getRandomChild() {
        return children.get((int)(Math.random() * children.size()));
    }
}

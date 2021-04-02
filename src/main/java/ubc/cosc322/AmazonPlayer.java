package ubc.cosc322;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import sfs2x.client.entities.Room;
import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;
import ubc.cosc322.search.MonteCarloTreeSearch;
import ubc.cosc322.search.ReinforcementLearning;
import ubc.cosc322.search.SearchNode;
import ubc.cosc322.search.SearchTree;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.Amazon.GameBoard;
import ygraph.ai.smartfox.games.amazons.AmazonsBoard;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * For testing and demo purposes only. An GUI Amazon client for human players 
 * @author yong.gao@ubc.ca
 */
public class AmazonPlayer extends GamePlayer {
  private static final Logger LOGGER = Logger.getLogger( AmazonPlayer.class.getName() );

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;

    private String userName = null;
    private String password = null;

    private boolean isWhite = true;
    private int turn = 0;

    private GameState board;
    private SearchTree search;
    private MonteCarloTreeSearch mcts;

    AmazonsBoard gBoard;

    public AmazonPlayer(String username, String password)
    {
        this.userName = username;
    	this.password = password;

        //Init GUI
    	this.gamegui = new BaseGameGUI(this);
        this.gamegui.add(new JLabel(new ImageIcon("C:/Users/Jordan/Downloads/gao.png")));
        this.gamegui.setBackground(Color.cyan);
        this.gamegui.revalidate();
        this.gamegui.repaint();
    }

    private void handleLocalMove() {
        turn++;
        SearchNode bestMove = mcts.findNextMove();

        if(bestMove.getQueen() == null) {
            System.out.println((bestMove.board.checkStatus() == 1)?"OKGN AI WINS!!!!! #1 #1 #1":"We have suffered a defeat. :(");
            return;
        }

        bestMove.getQueen().friendly = true;
        mcts.moveQueen(bestMove.getQueen(), bestMove.getArrow());

        gameClient.sendMoveMessage(bestMove.getQueen().oldPosition(), bestMove.getQueen().currentPos(), bestMove.getArrow().currentPos());
        this.gamegui.updateGameState(bestMove.getQueen().oldPosition(), bestMove.getQueen().currentPos(), bestMove.getArrow().currentPos());


        LOGGER.log(Level.INFO, "[" + bestMove.getQueen().prev_row + ", " + bestMove.getQueen().prev_col + "] -> [" + bestMove.getQueen().row + ", " + bestMove.getQueen().col + "] | [" + bestMove.getArrow().row + ", " + bestMove.getArrow().col + "]" + " Heuristic: " + bestMove.getHeuristic());
        
        //ReinforcementLearning.getInstance().serialize();
    }

    private void handleOpponentMove(Map<String, Object> msgDetails) {
        turn++;

		ArrayList<Integer> queenCurrent = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
		ArrayList<Integer> queenNext = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
		ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);

        LOGGER.log(Level.INFO, "[" + queenCurrent.get(0) + ", " + queenCurrent.get(1) + "] -> [" + queenNext.get(0) + ", " + queenNext.get(1) + "] | [" + arrowPos.get(0) + ", " + arrowPos.get(1) + "]");

        Queen enemy = new Queen(queenNext.get(0) - 1, queenNext.get(1) - 1, false);
        enemy.prev_row = queenCurrent.get(0) - 1;
        enemy.prev_col = queenCurrent.get(1) - 1;

        Arrow arrow = new Arrow(arrowPos.get(0) - 1, arrowPos.get(1) - 1);

        this.gamegui.updateGameState(queenCurrent, queenNext, arrowPos);

        if(mcts != null) {
            mcts.moveQueen(enemy, arrow);
            handleLocalMove();
        }
    }

    @Override
    public void onLogin()
    {
        LOGGER.log(Level.INFO, "Login Success");

        //Join Room
        List<Room> rooms = gameClient.getRoomList();
        getGameClient().joinRoom("Oyama Lake");
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails)
    {
        switch(messageType)
        {
            case GameMessage.GAME_ACTION_START:
                LOGGER.log(Level.INFO, "Game Started");
                
                // Set our colour
                isWhite = msgDetails.get("player-white").equals(this.userName);

                LOGGER.log(Level.INFO, "Color" + isWhite);

                

                // Initialize game state
                board = new GameState(isWhite);
                mcts = new MonteCarloTreeSearch(new SearchNode(board));
                mcts.isWhite = isWhite;

                // Make first move if black
                if(!isWhite)
                    handleLocalMove();
                 
                
                break;
            case GameMessage.GAME_ACTION_MOVE:
                LOGGER.log(Level.INFO, "Opponent Move");
                
                handleOpponentMove(msgDetails);
                break;

            case GameMessage.GAME_STATE_BOARD:
                Object temp = msgDetails.get("game-state");
                ArrayList<Integer> arr = (ArrayList<Integer>) temp;
                this.gamegui.setGameState(arr);
                break;
            default:

                break;
        }

        return true;
    }

    @Override
    public String userName() {
    	return userName;
    }

    @Override
    public GameClient getGameClient() {
        return this.gameClient;
    }

    @Override
    public BaseGameGUI getGameGUI() {
        return this.gamegui;
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, password, this);
    }
}


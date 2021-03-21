package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import sfs2x.client.entities.Room;
import ubc.cosc322.board.GameBoard;
import ubc.cosc322.search.SearchNode;
import ubc.cosc322.search.SearchTree;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;

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

    private GameBoard board;
    private SearchTree search;

    public AmazonPlayer(String username, String password)
    {    	   
      this.userName = username;
    	this.password = password;

      //Init GUI
    	this.gamegui = new BaseGameGUI(this);
    }

    public static void main(String[] args) {				 
    	AmazonPlayer player = new AmazonPlayer("Jordan", "cosc322");
    	
    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
                }
            });
    	}
    }

    private void handleLocalMove() {
        SearchNode bestMove = search.performMove();
        board.updateMoves();


        gameClient.sendMoveMessage(bestMove.getQueen().oldPosition(), bestMove.getQueen().currentPos(), bestMove.getArrow().currentPos());

        //Check if game is over
    }

    private void handleOpponentMove(Map<String, Object> msgDetails) {
        handleLocalMove();
    }

    @Override
    public void onLogin()
    {
        LOGGER.log(Level.INFO, "Login Success");

        //Join Room
        List<Room> rooms = gameClient.getRoomList();
        getGameClient().joinRoom(rooms.get(4).getName());
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

                // Initialize game state
                board = new GameBoard(isWhite);
                search = new SearchTree(new SearchNode(board));

                // Make first move if black
                if(!isWhite)
                    handleLocalMove();
                
                break;
            case GameMessage.GAME_ACTION_MOVE:
                LOGGER.log(Level.INFO, "Opponent Move");
                handleOpponentMove(msgDetails);
                this.gamegui.updateGameState(msgDetails);
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


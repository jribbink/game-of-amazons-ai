package ubc.cosc322;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Timer;
import sfs2x.client.entities.Room;
import ubc.cosc322.board.GameState;
import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.Queen;
import ubc.cosc322.messages.RoomModel;
import ubc.cosc322.messages.StateUpdate;
import ubc.cosc322.search.MonteCarloTreeSearch;
import ubc.cosc322.search.ReinforcementLearning;
import ubc.cosc322.search.SearchNode;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.Amazon.GameBoard;
import ygraph.ai.smartfox.games.amazons.AmazonsBoard;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter; 

/**
 * For testing and demo purposes only. An GUI Amazon client for human players 
 * @author yong.gao@ubc.ca
 */
public class AmazonPlayer extends GamePlayer {
    private static final Logger LOGGER = Logger.getLogger( AmazonPlayer.class.getName() );

    private GameClient gameClient = null; 
    //private BaseGameGUI gamegui = null;

    private String userName = null;
    private String password = null;

    private boolean isWhite = true;
    private int turn = 0;

    private MonteCarloTreeSearch mcts;

    AmazonsBoard gBoard;
    WebsocketClient client = new WebsocketClient(this);
    Gson gson = new Gson();

    private Room[] lastRooms = null;

    public AmazonPlayer(String username, String password)
    {
        this.userName = username;
    	this.password = password;

        //Init GUI
    	//this.gamegui = new BaseGameGUI(this);
        client.connect("ws://localhost:3222");
        reset();
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
        //this.gamegui.updateGameState(bestMove.getQueen().oldPosition(), bestMove.getQueen().currentPos(), bestMove.getArrow().currentPos());

        String logMsg = "[" + bestMove.getQueen().prev_row + ", " + bestMove.getQueen().prev_col + "] -> [" + bestMove.getQueen().row + ", " + bestMove.getQueen().col + "] | [" + bestMove.getArrow().row + ", " + bestMove.getArrow().col + "]" + " Heuristic: " + bestMove.getHeuristic();
        LOGGER.log(Level.INFO, logMsg);
        this.client.log(logMsg);
        
        //ReinforcementLearning.getInstance().serialize();

        //update game state
        client.updateState(mcts.root.board, isWhite);
    }

    private void handleOpponentMove(Map<String, Object> msgDetails) {
        turn++;

		ArrayList<Integer> queenCurrent = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
		ArrayList<Integer> queenNext = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
		ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);

        String logMsg = "[" + queenCurrent.get(0) + ", " + queenCurrent.get(1) + "] -> [" + queenNext.get(0) + ", " + queenNext.get(1) + "] | [" + arrowPos.get(0) + ", " + arrowPos.get(1) + "]";
        LOGGER.log(Level.INFO, logMsg);
        client.log(logMsg);

        Queen enemy = new Queen(queenNext.get(0) - 1, queenNext.get(1) - 1, false);
        enemy.prev_row = queenCurrent.get(0) - 1;
        enemy.prev_col = queenCurrent.get(1) - 1;

        Arrow arrow = new Arrow(arrowPos.get(0) - 1, arrowPos.get(1) - 1);

        //this.gamegui.updateGameState(queenCurrent, queenNext, arrowPos);

        if(mcts != null) {
            mcts.moveQueen(enemy, arrow);

            //Update GUI
            client.updateState(mcts.root.board, isWhite);

            handleLocalMove();
        }
    }

    public void reset() {
        this.mcts = null;
        client.updateState(new GameState(isWhite), isWhite);
    }

    @Override
    public void onLogin()
    {
        LOGGER.log(Level.INFO, "Login Success");

        Timer timer = new Timer();
        TimerTask roomTask = new TimerTask(){
            @Override
            public void run() {
                List<Room> roomList = gameClient.getRoomList();
                client.sendRoomList(roomList);
            }
        };
        timer.schedule(roomTask, 500, 500);
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails)
    {
        switch(messageType)
        {
            case GameMessage.GAME_ACTION_START:
                LOGGER.log(Level.INFO, "Game Started");
                //ws.broadcast("message");
                
                // Set our colour
                isWhite = msgDetails.get("player-white").equals(this.userName);

                LOGGER.log(Level.INFO, "Color" + isWhite);

                

                // Initialize game state
                GameState board = new GameState(isWhite);
                mcts = new MonteCarloTreeSearch(new SearchNode(board));
                mcts.ws = this.client;
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
                //client.updateState(arr, isWhite);
                //this.gamegui.setGameState(arr);
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
        //return this.gamegui;
        return null;
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, password, this);
    }
}


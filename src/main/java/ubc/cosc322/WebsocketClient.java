package ubc.cosc322;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.google.gson.Gson;

import sfs2x.client.entities.Room;
import ubc.cosc322.board.GameState;
import ubc.cosc322.messages.BaseMessage;
import ubc.cosc322.messages.LoggingMessage;
import ubc.cosc322.messages.MessageParser;
import ubc.cosc322.messages.RoleNegotiation;
import ubc.cosc322.messages.RoomChangeMessage;
import ubc.cosc322.messages.RoomListMessage;
import ubc.cosc322.messages.RoomModel;
import ubc.cosc322.messages.StateUpdate;
import ubc.cosc322.search.MonteCarloTreeSearch;

@ClientEndpoint
public class WebsocketClient {

    protected   WebSocketContainer container;
    protected   Session userSession = null;

    public Gson gson = new Gson();
    public MessageParser parser = new MessageParser(gson);

    public AmazonPlayer player = null;

    private RoomModel[] lastRooms = null;

    public WebsocketClient(AmazonPlayer player) {
        this.player = player;
        container = ContainerProvider.getWebSocketContainer();
    }

    public void sendRoomList(List<Room> roomList) {
      RoomModel[] rooms  = new RoomModel[roomList.size()];
      boolean same = (lastRooms == null)?false:true;
      for(int i = 0; i < roomList.size(); i++) 
      {
          rooms[i] = new RoomModel();
          rooms[i].name = roomList.get(i).getName();
          if(same && lastRooms[i].name != rooms[i].name) same = false;
          rooms[i].players = roomList.get(i).getUserCount();
          if(same && lastRooms[i].players != rooms[i].players) same = false;
          rooms[i].spectators = roomList.get(i).getSpectatorCount();
          if(same && lastRooms[i].spectators != rooms[i].spectators) same = false;
      }

      if(!same)
      {
          RoomListMessage msg = new RoomListMessage(rooms);
          send(gson.toJson(msg));
      }
    }

    public void updateState(GameState board, boolean isWhite) {
      String msg = gson.toJson(new StateUpdate(board, isWhite));
      send(msg);
    }

    public void log(String msg) {
      LoggingMessage logMsg = new LoggingMessage(msg);
      send(gson.toJson(logMsg));
    }

    public void connect(String sServer) {

          try {
              userSession = container.connectToServer(this, new URI(sServer));

            } catch (DeploymentException | URISyntaxException | IOException e) {
                e.printStackTrace();
            }

    }

    public void send(String sMsg) {
      try{
        userSession.getBasicRemote().sendText(sMsg);
      }
      catch(Exception ex) {
        System.out.println("couldn't send msg");
      }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected");

        try {
          Gson gson = new Gson();
          session.getBasicRemote().sendText(gson.toJson(new RoleNegotiation()));
  
          System.out.println("Handshaked");
        }
        catch(Exception ex) {
          System.out.println("Handshake FAILED!!!!");
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
      // On close
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        BaseMessage message = parser.parse(msg);
        if(message instanceof RoomChangeMessage) {
          RoomChangeMessage m = ((RoomChangeMessage)message);
          player.reset();
          if(m.room != "")
            player.getGameClient().joinRoom(m.room);
          else
            player.getGameClient().leaveCurrentRoom();
        }
    }

    public void Disconnect() throws IOException {
        userSession.close();
    }
}
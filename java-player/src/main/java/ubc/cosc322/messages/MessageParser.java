package ubc.cosc322.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class MessageParser {
    public Gson gson;
    public JsonParser parser = new JsonParser();

    public MessageParser(Gson gson) {
        this.gson = gson;
    }

    public BaseMessage parse(String msg)
    {
        BaseMessage parsed = gson.fromJson(msg, BaseMessage.class);
        switch (parsed.message_type) {
          case MessageType.STATE_UPDATE:
            return gson.fromJson(msg, StateUpdate.class);
          case MessageType.ROLE_NEGOTIATION:
            return gson.fromJson(msg, RoleNegotiation.class);
          case MessageType.ROOMLIST_MESSAGE:
            return gson.fromJson(msg, RoomListMessage.class);
          case MessageType.LOGGING_MESSAGE:
            return gson.fromJson(msg, LoggingMessage.class);
          case MessageType.ROOM_CHANGE_MESSAGE:
            return gson.fromJson(msg, RoomChangeMessage.class);
          default:
            return null;
        }
    }
}

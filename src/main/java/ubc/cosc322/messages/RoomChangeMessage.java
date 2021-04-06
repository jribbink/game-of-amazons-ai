package ubc.cosc322.messages;

public class RoomChangeMessage extends BaseMessage {
  public String room = "";

  public RoomChangeMessage() {
    
  }

  public RoomChangeMessage(String room) {
    message_type = MessageType.ROOM_CHANGE_MESSAGE;
    this.room = room;
  }
}

package ubc.cosc322.messages;

public class RoomListMessage extends BaseMessage {
  RoomModel[] rooms;

  public RoomListMessage(RoomModel[] rooms) {
    message_type = "room-update";
    this.rooms = rooms;
  }
}

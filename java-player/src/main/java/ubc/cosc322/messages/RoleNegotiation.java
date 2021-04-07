package ubc.cosc322.messages;

public class RoleNegotiation extends BaseMessage {
  public String client_type = RoleType.PLAYER_CLIENT;

  public RoleNegotiation() {
    message_type = MessageType.ROLE_NEGOTIATION;
  }
}

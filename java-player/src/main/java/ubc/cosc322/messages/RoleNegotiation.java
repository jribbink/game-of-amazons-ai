package ubc.cosc322.messages;

class RoleType {
  public static final String GUI_CLIENT = "gui-client";
  public static final String PLAYER_CLIENT = "player-client";
}

public class RoleNegotiation extends BaseMessage {
  public String client_type = RoleType.PLAYER_CLIENT;

  public RoleNegotiation() {
    message_type = "role-negotiation";
  }
}

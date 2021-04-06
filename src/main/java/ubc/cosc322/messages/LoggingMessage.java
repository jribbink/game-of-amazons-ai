package ubc.cosc322.messages;

import java.util.List;

public class LoggingMessage extends BaseMessage {
  public String[] lines;

  public LoggingMessage() {
    message_type = MessageType.LOGGING_MESSAGE;
  }

  public LoggingMessage(String msg) {
    this();
    this.lines = new String[1];
    this.lines[0] = msg;
  }

  public LoggingMessage(String[] msg) {
    this();
    lines = msg;
  }

  public LoggingMessage(List<String> msg) {
    this();
    lines = msg.toArray(new String[0]);
  }
}

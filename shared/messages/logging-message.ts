import { MessageType } from "../enum/message-type";
import { BaseMessage } from "./base-message";

export class LoggingMessage extends BaseMessage {
  message_type = MessageType.LOGGING_MESSAGE;
  lines: string[] = [];

  constructor(data: Partial<LoggingMessage>) {
    super();
    Object.assign(this, data);
  }
}

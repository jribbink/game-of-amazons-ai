import WebSocket from "ws";
import { MessageType } from "../enum/message-type";

export abstract class BaseMessage {
  abstract message_type: MessageType;
}

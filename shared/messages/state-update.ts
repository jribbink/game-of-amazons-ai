import { BaseMessage } from "./base-message";
import WebSocket from "ws";
import { MessageType } from "../enum/message-type";

export class StateUpdate extends BaseMessage {
  message_type = MessageType.STATE_UPDATE;
  state: number[][] = [[]];
  isWhite: boolean = false;
  ourTurn: boolean = false;

  constructor(data: Partial<StateUpdate>) {
    super();
    Object.assign(this, data);
  }
}

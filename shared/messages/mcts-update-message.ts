import { MessageType } from "../enum/message-type";
import { MCTSUpdate } from "../models/gameplay/MCTSUpdate";
import { BaseMessage } from "./base-message";

export class MCTSUpdateMessage extends BaseMessage {
  public message_type = MessageType.MCTS_UPDATE_MESSAGE;
  public updates: MCTSUpdate[] = [];

  constructor(data: Partial<MCTSUpdateMessage>) {
    super();
    Object.assign(this, data);
  }
}

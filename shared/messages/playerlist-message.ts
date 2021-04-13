import { MessageType } from "../enum/message-type";
import { PlayerClient } from "../models/player-client";
import { BotClient } from "../models/bot-client";
import { SocketClient } from "../models/socket-client";
import { BaseMessage } from "./base-message";

export class PlayerListMessage extends BaseMessage {
  message_type = MessageType.PLAYERLIST_MESSAGE;
  players?: (PlayerClient | BotClient)[];
  addingPlayers: boolean = true;

  constructor(data: Partial<PlayerListMessage>) {
    super();
    Object.assign(this, data);
  }
}

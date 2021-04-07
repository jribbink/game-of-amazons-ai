import WebSocket from "ws";
import { BaseMessage } from "./base-message";
import { MessageType } from "../enum/message-type";
import { StateUpdate } from "./state-update";
import { RoleNegotiation } from "./role-negotiation";
import { RoomMessage } from "./room-message";
import { PlayerListMessage } from "./playerlist-message";
import { LoggingMessage } from "./logging-message";
import { RoomChangeMessage } from "./room-change-message";
import { MCTSUpdateMessage } from "./mcts-update-message";

export class MessageParser {
  parse(msg: WebSocket.Data): BaseMessage | undefined {
    const message: string = msg.toString();
    var parsed = JSON.parse(message);

    switch (parsed.message_type) {
      case MessageType.STATE_UPDATE:
        return new StateUpdate(parsed);
      case MessageType.ROLE_NEGOTIATION:
        return new RoleNegotiation(parsed);
      case MessageType.ROOMLIST_MESSAGE:
        return new RoomMessage(parsed);
      case MessageType.PLAYERLIST_MESSAGE:
        return new PlayerListMessage(parsed);
      case MessageType.LOGGING_MESSAGE:
        return new LoggingMessage(parsed);
      case MessageType.ROOM_CHANGE_MESSAGE:
        return new RoomChangeMessage(parsed);
      case MessageType.MCTS_UPDATE_MESSAGE:
        return new MCTSUpdateMessage(parsed);
    }

    return undefined;
  }
}

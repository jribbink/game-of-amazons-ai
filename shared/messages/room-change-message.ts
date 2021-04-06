import { MessageType } from "../enum/message-type";
import { BaseMessage } from "./base-message";

export class RoomChangeMessage extends BaseMessage {
  message_type = MessageType.ROOM_CHANGE_MESSAGE;
  room?: string;

  constructor(data: Partial<RoomChangeMessage>) {
    super();
    Object.assign(this, data);
  }
}

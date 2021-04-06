import { MessageType } from "../enum/message-type";
import { Room } from "../models/room";
import { BaseMessage } from "./base-message";

export class RoomMessage extends BaseMessage {
  message_type = MessageType.ROOMLIST_MESSAGE;
  rooms: Room[] = [];

  constructor(data: Partial<RoomMessage>) {
    super();
    Object.assign(this, data);
  }
}

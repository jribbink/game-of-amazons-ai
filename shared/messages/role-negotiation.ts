import { MessageType } from "../enum/message-type";
import { RoleType } from "../enum/role-type";
import { BaseMessage } from "./base-message";

export class RoleNegotiation extends BaseMessage {
  message_type = MessageType.ROLE_NEGOTIATION;
  client_type?: RoleType = undefined;

  constructor(data: Partial<RoleNegotiation>) {
    super();
    Object.assign(this, data);
  }
}

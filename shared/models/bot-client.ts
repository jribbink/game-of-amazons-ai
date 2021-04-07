import { SocketClient } from "./socket-client";

export class BotClient extends SocketClient {
  ip: string;

  constructor(ip: string) {
    super();
    this.ip = ip;
  }
}

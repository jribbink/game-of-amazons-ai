import { SocketClient } from "./socket-client";

export class PlayerClient extends SocketClient {
  ip: string;

  constructor(ip: string) {
    super();
    this.ip = ip;
  }
}

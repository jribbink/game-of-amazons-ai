import Websocket from "ws";
import { BaseMessage } from "../../shared/messages/base-message";
import { MessageParser } from "../../shared/messages/message-parser";
import { MessageType } from "../../shared/enum/message-type";
import { StateUpdate } from "../../shared/messages/state-update";
import { SocketClient } from "../../shared/models/socket-client";
import { RoleNegotiation } from "../../shared/messages/role-negotiation";
import { RoleType } from "../../shared/enum/role-type";
import { GuiClient } from "../../shared/models/gui-client";
import { BotClient } from "../../shared/models/bot-client";
import { PlayerClient } from "../../shared/models/player-client";
import { PlayerListMessage } from "../../shared/messages/playerlist-message";
import { RoomMessage } from "../../shared/messages/room-message";
import { Room } from "./models/room";
import { LoggingMessage } from "../../shared/messages/logging-message";
import { Logger } from "./logger";
import { RoomChangeMessage } from "../../shared/messages/room-change-message";
import { MCTSUpdateMessage } from "../../shared/messages/mcts-update-message";
import { MCTSUpdate } from "./models/gameplay/MCTSUpdate";

const wss = new Websocket.Server({ port: 3222 });
const msgParser: MessageParser = new MessageParser();

const clients = new Map<Websocket, SocketClient>();
var state: number[][] | undefined = undefined;
var isWhite: boolean = true;

var rooms: Room[];
var currentRoom: string;

var searchState: MCTSUpdate[] = [];

var logger: Logger = new Logger();
logger.pushLines([
  "--------------------------------------------------\n\tOKGN AI AMAZONS CLIENT V0.1\n\t\t  A Yong Gao Project\n--------------------------------------------------\n",
]);

function broadcast(
  ws: Websocket,
  msg: any,
  recipients: Partial<{ player: boolean; bot: boolean; gui: boolean }>
) {
  wss.clients.forEach((client) => {
    if (
      client != ws &&
      ((recipients.gui && isGui(client)) ||
        (recipients.player && isPlayer(client)) ||
        (recipients.bot && isBot(client)))
    ) {
      client.send(JSON.stringify(msg));
    }
  });
}

function isBot(ws: Websocket) {
  return (clients.get(ws) ?? null) instanceof BotClient;
}

function isGui(ws: Websocket) {
  return (clients.get(ws) ?? null) instanceof GuiClient;
}

function isPlayer(ws: Websocket) {
  return (clients.get(ws) ?? null) instanceof PlayerClient;
}

function handshake(ws: Websocket) {
  // Send State information
  if (state != undefined) {
    ws.send(
      JSON.stringify(new StateUpdate({ state: state, isWhite: isWhite }))
    );
  }

  // Send Room Information
  if (rooms != null && isGui(ws)) {
    var roomMsg = new RoomMessage({ rooms: rooms });
    ws.send(JSON.stringify(roomMsg));
  }

  // Send Logging Information
  if (logger.lines != undefined && logger.lines.length != 0 && isGui(ws)) {
    var logMsg = new LoggingMessage({ lines: logger.lines });
    ws.send(JSON.stringify(logMsg));
  }

  // Send Current Room
  if (currentRoom != undefined && isGui(ws)) {
    var curRmMsg = new RoomChangeMessage({ room: currentRoom });
    ws.send(JSON.stringify(curRmMsg));
  }

  // Send Botnet
  var playerClients: PlayerClient[] = [];
  clients.forEach((client) => {
    if (client instanceof PlayerClient) playerClients.push(client);
  });
  ws.send(JSON.stringify(new PlayerListMessage({ players: playerClients })));

  console.log("Handshake Completed");
}

function connected(ws: Websocket) {
  clients.set(ws, new SocketClient());
  console.log("New Connection");
}

wss.on("connection", (ws) => {
  connected(ws);

  ws.on("message", (message) => {
    var msg: BaseMessage = msgParser.parse(message)!!;
    if (msg instanceof StateUpdate) {
      broadcast(ws, msg, { bot: true, gui: true });
      isWhite = msg.isWhite;
      state = msg.state;

      if (!msg.ourTurn) {
        var m = new MCTSUpdateMessage({ updates: searchState });
        broadcast(ws, m, { gui: true });
        searchState = [];
      }
    } else if (msg instanceof RoleNegotiation) {
      if (msg.client_type == RoleType.GUI_CLIENT) {
        clients.set(ws, new GuiClient());
      } else if (msg.client_type == RoleType.PLAYER_CLIENT) {
        var player_exists = false;
        clients.forEach((client) => {
          if (client instanceof PlayerClient) player_exists = true;
        });

        console.log(player_exists);

        if (player_exists) {
          clients.set(ws, new BotClient((ws as any)._socket.remoteAddress));
          ws.send(
            JSON.stringify(
              new RoleNegotiation({ client_type: RoleType.BOT_CLIENT })
            )
          );
          console.log("sent bot request");
        } else {
          clients.set(ws, new PlayerClient((ws as any)._socket.remoteAddress));
          ws.send(
            JSON.stringify(
              new RoleNegotiation({ client_type: RoleType.PLAYER_CLIENT })
            )
          );
        }
      }

      handshake(ws);
      broadcast(
        ws,
        new PlayerListMessage({
          players: [clients.get(ws) as PlayerClient | BotClient],
        }),
        { gui: true }
      );
    } else if (msg instanceof RoomMessage) {
      var same = true;
      if (rooms != null) {
        for (var i = 0; i < rooms.length; i++) {
          for (var prop in rooms[i]) {
            if ((rooms[i] as any)[prop] !== (msg.rooms[i] as any)[prop]) {
              same = false;
            }
          }
        }
      } else {
        same = false;
      }

      rooms = msg.rooms;
      if (!same) broadcast(ws, msg, { gui: true });
    } else if (msg instanceof LoggingMessage) {
      logger.pushLines(msg.lines);
      broadcast(ws, msg, { gui: true });
    } else if (msg instanceof RoomChangeMessage) {
      currentRoom = msg.room!!;
      broadcast(ws, msg, { player: true });
    } else if (msg instanceof MCTSUpdateMessage) {
      var m: MCTSUpdateMessage = msg;
      broadcast(ws, m, { player: true });

      var updates = m.updates;
      searchState.forEach((state) => {
        updates.forEach((update, idx) => {
          if (
            update.arrow?.col == state.arrow?.col &&
            update.arrow?.row == state.arrow?.row &&
            update.move?.row == state.move?.row &&
            update.move?.col == state.move?.col &&
            update.move?.friendly == state.move?.friendly &&
            update.move?.prev_col == state.move?.prev_col &&
            update.move?.prev_row == state.move?.prev_row
          ) {
            state.visits!! += update.visits!!;
            state.wins!! += update.wins!!;
            updates.splice(idx, 1);
          }
        });
      });
      searchState.push(...updates);
    }
  });

  ws.on("close", (code, reason) => {
    broadcast(
      ws,
      new PlayerListMessage({
        players: [clients.get(ws) as PlayerClient],
        addingPlayers: false,
      }),
      { gui: true }
    );
    clients.delete(ws);
    console.log("Lost Client");
  });
});

console.log("Websocket server started on port 3222");

import { Injectable } from '@angular/core';
import { Console } from 'node:console';
import { RoleType } from 'okgnai-shared/enum/role-type';
import { LoggingMessage } from 'okgnai-shared/messages/logging-message';
import { RoleNegotiation } from 'okgnai-shared/messages/role-negotiation';
import { BehaviorSubject, Observable } from 'rxjs';
import { BaseMessage } from '../../../../shared/messages/base-message';
import { MessageParser } from '../../../../shared/messages/message-parser';
import { PlayerListMessage } from '../../../../shared/messages/playerlist-message';
import { RoomMessage } from '../../../../shared/messages/room-message';
import { StateUpdate } from '../../../../shared/messages/state-update';
import { GuiClient } from '../../../../shared/models/gui-client';
import { PlayerClient } from '../../../../shared/models/player-client';
import { Room } from '../../../../shared/models/room';
import { RoomChangeMessage } from '../../../../shared/messages/room-change-message';
import { MCTSUpdate } from 'okgnai-shared/models/gameplay/MCTSUpdate';
import { MCTSUpdateMessage } from 'okgnai-shared/messages/mcts-update-message';

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  ws: WebSocket = new WebSocket('ws://192.168.1.89:3222/');

  state: number[][];
  ourTurn: boolean = false;
  lastStateUpdate: number = 0;
  isWhite: boolean = true;

  botnet: PlayerClient[] = [];

  rooms: Room[];
  currentRoom: string;

  logs: string[] = [];

  searchState: MCTSUpdate[] = [];
  winFactor: number;

  private parser = new MessageParser();

  constructor() {
    this.ws.onopen = (evt) => {
      this.negotiateConnection();
    };

    this.ws.onmessage = (msg) => {
      var data: BaseMessage = this.parser.parse(msg.data);

      if (data instanceof StateUpdate) {
        var state = data.state;
        for (var i = 0; i < state.length / 2; i++) {
          var tmp = state[i];
          state[i] = state[state.length - i - 1];
          state[state.length - i - 1] = tmp;
        }
        this.isWhite = data.isWhite;
        this.state = state;
        this.ourTurn = data.ourTurn;
        this.lastStateUpdate = new Date().getTime();
      } else if (data instanceof PlayerListMessage) {
        if (data.addingPlayers) {
          data.players.forEach((player) => {
            this.botnet.push(player);
          });
        } else {
          data.players.forEach((player) => {
            var idx = -1;
            this.botnet.forEach((p, i) => {
              if (p.id == player.id) idx = i;
            });
            if (idx != -1) {
              this.botnet.splice(idx, 1);
            }
          });
        }
      } else if (data instanceof RoomMessage) {
        this.rooms = data.rooms;
      } else if (data instanceof LoggingMessage) {
        this.logs.push(...data.lines);
      } else if (data instanceof RoomChangeMessage) {
        this.currentRoom = data.room!!;
      } else if (data instanceof MCTSUpdateMessage) {
        this.searchState = data.updates;

        var wins = 0;
        var playouts = 0;
        data.updates.forEach((state) => {
          wins += state.wins;
          playouts += state.visits;
        });
        this.winFactor = wins / playouts;

        this.searchState.sort((b, a) => {
          if (a.wins / a.visits > b.wins / b.visits) return 1;
          else if (a.wins / a.visits == b.wins / b.visits) return 0;
          else return -1;
        });
      } else {
        alert(JSON.stringify(data));
      }
    };
  }

  findBestMove() {
    var best: MCTSUpdate = null;
    this.searchState.forEach((move) => {
      if (best == null || best.wins / best.visits < move.wins / move.visits) {
        best = move;
      }
    });

    return best;
  }

  getWinFactor() {
    return this.winFactor;
  }

  negotiateConnection() {
    this.ws.send(
      JSON.stringify(new RoleNegotiation({ client_type: RoleType.GUI_CLIENT }))
    );
  }

  setRoom(room: string) {
    if (room == this.currentRoom) room = '';
    this.ws.send(JSON.stringify(new RoomChangeMessage({ room: room })));
    this.rooms.forEach((r) => {
      if (r.name == room) r.players++;
      if (r.name == this.currentRoom) r.players--;
    });
    this.currentRoom = room;
  }
}

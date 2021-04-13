import { Component, OnInit } from '@angular/core';
import { BotClient } from 'okgnai-shared/models/bot-client';
import { PlayerClient } from 'okgnai-shared/models/player-client';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'botnet-list',
  templateUrl: './botnet.component.html',
  styleUrls: ['./botnet.component.sass'],
})
export class BotnetComponent implements OnInit {
  botnet: (PlayerClient | BotClient)[];

  constructor(public socketService: WebsocketService) {
    this.botnet = socketService.botnet;
  }

  isBot(client: PlayerClient | BotClient) {
    return client instanceof BotClient;
  }

  ngOnInit(): void {}
}

import { Component, OnInit } from '@angular/core';
import { PlayerClient } from 'okgnai-shared/models/player-client';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'botnet-list',
  templateUrl: './botnet.component.html',
  styleUrls: ['./botnet.component.sass'],
})
export class BotnetComponent implements OnInit {
  botnet: PlayerClient[];

  constructor(public socketService: WebsocketService) {
    this.botnet = socketService.botnet;
  }

  test() {
    alert(this.botnet);
    alert(this.socketService.botnet);
  }

  ngOnInit(): void {}
}

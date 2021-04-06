import { Component, OnInit } from '@angular/core';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'gameplay-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.sass'],
})
export class LogsComponent implements OnInit {
  constructor(public socketService: WebsocketService) {}

  ngOnInit(): void {}

  getLogs(): string {
    return this.socketService.logs.join('\n');
  }
}

import { Component, OnInit } from '@angular/core';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'rooms-list',
  templateUrl: './rooms-list.component.html',
  styleUrls: ['./rooms-list.component.sass'],
})
export class RoomsListComponent implements OnInit {
  constructor(public socketService: WebsocketService) {}

  ngOnInit(): void {}

  setRoom(room: string) {
    this.socketService.setRoom(room);
  }
}

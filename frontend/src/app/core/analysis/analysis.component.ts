import { Component, OnInit } from '@angular/core';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'board-analysis',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.sass'],
})
export class AnalysisComponent implements OnInit {
  constructor(public socketService: WebsocketService) {}

  getAnalString() {
    var move = this.socketService.findBestMove();
    return (move.wins * 100) / move.visits + '% in ' + move.visits + ' Visits';
  }

  ngOnInit(): void {}
}

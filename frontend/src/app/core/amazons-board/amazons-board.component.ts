import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'amazons-board',
  templateUrl: './amazons-board.component.html',
  styleUrls: ['./amazons-board.component.sass'],
})
export class AmazonsBoardComponent implements OnInit {
  @Input()
  state: number[][];

  constructor(public socketService: WebsocketService) {}

  ngOnInit(): void {}

  numberToAscii(num: number) {
    return String.fromCharCode(97 + num);
  }

  whiteQueen(i: number, j: number) {
    var state = this.socketService.state;
    return state != undefined && state[i][j] != undefined && state[i][j] == 1;
  }

  blackQueen(i: number, j: number) {
    var state = this.socketService.state;
    return state != undefined && state[i][j] != undefined && state[i][j] == 2;
  }

  arrow(i: number, j: number) {
    var state = this.socketService.state;
    return state != undefined && state[i][j] != undefined && state[i][j] == 3;
  }

  tileColor(i: number, j: number): string {
    var color;
    if (this.arrow(i, j)) {
      return 'aqua';
    } else {
      return (i + j) % 2 == 0 ? 'olive' : 'lightgreen';
    }
  }
}

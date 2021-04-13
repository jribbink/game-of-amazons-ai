import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject, interval, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'amazons-board',
  templateUrl: './amazons-board.component.html',
  styleUrls: ['./amazons-board.component.sass'],
})
export class AmazonsBoardComponent implements OnInit {
  @Input()
  state: number[][];

  friendTime: string = '0:00';
  enemyTime: string = '0:00';

  constructor(public socketService: WebsocketService) {
    setInterval(() => {
      this.friendTime = this.getTime(true);
      this.enemyTime = this.getTime(false);
    }, 1000);
  }

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

  getTime(friendly: boolean) {
    var diff = new Date().getTime() - this.socketService.lastStateUpdate;
    if (diff > 30000) {
      return '0:00';
    }

    if (friendly && this.socketService.ourTurn) {
      return this.millisToMinutesAndSeconds(30000 - diff);
    } else if (!friendly && !this.socketService.ourTurn) {
      return this.millisToMinutesAndSeconds(30000 - diff);
    } else {
      return '0:00';
    }
  }

  millisToMinutesAndSeconds(millis) {
    var minutes = Math.floor(millis / 60000);
    var seconds = Math.floor((millis % 60000) / 1000);
    return minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
  }
}

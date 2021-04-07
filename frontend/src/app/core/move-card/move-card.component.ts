import { Component, Input, OnInit } from '@angular/core';
import { MCTSUpdate } from 'okgnai-shared/models/gameplay/MCTSUpdate';

@Component({
  selector: 'move-card',
  templateUrl: './move-card.component.html',
  styleUrls: ['./move-card.component.sass'],
})
export class MoveCardComponent implements OnInit {
  @Input()
  move: MCTSUpdate;

  @Input()
  parentWinFactor: number;

  constructor() {}

  ngOnInit(): void {}

  getGainScore() {
    return (
      Math.round(
        (this.move.wins / this.move.visits - this.parentWinFactor) * 10000
      ) / 100
    );
  }

  getMoveString() {
    return (
      '(' +
      (this.move.move.prev_row + 1) +
      ', ' +
      (this.move.move.prev_col + 1) +
      ') to (' +
      (this.move.move.row + 1) +
      ', ' +
      (this.move.move.col + 1) +
      ')  Arrow (' +
      (this.move.arrow.row + 1) +
      ', ' +
      (this.move.arrow.col + 1) +
      ')'
    );
  }
}

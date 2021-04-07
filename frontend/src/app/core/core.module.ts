import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AmazonsBoardComponent } from './amazons-board/amazons-board.component';
import { LogsComponent } from './logs/logs.component';
import { AnalysisComponent } from './analysis/analysis.component';
import { RoomsListComponent } from './rooms-list/rooms-list.component';
import { BotnetComponent } from './botnet/botnet.component';
import { WebsocketService } from '../services/websocket.service';
import { MoveCardComponent } from './move-card/move-card.component';

@NgModule({
  declarations: [
    AmazonsBoardComponent,
    LogsComponent,
    AnalysisComponent,
    RoomsListComponent,
    BotnetComponent,
    MoveCardComponent,
  ],
  exports: [
    AmazonsBoardComponent,
    LogsComponent,
    AnalysisComponent,
    RoomsListComponent,
    BotnetComponent,
  ],
  imports: [CommonModule],
})
export class CoreModule {}

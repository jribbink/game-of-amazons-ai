import { Arrow } from "./Arrow";
import { Queen } from "./Queen";

export class MCTSUpdate {
  move?: Queen;
  arrow?: Arrow;
  visits?: number;
  wins?: number;
}

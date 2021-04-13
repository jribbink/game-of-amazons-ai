package ubc.cosc322.search.heuristics;

import ubc.cosc322.board.GameState;

public abstract class Heuristic {
  public abstract double calc(GameState state);
}

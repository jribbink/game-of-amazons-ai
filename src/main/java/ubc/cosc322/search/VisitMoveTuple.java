package ubc.cosc322.search;

import java.io.Serializable;

public class VisitMoveTuple implements Serializable {
	int numWins = 0;
	int numVisit = 0;

	public VisitMoveTuple(int numWins, int numVisit)
	{
		this.numVisit = numVisit;
		this.numWins = numWins;
	}
}
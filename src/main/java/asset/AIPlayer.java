package asset;

import ai.IAI;
import program.Game;

public class AIPlayer extends Player {

	private IAI ai;
	
	public AIPlayer(Game game, Node startPosition, PlayerColor color, String name, IAI ai)
	{
		super(game, startPosition, color, name);
		this.isAi = true;
		this.ai = ai;
	}

	@Override
	public Step makeStep()
	{
		super.makeStep();
		Step step = ai.makeStep(game, this);
		executeStep(step);
		return step;
	}
	
}

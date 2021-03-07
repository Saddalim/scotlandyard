package asset;

import gui.HumanStepDialog;
import program.Game;

public class HumanPlayer extends Player {

	public HumanPlayer(Game game, Node startPosition, PlayerColor color, String name) {
		super(game, startPosition, color, name);
	}

	@Override
	public Step makeStep()
	{
		super.makeStep();
		Step step = new HumanStepDialog(this).ask();
		executeStep(step);
		return step;
	}

}

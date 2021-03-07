package ai;

import asset.Player;
import asset.Step;
import program.Game;

public interface IAI {

	public String getName();
	public Step makeStep(Game game, Player player);
	
}

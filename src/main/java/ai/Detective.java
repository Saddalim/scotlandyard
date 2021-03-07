package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import asset.LinkType;
import asset.MrXHistory;
import asset.Node;
import asset.Player;
import asset.Step;
import program.Game;

public class Detective implements IAI {

	private List<Route> plannedRoutes = null;
	
	@Override
	public String getName() {
		return "Lord Saddalim's generic detective AI";
	}
	
	private void log(Player player, String text)
	{
		System.out.println("DT IO " + player.getName() + ": " + text);
	}

	@Override
	public Step makeStep(Game game, Player player) {
		
		// TODO make detectives cooperate
		
		// Remove planned routes that are not followable
		
		MrXHistory mrXHistory = game.getMrXHistory();
		
		if (mrXHistory.positionHistoryLength() == 0)
		{
			// Phase where we don't know yet where Mr. X is. Try to get to a nearby traffic junction via taxi
			// DFS to a depth of 3, the maximum length we don't know about Mr.X's whereabouts
			
			
			if (plannedRoutes == null || plannedRoutes.size() < 1)
			{
				// TODO find junction at exact distance
				// TODO find junction with biggest score at exact distance
				int turnsToReveal = Game.mrXPublishRounds[0] - game.getRoundNum();
				Set<Route> routesToJunctions = AiUtils.findTrafficJunctions(player.getPosition(), turnsToReveal, true, player, true);
				plannedRoutes = AiUtils.sortRoutesTargetJunctionDescending(routesToJunctions);
				
				if (plannedRoutes == null || plannedRoutes.size() < 1)
				{
					// No traffic junction within reach, do a random step then try again
					log(player, "No traffic junction in reach, roaming...");
					return AiUtils.getRandomStep(game, player.getPosition(), true);
				}
				else
				{
					log(player, "Targeting traffic junction " + plannedRoutes.get(0).getTarget() + " at " + plannedRoutes.size() + " hops");
				}
			}

			
		}
		else
		{
			// We know Mr. X's position during at least 1 turn
			
			// Calculate to where he could get with the given circumstances
			Node lastKnownPosition = mrXHistory.positions.get(mrXHistory.positions.size() - 1);
			int lastKnownRoundNum = mrXHistory.roundNums.get(mrXHistory.roundNums.size() - 1);
			int turnsSinceThen = game.getRoundNum() - lastKnownRoundNum;
			if (turnsSinceThen != (mrXHistory.steps.size() - 1 - (lastKnownRoundNum - 1)))
			{
				log(player, "Number of turns elapsed does not match! " + turnsSinceThen + " / " + (mrXHistory.steps.size() - 1 - (lastKnownRoundNum - 1)));
			}
			List<LinkType> stepsSinceThen = mrXHistory.steps.subList(lastKnownRoundNum - 1, mrXHistory.steps.size());

			Set<Node> possiblePositions = AiUtils.getNodesReachableInTime(lastKnownPosition, turnsSinceThen, stepsSinceThen);
			log(player, "Seeking Mr. X, " + possiblePositions.size() + " possible targets found");
			
			if (possiblePositions.contains(player.getPosition()))
			{
				// If we're in the group, calculate Mr. X's shortest route to the farthest node from the developers, and try to intercept
				// TODO - stick with random for now
				log(player, "Inside target area");
			}
			else
			{
				// If we're not in the group, move towards it
				log(player, "Going towards target group");
				plannedRoutes.add(0, AiUtils.findCheapestPathToGroup(game.getBoard(), player.getPosition(), possiblePositions, player));
			}
		}
		
		if (plannedRoutes != null && plannedRoutes.size() > 0)
		{
			// Get the most convenient from the feasible planned routes (plannedRoutes needs to be ordered)
			Route routeToTake = null;
			for (Route route : plannedRoutes)
			{
				if (route.size() > 0 && player.isLinkFollowable(route.get(0)))
				{
					routeToTake = route;
					break;
				}
			}
			
			if (routeToTake != null)
			{
				// Delete other planned routes as they will become obsolete next turn, faster to delete here
				plannedRoutes.retainAll(new ArrayList<>(Arrays.asList(routeToTake)));
				log(player, "Following planned route: " + routeToTake);
				return new Step(routeToTake.removeFirst());
			}
			else
			{
				// None of the planned routes were feasible, clear them all and go random
				plannedRoutes.clear();
				log(player, "None of the planned routes are followable, roaming...");
				return AiUtils.getRandomStep(game, player.getPosition(), true);
			}
		}
		else
		{
			log(player, "No planned route at this point, this should not have happened.");
			return AiUtils.getRandomStep(game, player.getPosition(), true);
		}
	}
}

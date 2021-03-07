package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import asset.Board;
import asset.Link;
import asset.LinkType;
import asset.Node;
import asset.Player;
import asset.Step;
import program.Game;

public class AiUtils {

	/**
	 * Junction score above (<) a junction is considered a major traffic junction
	 */
	private static final int junctionTreshold = 6;
	
	/**
	 * Gets a random step from the given node.
	 * @param node
	 * @param preferTaxi selects a taxi link if true, and if available
	 * @return
	 */
	public static Step getRandomStep(Game game, Node node, boolean preferTaxi)
	{
		Set<Link> links;
		
		if (preferTaxi)
		{
			links = new HashSet<>();
			
			for (Link link : node.getLinks())
			{
				if (link.getType() == LinkType.Taxi) links.add(link);
			}
			
			if (links.size() == 0) links = node.getLinks();
		}
		else
		{
			links = node.getLinks();
		}
		
		Set<Link> filteredLinks = new HashSet<>();
		// Filter occupied nodes
		for (Link link : links)
		{
			if (game.isNodeFree(link.getNode2())) filteredLinks.add(link);
		}
		
		if (filteredLinks.size() < 1)
		{
			if (preferTaxi)
			{
				return getRandomStep(game, node, false);
			}
			else
			{
				System.out.println("No possible steps");
				return null;
			}
		}
		
		int idx = new Random().nextInt(filteredLinks.size() - 1);
		int i = 0;
		for (Link link : filteredLinks)
		{
			if (i == idx) return new Step(link);
			i++;
		}
		
		System.out.println("Invalid random link ID");
		return null;
	}
	
	/**
	 * Gives a set of nodes that are reachable by any means of transportation from the given node after the exact number of turns
	 * @param start Start node
	 * @param turns 
	 * @return
	 */
	public static Set<Node> getNodesReachableInTime(Node start, int turns)
	{
		Set<Node> nodes = new HashSet<>();
		
		if (turns == 0)
		{
			nodes.add(start);
		}
		else
		{
			for (Link link : start.getLinks())
			{
				nodes.addAll(getNodesReachableInTime(link.getNode2(), turns - 1));
			}
		}
		
		return nodes;
	}
	
	/**
	 * Gives a set of nodes that are reachable by the specified ways of transportation from the given node after the exact number of turns
	 * @param start
	 * @param turns
	 * @param steps
	 * @return
	 */
	public static Set<Node> getNodesReachableInTime(Node start, int turns, List<LinkType> steps)
	{
		Set<Node> nodes = new HashSet<>();
		
		if (turns == 0)
		{
			nodes.add(start);
		}
		else
		{
			LinkType stepType = steps.get(0);
			
			for (Link link : start.getLinks())
			{
				if (stepType == null /* joker */ || stepType == link.getType())
				{
					if (steps.size() > 1)
					{
						nodes.addAll(getNodesReachableInTime(link.getNode2(), turns - 1, steps.subList(1, steps.size())));
					}
					else
					{
						nodes.add(link.getNode2());
					}
				}
			}
		}
		
		return nodes;
	}
	
	/**
	 * Calculates the cost of a link given the link type and player's tickets
	 * @param link
	 * @return
	 */
	public static double getLinkCost(Link link, Player player)
	{
		double cost = Integer.MAX_VALUE;
		
		if (player.getTicketCnt(link.getType()) > 0)
		{
			switch (link.getType())
			{
			case Taxi: 
				cost = 1.0; 
				break;
			case Bus: 
				cost = 2.0; 
				break;
			case Metro:
				cost = 3.5;
				break;
			case Boat:
				System.out.println("Nobody should have boat tickets, wtf?! " + player.getName() + " has " + player.getTicketCnt(LinkType.Boat));
			}
		}
		else if (player.getJokerTicketCnt() > 0)
		{
			switch (link.getType())
			{
			case Taxi: 
				cost =  3.0; 
				break;
			case Bus: 
				cost =  5.5; 
				break;
			case Metro: 
				cost =  8.0; 
			break;
			case Boat: 
				cost =  3.5; 
				break;
			}
		}
		
		return cost;
	}
	
	/**
	 * Selects the cheapest Route from the set of routes given
	 * @param routes Routes to choose the cheapest from
	 * @return The cheapest route
	 */
	public static Route selectCheapestRoute(Set<Route> routes)
	{
		Route cheapestRoute = null;
		double cheapestPrice = Double.POSITIVE_INFINITY;
		
		for (Route route : routes)
		{
			if (route.getPrice() < cheapestPrice)
			{
				cheapestRoute = route;
				cheapestPrice = route.getPrice();
			}
		}
		
		return cheapestRoute;
	}
	
	/**
	 * Selects the Route to the biggest traffic junction among the endpoints of the given Routes
	 * @param routes Routes to choose from
	 * @return The Route to the biggest traffic junction
	 */
	public static Route selectRouteToBiggestJunction(Set<Route> routes)
	{
		Route bestRoute = null;
		double biggestJunction = 0.0;
		
		for (Route route : routes)
		{
			Node target = route.getTarget();
			double junctionPoints = getNodeQuality(target);
			if (junctionPoints > biggestJunction)
			{
				bestRoute = route;
				biggestJunction = junctionPoints;
			}
		}
		
		return bestRoute;
	}
	
	/**
	 * Sorts the given set of routes based on their target's junction scores, descending
	 * @param routes
	 * @return
	 */
	public static List<Route> sortRoutesTargetJunctionDescending(Set<Route> routes)
	{
		List<Route> sortedList = new ArrayList<>(routes);
		Collections.sort(sortedList, new Comparator<Route>() {

			@Override
			public int compare(Route o1, Route o2) {
				return Integer.compare(getNodeQuality(o2.getTarget()), getNodeQuality(o1.getTarget()));
			}
		});
		return sortedList;
	}
	
	/**
	 * Calculates the given node's link quality
	 * @param node
	 * @return
	 */
	public static int getNodeQuality(Node node)
	{
		int quality = 0;
		for (Link link : node.getLinks())
		{
			switch (link.getType())
			{
			case Boat:
				quality += 1;
				break;
			case Bus:
				quality += 2;
				break;
			case Metro:
				quality += 3;
				break;
			case Taxi:
				quality += 1;
				break;
			}
		}
		return quality;
	}
	
	/**
	 * Performs a DFS from the given node and returns Routes to all traffic junctions found
	 * @param from Node to start the search from
	 * @param depth Maximum or exact depth to search
	 * @param taxiOnly Use only taxi links if true
	 * @param player The player whose ticket counts will be taken into account
	 * @param exactDistance If true, only junctions reachable by exactly the given depth will be considered
	 * @return Route to a traffic junction, null if not found
	 */
	public static Set<Route> findTrafficJunctions(Node from, int depth, boolean taxiOnly, Player player, boolean exactDistance)
	{
		Set<Route> routes = new HashSet<>();
		
		// Any-depth junction search
		if (! exactDistance && getNodeQuality(from) > junctionTreshold)
		{
			routes.add(Route.reachedDestination);
		}
		// Exact-depth junction search
		if (exactDistance && depth == 0 && getNodeQuality(from) > junctionTreshold)
		{
			routes.add(Route.reachedDestination);
		}
		
		// If we have depth left, deepen tree
		if (depth != 0)
		{
			for (Link link : from.getLinks())
			{
				if (taxiOnly && link.getType() != LinkType.Taxi) continue;
				
				Set<Route> deeperRoutes = findTrafficJunctions(link.getNode2(), depth - 1, taxiOnly, player, exactDistance);
				
				for (Route deeperRoute : deeperRoutes)
				{
					if (deeperRoute != null && deeperRoute.hasReachedDestination())
					{
						routes.add(deeperRoute.appendLinkToFront(link, player));
					}
				}
				
			}
		}

		return routes;
	}
	
	/**
	 * Returns the cheapest link between two nodes given the player's circumstances (ticket count)
	 * @param node1 Link from
	 * @param node2 Link to
	 * @param player
	 * @return
	 */
	public static Link getCheapestLinkBetween(Node node1, Node node2, Player player)
	{
		Link cheapestLink = null;
		double cheapestPrice = Double.POSITIVE_INFINITY;
		
		for (Link link : node1.getLinks())
		{
			if (link.getNode2().equals(node2))
			{
				double newPrice = getLinkCost(link, player);
				if (newPrice < cheapestPrice)
				{
					cheapestLink = link;
					cheapestPrice = newPrice;
				}
			}
		}
		
		return cheapestLink;
	}
	
	/**
	 * Builds the shortest route between the two given nodes given the whole graph's distance from the source and predecessor list.
	 * Uses data built with Bellman-Ford algorithm 
	 * @param node1 Source node
	 * @param node2 Destination node
	 * @param distances Distance collection as built by the Bellman-Ford algorithm
	 * @param predecessors Predecessor collection as built by the Bellman-Ford algorithm
	 * @param player
	 * @return
	 */
	private static Route getShortestRouteBetween(Node node1, Node node2, Map<Node, Double> distances, Map<Node, Node> predecessors, Player player)
	{
		if (node1 == null || node2 == null) return null;
		if (node1.equals(node2)) return Route.reachedDestination;
		
		Node predecessor = predecessors.get(node2);
		return getShortestRouteBetween(node1, predecessor, distances, predecessors, player).appendLinkToEnd(getCheapestLinkBetween(predecessor, node2, player), player);
	}
	
	/**
	 * Run Bellman-Ford algorithm from the given node to all others given the player to calculate edge prices 
	 * @param board Board with all the nodes
	 * @param from Source node that must be included in the board
	 * @param player Player instance with set ticket counts to calculate edge weights
	 * @return
	 */
	public static Map<Node, Route> runBellmanFordFrom(Board board, Node from, Player player)
	{
		System.out.println("Starting Bellman-Ford from node " + from.getId() + "...");
		long timeOfStart = System.currentTimeMillis();
		
		// Init
		Map<Node, Double> distances = new HashMap<>();
		Map<Node, Node> predecessors = new HashMap<>();
		for (Node node : board.getNodes().values())
		{
			distances.put(node, Double.POSITIVE_INFINITY);
			predecessors.put(node, null);
		}
		
		distances.put(from, 0.0);
		
		// Iteration
		for (int i = 0; i < board.getNodes().size(); i++)
		{
			for (Node edgeFrom : board.getNodes().values())
			{
				for (Link link : edgeFrom.getLinks())
				{
					double newDistance = distances.get(edgeFrom) + getLinkCost(link, player);
					if (newDistance < distances.get(link.getNode2()))
					{
						distances.put(link.getNode2(), newDistance);
						predecessors.put(link.getNode2(), edgeFrom);
					}
				}
			}
		}
		
		// Build Routes
		Map<Node, Route> routes = new HashMap<>();
		
		for (Node node : board.getNodes().values())
		{
			routes.put(node, getShortestRouteBetween(from, node, distances, predecessors, player));
		}
		
		long timeElapsed = System.currentTimeMillis() - timeOfStart;
		System.out.println("Bellman-Ford finished from node " + from.getId() + ". Time elapsed: " + timeElapsed + " ms");
		
		return routes;
	}
	
	/**
	 * Finds the cheapest route to any of the given points
	 * @param from The node from where the search should start
	 * @param group The group of nodes where the search can end
	 * @param player The player whose ticket counts should be taken into account
	 * @return
	 */
	public static Route findCheapestPathToGroup(Board board, Node from, Set<Node> group, Player player)
	{
		// Use Bellman-Ford to calculate weights to all group members
		// TODO use modified Bellman-Ford to skip inter-group edges thus speeding it up
		
		// Since the graph is continuous, the only scenario when there's no route to the goal is when the goal group is empty
		if (group.size() == 0) return null;
		
		Map<Node, Route> routes = runBellmanFordFrom(board, from, player);
		
		Route cheapestRoute = null;
		double cheapestPrice = Double.POSITIVE_INFINITY;
		
		for (Node node : group)
		{
			Route route = routes.get(node);
			if (route != null)
			{
				double routePrice = route.getPrice();
				if (routePrice < cheapestPrice)
				{
					cheapestRoute = route;
					cheapestPrice = routePrice;
				}
			}
		}
		
		return cheapestRoute;
	}
	
}

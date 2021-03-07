package ai;

import java.util.LinkedList;
import asset.Link;
import asset.Player;

// TODO hide List interface to avoid inconsistent prices
public class Route extends LinkedList<Link>
{
	private static final long serialVersionUID = 965463853511938380L;
	
	private boolean found = false;
	private double price = 0.0;
	
	public Route() {}
	
	private Route(boolean found)
	{
		this.found = found;
	}
	
	private Route(Route route)
	{
		super(route);
	}
	
	public static final Route reachedDestination = new Route(true);
	
	public void setReachedDestination()
	{
		found = true;
	}
	
	public boolean hasReachedDestination()
	{
		return found;
	}
	
	public boolean equals(Route o)
	{
		return super.equals(o) && this.found == o.found && this.price == o.price;
	}
	
	public Route appendTo(Route o)
	{
		Route retVal = new Route();
		retVal.addAll(this);
		retVal.addAll(o);
		if (o.hasReachedDestination()) retVal.setReachedDestination();
		retVal.price = this.price + o.price;
		return retVal;
	}
	
	public Route appendLinkToFront(Link link, Player player)
	{
		Route newRoute = new Route(this);
		newRoute.addFirst(link);
		newRoute.price = this.price + AiUtils.getLinkCost(link, player);
		newRoute.found = this.found;
		return newRoute;
	}
	
	public Route appendLinkToEnd(Link link, Player player)
	{
		Route newRoute = new Route(this);
		newRoute.addLast(link);
		newRoute.price = this.price + AiUtils.getLinkCost(link, player);
		newRoute.found = this.found;
		return newRoute;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public asset.Node getTarget()
	{
		return this.get(this.size() - 1).getNode2();
	}
}


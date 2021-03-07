package asset;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Node implements Serializable {

	private static final long serialVersionUID = 4202888411059275527L;
	
	private int id;
	private Set<Link> links;
	
	private double x = 0.0;
	private double y = 0.0;
	
	public Node(int id)
	{
		this.id = id;
		links = new HashSet<>();
	}
	
	public void addLink(Link link)
	{
		links.add(link);
	}
	
	public void addLinkTo(Node node2, LinkType type)
	{
		links.add(new Link(this, node2, type));
	}
	
	public int getId()
	{
		return id;
	}
	
	public Set<Link> getLinks()
	{
		return links;
	}
	
	public int getLinkCnt()
	{
		return links.size();
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean canStepTo(Node to, LinkType via)
	{
		for (Link link : links)
		{
			if (to.equals(link.getNode2()) && via.equals(link.getType())) return true;
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return new Integer(id).toString();
	}
}

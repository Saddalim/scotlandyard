package asset;

import java.io.Serializable;

public class Link implements Serializable {

	private static final long serialVersionUID = -6393563901252777658L;
	
	private LinkType linkType;
	private Node node1, node2;
	
	public Link(Node node1, Node node2, LinkType type)
	{
		this.node1 = node1;
		this.node2 = node2;
		this.linkType = type;
	}
	
	public LinkType getType()
	{
		return linkType;
	}
	
	public boolean equals(Link o)
	{
		return this.node1.equals(o.node1) && this.node2.equals(o.node2) && this.linkType.equals(o.linkType);
	}
	
	public Node getNode1()
	{
		return node1;
	}
	
	public Node getNode2()
	{
		return node2;
	}
	
	@Override
	public String toString()
	{
		return node1.toString() + "-" + node2.toString() + " (" + linkType.toString() + ")";
	}
}

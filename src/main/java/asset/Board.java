package asset;

import java.util.Map;

public class Board {

	Map<Integer, Node> nodes;
	
	public Board(Map<Integer, Node> nodes)
	{
		this.nodes = nodes;
	}
	
	public Map<Integer, Node> getNodes()
	{
		return nodes;
	}
}

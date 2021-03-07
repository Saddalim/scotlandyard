package asset;

import java.util.ArrayList;
import java.util.List;

public class MrXHistory {

	public List<LinkType> steps = new ArrayList<>();
	public List<Node> positions = new ArrayList<>();
	public List<Integer> roundNums = new ArrayList<>();
	
	public int size()
	{
		return steps.size();
	}
	
	public int positionHistoryLength()
	{
		return positions.size();
	}
	
}

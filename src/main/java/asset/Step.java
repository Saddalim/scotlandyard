package asset;

import java.util.ArrayList;
import java.util.List;

// TODO make this extend from ArrayList<AtomicStep>
public class Step {

	public List<AtomicStep> steps;
	
	public Step()
	{
		steps = new ArrayList<>();
	}
	
	public Step(Link link)
	{
		this();
		steps.add(new AtomicStep(link, false));
	}
	
	public int getStepCnt()
	{
		return steps.size();
	}
	
}

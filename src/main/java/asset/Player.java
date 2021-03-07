package asset;

import java.util.HashMap;
import java.util.Map;

import program.Game;

public abstract class Player {

	protected Game game;
	protected Node position;
	private PlayerColor color;
	private String name;
	protected boolean isAi = false;
	private Map<LinkType, Integer> tickets;
	private int jokerTicketCnt = 0;
	private int doubleTicketCnt = 0;
	protected boolean justMadeADoubleStep = false;
	
	public Player(Game game, Node startPosition, PlayerColor color, String name)
	{
		this.game = game;
		position = startPosition;
		this.color = color;
		this.name = name;
		tickets = new HashMap<>();
		
		for (LinkType type : LinkType.values()) tickets.put(type, 0);
		
		if (color == PlayerColor.MrX)
		{
			tickets.put(LinkType.Taxi, 4);
			tickets.put(LinkType.Bus, 3);
			tickets.put(LinkType.Metro, 3);
			doubleTicketCnt = 2;
		}
		else
		{
			tickets.put(LinkType.Taxi, 10);
			tickets.put(LinkType.Bus, 8);
			tickets.put(LinkType.Metro, 4);
			jokerTicketCnt = 0;
			doubleTicketCnt = 0;
		}
	}
	
	@Override
	public String toString()
	{
		return name + " (" + color + ")" + (isAi ? " [AI]" : "");
	}
	
	public Node getPosition()
	{
		return position;
	}
	
	public PlayerColor getColor()
	{
		return color;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isAi()
	{
		return isAi;
	}
	
	public void giveTicket(LinkType type)
	{
		increaseTicketCnt(type);
	}
	
	public void setTicketCnt(LinkType type, int cnt)
	{
		tickets.put(type, cnt);
	}
	
	public void setJokerTicketCnt(int cnt)
	{
		jokerTicketCnt = cnt;
	}
	
	public int getTicketCnt(LinkType type)
	{
		return tickets.get(type).intValue();
	}
	
	public int getJokerTicketCnt()
	{
		return jokerTicketCnt;
	}
	
	public int getDoubleTicketCnt()
	{
		return doubleTicketCnt;
	}
	
	protected boolean stepVia(Link link)
	{
		return stepVia(new AtomicStep(link, false));
	}
	
	@Deprecated
	protected boolean stepVia(Link link, boolean useJoker)
	{
		return stepVia(new AtomicStep(link, useJoker));
	}
	
	protected boolean stepVia(AtomicStep step)
	{
		if (position.getLinks().contains(step.link))
		{
			if (step.joker)
				jokerTicketCnt--;
			else
				decreaseTicketCnt(step.link.getType());
			
			position = step.link.getNode2();
			return true;
		}
		
		return false;
	}
	
	protected boolean doubleStepVia(Link link1, Link link2)
	{
		return doubleStepVia(new AtomicStep(link1, false), new AtomicStep(link2, false));
	}
	
	@Deprecated
	protected boolean doubleStepVia(Link link1, boolean joker1, Link link2, boolean joker2)
	{
		doubleTicketCnt--;
		// TODO This does not check if step2 is feasible before executing step1, therefore can stuck at the end of step 1
		return stepVia(link1, joker1) && stepVia(link2, joker2);
	}
	
	protected boolean doubleStepVia(AtomicStep step1, AtomicStep step2)
	{
		doubleTicketCnt--;
		// TODO This does not check if step2 is feasible before executing step1, therefore can stuck at the end of step 1
		return stepVia(step1) && stepVia(step2);
	}
	
	protected void increaseTicketCnt(LinkType type)
	{
		tickets.put(type, new Integer(tickets.get(type).intValue() + 1));
	}
	
	protected void decreaseTicketCnt(LinkType type)
	{
		tickets.put(type, new Integer(tickets.get(type).intValue() - 1));
		
		if (color != PlayerColor.MrX) game.giveTicketToMrX(type);
	}
	
	public boolean justMadeADoubleStep()
	{
		return justMadeADoubleStep;
	}
	
	protected boolean executeStep(Step step)
	{
		if (step != null)
		{
			if (step.steps.size() == 1)
			{
				stepVia(step.steps.get(0));
				return true;
			}
			else if (step.steps.size() == 2)
			{
				doubleStepVia(step.steps.get(0), step.steps.get(1));
				justMadeADoubleStep = true;
				return true;
			}
			else
			{
				System.out.println("More than 3 steps :(");
				return false;
			}
		}
		else
		{
			for (Link link : getPosition().getLinks())
			{
				if (getTicketCnt(link.getType()) > 0 || getJokerTicketCnt() > 0)
				{
					// We had somewhere to step, but dialog returned null. This sucks.
					System.out.println("Player had somewhere to step but stepped nowhere :(");
					return false;
				}
			}
			
			// We had nowhere to step, move along
			return false;
		}
	}
	
	public Step makeStep()
	{
		justMadeADoubleStep = false;
		return null;
	}
	
	public boolean isLinkFollowable(Link link)
	{
		return isStepPossible(new Step(link));
	}
	
	public boolean isStepPossible(Step step)
	{
		boolean possible = true;
		switch (step.steps.size())
		{
		case 0: return true;
		case 2:
			possible &= getPosition().getLinks().contains(step.steps.get(1).link) && game.isNodeFree(step.steps.get(1).link.getNode2());
			// continue
		case 1:
			possible &= getPosition().getLinks().contains(step.steps.get(0).link) && game.isNodeFree(step.steps.get(0).link.getNode2());
			break;
		
		default: return false;
		}
		
		return possible;
	}
	
	public boolean isMrX()
	{
		return color == PlayerColor.MrX;
	}
}

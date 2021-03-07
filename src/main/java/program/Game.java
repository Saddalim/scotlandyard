package program;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import ai.Ai;
import ai.Detective;
import ai.MrX;
import asset.AIPlayer;
import asset.AtomicStep;
import asset.Board;
import asset.HumanPlayer;
import asset.LinkType;
import asset.MrXHistory;
import asset.Node;
import asset.Player;
import asset.PlayerColor;
import asset.Step;
import configuration.BoardParser;

public class Game {

	private Set<Player> players;
	private Player mrX = null;
	private Board board;
	private int roundNum = 0;
	private boolean mrXBusted = false;
	
	private MrXHistory mrXHistory = new MrXHistory();
	
	private Collection<GameStateChangedListener> gameStateChangedListeners = new LinkedList<>();
	
	public static final int[] startingPositions = {13, 26, 29, 34, 50, 53, 91, 94, 103, 112, 117, 132, 138, 141, 155, 174, 197, 198};
	public static final int[] mrXPublishRounds = {3, 8, 13, 18, 24};
	public static final int lastTurnNum = 24;
	
	private static final String[] randomDetectiveAiNames = {"Detektív Dezsõ", "Nyomozó Nyék", "Invesztigátor István", "Jard Jenõ", "Keresgélõ Kálmán", "Vizsgáló Vilmos"};
	private static final String[] randomMrXAINames = {"A Mester", "Gru", "Az Iksz", "James Bond", "A Hegylakó", "Pandacsöki Boborján", "Günther Schreiber"};
	
	public Game()
	{
		System.out.println("Parsing board...");
		board = BoardParser.parse();
		
		if (board == null)
		{
			System.out.println("Invalid board, aborting...");
			return;
		}
		
		System.out.println("Parsing done successfully");
		
		players = new HashSet<>();
	}
	
	public Board getBoard()
	{
		return board;
	}
	
	public Set<Player> getPlayers()
	{
		return players;
	}
	
	public boolean addPlayer(String name, PlayerColor color, Ai ai)
	{
		return addPlayer(name, color, ai, getRandomStartPositionId());
	}
	
	public boolean addPlayer(String name, PlayerColor color, Ai ai, int startPositionId)
	{
		for (Player player : players)
			if (player.getPosition().getId() == startPositionId) return false;
		
		if (ai == Ai.Human)
			return addHumanPlayer(name, color, startPositionId);
		else
			return addAIPlayer(name, color, startPositionId);
	}
	
	public boolean addRandomDetectiveAi()
	{
		if (isRunning()) return false;
		
		Random generator = new Random();
		
		// TODO this would be much more elegant by subtracting occupied values, but whatever...
		
		// Get color
		PlayerColor color = null;
		for (int i = 0; i < 100; i++)
		{
			color = PlayerColor.values()[generator.nextInt(PlayerColor.values().length - 1)];
			for (Player player : players)
			{
				if (player.getColor() == color)
				{
					color = null;
					break;
				}
			}
			
			if (color != null) break;
		}
		
		if (color == null) return false;
		
		// Get name
		String name = null;
		for (int i = 0; i < 100; i++)
		{
			name = randomDetectiveAiNames[generator.nextInt(randomDetectiveAiNames.length - 1)];
			for (Player player : players)
			{
				if (player.getName() == name)
				{
					name = null;
					break;
				}
			}
			
			if (name != null) break;
		}
		
		if (name == null) return false;
		
		return addAIPlayer(name, color);
		
	}
	
	public boolean addHumanPlayer(String name, PlayerColor color)
	{
		return addHumanPlayer(name, color, getRandomStartPositionId());
	}
	
	public boolean addHumanPlayer(String name, PlayerColor color, int startPositionId)
	{
		if (isRunning()) return false;
		if (! board.getNodes().containsKey(startPositionId)) return false;
		
		for (Player player : players)
		{
			if (player.getColor().equals(color)) return false;
		}
		
		players.add(new HumanPlayer(this, board.getNodes().get(startPositionId), color, name));
		return true;
	}
	
	public boolean addAIPlayer(String name, PlayerColor color)
	{
		return addAIPlayer(name, color, getRandomStartPositionId());
	}
	
	public boolean addAIPlayer(String name, PlayerColor color, int startPositionId)
	{
		if (isRunning()) return false;
		if (! board.getNodes().containsKey(startPositionId)) return false;
				
		for (Player player : players)
		{
			if (player.getColor().equals(color)) return false;
		}
		
		players.add(new AIPlayer(this, board.getNodes().get(startPositionId), color, name, color == PlayerColor.MrX ? new MrX() : new Detective()));
		return true;
	}
	
	public boolean removePlayer(Player player)
	{
		if (isRunning()) return false;
		
		for (Player regPlayer : players)
		{
			if (regPlayer.equals(player))
			{
				players.remove(regPlayer);
				return true;
			}
		}
		
		return false;
	}
	
	private int getRandomStartPositionId()
	{
		Node candidate = null;
		
		boolean foundSuitablePosition = false;
		Random generator = new Random();
		do
		{
			candidate = board.getNodes().get(startingPositions[generator.nextInt(startingPositions.length - 1)]);
			
			foundSuitablePosition = true;
			
			for (Player player : players)
			{
				if (player.getPosition().equals(candidate))
				{
					foundSuitablePosition = false;
					break;
				}
			}
		}
		while (! foundSuitablePosition);

		return candidate.getId();
	}
	
	public void start()
	{
		if (isRunning()) return;
		
		int detectiveCnt = 0;
		
		for (Player player : players)
		{
			if (player.getColor() == PlayerColor.MrX)
				mrX = player;
			else
				++detectiveCnt;
		}
		
		if (mrX != null && detectiveCnt > 0)
		{
			mrX.setJokerTicketCnt(detectiveCnt);
			roundNum = 1;
		}
	}
	
	public boolean isRunning()
	{
		return roundNum != 0 && roundNum != Integer.MAX_VALUE;
	}
	
	public boolean isFinished()
	{
		return roundNum == Integer.MAX_VALUE;
	}
	
	private void setFinished(boolean mrXBusted)
	{
		this.mrXBusted = mrXBusted;
		roundNum = Integer.MAX_VALUE;
	}
	
	public boolean isMrXBusted()
	{
		return mrXBusted;
	}
	
	public int getRoundNum()
	{
		return roundNum;
	}
	
	public void advance()
	{
		if (!isRunning()) return;
		
		Step mrXStep = mrX.makeStep();

		switch (mrXStep.getStepCnt())
		{
		case 1:
			mrXHistory.steps.add(censorMrXStepType(mrXStep.steps.get(0)));
			break;
		case 2:
			mrXHistory.steps.add(censorMrXStepType(mrXStep.steps.get(0)));
			if (checkCollisionOccured())
			{
				fireGameStateChanged();
				return;
			}
			publishMrXPositionIfNecessary();
			roundNum++;
			mrXHistory.steps.add(censorMrXStepType(mrXStep.steps.get(1)));
			break;
		default:
			System.out.println("Illegal move by Mr. X!");
		}
		
		if (checkCollisionOccured())
		{
			fireGameStateChanged();
			return;
		}
		if (roundNum > lastTurnNum) setFinished(false);
		fireGameStateChanged();
		
		publishMrXPositionIfNecessary();
		
		for (Player player : players)
		{
			if (! player.equals(mrX))
			{
				player.makeStep();
				if (checkCollisionOccured()) break;
				fireGameStateChanged();
			}
		}

		if (!isFinished())
		{
			roundNum++;
			if (roundNum > lastTurnNum) setFinished(false);
		}
		
		fireGameStateChanged(); // TODO this is a bit of an overkill
	}
	
	private LinkType censorMrXStepType(AtomicStep step)
	{
		if (step.joker || step.link.getType() == LinkType.Boat) return null;
		return step.link.getType();
	}
	
	private void publishMrXPositionIfNecessary()
	{
		if (isPublishingRound())
		{
			mrXHistory.positions.add(mrX.getPosition());
			mrXHistory.roundNums.add(roundNum);
		}
	}
	
	public boolean isPublishingRound()
	{
		// Idiotic Java does not give a straightforward way to tell if an array contains an item... shame...
		for (int turn : mrXPublishRounds) if (turn == roundNum) return true;
		return false;
	}
	
	public MrXHistory getMrXHistory()
	{
		return mrXHistory;
	}
	
	public void giveTicketToMrX(LinkType type)
	{
		if (!isRunning()) return;
		
		mrX.giveTicket(type);
	}
	
	public void subscribeForGameStateChanged(GameStateChangedListener listener)
	{
		gameStateChangedListeners.add(listener);
	}
	
	public void unsubscribeFromGameStateChanged(GameStateChangedListener listener)
	{
		gameStateChangedListeners.remove(listener);
	}
	
	private void fireGameStateChanged()
	{
		for (GameStateChangedListener listener : gameStateChangedListeners) listener.onGameStateChanged();
	}
	
	public boolean isNodeFree(Node node)
	{
		for (Player player : players) if (! player.isMrX() && player.getPosition().equals(node)) return false;
		return true;
	}
	
	private boolean checkCollisionOccured()
	{
		// This is a primitive approach, but there will never be so many players to use set functions
		for (Player player1 : players)
		{
			for (Player player2 : players)
			{
				if (! player1.equals(player2) && player1.getPosition().equals(player2.getPosition()))
				{
					if (player1.isMrX() || player2.isMrX())
					{
						System.out.println("Mr. X busted!");
						setFinished(true);
						return true;
					}
					else
					{
						System.out.println("Collision between detectives. This should not have happened.");
						setFinished(false);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
}

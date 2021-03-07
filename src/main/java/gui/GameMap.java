package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

import asset.Node;
import asset.Player;
import asset.PlayerColor;
import configuration.BoardParser;
import program.Game;
import program.GameStateChangedListener;

public class GameMap extends JLabel implements ActionListener, GameStateChangedListener {

	private static final long serialVersionUID = -5285177473046565987L;
	
	private static final boolean drawNodes = false;
	private static final boolean editNodes = false;
	private static final boolean alwaysShowMrX = true;
	
	private static final double nodeRadius = 10.0;
	private static final Color nodeColor = new Color(0, 0, 0, 128);
	private static final Color nodeNumberColor = new Color(255, 255, 255);
	
	private static final double playerOuterRadius = 18.0;
	private static final double playerInnerRadius = 8.0;
	private static final Color playerCenterColor = new Color(50, 50, 50);
	
	private BufferedImage mapImg = null;
	private Image resizedImg = null;
	private double scaleX = 1.0;
	private double scaleY = 1.0;
	
	private Game game = null;
	private Timer recalculateTimer = new Timer(20, this);
	
	JLabel infoLbl;
	JTextField nodeIdLbl;
	
	public GameMap(Game game, JLabel infoLbl, JTextField nodeIdLbl)
	{
		this.game = game;
		this.infoLbl = infoLbl;
		this.nodeIdLbl = nodeIdLbl;
		
		ClassLoader classLoader = BoardParser.class.getClassLoader();
		File fBoardFile = new File(classLoader.getResource("syboard_fade.jpg").getFile());
		try {
		    mapImg = ImageIO.read(fBoardFile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		recalculateTimer.setRepeats(false);
		resizeBoard();
		
		infoLbl.setText(mapImg.getWidth() + " x " + mapImg.getHeight());
		
		if (game != null) game.subscribeForGameStateChanged(this);
		
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent arg0)
			{
				recalculateTimer.restart();
			}
		});
		

		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent arg0)
			{
				infoLbl.setText(arg0.getX() + " - " + arg0.getY());
			}
		});
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (! editNodes) return;
				
				int nodeId = Integer.parseInt(nodeIdLbl.getText());
				Node actNode = game.getBoard().getNodes().get(nodeId);
				
				double imgX = e.getX() / scaleX;
				double imgY = e.getY() / scaleY;
				
				actNode.setX(imgX);
				actNode.setY(imgY);
				
				nodeId++;
				nodeIdLbl.setText(new Integer(nodeId).toString());
				
				repaint();
			}
		});
		
		nodeIdLbl.setText("1");
	}
	
	private void resizeBoard()
	{
		resizedImg = mapImg.getScaledInstance(Math.max(getWidth(), 1), Math.max(getHeight(), 1), Image.SCALE_FAST);
		setIcon(new ImageIcon(resizedImg)); 
		
		scaleX = (double) getWidth() / (double) mapImg.getWidth();
		scaleY = (double) getHeight() / (double) mapImg.getHeight();
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		// Don't draw circles in WindowBuilder
		if (game == null) return;

		if (drawNodes)
		{
			for (Node node : game.getBoard().getNodes().values())
			{
				double markX = node.getX() * scaleX;
				double markY = node.getY() * scaleY;
				Ellipse2D ellipse = new Ellipse2D.Double(markX - nodeRadius, markY - nodeRadius, nodeRadius * 2.0, nodeRadius * 2.0);
				
				g2d.setColor(nodeColor);
				g2d.fill(ellipse);
				
				g2d.setColor(nodeNumberColor);
				
				String nodeIdStr = new Integer(node.getId()).toString();
				Rectangle2D numberRect = g2d.getFontMetrics().getStringBounds(nodeIdStr, g2d);
				g2d.drawString(nodeIdStr, (int) (markX - (numberRect.getWidth() / 2.0)), (int) (markY + (numberRect.getHeight() / 2.0)));
			}
		}
		
		for (Player player : game.getPlayers())
		{
			if (player.getColor() == PlayerColor.MrX && ! game.isPublishingRound() && ! alwaysShowMrX) continue;
			
			double markX = player.getPosition().getX() * scaleX;
			double markY = player.getPosition().getY() * scaleY;
			Ellipse2D outerCircle = new Ellipse2D.Double(markX - playerOuterRadius, 
														 markY - playerOuterRadius, 
														 playerOuterRadius * 2.0, 
														 playerOuterRadius * 2.0);
			Ellipse2D innerCircle = new Ellipse2D.Double(markX - playerInnerRadius, 
														 markY - playerInnerRadius, 
														 playerInnerRadius * 2.0, 
														 playerInnerRadius * 2.0);
			
			g2d.setColor(PlayerColor.getAwtColor(player.getColor()));
			g2d.fill(outerCircle);
			
			g2d.setColor(playerCenterColor);
			g2d.fill(innerCircle);
			
			String nameStr = player.getName();
			if (player.isAi()) nameStr += " [AI]";
			Rectangle2D numberRect = g2d.getFontMetrics().getStringBounds(nameStr, g2d);
			
			g2d.setColor(nodeNumberColor);
			g2d.drawString(nameStr, (int) (markX - (numberRect.getWidth() / 2.0)), (int) (markY + (numberRect.getHeight() / 2.0)));
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		resizeBoard();
	}

	@Override
	public void onGameStateChanged()
	{
		repaint();
	}

}

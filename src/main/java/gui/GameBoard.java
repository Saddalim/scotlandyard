package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import program.Game;
import program.GameStateChangedListener;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.JTextField;

public class GameBoard extends JFrame implements GameStateChangedListener {

	
	private static final long serialVersionUID = -5940443679043268920L;
	private JPanel contentPane;
	private GameMap lblMap;
	private Game game;
	
	private JLabel lblCursorPos;

	/**
	 * Create the frame.
	 */
	public GameBoard(Game game)
	{
		setTitle("Szk\u00E1tl\u00F6nd J\u00E1rd B\u00F3rd Vj\u00FA");
		this.game = game;
		game.subscribeForGameStateChanged(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		JButton btnAdvanceGame = new JButton("K\u00F6r");
		btnAdvanceGame.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				game.advance();
			}
		});
		panel.add(btnAdvanceGame);
		
		JButton btnNewButton_1 = new JButton("Pontok mentése");
		btnNewButton_1.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					FileOutputStream fout = new FileOutputStream("board.fos");
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(game.getBoard().getNodes());
					oos.flush();
					fout.flush();
					oos.close();
					fout.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		panel.add(btnNewButton_1);
		
		lblCursorPos = new JLabel("CUR POS");
		panel.add(lblCursorPos);
		
		JTextField lblNodeId = new JTextField("NODE ID");
		panel.add(lblNodeId);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		lblMap = new GameMap(game, lblCursorPos, lblNodeId);
		
		
		panel_1.add(lblMap, BorderLayout.CENTER);
		
	}

	protected JLabel getLblMap() {
		return lblMap;
	}

	
	protected JLabel getLblCursorPos() {
		return lblCursorPos;
	}

	@Override
	public void onGameStateChanged()
	{
		if (game.isMrXBusted())
		{
			JOptionPane.showMessageDialog(this, "Mr X busted!");
		}
		else if (game.isFinished())
		{
			JOptionPane.showMessageDialog(this, "Mr X survived!");
		}
	}
}

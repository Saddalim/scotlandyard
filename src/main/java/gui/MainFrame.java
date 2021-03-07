package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ai.Ai;
import asset.Player;
import asset.PlayerColor;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import net.miginfocom.swing.MigLayout;
import program.Game;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 2292455670620751734L;
	private JPanel contentPane;
	private JTextField textFieldPlayerName;
	
	private DefaultListModel<Player> playerListModel = new DefaultListModel<>();
	
	private Game game = new Game();
	private JTextPane textPanePlayerStatus;
	private JButton btnStartGame;
	private JButton btnDeleteSelectedPlayer;
	private JButton btnAddPlayer;
	private JButton btnAdvanceGame;
	private JList<Player> listPlayers;
	private JComboBox<String> comboBoxStartPosition;
	private JButton btnRandomGp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Sz\u00E1ktl\u00F6nd J\u00E1rd - D\u00F6 G\u00E9m");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3, BorderLayout.NORTH);
		panel_3.setLayout(new MigLayout("", "[38px][grow]", "[20px][20px][][20px][]"));
		
		JLabel lblNv = new JLabel("N\u00E9v");
		panel_3.add(lblNv, "cell 0 0,grow");
		
		textFieldPlayerName = new JTextField();
		panel_3.add(textFieldPlayerName, "cell 1 0,grow");
		textFieldPlayerName.setColumns(10);
		
		JLabel lblSzn = new JLabel("Sz\u00EDn");
		panel_3.add(lblSzn, "cell 0 1,grow");
		
		JComboBox<PlayerColor> comboBoxPlayerColor = new JComboBox<>();
		panel_3.add(comboBoxPlayerColor, "cell 1 1,grow");
		for (PlayerColor color : PlayerColor.values())
		{
			comboBoxPlayerColor.addItem(color);
		}
		
		JLabel lblKezd = new JLabel("Kezd:");
		panel_3.add(lblKezd, "cell 0 2,grow");
		
		comboBoxStartPosition = new JComboBox<String>();
		panel_3.add(comboBoxStartPosition, "cell 1 2,growx");
		
		comboBoxStartPosition.addItem("Random");
		for (int nodeId : Game.startingPositions)
		{
			comboBoxStartPosition.addItem(new Integer(nodeId).toString());
		}
		
		JLabel lblAi = new JLabel("AI?");
		panel_3.add(lblAi, "cell 0 3,grow");
		
		JComboBox<Ai> comboBoxPlayerAI = new JComboBox<>();
		panel_3.add(comboBoxPlayerAI, "cell 1 3,grow");
		for (Ai ai : Ai.values())
		{
			comboBoxPlayerAI.addItem(ai);
		}
		
		btnAddPlayer = new JButton("Hozz\u00E1ad");
		btnAddPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				String name = textFieldPlayerName.getText();
				PlayerColor color = (PlayerColor) comboBoxPlayerColor.getSelectedItem();
				Ai ai = (Ai) comboBoxPlayerAI.getSelectedItem();
				String startPositionStr = (String) getTextFieldStartPosition().getSelectedItem();
				boolean succ = false;
				
				if (startPositionStr == "Random")
				{
					succ = game.addPlayer(name, color, ai);
				}
				else
				{
					try
					{
						succ = game.addPlayer(name, color, ai, Integer.parseInt(startPositionStr));
					}
					catch (NumberFormatException ex)
					{
						System.out.println("Illegal number format for start position: " + startPositionStr);
					}
				}
				
				if (succ)
				{
					refreshPlayersList();
					textFieldPlayerName.setText("");
					comboBoxStartPosition.setSelectedIndex(0);
					try
					{
						comboBoxPlayerColor.setSelectedIndex(comboBoxPlayerColor.getSelectedIndex() + 1);
					}
					catch (IllegalArgumentException ex)
					{
						// Out of colors
					}
					textFieldPlayerName.grabFocus();
				}
			}
		});
		panel_3.add(btnAddPlayer, "flowx,cell 1 4");
		
		btnRandomGp = new JButton("Random g\u00E9p");
		btnRandomGp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if (game.addRandomDetectiveAi()) refreshPlayersList();
			}
		});
		panel_3.add(btnRandomGp, "cell 1 4");
		
		JPanel panel_4 = new JPanel();
		panel.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_4.add(scrollPane);
		
		listPlayers = new JList<>(playerListModel);
		listPlayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(listPlayers);
		
		
		JPanel panel_5 = new JPanel();
		panel.add(panel_5, BorderLayout.SOUTH);
		
		btnDeleteSelectedPlayer = new JButton("Kijel\u00F6lt t\u00F6rl\u00E9se");
		btnDeleteSelectedPlayer.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int[] selection = getListPlayers().getSelectedIndices();
				if (selection.length != 1) return;
				
				getListPlayers().clearSelection();
				game.removePlayer(playerListModel.getElementAt(selection[0]));
				
				refreshPlayersList();
			}
		});
		panel_5.add(btnDeleteSelectedPlayer);
		
		btnStartGame = new JButton("Ind\u00EDt\u00E1s");
		btnStartGame.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				startGame();
				new GameBoard(game).setVisible(true);
			}
		});
		panel_5.add(btnStartGame);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.EAST);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		btnAdvanceGame = new JButton("K\u00F6r");
		btnAdvanceGame.setEnabled(false);
		btnAdvanceGame.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				game.advance();
				refreshPlayerStatus();
			}
		});
		panel_2.add(btnAdvanceGame, BorderLayout.SOUTH);
		
		textPanePlayerStatus = new JTextPane();
		textPanePlayerStatus.setEditable(false);
		panel_2.add(textPanePlayerStatus, BorderLayout.CENTER);
	}
	
	private void refreshPlayersList()
	{
		playerListModel.removeAllElements();
		for (Player player : game.getPlayers()) playerListModel.addElement(player);
	}
	
	private void refreshPlayerStatus()
	{
		String statusText = "";
		
		for (Player player : game.getPlayers())
		{
			statusText += player.toString() + "\n";
		}
		
		getTextPanePlayerStatus().setText(statusText);
	}
	
	private void startGame()
	{
		game.start();
		
		if (game.isRunning())
		{
			getBtnAddPlayer().setEnabled(false);
			getBtnRandomGp().setEnabled(false);
			getBtnDeleteSelectedPlayer().setEnabled(false);
			getBtnStartGame().setEnabled(false);
			
			getBtnAdvanceGame().setEnabled(true);
			
			refreshPlayerStatus();	
		}
	}

	protected JTextPane getTextPanePlayerStatus() {
		return textPanePlayerStatus;
	}
	protected JButton getBtnStartGame() {
		return btnStartGame;
	}
	protected JButton getBtnDeleteSelectedPlayer() {
		return btnDeleteSelectedPlayer;
	}
	protected JButton getBtnAddPlayer() {
		return btnAddPlayer;
	}
	protected JButton getBtnAdvanceGame() {
		return btnAdvanceGame;
	}
	protected JList<Player> getListPlayers() {
		return listPlayers;
	}
	protected JComboBox<String> getTextFieldStartPosition() {
		return comboBoxStartPosition;
	}
	protected JButton getBtnRandomGp() {
		return btnRandomGp;
	}
}

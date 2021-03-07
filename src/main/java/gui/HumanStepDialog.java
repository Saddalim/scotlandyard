package gui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import asset.AtomicStep;
import asset.Link;
import asset.LinkType;
import asset.Player;
import asset.PlayerColor;
import asset.Step;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class HumanStepDialog extends JDialog {

	private static final long serialVersionUID = 5955415682345860026L;
	private JPanel contentPane;
	private JTextField textFieldName;
	private JTextField textFieldPosition;
	private JTextField textFieldColor;
	
	private Step stepToTake = new Step();
	private JTextField textFieldTaxiTicketCnt;
	private JTextField textFieldBusTicketCnt;
	private JTextField textFieldMetroTicketCnt;
	private JTextField textFieldJokerTicketCnt;
	private JTextField textFieldDoubleTicketCnt;
	private JCheckBox chckbxDoubleStep;
	private JComboBox<Link> comboBoxFirstLink;
	private JComboBox<Link> comboBoxSecondLink;

	/**
	 * Create the frame.
	 */
	public HumanStepDialog(Player player) {
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setTitle("Human Step");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][grow][][grow]", "[15px][][][][][][][][]"));
		
		JPanel playerColorBar = new JPanel();
		contentPane.add(playerColorBar, "cell 0 0 4 1,grow");
		playerColorBar.setBackground(PlayerColor.getAwtColor(player.getColor()));
		
		JLabel lblNv = new JLabel("N\u00E9v:");
		contentPane.add(lblNv, "cell 0 1,alignx trailing");
		
		textFieldName = new JTextField();
		textFieldName.setEditable(false);
		contentPane.add(textFieldName, "cell 1 1,growx");
		textFieldName.setColumns(10);
		textFieldName.setText(player.getName());
		
		JLabel lblMostIttVan = new JLabel("Most itt van:");
		contentPane.add(lblMostIttVan, "cell 2 1,alignx trailing");
		
		textFieldPosition = new JTextField();
		textFieldPosition.setEditable(false);
		contentPane.add(textFieldPosition, "cell 3 1,growx");
		textFieldPosition.setColumns(10);
		textFieldPosition.setText(player.getPosition().toString());
		
		JLabel lblSzn = new JLabel("Sz\u00EDn:");
		contentPane.add(lblSzn, "cell 0 2,alignx trailing");
		
		textFieldColor = new JTextField();
		textFieldColor.setEditable(false);
		contentPane.add(textFieldColor, "cell 1 2,growx");
		textFieldColor.setColumns(10);
		textFieldColor.setText(player.getColor().toString());
		
		JLabel lblIdeLp = new JLabel("Ide l\u00E9p:");
		contentPane.add(lblIdeLp, "cell 2 2,alignx trailing");
		
		comboBoxFirstLink = new JComboBox<>();
		comboBoxFirstLink.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (getChckbxDoubleStep() != null && getChckbxDoubleStep().isSelected()) loadSecondLinks();
			}
		});
		contentPane.add(comboBoxFirstLink, "flowx,cell 3 2,growx");
		
		for (Link link : player.getPosition().getLinks())
		{
			if (player.getTicketCnt(link.getType()) > 0 || player.getJokerTicketCnt() > 0)
			{
				comboBoxFirstLink.addItem(link);
			}
		}
		
		JLabel lblJegyek = new JLabel("Jegyek");
		contentPane.add(lblJegyek, "cell 0 3");
		
		JLabel lblMsodik = new JLabel("M\u00E1sodik:");
		contentPane.add(lblMsodik, "cell 2 3,alignx trailing");
		
		comboBoxSecondLink = new JComboBox<>();
		comboBoxSecondLink.setEnabled(false);
		contentPane.add(comboBoxSecondLink, "flowx,cell 3 3,growx");
		
		JLabel lblTaxi = new JLabel("Taxi");
		contentPane.add(lblTaxi, "cell 0 4,alignx trailing");
		
		textFieldTaxiTicketCnt = new JTextField();
		textFieldTaxiTicketCnt.setEditable(false);
		contentPane.add(textFieldTaxiTicketCnt, "cell 1 4,growx");
		textFieldTaxiTicketCnt.setColumns(10);
		textFieldTaxiTicketCnt.setText(new Integer(player.getTicketCnt(LinkType.Taxi)).toString());
		
		JLabel lblBusz = new JLabel("Busz");
		contentPane.add(lblBusz, "cell 0 5,alignx trailing");
		
		textFieldBusTicketCnt = new JTextField();
		textFieldBusTicketCnt.setEditable(false);
		contentPane.add(textFieldBusTicketCnt, "cell 1 5,growx");
		textFieldBusTicketCnt.setColumns(10);
		textFieldBusTicketCnt.setText(new Integer(player.getTicketCnt(LinkType.Bus)).toString());
		
		JButton btnMehet = new JButton("Mehet");
		
		contentPane.add(btnMehet, "flowx,cell 3 5");
		
		JLabel lblMetro = new JLabel("Metro");
		contentPane.add(lblMetro, "cell 0 6,alignx trailing");
		
		textFieldMetroTicketCnt = new JTextField();
		textFieldMetroTicketCnt.setEditable(false);
		contentPane.add(textFieldMetroTicketCnt, "cell 1 6,growx");
		textFieldMetroTicketCnt.setColumns(10);
		textFieldMetroTicketCnt.setText(new Integer(player.getTicketCnt(LinkType.Metro)).toString());
		
		JLabel lblJoker = new JLabel("Joker");
		contentPane.add(lblJoker, "cell 0 7,alignx trailing");
		
		textFieldJokerTicketCnt = new JTextField();
		textFieldJokerTicketCnt.setEditable(false);
		contentPane.add(textFieldJokerTicketCnt, "cell 1 7,growx");
		textFieldJokerTicketCnt.setColumns(10);
		textFieldJokerTicketCnt.setText(new Integer(player.getJokerTicketCnt()).toString());
		
		JLabel lblx = new JLabel("2x");
		contentPane.add(lblx, "cell 0 8,alignx trailing");
		
		textFieldDoubleTicketCnt = new JTextField();
		textFieldDoubleTicketCnt.setEditable(false);
		contentPane.add(textFieldDoubleTicketCnt, "cell 1 8,growx");
		textFieldDoubleTicketCnt.setColumns(10);
		textFieldDoubleTicketCnt.setText(new Integer(player.getDoubleTicketCnt()).toString());
		
		this.setTitle("Hova lépjen szegény " + player.getName() + "?");
		
		JButton btnPass = new JButton("Passz");
		btnPass.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				stepToTake = null;
				setVisible(false);
				dispose();
			}
		});
		btnPass.setEnabled(false);
		contentPane.add(btnPass, "cell 3 5");
		
		chckbxDoubleStep = new JCheckBox("2x");
		contentPane.add(chckbxDoubleStep, "cell 3 4");
		
		JCheckBox chckbxUseJokerTicket1 = new JCheckBox("Jokerrel");
		
		contentPane.add(chckbxUseJokerTicket1, "cell 3 2");
		
		JCheckBox chckbxUseJokerTicket2 = new JCheckBox("Jokerrel");
		chckbxUseJokerTicket2.setEnabled(false);
		contentPane.add(chckbxUseJokerTicket2, "cell 3 3");
		
		if (player.getJokerTicketCnt() < 1)
		{
			chckbxUseJokerTicket1.setSelected(false);
			chckbxUseJokerTicket1.setEnabled(false);
			chckbxUseJokerTicket2.setSelected(false);
			chckbxUseJokerTicket2.setEnabled(false);
		}
		
		if (player.getDoubleTicketCnt() < 1)
		{
			chckbxDoubleStep.setSelected(false);
			chckbxDoubleStep.setEnabled(false);
		}
		
		if (comboBoxFirstLink.getItemCount() < 1)
		{
			btnMehet.setEnabled(false);
			btnPass.setEnabled(true);
		}
		
		chckbxUseJokerTicket1.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (player.getJokerTicketCnt() < 2 && chckbxUseJokerTicket2.isSelected())
				{
					chckbxUseJokerTicket2.setSelected(false);
				}
			}
		});
		
		chckbxUseJokerTicket2.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (player.getJokerTicketCnt() < 2 && chckbxUseJokerTicket1.isSelected())
				{
					chckbxUseJokerTicket1.setSelected(false);
				}
			}
		});
		
		chckbxDoubleStep.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent arg0)
			{
				boolean doubleOn = chckbxDoubleStep.isSelected();
				
				comboBoxSecondLink.setEnabled(doubleOn);
				chckbxUseJokerTicket2.setEnabled(doubleOn);
				
				if (doubleOn) loadSecondLinks();
				else getComboBoxSecondLink().removeAllItems();
			}
		});
		
		btnMehet.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				stepToTake.steps.add(new AtomicStep((Link) comboBoxFirstLink.getSelectedItem(), chckbxUseJokerTicket1.isSelected()));
				
				if (chckbxDoubleStep.isSelected())
				{
					stepToTake.steps.add(new AtomicStep((Link) comboBoxSecondLink.getSelectedItem(), chckbxUseJokerTicket2.isSelected()));
				}
				
				setVisible(false);
				dispose();
			}
		});
	}
	
	private void loadSecondLinks()
	{
		Link firstLink = (Link) getComboBoxFirstLink().getSelectedItem();
		JComboBox<Link> cbSecondLink = getComboBoxSecondLink();
		cbSecondLink.removeAllItems();
		
		for (Link link : firstLink.getNode2().getLinks()) cbSecondLink.addItem(link);
	}
	
	public Step ask()
	{
		setModal(true);
		setVisible(true);
		return stepToTake;
	}

	protected JCheckBox getChckbxDoubleStep() {
		return chckbxDoubleStep;
	}
	protected JComboBox<Link> getComboBoxFirstLink() {
		return comboBoxFirstLink;
	}
	protected JComboBox<Link> getComboBoxSecondLink() {
		return comboBoxSecondLink;
	}
}

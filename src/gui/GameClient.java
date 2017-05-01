package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import board.Board;

import players.Player;
import server.Client;
import server.Server;
import server.ServerCLI;

import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.ImageIcon;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.SystemColor;
/**
 * the game client class that handles the game GUI and client actions
 * @author mjschaub
 *
 */
public class GameClient extends JFrame implements Runnable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblDealercardfour;
	private JLabel lblDealercardthree;
	private JLabel lblPlayer0cardtwo;
	private JLabel lblDealercardone;
	private JLabel lblDealercardtwo;
	private JLabel lblDealercardtwoseen;
	private JLabel lblPlayer0cardone;
	private JLabel lblPlayer0cardthree;
	private JLabel lblPlayer0cardfour;
	private JLabel lblMoney;
	private JLabel lblBet;
	private JLabel lblPlayer1cardfour;
	private JLabel lblPlayer1cardthree;
	private JLabel lblPlayer1cardtwo;
	private JLabel lblPlayer1cardone;
	private JLabel lblPlayer2cardfour;
	private JLabel lblPlayer2cardthree;
	private JLabel lblPlayer2cardtwo;
	private JLabel lblPlayer2cardone;
	private JLabel lblPlayer3cardfour;
	private JLabel lblPlayer3cardthree;
	private JLabel lblPlayer3cardtwo;
	private JLabel lblPlayer3cardone;
	private JLabel lblPlayer4cardfour;
	private JLabel lblPlayer4cardthree;
	private JLabel lblPlayer4cardtwo;
	private JLabel lblPlayer4cardone;
	private JLabel lblPlayer0;
	private JLabel lblPlayer1;
	private JLabel lblPlayer2;
	private JLabel lblPlayer3;
	private JLabel lblPlayer4;
	
	
	private boolean isRunning = false;
	private Client myClient;
	private int playerID;
	private Thread serverListen;
	private Thread clientRunning;
	
	
	private Map<String, JLabel> labels = new HashMap<String, JLabel>();
	private JButton btnReadyToPlay;
	private JButton btnDoubleDown;
	private JButton btnStand;
	private JButton btnHit;
	private JLabel lblDealercardsix;
	private JLabel lblPlayer0leftsplitone;
	private JLabel lblPlayer0rightsplitfive;
	private JLabel lblPlayer0rightsplitfour;
	private JLabel lblPlayer0rightsplitthree;
	private JLabel lblPlayer0rightsplittwo;
	private JLabel lblPlayer0rightsplitone;
	private JLabel lblPlayer0leftsplitfive;
	private JLabel lblPlayer0leftsplitfour;
	private JLabel lblPlayer0leftsplitthree;
	private JLabel lblPlayer0leftsplittwo;
	private JLabel lblDealercardfive;
	private JLabel lblPlayer0cardfive;
	private JLabel lblPlayer1cardfive;
	private JLabel lblPlayer2cardfive;
	private JLabel lblPlayer3cardfive;
	private JLabel lblPlayer4cardfive;
	private JLabel lblGamestate;
	private JButton btnSplit;
	private JLabel lblPlayer0cardsix;
	private JLabel lblPlayer1cardsix;
	private JLabel lblPlayer2cardsix;
	private JLabel lblPlayer3cardsix;
	private JLabel lblPlayer4cardsix;
	private JLabel lblPlayer1leftsplitone;
	private JLabel lblPlayer1leftsplittwo;
	private JLabel lblPlayer1leftsplitthree;
	private JLabel lblPlayer1leftsplitfour;
	private JLabel lblPlayer1leftsplitfive;
	private JLabel lblPlayer1rightsplitone;
	private JLabel lblPlayer1rightsplittwo;
	private JLabel lblPlayer1rightsplitthree;
	private JLabel lblPlayer1rightsplitfour;
	private JLabel lblPlayer1rightsplitfive;
	private JLabel lblPlayer2rightsplitfive;
	private JLabel lblPlayer2rightsplitfour;
	private JLabel lblPlayer2leftsplitone;
	private JLabel lblPlayer2leftsplittwo;
	private JLabel lblPlayer2leftsplitthree;
	private JLabel lblPlayer2leftsplitfour;
	private JLabel lblPlayer2leftsplitfive;
	private JLabel lblPlayer2rightsplitone;
	private JLabel lblPlayer2rightsplittwo;
	private JLabel lblPlayer2rightsplitthree;
	private JLabel lblPlayer3rightsplitfive;
	private JLabel lblPlayer3rightsplitfour;
	private JLabel lblPlayer3leftsplitone;
	private JLabel lblPlayer3leftsplittwo;
	private JLabel lblPlayer3leftsplitthree;
	private JLabel lblPlayer3leftsplitfour;
	private JLabel lblPlayer3leftsplitfive;
	private JLabel lblPlayer3rightsplitone;
	private JLabel lblPlayer3rightsplittwo;
	private JLabel lblPlayer3rightsplitthree;
	private JLabel lblPlayer4rightsplitfive;
	private JLabel lblPlayer4rightsplitfour;
	private JLabel lblPlayer4leftsplitone;
	private JLabel lblPlayer4leftsplittwo;
	private JLabel lblPlayer4leftsplitthree;
	private JLabel lblPlayer4leftsplitfour;
	private JLabel lblPlayer4leftsplitfive;
	private JLabel lblPlayer4rightsplitone;
	private JLabel lblPlayer4rightsplittwo;
	private JLabel lblPlayer4rightsplitthree;
	
	
	
	/**
	 * Create the frame.
	 */
	public GameClient(String name, double money, double bet) 
	{
		setResizable(false);
		this.myClient = new Client();
		boolean didConnect = myClient.openConnection();
		if(!didConnect)
			System.err.println("failed to connect!");
		
		String playerJoined = "Connect,Player joined game,"+name+","+money+","+bet+",";
		sendAction(playerJoined);
		setupGUI();
		isRunning = true;
		clientRunning = new Thread(this,"client running");
		clientRunning.start();
	}
	/**
	 * runs the client thread
	 */
	public void run() 
	{
		listenForAction();
	}
	
	/**
	 * this is going to send the action to the server that a player wants to make in the game
	 * @param action the string action
	 */
	private void sendAction(String action)
	{
		if(action.equals(""))
			return;
		System.out.println("client sending: "+action);
		myClient.sendPacket(action.getBytes());
		
	}
	/**
	 * the client's main thread where it listens for server responses or actions
	 */
	public void listenForAction()
	{
		
		serverListen = new Thread("listen for server") 
		{
			public void run()
			{
				while(isRunning)
				{
					String serverAction = myClient.receivePacket();
					System.out.println(serverAction);	
					parseActions(serverAction);
					
				}
			}
		};
		serverListen.start();
	}
	/**
	 * parses the actions the server sends and makes the corresponding changes to the GUI 
	 * @param msg the message the server sent
	 */
	private void parseActions(String msg)
	{
		String[] arguments = msg.split(",");
		
		if(msg.startsWith("Connected"))
		{
			playerID = Integer.parseInt(arguments[1]);
			String money = arguments[2];
			String bet = arguments[3];
			lblMoney.setText("Money: $"+money);
			lblBet.setText("Current Bet: $"+bet);
		}
		else if(msg.startsWith("Ping"))
		{
			String action = "Ping,"+playerID+",";
			sendAction(action);
		}
		else if(msg.startsWith("PlaceAtTable"))
		{
			int Id = Integer.parseInt(arguments[1]);
			String name = arguments[2];
			String label = "lblPlayer"+Id;
			labels.get(label).setText(name);
			
		}
		else if(msg.startsWith("Disconnected"))
			handleDisconnect(arguments);
		else if(msg.startsWith("StartGame"))
			startUpGame(arguments);
		else if(msg.startsWith("EndGame"))
			handleEndGame(arguments);
		else if(msg.startsWith("Split"))
			handleSplit(arguments);	
		else if(msg.startsWith("Hit"))
			handleHit(arguments);
		else if(msg.startsWith("Stand"))
			handleStand(arguments);
		else if(msg.startsWith("RemoveReadyUp"))
			this.btnReadyToPlay.setVisible(false);
		else if(msg.startsWith("ShowReadyUp"))
		{
			lblGamestate.setText("Ready up to play a game!");
			this.btnReadyToPlay.setVisible(true);
		}
		else if(msg.startsWith("Dealer"))
			dealerTurn(arguments);
		else if(msg.startsWith("GameState"))
			this.lblGamestate.setText(msg.split(",")[1]);
		else if(msg.startsWith("PlayAgain"))
			handleReplaying(arguments);
	}
	/**
	 * deals with the GUI logic of replaying, getting new bets and starting a new deal
	 * @param arguments 
	 */
	private void handleReplaying(String[] arguments) 
	{
		double bet = Double.parseDouble(arguments[1]);
		lblBet.setText("Current Bet: $"+bet);
		this.btnReadyToPlay.setVisible(true);
		btnHit.setVisible(false);
		btnStand.setVisible(false);
		btnDoubleDown.setVisible(false);
		btnSplit.setVisible(false);
		this.lblGamestate.setText("Waiting for other players to finish game and ready up");
		for(int i = 0; i < 6; i++)
		{
			for(int j = 1;j <18; j++)
			{
				String labelIdentifier = "";
				if(i != 5)
					labelIdentifier = "lblPlayer"+i;
				else if(j < 7 || j > 16)
					labelIdentifier = "lblDealer";
				else
					continue;
				if(j == 1)
					labelIdentifier+="cardone";
				else if(j == 2)
					labelIdentifier+="cardtwo";
				else if(j == 3)
					labelIdentifier+="cardthree";
				else if(j == 4)
					labelIdentifier+="cardfour";
				else if(j == 5)
					labelIdentifier+="cardfive";
				else if(j == 6)
					labelIdentifier+="cardsix";
				else if(j == 7 && i !=5)
					labelIdentifier+="leftsplitone";
				else if(j == 8 && i !=5)
					labelIdentifier+="leftsplittwo";
				else if(j == 9 && i !=5)
					labelIdentifier+="leftsplitthree";
				else if(j == 10 && i !=5)
					labelIdentifier+="leftsplitfour";
				else if(j == 11 && i !=5)
					labelIdentifier+="leftsplitfive";
				else if(j == 12 && i !=5)
					labelIdentifier+="rightsplitone";
				else if(j == 13 && i !=5)
					labelIdentifier+="rightsplittwo";
				else if(j == 14 && i !=5)
					labelIdentifier+="rightsplitthree";
				else if(j == 15 && i !=5)
					labelIdentifier+="rightsplitfour";
				else if(j == 16 && i !=5)
					labelIdentifier+="rightsplitfive";
				else if(j== 17 && i !=5)
					continue;
				else if(j == 17 && i == 5)
					labelIdentifier+="cardtwoseen";
				
				labels.get(labelIdentifier).setVisible(false);
				
			}	
		}
	}
	/**
	 * For the dealer's turn, shows their cards
	 * @param arguments
	 */
	private void dealerTurn(String[] arguments) 
	{
		lblDealercardtwo.setVisible(false);
		btnHit.setVisible(false);
		btnStand.setVisible(false);
		btnDoubleDown.setVisible(false);
		btnSplit.setVisible(false);
		for(int i = 1;i < arguments.length-1; i++)
		{
			String labelIdentifier = "lblDealer";
			if(i == 1)
				labelIdentifier+="cardone";
			else if(i == 2)
				labelIdentifier+="cardtwoseen";
			else if(i == 3)
				labelIdentifier+="cardthree";
			else if(i == 4)
				labelIdentifier+="cardfour";
			else if(i == 5)
				labelIdentifier+="cardfive";
			else if(i == 6)
				labelIdentifier+="cardsix";
			System.out.println(labelIdentifier+": "+arguments[i]);
			labels.get(labelIdentifier).setText(arguments[i]);
			labels.get(labelIdentifier).setVisible(true);
		}
	}
	/**
	 * Does the GUI logic for standing, changing turns or having the dealer go
	 * @param arguments
	 */
	private void handleStand(String[] arguments)
	{
		if(playerID == Integer.parseInt(arguments[1]))
		{
			lblGamestate.setText("It's currently your turn!");
			btnHit.setVisible(true);
			btnStand.setVisible(true);
			if(Integer.parseInt(arguments[2]) == 1)
				btnDoubleDown.setVisible(true);
			if(Integer.parseInt(arguments[3]) == 1)
				btnSplit.setVisible(true);
			
		}
		else
		{
			btnHit.setVisible(false);
			btnStand.setVisible(false);
			btnDoubleDown.setVisible(false);
			btnSplit.setVisible(false);
			lblGamestate.setText("You have finished your turn, wait for others to catch up!");
		}
	}
	/**
	 * Gives a player their new card when they hit
	 * @param arguments
	 */
	private void handleHit(String[] arguments) 
	{
		int Id = Integer.parseInt(arguments[1]);
		int split = Integer.parseInt(arguments[2]);
		btnDoubleDown.setVisible(false);
		btnSplit.setVisible(false);
		for(int i = 3;i < arguments.length-1; i++)
		{
			String labelIdentifier = "lblPlayer"+Id;
			if(split == 0)
			{
				if(i == 3)
					labelIdentifier+="cardone";
				else if(i == 4)
					labelIdentifier+="cardtwo";
				else if(i == 5)
					labelIdentifier+="cardthree";
				else if(i == 6)
					labelIdentifier+="cardfour";
				else if(i == 7)
					labelIdentifier+="cardfive";
				else if(i == 8)
					labelIdentifier+="cardsix";
			}
			else if(split == 1)
			{
				if(i == 3)
					labelIdentifier+="leftsplitone";
				else if(i == 4)
					labelIdentifier+="leftsplittwo";
				else if(i == 5)
					labelIdentifier+="leftsplitthree";
				else if(i == 6)
					labelIdentifier+="leftsplitfour";
				else if(i == 7)
					labelIdentifier+="leftsplitfive";
			}
			else if(split == 2)
			{
				if(i == 3)
					labelIdentifier+="rightsplitone";
				else if(i == 4)
					labelIdentifier+="rightsplittwo";
				else if(i == 5)
					labelIdentifier+="rightsplitthree";
				else if(i == 6)
					labelIdentifier+="rightsplitfour";
				else if(i == 7)
					labelIdentifier+="rightsplitfive";
			}
			labels.get(labelIdentifier).setText(arguments[i]);
			labels.get(labelIdentifier).setVisible(true);
		}
	}
	/**
	 * deals with a player splitting, moving their cards up to the split card section
	 * @param arguments
	 */
	private void handleSplit(String[] arguments) 
	{
		int Id = Integer.parseInt(arguments[1]);
		if(this.playerID == Id)
		{
			btnSplit.setVisible(false);
			lblBet.setText("Current Bet: $"+arguments[2]);
		}
		lblGamestate.setText("You have chosen to split, play the left hand first!");
		labels.get("lblPlayer"+Id+"cardone").setVisible(false);
		labels.get("lblPlayer"+Id+"cardtwo").setVisible(false);
		for(int i = 3; i < arguments.length-1; i++)
		{
			String labelIdentifier ="";
			if(i ==3 || i ==4)
				labelIdentifier = "lblPlayer"+Id+"leftsplit";
			else
				labelIdentifier = "lblPlayer"+Id+"rightsplit";
			if(i == 3 || i == 5)
				labelIdentifier += "one";
			else
				labelIdentifier += "two";
			labels.get(labelIdentifier).setText(arguments[i]);
			labels.get(labelIdentifier).setVisible(true);
		}
	}
	/**
	 * Ends the game, showing the ending prompts to either play again or exit
	 * @param arguments
	 */
	private void handleEndGame(String[] arguments) 
	{
		double newMoney = Double.parseDouble(arguments[3]);
		lblMoney.setText("Money: $"+newMoney);
		int won = Integer.parseInt(arguments[2]);
		int split = Integer.parseInt(arguments[4]);
		int choice =1;
		Object[] options = {"Play Again","Stop playing"};
		if(split == 1)
		{
			if(won == 0)
				JOptionPane.showMessageDialog(null, "Your left hand has lost this round!");
			else if(won == 1)
				JOptionPane.showMessageDialog(null, "Your left hand has won this round!");
			else if(won == 2)
				JOptionPane.showMessageDialog(null, "Your left hand has tied this round!");
			else if(won == 3)
				JOptionPane.showMessageDialog(null, "Your left hand has forfeited this round!");
		}
		else if(split == 2)
		{
			
			if(won == 0)
				choice = JOptionPane.showOptionDialog(this,
							"Your right hand has lost this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 1)
				choice = JOptionPane.showOptionDialog(this,
							"Your right hand has won this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 2)
				choice = JOptionPane.showOptionDialog(this,
							"Your right hand has tied this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 3)
				choice = JOptionPane.showOptionDialog(this,
							"Your right hand has forfeited this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			
		}
		else if(split == 3)
		{
			if(won == 0)
				choice = JOptionPane.showOptionDialog(this,
							"Your right hand has lost this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 1)
				choice = JOptionPane.showOptionDialog(this,
							"Your left hand has won this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 2)
				choice = JOptionPane.showOptionDialog(this,
							"Your left hand has tied this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 3)
				choice = JOptionPane.showOptionDialog(this,
							"Your left hand has forfeited this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
		}
		else if(split == 4)
		{
			if(won == 0)
				JOptionPane.showMessageDialog(null, "Your right hand has lost this round!");
			else if(won == 1)
				JOptionPane.showMessageDialog(null, "Your right hand has won this round!");
			else if(won == 2)
				JOptionPane.showMessageDialog(null, "Your right hand has tied this round!");
			else if(won == 3)
				JOptionPane.showMessageDialog(null, "Your right hand has forfeited this round!");
		}
		else
		{
			if(won == 0)
				choice = JOptionPane.showOptionDialog(this,
							"You have lost this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 1)
				choice = JOptionPane.showOptionDialog(this,
							"You have won this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 2)
				choice = JOptionPane.showOptionDialog(this,
							"You have tied this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			else if(won == 3)
				choice = JOptionPane.showOptionDialog(this,
							"You have forfeited this round!",
							"Would you like to play again?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]);
			
		}
		if(split == 1 || split == 4)
			return;
		if(choice == 0)
		{
			double newBet = Double.parseDouble(JOptionPane.showInputDialog(this, "Insert next bet: ", "(eg. 500)"));
			lblBet.setText("Current Bet: $"+newBet);
			String playAgain = "PlayAgain,"+playerID+","+newBet+",";
			sendAction(playAgain);	//play another game
		}
		else
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // quit
	}
	/**
	 * Starts up a fresh game with the cards that were dealt
	 * @param arguments
	 */
	private void startUpGame(String[] arguments) 
	{
		this.btnReadyToPlay.setVisible(false);
		int canDoubleDown = Integer.parseInt(arguments[1]);
		int currTurn = Integer.parseInt(arguments[2]);
		int canSplit = Integer.parseInt(arguments[4]);
		if(playerID == currTurn)
		{
			lblGamestate.setText("It's currently your turn!");
			btnHit.setVisible(true);
			btnStand.setVisible(true);
			if(canDoubleDown == 1)
				btnDoubleDown.setVisible(true);
			if(canSplit == 1)
				this.btnSplit.setVisible(true);
		}
		else
			lblGamestate.setText("It's currently someone else's turn!");
		String dealerCard = arguments[3];
		lblDealercardone.setText(dealerCard);
		lblDealercardone.setVisible(true);
		lblDealercardtwo.setVisible(true);
		for(int i = 5; i < arguments.length-1; i+=3)
		{
			int playerToGiveCards = Integer.parseInt(arguments[i]);
			String cardone = arguments[i+1];
			String cardtwo = arguments[i+2];
			String labelIdentifier = "lblPlayer"+playerToGiveCards+"cardone";
			String labelIdentifier2 = "lblPlayer"+playerToGiveCards+"cardtwo";
			labels.get(labelIdentifier).setText(cardone);
			labels.get(labelIdentifier2).setText(cardtwo);
			labels.get(labelIdentifier).setVisible(true);
			labels.get(labelIdentifier2).setVisible(true);
		}
	}
	/**
	 * Handles a disconnection, removing their cards and name
	 * @param arguments
	 */
	private void handleDisconnect(String[] arguments) 
	{
		int Id = Integer.parseInt(arguments[1]);
		String label = "lblPlayer"+Id;
		labels.get(label).setText("");
		labels.get("lblPlayer"+Id+"cardone").setVisible(false);
		labels.get("lblPlayer"+Id+"cardtwo").setVisible(false);
		labels.get("lblPlayer"+Id+"cardthree").setVisible(false);
		labels.get("lblPlayer"+Id+"cardfour").setVisible(false);
		labels.get("lblPlayer"+Id+"cardfive").setVisible(false);
		labels.get("lblPlayer"+Id+"cardsix").setVisible(false);
	}

	/**
	 * makes the main game GUI
	 */
	private void setupGUI()
	{
		setTitle("Blackjack Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(1029,618);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		String placeHolderCard = "K"+Character.toString((char)0x2660);
		
		lblPlayer0rightsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer0rightsplitfive.setVisible(false);
		lblPlayer0rightsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0rightsplitfive.setOpaque(true);
		lblPlayer0rightsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0rightsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0rightsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer0rightsplitfive.setBounds(289, 73, 30, 50);
		contentPane.add(lblPlayer0rightsplitfive);
		
		lblPlayer0rightsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer0rightsplitfour.setVisible(false);
		lblPlayer0rightsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0rightsplitfour.setOpaque(true);
		lblPlayer0rightsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0rightsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0rightsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer0rightsplitfour.setBounds(259, 73, 30, 50);
		contentPane.add(lblPlayer0rightsplitfour);
		
		lblPlayer0rightsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer0rightsplitthree.setVisible(false);
		lblPlayer0rightsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0rightsplitthree.setOpaque(true);
		lblPlayer0rightsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0rightsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0rightsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer0rightsplitthree.setBounds(229, 73, 30, 50);
		contentPane.add(lblPlayer0rightsplitthree);
		
		lblPlayer0rightsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer0rightsplittwo.setVisible(false);
		lblPlayer0rightsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0rightsplittwo.setOpaque(true);
		lblPlayer0rightsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0rightsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0rightsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer0rightsplittwo.setBounds(199, 73, 30, 50);
		contentPane.add(lblPlayer0rightsplittwo);
		
		lblPlayer0rightsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer0rightsplitone.setVisible(false);
		lblPlayer0rightsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0rightsplitone.setOpaque(true);
		lblPlayer0rightsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0rightsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0rightsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer0rightsplitone.setBounds(169, 73, 30, 50);
		contentPane.add(lblPlayer0rightsplitone);
		
		lblPlayer0leftsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer0leftsplitfive.setVisible(false);
		lblPlayer0leftsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0leftsplitfive.setOpaque(true);
		lblPlayer0leftsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0leftsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0leftsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer0leftsplitfive.setBounds(120, 73, 30, 50);
		contentPane.add(lblPlayer0leftsplitfive);
		
		lblPlayer0leftsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer0leftsplitfour.setVisible(false);
		lblPlayer0leftsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0leftsplitfour.setOpaque(true);
		lblPlayer0leftsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0leftsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0leftsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer0leftsplitfour.setBounds(90, 73, 30, 50);
		contentPane.add(lblPlayer0leftsplitfour);
		
		lblPlayer0leftsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer0leftsplitthree.setVisible(false);
		lblPlayer0leftsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0leftsplitthree.setOpaque(true);
		lblPlayer0leftsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0leftsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0leftsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer0leftsplitthree.setBounds(60, 73, 30, 50);
		contentPane.add(lblPlayer0leftsplitthree);
		
		lblPlayer0leftsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer0leftsplittwo.setVisible(false);
		lblPlayer0leftsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0leftsplittwo.setOpaque(true);
		lblPlayer0leftsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0leftsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0leftsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer0leftsplittwo.setBounds(30, 73, 30, 50);
		contentPane.add(lblPlayer0leftsplittwo);
		
		lblPlayer0leftsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer0leftsplitone.setVisible(false);
		lblPlayer0leftsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0leftsplitone.setOpaque(true);
		lblPlayer0leftsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0leftsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0leftsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer0leftsplitone.setBounds(0, 73, 30, 50);
		contentPane.add(lblPlayer0leftsplitone);
		
		lblPlayer1rightsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer1rightsplitfive.setVisible(false);
		lblPlayer1rightsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1rightsplitfive.setOpaque(true);
		lblPlayer1rightsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1rightsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1rightsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer1rightsplitfive.setBounds(289, 143, 30, 50);
		contentPane.add(lblPlayer1rightsplitfive);
		
		lblPlayer1rightsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer1rightsplitfour.setVisible(false);
		lblPlayer1rightsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1rightsplitfour.setOpaque(true);
		lblPlayer1rightsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1rightsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1rightsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer1rightsplitfour.setBounds(259, 143, 30, 50);
		contentPane.add(lblPlayer1rightsplitfour);
		
		lblPlayer1rightsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer1rightsplitthree.setVisible(false);
		lblPlayer1rightsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1rightsplitthree.setOpaque(true);
		lblPlayer1rightsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1rightsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1rightsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer1rightsplitthree.setBounds(229, 143, 30, 50);
		contentPane.add(lblPlayer1rightsplitthree);
		
		lblPlayer1rightsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer1rightsplittwo.setVisible(false);
		lblPlayer1rightsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1rightsplittwo.setOpaque(true);
		lblPlayer1rightsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1rightsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1rightsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer1rightsplittwo.setBounds(199, 143, 30, 50);
		contentPane.add(lblPlayer1rightsplittwo);
		
		lblPlayer1rightsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer1rightsplitone.setVisible(false);
		lblPlayer1rightsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1rightsplitone.setOpaque(true);
		lblPlayer1rightsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1rightsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1rightsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer1rightsplitone.setBounds(169, 143, 30, 50);
		contentPane.add(lblPlayer1rightsplitone);
		
		lblPlayer1leftsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer1leftsplitfive.setVisible(false);
		lblPlayer1leftsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1leftsplitfive.setOpaque(true);
		lblPlayer1leftsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1leftsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1leftsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer1leftsplitfive.setBounds(120, 143, 30, 50);
		contentPane.add(lblPlayer1leftsplitfive);
		
		lblPlayer1leftsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer1leftsplitfour.setVisible(false);
		lblPlayer1leftsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1leftsplitfour.setOpaque(true);
		lblPlayer1leftsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1leftsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1leftsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer1leftsplitfour.setBounds(90, 143, 30, 50);
		contentPane.add(lblPlayer1leftsplitfour);
		
		lblPlayer1leftsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer1leftsplitthree.setVisible(false);
		lblPlayer1leftsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1leftsplitthree.setOpaque(true);
		lblPlayer1leftsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1leftsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1leftsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer1leftsplitthree.setBounds(60, 143, 30, 50);
		contentPane.add(lblPlayer1leftsplitthree);
		
		lblPlayer1leftsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer1leftsplittwo.setVisible(false);
		lblPlayer1leftsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1leftsplittwo.setOpaque(true);
		lblPlayer1leftsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1leftsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1leftsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer1leftsplittwo.setBounds(30, 143, 30, 50);
		contentPane.add(lblPlayer1leftsplittwo);
		
		lblPlayer1leftsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer1leftsplitone.setVisible(false);
		lblPlayer1leftsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1leftsplitone.setOpaque(true);
		lblPlayer1leftsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1leftsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1leftsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer1leftsplitone.setBounds(0, 143, 30, 50);
		contentPane.add(lblPlayer1leftsplitone);
		
		lblPlayer2rightsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer2rightsplitfive.setVisible(false);
		lblPlayer2rightsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2rightsplitfive.setOpaque(true);
		lblPlayer2rightsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2rightsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2rightsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer2rightsplitfive.setBounds(289, 220, 30, 50);
		contentPane.add(lblPlayer2rightsplitfive);
		
		lblPlayer2rightsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer2rightsplitfour.setVisible(false);
		lblPlayer2rightsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2rightsplitfour.setOpaque(true);
		lblPlayer2rightsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2rightsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2rightsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer2rightsplitfour.setBounds(259, 220, 30, 50);
		contentPane.add(lblPlayer2rightsplitfour);
		
		lblPlayer2rightsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer2rightsplitthree.setVisible(false);
		lblPlayer2rightsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2rightsplitthree.setOpaque(true);
		lblPlayer2rightsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2rightsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2rightsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer2rightsplitthree.setBounds(229, 220, 30, 50);
		contentPane.add(lblPlayer2rightsplitthree);
		
		lblPlayer2rightsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer2rightsplittwo.setVisible(false);
		lblPlayer2rightsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2rightsplittwo.setOpaque(true);
		lblPlayer2rightsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2rightsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2rightsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer2rightsplittwo.setBounds(199, 220, 30, 50);
		contentPane.add(lblPlayer2rightsplittwo);
		
		lblPlayer2rightsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer2rightsplitone.setVisible(false);
		lblPlayer2rightsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2rightsplitone.setOpaque(true);
		lblPlayer2rightsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2rightsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2rightsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer2rightsplitone.setBounds(169, 220, 30, 50);
		contentPane.add(lblPlayer2rightsplitone);
		
		lblPlayer2leftsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer2leftsplitfive.setVisible(false);
		lblPlayer2leftsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2leftsplitfive.setOpaque(true);
		lblPlayer2leftsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2leftsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2leftsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer2leftsplitfive.setBounds(120, 220, 30, 50);
		contentPane.add(lblPlayer2leftsplitfive);
		
		lblPlayer2leftsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer2leftsplitfour.setVisible(false);
		lblPlayer2leftsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2leftsplitfour.setOpaque(true);
		lblPlayer2leftsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2leftsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2leftsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer2leftsplitfour.setBounds(90, 220, 30, 50);
		contentPane.add(lblPlayer2leftsplitfour);
		
		lblPlayer2leftsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer2leftsplitthree.setVisible(false);
		lblPlayer2leftsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2leftsplitthree.setOpaque(true);
		lblPlayer2leftsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2leftsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2leftsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer2leftsplitthree.setBounds(60, 220, 30, 50);
		contentPane.add(lblPlayer2leftsplitthree);
		
		lblPlayer2leftsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer2leftsplittwo.setVisible(false);
		lblPlayer2leftsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2leftsplittwo.setOpaque(true);
		lblPlayer2leftsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2leftsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2leftsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer2leftsplittwo.setBounds(30, 220, 30, 50);
		contentPane.add(lblPlayer2leftsplittwo);
		
		lblPlayer2leftsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer2leftsplitone.setVisible(false);
		lblPlayer2leftsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2leftsplitone.setOpaque(true);
		lblPlayer2leftsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2leftsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2leftsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer2leftsplitone.setBounds(0, 220, 30, 50);
		contentPane.add(lblPlayer2leftsplitone);
		
		lblPlayer3rightsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer3rightsplitfive.setVisible(false);
		lblPlayer3rightsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3rightsplitfive.setOpaque(true);
		lblPlayer3rightsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3rightsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3rightsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer3rightsplitfive.setBounds(998, 143, 30, 50);
		contentPane.add(lblPlayer3rightsplitfive);
		
		lblPlayer3rightsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer3rightsplitfour.setVisible(false);
		lblPlayer3rightsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3rightsplitfour.setOpaque(true);
		lblPlayer3rightsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3rightsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3rightsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer3rightsplitfour.setBounds(968, 143, 30, 50);
		contentPane.add(lblPlayer3rightsplitfour);
		
		lblPlayer3rightsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer3rightsplitthree.setVisible(false);
		lblPlayer3rightsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3rightsplitthree.setOpaque(true);
		lblPlayer3rightsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3rightsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3rightsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer3rightsplitthree.setBounds(938, 143, 30, 50);
		contentPane.add(lblPlayer3rightsplitthree);
		
		lblPlayer3rightsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer3rightsplittwo.setVisible(false);
		lblPlayer3rightsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3rightsplittwo.setOpaque(true);
		lblPlayer3rightsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3rightsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3rightsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer3rightsplittwo.setBounds(908, 143, 30, 50);
		contentPane.add(lblPlayer3rightsplittwo);
		
		lblPlayer3rightsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer3rightsplitone.setVisible(false);
		lblPlayer3rightsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3rightsplitone.setOpaque(true);
		lblPlayer3rightsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3rightsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3rightsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer3rightsplitone.setBounds(878, 143, 30, 50);
		contentPane.add(lblPlayer3rightsplitone);
		
		lblPlayer3leftsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer3leftsplitfive.setVisible(false);
		lblPlayer3leftsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3leftsplitfive.setOpaque(true);
		lblPlayer3leftsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3leftsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3leftsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer3leftsplitfive.setBounds(829, 143, 30, 50);
		contentPane.add(lblPlayer3leftsplitfive);
		
		lblPlayer3leftsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer3leftsplitfour.setVisible(false);
		lblPlayer3leftsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3leftsplitfour.setOpaque(true);
		lblPlayer3leftsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3leftsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3leftsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer3leftsplitfour.setBounds(799, 143, 30, 50);
		contentPane.add(lblPlayer3leftsplitfour);
		
		lblPlayer3leftsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer3leftsplitthree.setVisible(false);
		lblPlayer3leftsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3leftsplitthree.setOpaque(true);
		lblPlayer3leftsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3leftsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3leftsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer3leftsplitthree.setBounds(769, 143, 30, 50);
		contentPane.add(lblPlayer3leftsplitthree);
		
		lblPlayer3leftsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer3leftsplittwo.setVisible(false);
		lblPlayer3leftsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3leftsplittwo.setOpaque(true);
		lblPlayer3leftsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3leftsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3leftsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer3leftsplittwo.setBounds(739, 143, 30, 50);
		contentPane.add(lblPlayer3leftsplittwo);
		
		lblPlayer3leftsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer3leftsplitone.setVisible(false);
		lblPlayer3leftsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3leftsplitone.setOpaque(true);
		lblPlayer3leftsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3leftsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3leftsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer3leftsplitone.setBounds(709, 143, 30, 50);
		contentPane.add(lblPlayer3leftsplitone);
		
		lblPlayer4rightsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer4rightsplitfive.setVisible(false);
		lblPlayer4rightsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4rightsplitfive.setOpaque(true);
		lblPlayer4rightsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4rightsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4rightsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer4rightsplitfive.setBounds(998, 230, 30, 50);
		contentPane.add(lblPlayer4rightsplitfive);
		
		lblPlayer4rightsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer4rightsplitfour.setVisible(false);
		lblPlayer4rightsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4rightsplitfour.setOpaque(true);
		lblPlayer4rightsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4rightsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4rightsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer4rightsplitfour.setBounds(968, 230, 30, 50);
		contentPane.add(lblPlayer4rightsplitfour);
		
		lblPlayer4rightsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer4rightsplitthree.setVisible(false);
		lblPlayer4rightsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4rightsplitthree.setOpaque(true);
		lblPlayer4rightsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4rightsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4rightsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer4rightsplitthree.setBounds(938, 230, 30, 50);
		contentPane.add(lblPlayer4rightsplitthree);
		
		lblPlayer4rightsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer4rightsplittwo.setVisible(false);
		lblPlayer4rightsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4rightsplittwo.setOpaque(true);
		lblPlayer4rightsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4rightsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4rightsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer4rightsplittwo.setBounds(908, 230, 30, 50);
		contentPane.add(lblPlayer4rightsplittwo);
		
		lblPlayer4rightsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer4rightsplitone.setVisible(false);
		lblPlayer4rightsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4rightsplitone.setOpaque(true);
		lblPlayer4rightsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4rightsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4rightsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer4rightsplitone.setBounds(878, 230, 30, 50);
		contentPane.add(lblPlayer4rightsplitone);
		
		lblPlayer4leftsplitfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer4leftsplitfive.setVisible(false);
		lblPlayer4leftsplitfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4leftsplitfive.setOpaque(true);
		lblPlayer4leftsplitfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4leftsplitfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4leftsplitfive.setBackground(SystemColor.controlHighlight);
		lblPlayer4leftsplitfive.setBounds(829, 230, 30, 50);
		contentPane.add(lblPlayer4leftsplitfive);
		
		lblPlayer4leftsplitfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer4leftsplitfour.setVisible(false);
		lblPlayer4leftsplitfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4leftsplitfour.setOpaque(true);
		lblPlayer4leftsplitfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4leftsplitfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4leftsplitfour.setBackground(SystemColor.controlHighlight);
		lblPlayer4leftsplitfour.setBounds(799, 230, 30, 50);
		contentPane.add(lblPlayer4leftsplitfour);
		
		lblPlayer4leftsplitthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer4leftsplitthree.setVisible(false);
		lblPlayer4leftsplitthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4leftsplitthree.setOpaque(true);
		lblPlayer4leftsplitthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4leftsplitthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4leftsplitthree.setBackground(SystemColor.controlHighlight);
		lblPlayer4leftsplitthree.setBounds(769, 230, 30, 50);
		contentPane.add(lblPlayer4leftsplitthree);
		
		lblPlayer4leftsplittwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer4leftsplittwo.setVisible(false);
		lblPlayer4leftsplittwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4leftsplittwo.setOpaque(true);
		lblPlayer4leftsplittwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4leftsplittwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4leftsplittwo.setBackground(SystemColor.controlHighlight);
		lblPlayer4leftsplittwo.setBounds(739, 230, 30, 50);
		contentPane.add(lblPlayer4leftsplittwo);
		
		lblPlayer4leftsplitone = new JLabel("<html>10<br>♠</html>");
		lblPlayer4leftsplitone.setVisible(false);
		lblPlayer4leftsplitone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4leftsplitone.setOpaque(true);
		lblPlayer4leftsplitone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4leftsplitone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4leftsplitone.setBackground(SystemColor.controlHighlight);
		lblPlayer4leftsplitone.setBounds(709, 230, 30, 50);
		contentPane.add(lblPlayer4leftsplitone);
		
		lblDealercardsix = new JLabel("<html>10<br>♠</html>");
		lblDealercardsix.setVisible(false);
		lblDealercardsix.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardsix.setOpaque(true);
		lblDealercardsix.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardsix.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardsix.setBackground(SystemColor.controlHighlight);
		lblDealercardsix.setBounds(500, 155, 50, 68);
		contentPane.add(lblDealercardsix);
		
		lblDealercardfive = new JLabel("<html>10<br>♠</html>");
		lblDealercardfive.setVisible(false);
		lblDealercardfive.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardfive.setOpaque(true);
		lblDealercardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardfive.setBackground(SystemColor.controlHighlight);
		lblDealercardfive.setBounds(470, 155, 50, 68);
		contentPane.add(lblDealercardfive);
		
		lblDealercardfour = new JLabel("<html>10<br>♠</html>");
		lblDealercardfour.setVisible(false);
		lblDealercardfour.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardfour.setOpaque(true);
		lblDealercardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardfour.setBackground(SystemColor.controlHighlight);
		lblDealercardfour.setBounds(440, 155, 50, 68);
		contentPane.add(lblDealercardfour);
		
		lblDealercardthree = new JLabel("<html>10<br>♠</html>");
		lblDealercardthree.setVisible(false);
		lblDealercardthree.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardthree.setOpaque(true);
		lblDealercardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardthree.setBackground(SystemColor.controlHighlight);
		lblDealercardthree.setBounds(410, 155, 50, 68);
		contentPane.add(lblDealercardthree);
		
		lblDealercardtwo = new JLabel("");
		lblDealercardtwo.setVisible(false);
		lblDealercardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardtwo.setOpaque(true);
		lblDealercardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardtwo.setBackground(SystemColor.activeCaption);
		lblDealercardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardtwo.setBounds(380, 155, 50, 68);
		contentPane.add(lblDealercardtwo);
		
		lblDealercardone = new JLabel(placeHolderCard);
		lblDealercardone.setVisible(false);
		
		lblDealercardtwoseen = new JLabel(placeHolderCard);
		lblDealercardtwoseen.setVisible(false);
		lblDealercardtwoseen.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardtwoseen.setOpaque(true);
		lblDealercardtwoseen.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardtwoseen.setBackground(SystemColor.controlHighlight);
		lblDealercardtwoseen.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardtwoseen.setBounds(380, 155, 50, 68);
		contentPane.add(lblDealercardtwoseen);
		labels.put("lblDealercardtwoseen", lblDealercardtwoseen);
		lblDealercardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardone.setOpaque(true);
		lblDealercardone.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardone.setBackground(SystemColor.controlHighlight);
		lblDealercardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardone.setBounds(350, 155, 50, 68);
		contentPane.add(lblDealercardone);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setToolTipText("Game options");
		menuBar.setBounds(20, 1, 113, 21);
		contentPane.add(menuBar);
		
		JMenu mnGameOptions = new JMenu();
		mnGameOptions.setText("Game Options");
		menuBar.add(mnGameOptions);
		
		JMenuItem mntmForfeitHand = new JMenuItem("Forfeit hand");
		mntmForfeitHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					String forfeit = "ForfeitHand,"+playerID+",";
					sendAction(forfeit);
			}
		});
		mnGameOptions.add(mntmForfeitHand);
		
		lblPlayer0cardsix = new JLabel("<html>10<br>♠</html>");
		lblPlayer0cardsix.setVisible(false);
		lblPlayer0cardsix.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardsix.setOpaque(true);
		lblPlayer0cardsix.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardsix.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardsix.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardsix.setBounds(175, 322, 50, 68);
		contentPane.add(lblPlayer0cardsix);
		
		lblPlayer0cardfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer0cardfive.setVisible(false);
		lblPlayer0cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardfive.setOpaque(true);
		lblPlayer0cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardfive.setBounds(145, 322, 50, 68);
		contentPane.add(lblPlayer0cardfive);
		
		lblPlayer0cardfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer0cardfour.setVisible(false);
		lblPlayer0cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardfour.setOpaque(true);
		lblPlayer0cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardfour.setBounds(113, 322, 50, 68);
		contentPane.add(lblPlayer0cardfour);
		
		lblPlayer0cardthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer0cardthree.setVisible(false);
		lblPlayer0cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardthree.setOpaque(true);
		lblPlayer0cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardthree.setBounds(83, 322, 50, 68);
		contentPane.add(lblPlayer0cardthree);
		
		lblPlayer0cardtwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer0cardtwo.setVisible(false);
		lblPlayer0cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardtwo.setOpaque(true);
		lblPlayer0cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardtwo.setBounds(51, 322, 50, 68);
		contentPane.add(lblPlayer0cardtwo);
		
		lblPlayer0cardone = new JLabel("<html>10<br>♠</html>");
		lblPlayer0cardone.setVisible(false);
		lblPlayer0cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardone.setOpaque(true);
		lblPlayer0cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardone.setBounds(20, 322, 50, 68);
		contentPane.add(lblPlayer0cardone);
		
		lblPlayer1cardsix = new JLabel("<html>10<br>♠</html>");
		lblPlayer1cardsix.setVisible(false);
		lblPlayer1cardsix.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardsix.setOpaque(true);
		lblPlayer1cardsix.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardsix.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardsix.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardsix.setBounds(362, 392, 50, 68);
		contentPane.add(lblPlayer1cardsix);
		
		lblPlayer1cardfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer1cardfive.setVisible(false);
		lblPlayer1cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardfive.setOpaque(true);
		lblPlayer1cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardfive.setBounds(331, 392, 50, 68);
		contentPane.add(lblPlayer1cardfive);
		
		lblPlayer1cardfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer1cardfour.setVisible(false);
		lblPlayer1cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardfour.setOpaque(true);
		lblPlayer1cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardfour.setBounds(300, 392, 50, 68);
		contentPane.add(lblPlayer1cardfour);
		
		lblPlayer1cardthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer1cardthree.setVisible(false);
		lblPlayer1cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardthree.setOpaque(true);
		lblPlayer1cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardthree.setBounds(269, 392, 50, 68);
		contentPane.add(lblPlayer1cardthree);
		
		lblPlayer1cardtwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer1cardtwo.setVisible(false);
		lblPlayer1cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardtwo.setOpaque(true);
		lblPlayer1cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardtwo.setBounds(241, 392, 50, 68);
		contentPane.add(lblPlayer1cardtwo);
		
		lblPlayer1cardone = new JLabel("<html>10<br>♠</html>");
		lblPlayer1cardone.setVisible(false);
		lblPlayer1cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardone.setOpaque(true);
		lblPlayer1cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardone.setBounds(207, 392, 50, 68);
		contentPane.add(lblPlayer1cardone);
		
		lblPlayer2cardsix = new JLabel("<html>10<br>♠</html>");
		lblPlayer2cardsix.setVisible(false);
		lblPlayer2cardsix.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardsix.setOpaque(true);
		lblPlayer2cardsix.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardsix.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardsix.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardsix.setBounds(594, 421, 50, 68);
		contentPane.add(lblPlayer2cardsix);
		
		lblPlayer2cardfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer2cardfive.setVisible(false);
		lblPlayer2cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardfive.setOpaque(true);
		lblPlayer2cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardfive.setBounds(561, 421, 50, 68);
		contentPane.add(lblPlayer2cardfive);
		
		lblPlayer2cardfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer2cardfour.setVisible(false);
		lblPlayer2cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardfour.setOpaque(true);
		lblPlayer2cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardfour.setBounds(527, 421, 50, 68);
		contentPane.add(lblPlayer2cardfour);
		
		lblPlayer2cardthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer2cardthree.setVisible(false);
		lblPlayer2cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardthree.setOpaque(true);
		lblPlayer2cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardthree.setBounds(499, 421, 50, 68);
		contentPane.add(lblPlayer2cardthree);
		
		lblPlayer2cardtwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer2cardtwo.setVisible(false);
		lblPlayer2cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardtwo.setOpaque(true);
		lblPlayer2cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardtwo.setBounds(465, 421, 50, 68);
		contentPane.add(lblPlayer2cardtwo);
		
		lblPlayer2cardone = new JLabel("<html>10<br>♠</html>");
		lblPlayer2cardone.setVisible(false);
		lblPlayer2cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardone.setOpaque(true);
		lblPlayer2cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardone.setBounds(437, 421, 50, 68);
		contentPane.add(lblPlayer2cardone);
		
		lblPlayer3cardsix = new JLabel("<html>10<br>♠</html>");
		lblPlayer3cardsix.setVisible(false);
		lblPlayer3cardsix.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardsix.setOpaque(true);
		lblPlayer3cardsix.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardsix.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardsix.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardsix.setBounds(808, 392, 50, 68);
		contentPane.add(lblPlayer3cardsix);
		
		lblPlayer3cardfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer3cardfive.setVisible(false);
		lblPlayer3cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardfive.setOpaque(true);
		lblPlayer3cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardfive.setBounds(775, 392, 50, 68);
		contentPane.add(lblPlayer3cardfive);
		
		lblPlayer3cardfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer3cardfour.setVisible(false);
		lblPlayer3cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardfour.setOpaque(true);
		lblPlayer3cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardfour.setBounds(746, 392, 50, 68);
		contentPane.add(lblPlayer3cardfour);
		
		lblPlayer3cardthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer3cardthree.setVisible(false);
		lblPlayer3cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardthree.setOpaque(true);
		lblPlayer3cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardthree.setBounds(713, 392, 50, 68);
		contentPane.add(lblPlayer3cardthree);
		
		lblPlayer3cardtwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer3cardtwo.setVisible(false);
		lblPlayer3cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardtwo.setOpaque(true);
		lblPlayer3cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardtwo.setBounds(684, 392, 50, 68);
		contentPane.add(lblPlayer3cardtwo);
		
		lblPlayer3cardone = new JLabel("<html>10<br>♠</html>");
		lblPlayer3cardone.setVisible(false);
		lblPlayer3cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardone.setOpaque(true);
		lblPlayer3cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardone.setBounds(656, 392, 50, 68);
		contentPane.add(lblPlayer3cardone);
		
		lblPlayer4cardsix = new JLabel("<html>10<br>♠</html>");
		lblPlayer4cardsix.setVisible(false);
		lblPlayer4cardsix.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardsix.setOpaque(true);
		lblPlayer4cardsix.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardsix.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardsix.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardsix.setBounds(978, 322, 50, 68);
		contentPane.add(lblPlayer4cardsix);
		
		lblPlayer4cardfive = new JLabel("<html>10<br>♠</html>");
		lblPlayer4cardfive.setVisible(false);
		lblPlayer4cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardfive.setOpaque(true);
		lblPlayer4cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardfive.setBounds(945, 322, 50, 68);
		contentPane.add(lblPlayer4cardfive);
		
		lblPlayer4cardfour = new JLabel("<html>10<br>♠</html>");
		lblPlayer4cardfour.setVisible(false);
		lblPlayer4cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardfour.setOpaque(true);
		lblPlayer4cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardfour.setBounds(916, 322, 50, 68);
		contentPane.add(lblPlayer4cardfour);
		
		lblPlayer4cardthree = new JLabel("<html>10<br>♠</html>");
		lblPlayer4cardthree.setVisible(false);
		lblPlayer4cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardthree.setOpaque(true);
		lblPlayer4cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardthree.setBounds(883, 322, 50, 68);
		contentPane.add(lblPlayer4cardthree);
		
		lblPlayer4cardtwo = new JLabel("<html>10<br>♠</html>");
		lblPlayer4cardtwo.setVisible(false);
		lblPlayer4cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardtwo.setOpaque(true);
		lblPlayer4cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardtwo.setBounds(854, 322, 50, 68);
		contentPane.add(lblPlayer4cardtwo);
		
		lblPlayer4cardone = new JLabel("<html>10<br>♠</html>");
		lblPlayer4cardone.setVisible(false);
		lblPlayer4cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardone.setOpaque(true);
		lblPlayer4cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardone.setBounds(825, 322, 50, 68);
		contentPane.add(lblPlayer4cardone);
		
		lblMoney = new JLabel("Money: $500");
		lblMoney.setForeground(Color.WHITE);
		lblMoney.setFont(new Font("Dialog", Font.BOLD, 20));
		lblMoney.setBounds(302, 334, 197, 35);
		contentPane.add(lblMoney);
		
		lblBet = new JLabel("Current Bet: $50");
		lblBet.setFont(new Font("Dialog", Font.BOLD, 20));
		lblBet.setForeground(Color.WHITE);
		lblBet.setBounds(510, 331, 243, 39);
		contentPane.add(lblBet);
		
		btnHit = new JButton("Hit");
		btnHit.setToolTipText("Hit on your turn to add another card to your hand");
		btnHit.setVisible(false);
		btnHit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String action = "Hit,"+playerID+",";
				sendAction(action);
			}
		});
		btnHit.setBounds(339, 547, 132, 34);
		contentPane.add(btnHit);
		
		btnStand = new JButton("Stand");
		btnStand.setToolTipText("Stand to end your turn if you think you can beat the dealer");
		btnStand.setVisible(false);
		btnStand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String action = "Stand,"+playerID+",";
				sendAction(action);
			}
		});
		btnStand.setBounds(510, 547, 132, 34);
		contentPane.add(btnStand);
		
		btnDoubleDown = new JButton("Double Down");
		btnDoubleDown.setToolTipText("Double's your current bet and gives you one more card as well as ending your turn");
		btnDoubleDown.setVisible(false);
		btnDoubleDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String action = "DoubleDown,"+playerID+",";
				sendAction(action);
			}
		});
		btnDoubleDown.setBounds(678, 547, 132, 34);
		contentPane.add(btnDoubleDown);
		
		lblPlayer0 = new JLabel("");
		lblPlayer0.setForeground(Color.WHITE);
		lblPlayer0.setFont(new Font("Noto Sans CJK JP Medium", Font.BOLD, 16));
		lblPlayer0.setBounds(63, 421, 132, 23);
		contentPane.add(lblPlayer0);
		
		lblPlayer1 = new JLabel("");
		lblPlayer1.setForeground(Color.WHITE);
		lblPlayer1.setFont(new Font("Noto Sans CJK JP Medium", Font.BOLD, 16));
		lblPlayer1.setBounds(253, 493, 122, 24);
		contentPane.add(lblPlayer1);
		
		lblPlayer2 = new JLabel("");
		lblPlayer2.setForeground(Color.WHITE);
		lblPlayer2.setFont(new Font("Noto Sans CJK JP Medium", Font.BOLD, 16));
		lblPlayer2.setBounds(465, 511, 140, 24);
		contentPane.add(lblPlayer2);
		
		lblPlayer3 = new JLabel("");
		lblPlayer3.setForeground(Color.WHITE);
		lblPlayer3.setFont(new Font("Noto Sans CJK JP Medium", Font.BOLD, 16));
		lblPlayer3.setBounds(676, 493, 148, 24);
		contentPane.add(lblPlayer3);
		
		lblPlayer4 = new JLabel("");
		lblPlayer4.setForeground(Color.WHITE);
		lblPlayer4.setFont(new Font("Noto Sans CJK JP Medium", Font.BOLD, 16));
		lblPlayer4.setBounds(873, 429, 144, 24);
		contentPane.add(lblPlayer4);
		
		btnReadyToPlay = new JButton("Ready to play");
		btnReadyToPlay.setToolTipText("Ready up to be in the next game");
		btnReadyToPlay.setVisible(false);
		btnReadyToPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String serverAction = "ReadyToPlay,"+playerID+",";
				sendAction(serverAction);
				
			}
		});
		btnReadyToPlay.setBounds(401, 275, 170, 34);
		contentPane.add(btnReadyToPlay);
		
		btnSplit = new JButton("Split");
		btnSplit.setToolTipText("Split your cards to double your bet and let you play two hands at once");
		btnSplit.setVisible(false);
		btnSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String serverAction = "Split,"+playerID+",";
				sendAction(serverAction);
			}
		});
		btnSplit.setBounds(840, 547, 132, 34);
		contentPane.add(btnSplit);
		
		lblGamestate = new JLabel("");
		lblGamestate.setFont(new Font("Dialog", Font.BOLD, 15));
		lblGamestate.setForeground(Color.CYAN);
		lblGamestate.setBounds(341, 89, 533, 35);
		contentPane.add(lblGamestate);
		
		
		JLabel lblBackground = new JLabel("");
		lblBackground.setBounds(0, 1, 1029, 618);
		lblBackground.setIcon(new ImageIcon(GameClient.class.getResource("/gui/images/blackjackboard.png")));
		contentPane.add(lblBackground);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				System.out.println("Player has left the game");
				String action = "Disconnect,"+playerID+",";
				sendAction(action);
				myClient.closeConnection();
				isRunning = false;
			}
		});
		
		labels.put("lblPlayer0cardone", lblPlayer0cardone);
		labels.put("lblPlayer0cardtwo", lblPlayer0cardtwo);
		labels.put("lblPlayer0cardthree", lblPlayer0cardthree);
		labels.put("lblPlayer0cardfour", lblPlayer0cardfour);
		labels.put("lblPlayer0cardfive", lblPlayer0cardfive);
		labels.put("lblPlayer0cardsix", lblPlayer0cardsix);
		labels.put("lblPlayer1cardone", lblPlayer1cardone);
		labels.put("lblPlayer1cardtwo", lblPlayer1cardtwo);
		labels.put("lblPlayer1cardthree", lblPlayer1cardthree);
		labels.put("lblPlayer1cardfour", lblPlayer1cardfour);
		labels.put("lblPlayer1cardfive", lblPlayer1cardfive);
		labels.put("lblPlayer1cardsix", lblPlayer1cardsix);
		labels.put("lblPlayer2cardone", lblPlayer2cardone);
		labels.put("lblPlayer2cardtwo", lblPlayer2cardtwo);
		labels.put("lblPlayer2cardthree", lblPlayer2cardthree);
		labels.put("lblPlayer2cardfour", lblPlayer2cardfour);
		labels.put("lblPlayer2cardfive", lblPlayer2cardfive);
		labels.put("lblPlayer2cardsix", lblPlayer2cardsix);
		labels.put("lblPlayer3cardone", lblPlayer3cardone);
		labels.put("lblPlayer3cardtwo", lblPlayer3cardtwo);
		labels.put("lblPlayer3cardthree", lblPlayer3cardthree);
		labels.put("lblPlayer3cardfour", lblPlayer3cardfour);
		labels.put("lblPlayer3cardfive", lblPlayer3cardfive);
		labels.put("lblPlayer3cardsix", lblPlayer3cardsix);
		labels.put("lblPlayer4cardone", lblPlayer4cardone);
		labels.put("lblPlayer4cardtwo", lblPlayer4cardtwo);
		labels.put("lblPlayer4cardthree", lblPlayer4cardthree);
		labels.put("lblPlayer4cardfour", lblPlayer4cardfour);
		labels.put("lblPlayer4cardfive", lblPlayer4cardfive);
		labels.put("lblPlayer4cardsix", lblPlayer4cardsix);
		labels.put("lblPlayer0", lblPlayer0);
		labels.put("lblPlayer1", lblPlayer1);
		labels.put("lblPlayer2", lblPlayer2);
		labels.put("lblPlayer3", lblPlayer3);
		labels.put("lblPlayer4", lblPlayer4);
		labels.put("lblDealercardone", lblDealercardone);
		labels.put("lblDealercardtwo", lblDealercardtwo);
		labels.put("lblDealercardthree", lblDealercardthree);
		labels.put("lblDealercardfour", lblDealercardfour);
		labels.put("lblDealercardfive", lblDealercardfive);
		labels.put("lblDealercardsix", lblDealercardsix);
		labels.put("lblPlayer0leftsplitone",lblPlayer0leftsplitone);
		labels.put("lblPlayer0rightsplitfive",lblPlayer0rightsplitfive);
		labels.put("lblPlayer0rightsplitfour",lblPlayer0rightsplitfour);
		labels.put("lblPlayer0rightsplitthree",lblPlayer0rightsplitthree);
		labels.put("lblPlayer0rightsplittwo",lblPlayer0rightsplittwo);
		labels.put("lblPlayer0rightsplitone",lblPlayer0rightsplitone);
		labels.put("lblPlayer0leftsplitfive",lblPlayer0leftsplitfive);
		labels.put("lblPlayer0leftsplitfour",lblPlayer0leftsplitfour);
		labels.put("lblPlayer0leftsplitthree",lblPlayer0leftsplitthree);
		labels.put("lblPlayer0leftsplittwo",lblPlayer0leftsplittwo);
		labels.put("lblPlayer0leftsplitone",lblPlayer0leftsplitone);
		labels.put("lblPlayer1rightsplitfive",lblPlayer1rightsplitfive);
		labels.put("lblPlayer1rightsplitfour",lblPlayer1rightsplitfour);
		labels.put("lblPlayer1rightsplitthree",lblPlayer1rightsplitthree);
		labels.put("lblPlayer1rightsplittwo",lblPlayer1rightsplittwo);
		labels.put("lblPlayer1rightsplitone",lblPlayer1rightsplitone);
		labels.put("lblPlayer1leftsplitfive",lblPlayer1leftsplitfive);
		labels.put("lblPlayer1leftsplitfour",lblPlayer1leftsplitfour);
		labels.put("lblPlayer1leftsplitthree",lblPlayer1leftsplitthree);
		labels.put("lblPlayer1leftsplittwo",lblPlayer1leftsplittwo);
		labels.put("lblPlayer1leftsplitone",lblPlayer1leftsplitone);
		labels.put("lblPlayer2rightsplitfive",lblPlayer2rightsplitfive);
		labels.put("lblPlayer2rightsplitfour",lblPlayer2rightsplitfour);
		labels.put("lblPlayer2rightsplitthree",lblPlayer2rightsplitthree);
		labels.put("lblPlayer2rightsplittwo",lblPlayer2rightsplittwo);
		labels.put("lblPlayer2rightsplitone",lblPlayer2rightsplitone);
		labels.put("lblPlayer2leftsplitfive",lblPlayer2leftsplitfive);
		labels.put("lblPlayer2leftsplitfour",lblPlayer2leftsplitfour);
		labels.put("lblPlayer2leftsplitthree",lblPlayer2leftsplitthree);
		labels.put("lblPlayer2leftsplittwo",lblPlayer2leftsplittwo);
		labels.put("lblPlayer2leftsplitone",lblPlayer2leftsplitone);
		labels.put("lblPlayer3rightsplitfive",lblPlayer3rightsplitfive);
		labels.put("lblPlayer3rightsplitfour",lblPlayer3rightsplitfour);
		labels.put("lblPlayer3rightsplitthree",lblPlayer3rightsplitthree);
		labels.put("lblPlayer3rightsplittwo",lblPlayer3rightsplittwo);
		labels.put("lblPlayer3rightsplitone",lblPlayer3rightsplitone);
		labels.put("lblPlayer3leftsplitfive",lblPlayer3leftsplitfive);
		labels.put("lblPlayer3leftsplitfour",lblPlayer3leftsplitfour);
		labels.put("lblPlayer3leftsplitthree",lblPlayer3leftsplitthree);
		labels.put("lblPlayer3leftsplittwo",lblPlayer3leftsplittwo);
		labels.put("lblPlayer3leftsplitone",lblPlayer3leftsplitone);
		labels.put("lblPlayer4rightsplitfive",lblPlayer4rightsplitfive);
		labels.put("lblPlayer4rightsplitfour",lblPlayer4rightsplitfour);
		labels.put("lblPlayer4rightsplitthree",lblPlayer4rightsplitthree);
		labels.put("lblPlayer4rightsplittwo",lblPlayer4rightsplittwo);
		labels.put("lblPlayer4rightsplitone",lblPlayer4rightsplitone);
		labels.put("lblPlayer4leftsplitfive",lblPlayer4leftsplitfive);
		labels.put("lblPlayer4leftsplitfour",lblPlayer4leftsplitfour);
		labels.put("lblPlayer4leftsplitthree",lblPlayer4leftsplitthree);
		labels.put("lblPlayer4leftsplittwo",lblPlayer4leftsplittwo);
		labels.put("lblPlayer4leftsplitone",lblPlayer4leftsplitone);
		
		setVisible(true);
	}
}

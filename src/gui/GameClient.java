package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

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
	private JLabel lblDealercardfive;
	private JLabel lblPlayer0cardfive;
	private JLabel lblPlayer1cardfive;
	private JLabel lblPlayer2cardfive;
	private JLabel lblPlayer3cardfive;
	private JLabel lblPlayer4cardfive;

	 
	
	//private Board gameBoard;
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
	private void parseActions(String msg)
	{
		if(msg.startsWith("Connected"))
		{
			String[] arguments = msg.split(",");
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
			String[] arguments = msg.split(",");
			int Id = Integer.parseInt(arguments[1]);
			String name = arguments[2];
			String label = "lblPlayer"+Id;
			labels.get(label).setText(name);
			
		}
		else if(msg.startsWith("Disconnected"))
		{
			String[] arguments = msg.split(",");
			int Id = Integer.parseInt(arguments[1]);
			String label = "lblPlayer"+Id;
			labels.get(label).setText("");
			labels.get("lblPlayer"+Id+"cardone").setVisible(false);
			labels.get("lblPlayer"+Id+"cardtwo").setVisible(false);
			labels.get("lblPlayer"+Id+"cardthree").setVisible(false);
			labels.get("lblPlayer"+Id+"cardfour").setVisible(false);
		}
		else if(msg.startsWith("StartGame"))
		{
			this.btnReadyToPlay.setVisible(false);
			String[] arguments = msg.split(",");
			int typeOfGame = Integer.parseInt(arguments[1]);
			int currTurn = Integer.parseInt(arguments[2]);
			if(playerID == currTurn)
			{
				btnHit.setVisible(true);
				btnStand.setVisible(true);
				btnDoubleDown.setVisible(true);
			}
			String dealerCard = arguments[3];
			lblDealercardone.setText(dealerCard);
			lblDealercardone.setVisible(true);
			lblDealercardtwo.setVisible(true);
			for(int i = 4; i < arguments.length-1; i+=3)
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
		else if(msg.startsWith("EndGame"))
		{
			String[] arguments = msg.split(",");
			double newMoney = Double.parseDouble(arguments[3]);
			lblMoney.setText("Money: $"+newMoney);
			int won = Integer.parseInt(arguments[2]);
			int choice =1;
			if(won == 0)
			{
				//JOptionPane.showMessageDialog(null, "You have lost this round!");
        		Object[] options = {"Play Again","Stop playing"};
        		choice = JOptionPane.showOptionDialog(this,
        					"You have lost this round!",
        					"Would you like to play again?",
        					JOptionPane.YES_NO_OPTION,
        					JOptionPane.QUESTION_MESSAGE,
        					null,
        					options,
        					options[1]);
        		
			}
			else if(won == 1)
			{
				//JOptionPane.showMessageDialog(null, "You have won this round!");
				Object[] options = {"Play Again","Stop playing"};
        		choice = JOptionPane.showOptionDialog(this,
        					"You have won this round!",
        					"Would you like to play again?",
        					JOptionPane.YES_NO_OPTION,
        					JOptionPane.QUESTION_MESSAGE,
        					null,
        					options,
        					options[1]);
			}
			else if(won == 2)
			{
				//JOptionPane.showMessageDialog(null, "You have tied this round!");
				Object[] options = {"Play Again","Stop playing"};
        		choice = JOptionPane.showOptionDialog(this,
        					"You have tied this round!",
        					"Would you like to play again?",
        					JOptionPane.YES_NO_OPTION,
        					JOptionPane.QUESTION_MESSAGE,
        					null,
        					options,
        					options[1]);
        	
			}
			if(choice == 0)
    		{
    			double newBet = Double.parseDouble(JOptionPane.showInputDialog(this, "Insert next bet: ", "Set up next game"));
    			lblBet.setText("Current Bet: $"+newBet);
    			String playAgain = "PlayAgain,"+playerID+","+newBet+",";
    			sendAction(playAgain);	//play another game
    		}
    		else
    		{
    			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // quit
    		}
		}
		else if(msg.startsWith("Forfeit"))
		{
			//show the buttons to the next player or do the dealer actions
		}
		else if(msg.startsWith("Hit"))
		{
			String[] arguments = msg.split(",");
			int Id = Integer.parseInt(arguments[1]);
			btnDoubleDown.setVisible(false);
			for(int i = 2;i < arguments.length-1; i++)
			{
				String labelIdentifier = "lblPlayer"+Id;
				if(i == 2)
					labelIdentifier+="cardone";
				else if(i == 3)
					labelIdentifier+="cardtwo";
				else if(i == 4)
					labelIdentifier+="cardthree";
				else if(i == 5)
					labelIdentifier+="cardfour";
				else if(i == 6)
					labelIdentifier+="cardfive";
				labels.get(labelIdentifier).setText(arguments[i]);
				labels.get(labelIdentifier).setVisible(true);
			}
			
		}
		else if(msg.startsWith("Stand"))
		{
			String[] arguments = msg.split(",");
			if(playerID == Integer.parseInt(arguments[1]))
			{
				btnHit.setVisible(true);
				btnStand.setVisible(true);
				btnDoubleDown.setVisible(true);
			}
			else
			{
				btnHit.setVisible(false);
				btnStand.setVisible(false);
				btnDoubleDown.setVisible(false);
			}
			
		}
		else if(msg.startsWith("Dealer"))
		{
			lblDealercardtwo.setVisible(false);
			btnHit.setVisible(false);
			btnStand.setVisible(false);
			btnDoubleDown.setVisible(false);
			String[] arguments = msg.split(",");
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
		//setBounds(5, 5, 1029,618);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		String placeHolderCard = "K"+Character.toString((char)0x2660);
		
		lblDealercardsix = new JLabel("K♠");
		lblDealercardsix.setVisible(false);
		lblDealercardsix.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardsix.setOpaque(true);
		lblDealercardsix.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardsix.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardsix.setBackground(SystemColor.controlHighlight);
		lblDealercardsix.setBounds(580, 155, 50, 68);
		contentPane.add(lblDealercardsix);
		
		lblDealercardfive = new JLabel("K♠");
		lblDealercardfive.setVisible(false);
		lblDealercardfive.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardfive.setOpaque(true);
		lblDealercardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardfive.setBackground(SystemColor.controlHighlight);
		lblDealercardfive.setBounds(531, 155, 50, 68);
		contentPane.add(lblDealercardfive);
		
		lblDealercardfour = new JLabel("K♠");
		lblDealercardfour.setVisible(false);
		lblDealercardfour.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardfour.setOpaque(true);
		lblDealercardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardfour.setBackground(SystemColor.controlHighlight);
		lblDealercardfour.setBounds(484, 155, 50, 68);
		contentPane.add(lblDealercardfour);
		
		lblDealercardthree = new JLabel("K♠");
		lblDealercardthree.setVisible(false);
		lblDealercardthree.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardthree.setOpaque(true);
		lblDealercardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardthree.setBackground(SystemColor.controlHighlight);
		lblDealercardthree.setBounds(437, 155, 50, 68);
		contentPane.add(lblDealercardthree);
		
		lblDealercardtwo = new JLabel("");
		lblDealercardtwo.setVisible(false);
		lblDealercardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardtwo.setOpaque(true);
		lblDealercardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardtwo.setBackground(SystemColor.activeCaption);
		lblDealercardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardtwo.setBounds(391, 155, 50, 68);
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
		lblDealercardtwoseen.setBounds(391, 155, 50, 68);
		contentPane.add(lblDealercardtwoseen);
		labels.put("lblDealercardtwoseen", lblDealercardtwoseen);
		lblDealercardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardone.setOpaque(true);
		lblDealercardone.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardone.setBackground(SystemColor.controlHighlight);
		lblDealercardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardone.setBounds(341, 155, 50, 68);
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
		
		/*JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Player has left the game");
				String action = "Disconnect,"+playerID+",";
				sendAction(action);
				myClient.closeConnection();
				isRunning = false;
			}
		});
		mnGameOptions.add(mntmQuit);*/
		lblPlayer0cardfive = new JLabel("K♠");
		lblPlayer0cardfive.setVisible(false);
		lblPlayer0cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardfive.setOpaque(true);
		lblPlayer0cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardfive.setBounds(165, 322, 50, 68);
		contentPane.add(lblPlayer0cardfive);
		
		lblPlayer0cardfour = new JLabel("K♠");
		lblPlayer0cardfour.setVisible(false);
		lblPlayer0cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardfour.setOpaque(true);
		lblPlayer0cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardfour.setBounds(131, 322, 50, 68);
		contentPane.add(lblPlayer0cardfour);
		
		lblPlayer0cardthree = new JLabel("K♠");
		lblPlayer0cardthree.setVisible(false);
		lblPlayer0cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardthree.setOpaque(true);
		lblPlayer0cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardthree.setBounds(93, 322, 50, 68);
		contentPane.add(lblPlayer0cardthree);
		
		lblPlayer0cardtwo = new JLabel("K♠");
		lblPlayer0cardtwo.setVisible(false);
		lblPlayer0cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardtwo.setOpaque(true);
		lblPlayer0cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardtwo.setBounds(56, 322, 50, 68);
		contentPane.add(lblPlayer0cardtwo);
		
		lblPlayer0cardone = new JLabel("K♠");
		lblPlayer0cardone.setVisible(false);
		lblPlayer0cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardone.setOpaque(true);
		lblPlayer0cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardone.setBounds(20, 322, 50, 68);
		contentPane.add(lblPlayer0cardone);
		
		lblPlayer1cardfive = new JLabel("K♠");
		lblPlayer1cardfive.setVisible(false);
		lblPlayer1cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardfive.setOpaque(true);
		lblPlayer1cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardfive.setBounds(352, 392, 50, 68);
		contentPane.add(lblPlayer1cardfive);
		
		lblPlayer1cardfour = new JLabel("K♠");
		lblPlayer1cardfour.setVisible(false);
		lblPlayer1cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardfour.setOpaque(true);
		lblPlayer1cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardfour.setBounds(314, 392, 50, 68);
		contentPane.add(lblPlayer1cardfour);
		
		lblPlayer1cardthree = new JLabel("K♠");
		lblPlayer1cardthree.setVisible(false);
		lblPlayer1cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardthree.setOpaque(true);
		lblPlayer1cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardthree.setBounds(280, 392, 50, 68);
		contentPane.add(lblPlayer1cardthree);
		
		lblPlayer1cardtwo = new JLabel("K♠");
		lblPlayer1cardtwo.setVisible(false);
		lblPlayer1cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardtwo.setOpaque(true);
		lblPlayer1cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardtwo.setBounds(243, 392, 50, 68);
		contentPane.add(lblPlayer1cardtwo);
		
		lblPlayer1cardone = new JLabel("K♠");
		lblPlayer1cardone.setVisible(false);
		lblPlayer1cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardone.setOpaque(true);
		lblPlayer1cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardone.setBounds(207, 392, 50, 68);
		contentPane.add(lblPlayer1cardone);
		
		lblPlayer2cardfive = new JLabel("K♠");
		lblPlayer2cardfive.setVisible(false);
		lblPlayer2cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardfive.setOpaque(true);
		lblPlayer2cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardfive.setBounds(580, 421, 50, 68);
		contentPane.add(lblPlayer2cardfive);
		
		lblPlayer2cardfour = new JLabel("K♠");
		lblPlayer2cardfour.setVisible(false);
		lblPlayer2cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardfour.setOpaque(true);
		lblPlayer2cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardfour.setBounds(544, 421, 50, 68);
		contentPane.add(lblPlayer2cardfour);
		
		lblPlayer2cardthree = new JLabel("K♠");
		lblPlayer2cardthree.setVisible(false);
		lblPlayer2cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardthree.setOpaque(true);
		lblPlayer2cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardthree.setBounds(510, 421, 50, 68);
		contentPane.add(lblPlayer2cardthree);
		
		lblPlayer2cardtwo = new JLabel("K♠");
		lblPlayer2cardtwo.setVisible(false);
		lblPlayer2cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardtwo.setOpaque(true);
		lblPlayer2cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardtwo.setBounds(473, 421, 50, 68);
		contentPane.add(lblPlayer2cardtwo);
		
		lblPlayer2cardone = new JLabel("K♠");
		lblPlayer2cardone.setVisible(false);
		lblPlayer2cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardone.setOpaque(true);
		lblPlayer2cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardone.setBounds(437, 421, 50, 68);
		contentPane.add(lblPlayer2cardone);
		
		lblPlayer3cardfive = new JLabel("K♠");
		lblPlayer3cardfive.setVisible(false);
		lblPlayer3cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardfive.setOpaque(true);
		lblPlayer3cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardfive.setBounds(800, 392, 50, 68);
		contentPane.add(lblPlayer3cardfive);
		
		lblPlayer3cardfour = new JLabel("K♠");
		lblPlayer3cardfour.setVisible(false);
		lblPlayer3cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardfour.setOpaque(true);
		lblPlayer3cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardfour.setBounds(763, 392, 50, 68);
		contentPane.add(lblPlayer3cardfour);
		
		lblPlayer3cardthree = new JLabel("K♠");
		lblPlayer3cardthree.setVisible(false);
		lblPlayer3cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardthree.setOpaque(true);
		lblPlayer3cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardthree.setBounds(729, 392, 50, 68);
		contentPane.add(lblPlayer3cardthree);
		
		lblPlayer3cardtwo = new JLabel("K♠");
		lblPlayer3cardtwo.setVisible(false);
		lblPlayer3cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardtwo.setOpaque(true);
		lblPlayer3cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardtwo.setBounds(692, 392, 50, 68);
		contentPane.add(lblPlayer3cardtwo);
		
		lblPlayer3cardone = new JLabel("K♠");
		lblPlayer3cardone.setVisible(false);
		lblPlayer3cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardone.setOpaque(true);
		lblPlayer3cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardone.setBounds(656, 392, 50, 68);
		contentPane.add(lblPlayer3cardone);
		
		lblPlayer4cardfive = new JLabel("K♠");
		lblPlayer4cardfive.setVisible(false);
		lblPlayer4cardfive.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardfive.setOpaque(true);
		lblPlayer4cardfive.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardfive.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardfive.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardfive.setBounds(967, 322, 50, 68);
		contentPane.add(lblPlayer4cardfive);
		
		lblPlayer4cardfour = new JLabel("K♠");
		lblPlayer4cardfour.setVisible(false);
		lblPlayer4cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardfour.setOpaque(true);
		lblPlayer4cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardfour.setBounds(932, 322, 50, 68);
		contentPane.add(lblPlayer4cardfour);
		
		lblPlayer4cardthree = new JLabel("K♠");
		lblPlayer4cardthree.setVisible(false);
		lblPlayer4cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardthree.setOpaque(true);
		lblPlayer4cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardthree.setBounds(898, 322, 50, 68);
		contentPane.add(lblPlayer4cardthree);
		
		lblPlayer4cardtwo = new JLabel("K♠");
		lblPlayer4cardtwo.setVisible(false);
		lblPlayer4cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardtwo.setOpaque(true);
		lblPlayer4cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardtwo.setBounds(861, 322, 50, 68);
		contentPane.add(lblPlayer4cardtwo);
		
		lblPlayer4cardone = new JLabel("K♠");
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
		btnHit.setVisible(false);
		btnHit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String action = "Hit,"+playerID+",";
				sendAction(action);
			}
		});
		btnHit.setBounds(339, 547, 181, 34);
		contentPane.add(btnHit);
		
		btnStand = new JButton("Stand");
		btnStand.setVisible(false);
		btnStand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String action = "Stand,"+playerID+",";
				sendAction(action);
			}
		});
		btnStand.setBounds(548, 547, 181, 34);
		contentPane.add(btnStand);
		
		btnDoubleDown = new JButton("Double Down");
		btnDoubleDown.setVisible(false);
		btnDoubleDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String action = "DoubleDown,"+playerID+",";
				sendAction(action);
			}
		});
		btnDoubleDown.setBounds(763, 547, 197, 34);
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
		btnReadyToPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String serverAction = "ReadyToPlay,"+playerID+",";
				sendAction(serverAction);
				
			}
		});
		btnReadyToPlay.setBounds(401, 275, 170, 34);
		contentPane.add(btnReadyToPlay);
		
		
		JLabel lblBackground = new JLabel("");
		lblBackground.setBounds(0, 0, 1029, 618);
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
		labels.put("lblPlayer1cardone", lblPlayer1cardone);
		labels.put("lblPlayer1cardtwo", lblPlayer1cardtwo);
		labels.put("lblPlayer1cardthree", lblPlayer1cardthree);
		labels.put("lblPlayer1cardfour", lblPlayer1cardfour);
		labels.put("lblPlayer1cardfive", lblPlayer1cardfive);
		labels.put("lblPlayer2cardone", lblPlayer2cardone);
		labels.put("lblPlayer2cardtwo", lblPlayer2cardtwo);
		labels.put("lblPlayer2cardthree", lblPlayer2cardthree);
		labels.put("lblPlayer2cardfour", lblPlayer2cardfour);
		labels.put("lblPlayer2cardfive", lblPlayer2cardfive);
		labels.put("lblPlayer3cardone", lblPlayer3cardone);
		labels.put("lblPlayer3cardtwo", lblPlayer3cardtwo);
		labels.put("lblPlayer3cardthree", lblPlayer3cardthree);
		labels.put("lblPlayer3cardfour", lblPlayer3cardfour);
		labels.put("lblPlayer3cardfive", lblPlayer3cardfive);
		labels.put("lblPlayer4cardone", lblPlayer4cardone);
		labels.put("lblPlayer4cardtwo", lblPlayer4cardtwo);
		labels.put("lblPlayer4cardthree", lblPlayer4cardthree);
		labels.put("lblPlayer4cardfour", lblPlayer4cardfour);
		labels.put("lblPlayer4cardfive", lblPlayer4cardfive);
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
		
		
		
		setVisible(true);
	}
}

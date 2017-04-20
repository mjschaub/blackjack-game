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
					
					/*if(serverAction instanceof Board)
					{
						System.out.println("attached board");
						gameBoard = (Board) serverAction;
					}
					else
					{*/
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
			//System.out.println(gameBoard);
			//myClient.setPlayer(gameBoard.getPlayerByID(Integer.parseInt(arguments[1])));
			playerID = Integer.parseInt(arguments[1]);
			
			//TODO: make UI show money and bet
		}
		else if(msg.startsWith("Ping"))
		{
			//String action = "Ping,"+myClient.clientPlayer.getPlayerNum();
			String action = "Ping,"+playerID+",";
			sendAction(action);
			
		}
		else if(msg.startsWith("PlaceAtTable"))
		{
			
		}
		else if(msg.startsWith("Disconnect"))
		{
			
		}
		else if(msg.startsWith("StartGame"))
		{
			
		}
		else if(msg.startsWith("Forfeit"))
		{
			
		}
		else if(msg.startsWith("Hit"))
		{
			
		}
		else if(msg.startsWith("Stand"))
		{
			
		}
		else if(msg.startsWith("Deal"))
		{
			
		}
		else if(msg.startsWith("Bet"))
		{
			
		}
		else if(msg.startsWith("PlayAgain"))
		{
			
		}
		else if(msg.startsWith("StartGame"))
		{
			
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
		
		lblDealercardfour = new JLabel("K♠");
		lblDealercardfour.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardfour.setOpaque(true);
		lblDealercardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardfour.setBackground(SystemColor.controlHighlight);
		lblDealercardfour.setBounds(500, 155, 61, 79);
		contentPane.add(lblDealercardfour);
		
		lblDealercardthree = new JLabel("K♠");
		lblDealercardthree.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardthree.setOpaque(true);
		lblDealercardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardthree.setBackground(SystemColor.controlHighlight);
		lblDealercardthree.setBounds(465, 155, 61, 79);
		contentPane.add(lblDealercardthree);
		
		lblDealercardtwo = new JLabel("");
		lblDealercardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardtwo.setOpaque(true);
		lblDealercardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardtwo.setBackground(SystemColor.activeCaption);
		lblDealercardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardtwo.setBounds(427, 155, 61, 79);
		contentPane.add(lblDealercardtwo);
		
		lblDealercardone = new JLabel(placeHolderCard);
		lblDealercardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblDealercardone.setOpaque(true);
		lblDealercardone.setVerticalAlignment(SwingConstants.TOP);
		lblDealercardone.setBackground(SystemColor.controlHighlight);
		lblDealercardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblDealercardone.setBounds(392, 155, 61, 79);
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
		
		lblPlayer0cardfour = new JLabel("K♠");
		lblPlayer0cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardfour.setOpaque(true);
		lblPlayer0cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardfour.setBounds(134, 328, 61, 79);
		contentPane.add(lblPlayer0cardfour);
		
		lblPlayer0cardthree = new JLabel("K♠");
		lblPlayer0cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardthree.setOpaque(true);
		lblPlayer0cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardthree.setBounds(100, 328, 61, 79);
		contentPane.add(lblPlayer0cardthree);
		
		lblPlayer0cardtwo = new JLabel("K♠");
		lblPlayer0cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardtwo.setOpaque(true);
		lblPlayer0cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardtwo.setBounds(63, 328, 61, 79);
		contentPane.add(lblPlayer0cardtwo);
		
		lblPlayer0cardone = new JLabel("K♠");
		lblPlayer0cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer0cardone.setOpaque(true);
		lblPlayer0cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer0cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer0cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer0cardone.setBounds(27, 328, 61, 79);
		contentPane.add(lblPlayer0cardone);
		
		lblPlayer1cardfour = new JLabel("K♠");
		lblPlayer1cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardfour.setOpaque(true);
		lblPlayer1cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardfour.setBounds(314, 392, 61, 79);
		contentPane.add(lblPlayer1cardfour);
		
		lblPlayer1cardthree = new JLabel("K♠");
		lblPlayer1cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardthree.setOpaque(true);
		lblPlayer1cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardthree.setBounds(280, 392, 61, 79);
		contentPane.add(lblPlayer1cardthree);
		
		lblPlayer1cardtwo = new JLabel("K♠");
		lblPlayer1cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardtwo.setOpaque(true);
		lblPlayer1cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardtwo.setBounds(243, 392, 61, 79);
		contentPane.add(lblPlayer1cardtwo);
		
		lblPlayer1cardone = new JLabel("K♠");
		lblPlayer1cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer1cardone.setOpaque(true);
		lblPlayer1cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer1cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer1cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer1cardone.setBounds(207, 392, 61, 79);
		contentPane.add(lblPlayer1cardone);
		
		lblPlayer2cardfour = new JLabel("K♠");
		lblPlayer2cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardfour.setOpaque(true);
		lblPlayer2cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardfour.setBounds(544, 421, 61, 79);
		contentPane.add(lblPlayer2cardfour);
		
		lblPlayer2cardthree = new JLabel("K♠");
		lblPlayer2cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardthree.setOpaque(true);
		lblPlayer2cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardthree.setBounds(510, 421, 61, 79);
		contentPane.add(lblPlayer2cardthree);
		
		lblPlayer2cardtwo = new JLabel("K♠");
		lblPlayer2cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardtwo.setOpaque(true);
		lblPlayer2cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardtwo.setBounds(473, 421, 61, 79);
		contentPane.add(lblPlayer2cardtwo);
		
		lblPlayer2cardone = new JLabel("K♠");
		lblPlayer2cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer2cardone.setOpaque(true);
		lblPlayer2cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer2cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer2cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer2cardone.setBounds(437, 421, 61, 79);
		contentPane.add(lblPlayer2cardone);
		
		lblPlayer3cardfour = new JLabel("K♠");
		lblPlayer3cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardfour.setOpaque(true);
		lblPlayer3cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardfour.setBounds(763, 392, 61, 79);
		contentPane.add(lblPlayer3cardfour);
		
		lblPlayer3cardthree = new JLabel("K♠");
		lblPlayer3cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardthree.setOpaque(true);
		lblPlayer3cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardthree.setBounds(729, 392, 61, 79);
		contentPane.add(lblPlayer3cardthree);
		
		lblPlayer3cardtwo = new JLabel("K♠");
		lblPlayer3cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardtwo.setOpaque(true);
		lblPlayer3cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardtwo.setBounds(692, 392, 61, 79);
		contentPane.add(lblPlayer3cardtwo);
		
		lblPlayer3cardone = new JLabel("K♠");
		lblPlayer3cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer3cardone.setOpaque(true);
		lblPlayer3cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer3cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer3cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer3cardone.setBounds(656, 392, 61, 79);
		contentPane.add(lblPlayer3cardone);
		
		lblPlayer4cardfour = new JLabel("K♠");
		lblPlayer4cardfour.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardfour.setOpaque(true);
		lblPlayer4cardfour.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardfour.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardfour.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardfour.setBounds(956, 338, 61, 79);
		contentPane.add(lblPlayer4cardfour);
		
		lblPlayer4cardthree = new JLabel("K♠");
		lblPlayer4cardthree.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardthree.setOpaque(true);
		lblPlayer4cardthree.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardthree.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardthree.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardthree.setBounds(922, 338, 61, 79);
		contentPane.add(lblPlayer4cardthree);
		
		lblPlayer4cardtwo = new JLabel("K♠");
		lblPlayer4cardtwo.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardtwo.setOpaque(true);
		lblPlayer4cardtwo.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardtwo.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardtwo.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardtwo.setBounds(885, 338, 61, 79);
		contentPane.add(lblPlayer4cardtwo);
		
		lblPlayer4cardone = new JLabel("K♠");
		lblPlayer4cardone.setVerticalAlignment(SwingConstants.TOP);
		lblPlayer4cardone.setOpaque(true);
		lblPlayer4cardone.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPlayer4cardone.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPlayer4cardone.setBackground(SystemColor.controlHighlight);
		lblPlayer4cardone.setBounds(849, 338, 61, 79);
		contentPane.add(lblPlayer4cardone);
		
		lblMoney = new JLabel("Money: $500");
		lblMoney.setForeground(Color.WHITE);
		lblMoney.setFont(new Font("Dialog", Font.BOLD, 20));
		lblMoney.setBounds(339, 338, 159, 24);
		contentPane.add(lblMoney);
		
		lblBet = new JLabel("Current Bet: $50");
		lblBet.setFont(new Font("Dialog", Font.BOLD, 20));
		lblBet.setForeground(Color.WHITE);
		lblBet.setBounds(510, 331, 197, 39);
		contentPane.add(lblBet);
		
		JButton btnHit = new JButton("Hit");
		btnHit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnHit.setBounds(330, 530, 181, 34);
		contentPane.add(btnHit);
		
		JButton btnStand = new JButton("Stand");
		btnStand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnStand.setBounds(545, 530, 181, 34);
		contentPane.add(btnStand);
		
		JButton btnDoubleDown = new JButton("Double Down");
		btnDoubleDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnDoubleDown.setBounds(761, 530, 197, 34);
		contentPane.add(btnDoubleDown);
		
		lblPlayer0 = new JLabel("Player1");
		lblPlayer0.setForeground(Color.BLACK);
		lblPlayer0.setFont(new Font("Dialog", Font.BOLD, 14));
		lblPlayer0.setBounds(63, 421, 70, 15);
		contentPane.add(lblPlayer0);
		
		lblPlayer1 = new JLabel("Player2");
		lblPlayer1.setForeground(Color.BLACK);
		lblPlayer1.setFont(new Font("Dialog", Font.BOLD, 14));
		lblPlayer1.setBounds(253, 483, 70, 15);
		contentPane.add(lblPlayer1);
		
		lblPlayer2 = new JLabel("Player3");
		lblPlayer2.setForeground(Color.BLACK);
		lblPlayer2.setFont(new Font("Dialog", Font.BOLD, 14));
		lblPlayer2.setBounds(483, 512, 70, 15);
		contentPane.add(lblPlayer2);
		
		lblPlayer3 = new JLabel("Player4");
		lblPlayer3.setForeground(Color.BLACK);
		lblPlayer3.setFont(new Font("Dialog", Font.BOLD, 14));
		lblPlayer3.setBounds(702, 483, 70, 15);
		contentPane.add(lblPlayer3);
		
		lblPlayer4 = new JLabel("Player5");
		lblPlayer4.setForeground(Color.BLACK);
		lblPlayer4.setFont(new Font("Dialog", Font.BOLD, 14));
		lblPlayer4.setBounds(885, 429, 70, 15);
		contentPane.add(lblPlayer4);
		
		
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
		
		setVisible(true);
	}


	
}

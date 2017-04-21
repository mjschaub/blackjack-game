package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import players.Player;

import board.Board;
import board.Card;

/**
 * the Server class that handles all of the networking for the server of the blackjack game
 * @author mjschaub
 *
 */
public class Server implements Runnable 
{

	public Board gameBoard;
	private ArrayList<String> playerPings = new ArrayList<String>();
	private ArrayList<Player> numPlayersReady = new ArrayList<Player>();
	private int idxTurn;
	
	
	private int port;
	private DatagramSocket socket;
	private Thread serverThread, manageClient, sendThread, receiveThread;
	private boolean isRunning;
	/**
	 * the constructor for a server to be called and made on whatever port is specified
	 * @param port
	 */
	public Server(int port)
	{
		gameBoard = new Board();
		this.port = port;
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.serverThread = new Thread(this,"Server");
		serverThread.start();
		this.isRunning = false;
		
	}
	/**
	 * the run method because we are implementing runnable that starts the threads needed for the server
	 */
	public void run()
	{
		isRunning = true;
		System.out.println("Server started on port 8000");
		setupClients();
		receivePackets();
	}
	/**
	 * sets up the information about the clients, eventually going to deal with logging them in and out
	 */
	private void setupClients()
	{
		manageClient = new Thread("clientThread") 
		{
			public void run()
			{
				while(isRunning) //manage all the client players
				{
					sendActionsToEveryone("Ping");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int i = 0; i < gameBoard.getNumPlayers(); i++)
					{
						System.out.println(playerPings);
						if(!playerPings.contains(Integer.toString(i)))
						{
							if(gameBoard.getPlayerByID(i).numAttempts > 5)
							{
								gameBoard.playerHasLeft(i);
								System.out.println("player has left");
								String actionForClients = "Disconnected,"+i+",";
								sendActionsToEveryone(actionForClients);
							}
							else
								gameBoard.getPlayerByID(i).numAttempts++;
						}
						else
						{
							playerPings.remove(new String(Integer.toString(i)));
							gameBoard.getPlayerByID(i).numAttempts = 0;
						}
					}
				}
			}
		};
		manageClient.start();
	}
	/**
	 * a method for the server to receive packets from each of the clients, which come in as strings
	 */
	private void receivePackets()
	{
		receiveThread = new Thread("ReceivePackets")
		{
			public void run() //manage receiving actions from clients
			{
				while(isRunning)
				{
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data,data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String actionReceived = new String(packet.getData());
					parseActions(actionReceived,packet);
					System.out.println(actionReceived);
				}
			}
		};
		receiveThread.start();
	}
	private void sendPackets(final byte[] data, final InetAddress address, final int port)
	{
		
		sendThread = new Thread("sending data")
		{
			public void run()
			{
				DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}

	
	private void parseActions(String msg,DatagramPacket packet)
	{
		if(msg.startsWith("Connect"))
		{
			String[] arguments = msg.split(",");
			String name = arguments[2]; //parse from actionReceived
			double startingMoney = Double.parseDouble(arguments[3]);
			double currBet = Double.parseDouble(arguments[4]);
			gameBoard.addPlayer(name, startingMoney, currBet,packet.getAddress(),packet.getPort());
			//sendPackets(gameBoard,packet.getAddress(),packet.getPort());
			String idOfPlayerAdded = "Connected,"+(gameBoard.getNumPlayers()-1)+","+startingMoney+","+currBet+",";
			sendPackets(idOfPlayerAdded.getBytes(),packet.getAddress(),packet.getPort());
			for(int i =0;i < gameBoard.getNumPlayers(); i++)
			{
				String setupPlayerForGame = "PlaceAtTable,"+i+","+gameBoard.getPlayerByID(i).getName()+",";
				sendActionsToEveryone(setupPlayerForGame);
			}
			
		}
		else if(msg.startsWith("Disconnect"))
		{
			String[] arguments = msg.split(",");
			int idOfPlayerLeaving = Integer.parseInt(arguments[1]);
			gameBoard.playerHasLeft(idOfPlayerLeaving);
			if(numPlayersReady.contains(gameBoard.getPlayerByID(idOfPlayerLeaving)))
				numPlayersReady.remove(gameBoard.getPlayerByID(idOfPlayerLeaving));
			String actionForClients = "Disconnected,"+idOfPlayerLeaving+",";
			sendActionsToEveryone(actionForClients);
			
			
		}
		else if(msg.startsWith("Ping"))
		{
			playerPings.add(msg.split(",")[1]);
			System.out.println("added ping to playerPings for: "+playerPings.get(0));
		}
		else if(msg.startsWith("ReadyToPlay"))
		{
			this.numPlayersReady.add(gameBoard.getPlayerByID(Integer.parseInt(msg.split(",")[1])));
			if(numPlayersReady.size() == gameBoard.getNumPlayers())
			{
				int typeOfGame = 0; //to be implemented week 4
				gameBoard.shuffle();
				gameBoard.dealDealerCard();
				gameBoard.dealDealerCard();
				gameBoard.turn = numPlayersReady.get(0).getPlayerNum();
				idxTurn = 0;
				String startGame = "StartGame,"+typeOfGame+","+gameBoard.turn+","+gameBoard.dealer.cardsToShow().get(0).getImageString()+",";
				for(int i = 0; i < numPlayersReady.size(); i++)
				{
					gameBoard.dealPlayerCard(numPlayersReady.get(i).getPlayerNum());
					gameBoard.dealPlayerCard(numPlayersReady.get(i).getPlayerNum());
					startGame += numPlayersReady.get(i).getPlayerNum()+","+numPlayersReady.get(i).showHand().get(0)+","+numPlayersReady.get(i).showHand().get(1)+",";
					
				}
				sendActionsToEveryone(startGame);
				for(int i = 0; i < numPlayersReady.size(); i++)
				{
					Player currPlayer = gameBoard.getPlayerByID(numPlayersReady.get(i).getPlayerNum());
					if(gameBoard.dealer.getBlackJackScore() == 21)
					{	
						if(currPlayer.getBlackjackScore() == 21)
						{
							gameBoard.setPlayerWonOrLoss(currPlayer, 2);
							String gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"2"+","+currPlayer.getMoney()+",";
							this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
						}
						else
						{
							gameBoard.setPlayerWonOrLoss(currPlayer, 0);
							String gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+",";
							this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
						}
					}
					if(currPlayer.getBlackjackScore() == 21)
					{
						gameBoard.setPlayerWonOrLoss(currPlayer, 1);
						String gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"1"+","+currPlayer.getMoney()+",";
						this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
					}
				}
			}
				
		}
		else if(msg.startsWith("Hit"))
		{
			int Id = Integer.parseInt(msg.split(",")[1]);
			gameBoard.dealPlayerCard(Id);
			Player currPlayer = gameBoard.getPlayerByID(Id);
			if(currPlayer.getBlackjackScore() > 21)
			{
				gameBoard.setPlayerWonOrLoss(currPlayer, 0);
				String gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+",";
				this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
			}
			String addCards = "Hit,"+Id+",";
			ArrayList<String> cards = gameBoard.getPlayerByID(Id).showHand();
			for(int i =0; i < cards.size(); i++)
			{
				addCards+=cards.get(i)+",";
			}
			sendActionsToEveryone(addCards);
		}
		else if(msg.startsWith("Stand"))
		{
			int Id = Integer.parseInt(msg.split(",")[1]);
			
			idxTurn++;
			if(idxTurn >= numPlayersReady.size())
			{
				//dealer goes
				boolean dealerWentOver = false;
				while(gameBoard.dealer.getBlackJackScore() <= 16)
				{
					gameBoard.dealDealerCard();
				}
				gameBoard.dealer.nextTurn();
				ArrayList<Card> dealerCards = gameBoard.dealer.cardsToShow();
				String showDealerCards = "Dealer,";
				for(int i =0;i < dealerCards.size(); i++)
				{
					showDealerCards+=dealerCards.get(i).getImageString()+",";
				}
				sendActionsToEveryone(showDealerCards);
				if(gameBoard.dealer.getBlackJackScore() > 21)
				{
					for(int i = 0; i < this.numPlayersReady.size(); i++)
					{
						gameBoard.setPlayerWonOrLoss(numPlayersReady.get(i),1);
						String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+numPlayersReady.get(i).getMoney()+",";
						sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
					}
					dealerWentOver=true;
				}
				if(!dealerWentOver)
				{
					for(int i = 0; i < numPlayersReady.size();i++)
					{
						Player currPlayer = numPlayersReady.get(i);
						if(currPlayer.getBlackjackScore() > gameBoard.dealer.getBlackJackScore())
						{
							gameBoard.setPlayerWonOrLoss(currPlayer, 1);
							String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+currPlayer.getMoney()+",";
							sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
						}
						else if(currPlayer.getBlackjackScore() < gameBoard.dealer.getBlackJackScore())
						{
							gameBoard.setPlayerWonOrLoss(currPlayer, 0);
							String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"0"+","+currPlayer.getMoney()+",";
							sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
						}
						else
						{
							gameBoard.setPlayerWonOrLoss(currPlayer, 2);
							String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"2"+","+currPlayer.getMoney()+",";
							sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
						}
					}
				}
			}
			else
			{
				int newPlayersTurn = numPlayersReady.get(idxTurn).getPlayerNum();
				String clientAction = "Stand,"+newPlayersTurn+",";
				sendActionsToEveryone(clientAction);
			}
			
		}
		else if(msg.startsWith("PlayAgain"))
		{
			String[] arguments = msg.split(",");
			int Id = Integer.parseInt(arguments[1]);
			double newBet = Double.parseDouble(arguments[2]);
			gameBoard.getPlayerByID(Id).setCurrBet(newBet);
			//go through the players and clear their cards
			//clear the dealers cards
			//call the start game
		}
		else if(msg.startsWith("Forfeit"))
		{
			//give half of the bet back and go to the next player
		}
		
	}
	private void sendActionsToEveryone(String msg)
	{
		//System.out.println("server sending: "+msg);
		for(int i = 0; i < gameBoard.getNumPlayers(); i++)
		{
			Player currPlayer = gameBoard.getPlayerByID(i);
			sendPackets(msg.getBytes(),currPlayer.address,currPlayer.port);
		}
	}
	
}

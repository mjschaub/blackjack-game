package server;

import java.io.IOException;
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
	private boolean gameRunning;
	private int split = 0;
	
	
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
	 * sets up the client connections and automatically pings them with a thread to make sure they haven't disconnected and if they do, to disconnect them
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
						Thread.sleep(10000);
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
								if(gameBoard.getPlayerByID(i) == numPlayersReady.get(idxTurn))
									standTurn();
								System.out.println("player has left");
								if(numPlayersReady.contains(gameBoard.getPlayerByID(i)))
								{
									numPlayersReady.remove(gameBoard.getPlayerByID(i));
								}
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
	/**
	 * sends packets to a certain address or port that is connected
	 * @param data the bytes of the string to be sent
	 * @param address ip address of the receiver
	 * @param port the port number
	 */
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

	/**
	 * parses the actions that the client has sent to the server, deals with game logic and how clients should respond to certain game events
	 * @param msg the message sent from one of the clients
	 * @param packet the packet that was sent along with it
	 */
	private void parseActions(String msg,DatagramPacket packet)
	{
		String[] arguments = msg.split(",");
		if(msg.startsWith("Connect"))
		{
			String name = arguments[2]; //parse from actionReceived
			double startingMoney = Double.parseDouble(arguments[3]);
			double currBet = Double.parseDouble(arguments[4]);
			gameBoard.addPlayer(name, startingMoney, currBet,packet.getAddress(),packet.getPort());
			String idOfPlayerAdded = "Connected,"+(gameBoard.getNumPlayers()-1)+","+startingMoney+","+currBet+",";
			sendPackets(idOfPlayerAdded.getBytes(),packet.getAddress(),packet.getPort());
			for(int i =0;i < gameBoard.getNumPlayers(); i++)
			{
				String setupPlayerForGame = "PlaceAtTable,"+i+","+gameBoard.getPlayerByID(i).getName()+",";
				sendActionsToEveryone(setupPlayerForGame);
			}
			sendActionsToEveryone("ShowReadyUp,");
			
		}
		else if(msg.startsWith("Disconnect"))
		{
			int idOfPlayerLeaving = Integer.parseInt(arguments[1]);
			int exitAfterTurn = Integer.parseInt(arguments[2]);
			Player currPlayer = gameBoard.getPlayerByID(idOfPlayerLeaving);
			if(numPlayersReady.contains(gameBoard.getPlayerByID(idOfPlayerLeaving)))
			{
				if(exitAfterTurn == 0 && gameBoard.getPlayerByID(idOfPlayerLeaving) == currPlayer)
				{
					standTurn();
					split = 0;
				}
				else if(exitAfterTurn == 1 && gameBoard.getPlayerByID(idOfPlayerLeaving) == currPlayer)
				{
					standTurn();
					split = 0;
				}
				numPlayersReady.remove(gameBoard.getPlayerByID(idOfPlayerLeaving));
			}
			
			String actionForClients = "Disconnected,"+idOfPlayerLeaving+",";
			sendActionsToEveryone(actionForClients);
			gameBoard.playerHasLeft(idOfPlayerLeaving);
			
		}
		else if(msg.startsWith("Ping"))
			playerPings.add(msg.split(",")[1]);
		else if(msg.startsWith("ReadyToPlay"))
			startGameLogic(msg);	
		else if(msg.startsWith("Hit"))
			hitHand(msg);
		else if(msg.startsWith("Stand"))
		{
			int Id = Integer.parseInt(msg.split(",")[1]);
			if(split == 1)
			{
				String gameState = "GameState,You are now playing your right hand!";
				sendPackets(gameState.getBytes(),gameBoard.getPlayerByID(Id).address, gameBoard.getPlayerByID(Id).port);
			}
			standTurn();
			
		}
		else if(msg.startsWith("PlayAgain"))
			playAgain(arguments);
		else if(msg.startsWith("Forfeit"))
			hasForfeit(arguments);
		else if(msg.startsWith("Split"))
			hasSplit(arguments);
		else if(msg.startsWith("DoubleDown"))
			doubleDown(arguments);
		
	}
	/**
	 * starts up the game and deals with everyone readying up, then deals cards to everyone
	 * @param msg
	 */
	private void startGameLogic(String msg) 
	{
		int Id = Integer.parseInt(msg.split(",")[1]);
		this.numPlayersReady.add(gameBoard.getPlayerByID(Id));
		String removeReadyUpButton = "RemoveReadyUp,";
		sendPackets(removeReadyUpButton.getBytes(),gameBoard.getPlayerByID(Id).address,gameBoard.getPlayerByID(Id).port);
		String changeLabel = "GameState,Waiting for others to ready up!,";
		sendPackets(changeLabel.getBytes(),gameBoard.getPlayerByID(Id).address,gameBoard.getPlayerByID(Id).port);
		System.out.println(gameBoard.getNumPlayers());
		if(numPlayersReady.size() == gameBoard.getNumPlayers())
		{
			gameRunning = true;
			int canDoubleDown = 0;
			int canSplit = 0;
			gameBoard.shuffle();
			gameBoard.dealDealerCard();
			gameBoard.dealDealerCard();
			gameBoard.turn = numPlayersReady.get(0).getPlayerNum();
			idxTurn = 0;
			if(numPlayersReady.get(idxTurn).getCurrBet()*2 <= numPlayersReady.get(idxTurn).getMoney())
			{
				canDoubleDown = 1;
				canSplit = 1;
			}
			String startGame = "StartGame,"+canDoubleDown+","+gameBoard.turn+","+gameBoard.dealer.cardsToShow().get(0).getImageString()+",";
			for(int i = 0; i < numPlayersReady.size(); i++)
			{
				gameBoard.dealPlayerCard(numPlayersReady.get(i).getPlayerNum(),0);
				gameBoard.dealPlayerCard(numPlayersReady.get(i).getPlayerNum(),0);
				if(i == 0)
				{
					canSplit *= numPlayersReady.get(i).canPlayerSplit();
					startGame+=canSplit+",";
				}
				startGame += numPlayersReady.get(i).getPlayerNum()+","+numPlayersReady.get(i).showHand(0).get(0)+","+numPlayersReady.get(i).showHand(0).get(1)+",";
				
			}
			sendActionsToEveryone(startGame);
			canDoubleDown = 0;
			canSplit = 0;
			for(int i = 0; i < numPlayersReady.size(); i++)
			{
				Player currPlayer = gameBoard.getPlayerByID(numPlayersReady.get(i).getPlayerNum());
				if(gameBoard.dealer.getBlackJackScore() == 21)
				{	
					if(currPlayer.getBlackjackScore(0) == 21)
					{
						gameBoard.setPlayerWonOrLoss(currPlayer, 2,split);
						gameBoard.dealer.nextTurn();
						ArrayList<Card> dealerCards = gameBoard.dealer.cardsToShow();
						String showDealerCards = "Dealer,";
						for(int j =0;j < dealerCards.size(); j++)
						{
							showDealerCards+=dealerCards.get(j).getImageString()+",";
						}
						sendPackets(showDealerCards.getBytes(),currPlayer.address,currPlayer.port);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						String gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"2"+","+currPlayer.getMoney()+","+split+",";;
						this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
					}
					else
					{
						gameBoard.setPlayerWonOrLoss(currPlayer, 0,split);
						gameBoard.dealer.nextTurn();
						ArrayList<Card> dealerCards = gameBoard.dealer.cardsToShow();
						String showDealerCards = "Dealer,";
						for(int j =0;j < dealerCards.size(); j++)
						{
							showDealerCards+=dealerCards.get(j).getImageString()+",";
						}
						sendPackets(showDealerCards.getBytes(),currPlayer.address,currPlayer.port);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						String gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+split+",";;
						this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
					}
				}
				if(currPlayer.getBlackjackScore(0) == 21)
				{
					gameBoard.setPlayerWonOrLoss(currPlayer, 1, split);
					String gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"1"+","+currPlayer.getMoney()+","+split+",";;
					this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
					if(numPlayersReady.get(idxTurn) == currPlayer)
						standTurn();
				}
			}
		}
	}
	/**
	 * Hits for the current hand, dealing it a new card
	 * @param msg
	 */
	private void hitHand(String msg) 
	{
		int Id = Integer.parseInt(msg.split(",")[1]);
		gameBoard.dealPlayerCard(Id,split);
		Player currPlayer = gameBoard.getPlayerByID(Id);
		
		String addCards = "Hit,"+Id+","+split+",";
		ArrayList<String> cards = gameBoard.getPlayerByID(Id).showHand(split);
		for(int i =0; i < cards.size(); i++)
		{
			addCards+=cards.get(i)+",";
		}
		sendActionsToEveryonePlaying(addCards);
		if(currPlayer.getBlackjackScore(split) > 21)
		{
			
			gameBoard.setPlayerWonOrLoss(currPlayer, 0,split);
			String gameEnd;
			if(split == 0)
				gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"0"+",";
			else if(split == 1)
			{
				gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"1"+",";
				String gameState = "GameState,You are now playing your right hand!";
				sendPackets(gameState.getBytes(),gameBoard.getPlayerByID(Id).address, gameBoard.getPlayerByID(Id).port);
			}
			else
			{
				if(currPlayer.leftHand.getCardCount() == 0)
					gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"2"+",";
				else
					gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"4"+",";
			}
			this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
			standTurn();
			if(numPlayersReady.contains(gameBoard.getPlayerByID(Id)) && split == 0)
				numPlayersReady.remove(gameBoard.getPlayerByID(Id));
			
		}
	}
	/**
	 * Replays the game and clears the board for the next game
	 * @param arguments
	 */
	private void playAgain(String[] arguments) 
	{
		int Id = Integer.parseInt(arguments[1]);
		double newBet = Double.parseDouble(arguments[2]);
		gameBoard.getPlayerByID(Id).setCurrBet(newBet);
		gameBoard.dealer.clearHand();
		gameBoard.shuffle();
		
		String playAgain = "PlayAgain,"+gameBoard.getPlayerByID(Id).getCurrBet()+",";
		sendPackets(playAgain.getBytes(), gameBoard.getPlayerByID(Id).address, gameBoard.getPlayerByID(Id).port);
		
		if(numPlayersReady.contains(gameBoard.getPlayerByID(Id)))
		{
			numPlayersReady.remove(gameBoard.getPlayerByID(Id));
		}
		for(int i = 0; i <gameBoard.getNumPlayers(); i++)
		{
			if(!numPlayersReady.contains(gameBoard.getPlayerByID(i)))
				sendPackets("ShowReadyUp,".getBytes(), gameBoard.getPlayerByID(Id).address, gameBoard.getPlayerByID(Id).port);
		}
	}
	/**
	 * Deals with the logic when a player forfeits a hand
	 * @param arguments
	 */
	private void hasForfeit(String[] arguments) {
		int Id = Integer.parseInt(arguments[1]);
		Player currPlayer = gameBoard.getPlayerByID(Id);
		currPlayer.setCurrBet(.5*currPlayer.getCurrBet());
		gameBoard.setPlayerWonOrLoss(currPlayer, 0,split);
		standTurn();
		if(numPlayersReady.contains(gameBoard.getPlayerByID(Id)))
			numPlayersReady.remove(gameBoard.getPlayerByID(Id));
		
		String end;
		if(split == 0)
			end = "EndGame,"+currPlayer.getPlayerNum()+","+"3"+","+currPlayer.getMoney()+","+"0"+",";
		else if(split == 1)
		{
			end = "EndGame,"+currPlayer.getPlayerNum()+","+"3"+","+currPlayer.getMoney()+","+"1"+",";
			String gameState = "GameState,You are now playing your right hand!";
			sendPackets(gameState.getBytes(),gameBoard.getPlayerByID(Id).address, gameBoard.getPlayerByID(Id).port);
		}
		else
		{
			if(currPlayer.leftHand.getCardCount() == 0)
				end = "EndGame,"+currPlayer.getPlayerNum()+","+"3"+","+currPlayer.getMoney()+","+"2"+",";
			else
				end = "EndGame,"+currPlayer.getPlayerNum()+","+"3"+","+currPlayer.getMoney()+","+"4"+",";
		}
		this.sendPackets(end.getBytes(), currPlayer.address, currPlayer.port);
	}
	/**
	 * deals with the logic when a player splits their hand into two
	 * @param arguments
	 */
	private void hasSplit(String[] arguments) 
	{
		int Id = Integer.parseInt(arguments[1]);
		Player currPlayer = gameBoard.getPlayerByID(Id);
		currPlayer.setCurrBet(2*currPlayer.getCurrBet());
		currPlayer.leftHand.addCard(currPlayer.currHand.getCard(0));
		currPlayer.rightHand.addCard(currPlayer.currHand.getCard(1));
		currPlayer.emptyNormalHand();
		gameBoard.dealPlayerCard(Id, 1);
		gameBoard.dealPlayerCard(Id, 2);
		String showSplit = "Split,"+Id+","+currPlayer.getCurrBet()+","+currPlayer.showHand(1).get(0)+","+currPlayer.showHand(1).get(1)+","
							+currPlayer.showHand(2).get(0)+","+currPlayer.showHand(2).get(1)+",";
		sendActionsToEveryonePlaying(showSplit);
		split = 1;
	}
	/**
	 * deals with the logic of when a player doubles down
	 * @param arguments
	 */
	private void doubleDown(String[] arguments) 
	{
		int Id = Integer.parseInt(arguments[1]);
		Player currPlayer = gameBoard.getPlayerByID(Id);
		currPlayer.setCurrBet(2*currPlayer.getCurrBet());
		gameBoard.dealPlayerCard(Id,split);
		ArrayList<String> cards;
		String addCards ="Hit,"+Id+","+split+",";
		if(split == 1)
		{
			String gameState = "GameState,You are now playing your right hand!";
			sendPackets(gameState.getBytes(),gameBoard.getPlayerByID(Id).address, gameBoard.getPlayerByID(Id).port);
		}
		cards = gameBoard.getPlayerByID(Id).showHand(split);
		for(int i =0; i < cards.size(); i++)
		{
			addCards+=cards.get(i)+",";
		}
		sendActionsToEveryonePlaying(addCards);
		if(currPlayer.getBlackjackScore(split) > 21)
		{
			
			gameBoard.setPlayerWonOrLoss(currPlayer, 0,split);
			String gameEnd;
			if(split == 0)
				gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"0"+",";
			else if(split == 1)
				gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"1"+",";
			else
			{
				if(currPlayer.leftHand.getCardCount() == 0)
					gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"2"+",";
				else
					gameEnd = "EndGame,"+currPlayer.getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+"4"+",";
			}
			this.sendPackets(gameEnd.getBytes(), currPlayer.address, currPlayer.port);
			standTurn();
			if(numPlayersReady.contains(gameBoard.getPlayerByID(Id)) && split == 0)
				numPlayersReady.remove(gameBoard.getPlayerByID(Id));
			
		}
		else
			standTurn();
	}
	/**
	 * stands the turn for the current player, then dealing with end game scenarios if its now the dealer's turn
	 */
	private void standTurn() 
	{
		if(split == 1)
		{
			split++;
			return;
		}
		else if(split == 2)
		{
			split = 0;
		}
		idxTurn++;
		if((idxTurn >= numPlayersReady.size()))
		{
			//dealer goes after everyone
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
			sendActionsToEveryonePlaying(showDealerCards);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(gameBoard.dealer.getBlackJackScore() > 21)
			{
				for(int i = 0; i < this.numPlayersReady.size(); i++)
				{
					if(numPlayersReady.get(i).currHand.getCardCount() != 0)
					{
						gameBoard.setPlayerWonOrLoss(numPlayersReady.get(i),1,0);
						String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+numPlayersReady.get(i).getMoney()+","+"0,";
						sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
					}
					else
					{
						if(numPlayersReady.get(i).leftHand.getCardCount() != 0)
						{
							if(numPlayersReady.get(i).rightHand.getCardCount() != 0)
							{
								gameBoard.setPlayerWonOrLoss(numPlayersReady.get(i),1,1);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+numPlayersReady.get(i).getMoney()+","+"1,";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
							else
							{
								gameBoard.setPlayerWonOrLoss(numPlayersReady.get(i),1,1);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+numPlayersReady.get(i).getMoney()+","+"3,";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
						}
						if(numPlayersReady.get(i).rightHand.getCardCount() != 0)
						{
							gameBoard.setPlayerWonOrLoss(numPlayersReady.get(i),1,2);
							String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+numPlayersReady.get(i).getMoney()+","+"2,";
							sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
						}
					}
				}
				dealerWentOver=true;
			}
			if(!dealerWentOver)
			{
				for(int i = 0; i < numPlayersReady.size();i++)
				{
					Player currPlayer = numPlayersReady.get(i);
					for(int j = 0; j < 3; j++)
					{
						if(currPlayer.getHand(j).getCardCount() == 0)
							continue;
						if(currPlayer.getBlackjackScore(j) > gameBoard.dealer.getBlackJackScore())
						{
							if(j == 1 && currPlayer.rightHand.getCardCount() == 0)
							{
								gameBoard.setPlayerWonOrLoss(currPlayer, 1,j);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+currPlayer.getMoney()+","+3+",";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
							else
							{
								gameBoard.setPlayerWonOrLoss(currPlayer, 1,j);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"1"+","+currPlayer.getMoney()+","+j+",";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
						}
						else if(currPlayer.getBlackjackScore(j) < gameBoard.dealer.getBlackJackScore())
						{
							if(j == 1 && currPlayer.rightHand.getCardCount() == 0)
							{
								gameBoard.setPlayerWonOrLoss(currPlayer, 0,j);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+3+",";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
							else
							{
								gameBoard.setPlayerWonOrLoss(currPlayer, 0,j);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"0"+","+currPlayer.getMoney()+","+j+",";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
						}
						else
						{
							if(j == 1 && currPlayer.rightHand.getCardCount() == 0)
							{
								gameBoard.setPlayerWonOrLoss(currPlayer, 2,j);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"2"+","+currPlayer.getMoney()+","+3+",";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
							else
							{
								gameBoard.setPlayerWonOrLoss(currPlayer, 2,j);
								String gameEnd = "EndGame,"+numPlayersReady.get(i).getPlayerNum()+","+"2"+","+currPlayer.getMoney()+","+j+",";
								sendPackets(gameEnd.getBytes(), numPlayersReady.get(i).address, numPlayersReady.get(i).port);
							}
						}
					}
				}
			}
		}
		else
		{
			int canDoubleDown = 0;
			int canSplit = 0;
			int newPlayersTurn = numPlayersReady.get(idxTurn).getPlayerNum();
			if(numPlayersReady.get(idxTurn).getCurrBet()*2 <= numPlayersReady.get(idxTurn).getMoney())
			{
				canDoubleDown = 1;
				canSplit = 1;
			}
			canSplit *= numPlayersReady.get(idxTurn).canPlayerSplit();
			String clientAction = "Stand,"+newPlayersTurn+","+canDoubleDown+","+canSplit+",";
			sendActionsToEveryonePlaying(clientAction);
		}
	}
	/**
	 * Has the server send actions to every client connected currently
	 * @param msg the message it is sending
	 */
	private void sendActionsToEveryone(String msg)
	{
		
		for(int i = 0; i < gameBoard.getNumPlayers(); i++)
		{
			Player currPlayer = gameBoard.getPlayerByID(i);
			sendPackets(msg.getBytes(),currPlayer.address,currPlayer.port);
		}
	}
	/**
	 * Has the server send actions to every client playing currently
	 * @param msg the message it is sending
	 */
	private void sendActionsToEveryonePlaying(String msg)
	{
		
		for(int i = 0; i < numPlayersReady.size(); i++)
		{
			Player currPlayer = numPlayersReady.get(i);
			sendPackets(msg.getBytes(),currPlayer.address,currPlayer.port);
		}
	}
	
}

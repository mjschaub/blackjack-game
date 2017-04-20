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

/**
 * the Server class that handles all of the networking for the server of the blackjack game
 * @author mjschaub
 *
 */
public class Server implements Runnable 
{

	public Board gameBoard;
	private ArrayList<String> playerPings = new ArrayList<String>();
	
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
	/*
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
	/*private void sendPackets(Object obj, final InetAddress address, final int port)
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		final byte[] data = baos.toByteArray();
		
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
	}*/
	
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
			String setupPlayerForGame = "PlaceAtTable,"+(gameBoard.getNumPlayers()-1)+","+name+",";
			sendActionsToEveryone(setupPlayerForGame);
			
		}
		else if(msg.startsWith("Disconnect"))
		{
			String[] arguments = msg.split(",");
			int idOfPlayerLeaving = Integer.parseInt(arguments[1]);
			gameBoard.playerHasLeft(idOfPlayerLeaving);
			String actionForClients = "Disconnected,"+idOfPlayerLeaving+",";
			sendActionsToEveryone(actionForClients);
			
		}
		else if(msg.startsWith("Ping"))
		{
			playerPings.add(msg.split(",")[1]);
			System.out.println("added ping to playerPings for: "+playerPings.get(0));
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

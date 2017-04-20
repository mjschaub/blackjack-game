/**
 * 
 */
package server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import players.Player;



/**
 * @author mjschaub
 *
 */
public class Client 
{
	//public Player clientPlayer;
	private DatagramSocket socket;
	private InetAddress ip;
	private Thread sendingThread;
	
	public Client()
	{
		
	}
	/**
	 * opens a connection for the given client
	 * @return true if connection is successful or false if not
	 */
	public boolean openConnection()
	{
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName("127.0.0.1");
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	/**
	 * receives the packet for each of the clients
	 * @return
	 */
	public String receivePacket()
	{
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		
		try {
			socket.receive(packet); //freezes the process until it receives an action
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream is;
		Object obj = null;
		try {
			is = new ObjectInputStream(new BufferedInputStream(byteStream));

			while(is.available() > 0)
				obj = is.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		String action = new String(packet.getData());
		return action;
		
	}
	/**
	 * sends the packet given the data in bytes of the string action
	 * @param data the bytes array of the string action
	 */
	public void sendPacket(final byte[] data)
	{
		sendingThread = new Thread("send")
		{
			public void run()
			{
				DatagramPacket packet = new DatagramPacket(data,data.length,ip,8000);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendingThread.start();
	}
	/*public void setPlayer(Player client)
	{
		this.clientPlayer = client;
		System.out.println("set player: "+client+" to client");
	}*/
	public void closeConnection()
	{
		new Thread()
		{
			public void run()
			{
				synchronized (socket)
				{
					socket.close();
				}
			}
		}.start();
	}
	
	
	
}

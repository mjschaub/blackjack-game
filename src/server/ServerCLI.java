package server;

/**
 * the class to be run on the command line to run the server
 * @author mjschaub
 *
 */
public class ServerCLI 
{
	public Server server;

	
	/**
	 * the main method to start up the server and allow for incoming connections
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length != 0)
		{
			System.out.println("Usage: Java -jar Blackjack");
			return;
		}
		new ServerCLI();
		
	}
	/**
	 * the constructor for the server startup class which creates a new server on port 8000
	 */
	public ServerCLI()
	{
		server = new Server(8000);
	}
}

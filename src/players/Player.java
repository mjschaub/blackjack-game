package players;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

import board.Card;
import board.Hand;

/**
 * @author mjschaub
 * the Player class
 */
public class Player
{

	private int playerNum;
	private double money;
	private double currBet;
	private int hasWon; //0 for lost, 1 for win, 2 for tie
	private Hand currHand;
	public String name;
	public int numAttempts = 0;
	public int port;
	public InetAddress address;

	/**
	 * the player constructor that initializes anything a player needs
	 * @param playerNum the player id number
	 * @param name the player's name entered
	 * @param startingMoney the player's starting money
	 * @param currBet the player's first bet
	 */
	public Player(int playerNum, String name, double startingMoney, double currBet,InetAddress address, int port)
	{
		this.playerNum = playerNum;
		this.money = startingMoney;
		this.currBet = currBet;
		this.hasWon = 0;
		this.currHand = new Hand();
		this.name = name;
		this.address = address;
		this.port = port;
		
	}
	/**
	 * changes the money amount of the player depending on if the player won/lost/tied
	 */
	public void changeMoneyAmount()
	{
		if(hasWon == 1)
			this.money+=currBet;
		else if(hasWon == 0)
			this.money-=currBet;
		this.hasWon = 0;
		currBet = 0;
	}
	/**
	 * gets the current bet amount to visualize
	 * @return the double bet amount
	 */
	public double getCurrBet() 
	{
		return currBet;
	}
	/**
	 * sets the current bet to something new for a new turn
	 * @param currBet the bet amount
	 */
	public void setCurrBet(double currBet) 
	{
		this.currBet = currBet;
	}
	/**
	 * gets the player's id number
	 * @return the integer id
	 */
	public int getPlayerNum()
	{
		return this.playerNum;
	}
	/**
	 * gets the arraylist of cards the player has to visualize
	 * @return
	 */
	public ArrayList<String> showHand()
	{
		return this.currHand.showCards();
	}
	/**
	 * gets the amount of total money a player has to visualize
	 * @return the double money amount
	 */
	public double getMoney()
	{
		return this.money;
	}
	/**
	 * overrides the toString method to instead print out the player's name
	 * @return the String name
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * gets the blackjack score of the hand they currently have
	 * @return the integer value of the score
	 */
	public int getBlackjackScore()
	{
		return this.currHand.getBlackjacktotalValue();
	}

	/**
	 * is called when a player hits or requests another card to be dealt to their hand
	 * @param newCard the new card to add
	 */
	public void hit(Card newCard)
	{
		currHand.addCard(newCard);
	}
	/**
	 * empties the hand and prepares the player to get a new bet
	 */
	public void prepForNewGame()
	{
		currHand.emptyHand();
		this.changeMoneyAmount();
	}
	/**
	 * sets whether this player has won/lost/tied the current game
	 * @param num the integer representation of this
	 */
	public void setWon(int num)
	{
		this.hasWon = num;
	}
	public int canPlayerSplit()
	{
		if(this.currHand.isAbleToSplit())
			return 1;
		return 0;
	}

	
}

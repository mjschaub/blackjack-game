/**
 * 
 */
package board;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

import players.Dealer;
import players.Player;

/**
 * @author mjschaub
 * the class for board logic and the game loop
 */
public class Board
{
	
	public Dealer dealer;
	public ArrayList<Player> players;
	private Deck gameDeck;
	public int turn; // -1 for dealer, player id from 0 - numPlayers
	private int numPlayers;
	private boolean isRunning;
	/**
	 * the board constructor to initialize the player ArrayList and the dealer
	 */
	public Board()
	{
		players = new ArrayList<Player>();
		dealer = new Dealer();
		gameDeck = new Deck();
		turn = 0;
		isRunning=true;
	}
	/**
	 * runs the game loop and end game logic for if a player will win or not and lets a player restart the game
	 */
	public void runGameLoop()
	{
		do
		{
			gameDeck.shuffle();
			dealDealerCard();
			dealDealerCard();
			for(int i = 0; i < numPlayers;i++)
			{
				Player currPlayer = this.getPlayerByID(i);
				dealPlayerCard(i,0);
				dealPlayerCard(i,0);
				if(dealer.getBlackJackScore() == 21)
				{	
					if(currPlayer.getBlackjackScore(0) == 21)
					{
						setPlayerWonOrLoss(currPlayer, 2,0);
					}
					else
					{
						setPlayerWonOrLoss(currPlayer, 0,0);
					}
				}
				if(currPlayer.getBlackjackScore(0) == 21)
				{
					setPlayerWonOrLoss(currPlayer, 1,0);
				}
				//show user his/her own and dealer cards
				//prompt to ask whether they want to hit or stand
				int action = 0; //connect to user depending on what he wants to do
				while(action == 0)
				{
					dealPlayerCard(i,0);
					if(currPlayer.getBlackjackScore(0) > 21)
					{
						setPlayerWonOrLoss(currPlayer, 0,0);
					}
					//prompt again
					if(action == 1)
						break;
				}
					
			}
			
			while(dealer.getBlackJackScore() <= 16)
			{
				this.dealDealerCard();
				if(dealer.getBlackJackScore() > 21)
				{
					for(int i = 0; i < numPlayers; i++)
						setPlayerWonOrLoss(getPlayerByID(i),1,0);
				}
			}
			for(int i = 0; i < numPlayers;i++)
			{
				Player currPlayer = this.getPlayerByID(i);
				if(currPlayer.getBlackjackScore(0) > dealer.getBlackJackScore())
				{
					setPlayerWonOrLoss(currPlayer, 1,0);
				}
				else if(currPlayer.getBlackjackScore(0) < dealer.getBlackJackScore())
				{
					setPlayerWonOrLoss(currPlayer, 0,0);
				}
				else
				{
					setPlayerWonOrLoss(currPlayer, 2,0);
				}
			}
			
			
		} while(isRunning);
		
	}
	/**
	 * adds a player to the game
	 * @param name player's name
	 * @param startingMoney player's money amount
	 * @param currBet player's bet
	 */
	public void addPlayer(String name, double startingMoney, double currBet,InetAddress address,int port)
	{
		int newPlayerNum = players.size();
		numPlayers++;
		players.add(new Player(newPlayerNum,name,startingMoney,currBet,address,port));
		System.out.println("player was created and added to game");
	}
	/**
	 * when a player leaves the game, remove them by ID
	 * @param ID the id of the player
	 */
	public void playerHasLeft(int ID)
	{
		players.remove(getPlayerByID(ID));
		numPlayers--;
	}
	/**
	 * removes a player from the game by object
	 * @param player the player object
	 */
	public void playerHasLeft(Player player)
	{
		players.remove(player);
		numPlayers--;
	}
	/**
	 * changes the turn in the game, either letting it know it's the dealer's turn or another players
	 */
	public void changeTurns()
	{
		if(turn != -1 && turn == players.size()-1)
			turn = -1;
		else
			turn++;
	}
	/**
	 * gets player by Id
	 * @param ID the ID of the player
	 * @return the player object
	 */
	public Player getPlayerByID(int ID)
	{
		for(Player x : players)
		{
			
			if(x.getPlayerNum() == ID)
				return x;
			
		}
		return null;
	}
	/**
	 * gets the number of players in the game
	 * @return
	 */
	public int getNumPlayers()
	{
		return numPlayers;
	}
	/**
	 * gets whoever's turn it is
	 * @return the integer id of the player/dealer
	 */
	public int getTurn()
	{
		return this.turn;
	}
	/**
	 * deals a player a card for whoever's turn it is
	 * @param turn the turn it is
	 */
	public void dealPlayerCard(int turn, int hand)
	{
		Player currPlayer = getPlayerByID(turn);
		currPlayer.hit(gameDeck.dealCard(), hand);
	
	}
	/**
	 * gives the dealer a card
	 */
	public void dealDealerCard()
	{
		dealer.giveCard(gameDeck.dealCard());
	}
	/**
	 * sets the player to either win/lose/tie 
	 * @param curr the current player
	 * @param won the integer value of winning/losing/a tie
	 */
	public void setPlayerWonOrLoss(Player curr, int won, int split)
	{
		curr.setWon(won,split);
		curr.prepForNewGame(split);
	}
	/**
	 * shuffles the game board's deck
	 */
	public void shuffle()
	{
		gameDeck.shuffle();
	}
	

}

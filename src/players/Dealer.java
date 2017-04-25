/**
 * 
 */
package players;

import java.io.Serializable;
import java.util.ArrayList;

import board.Card;
import board.Hand;

/**
 * @author mjschaub
 * the Dealer class
 */
public class Dealer
{

	private Hand dealerHand;
	private int turn;
	/**
	 * the constructor for the dealer and initializes a hand and his turn to 0
	 */
	public Dealer()
	{
		this.dealerHand = new Hand();
		this.turn = 0;
	}
	/**
	 * checks to see if the dealer has 21
	 * @return
	 */
	public boolean hasBlackjack()
	{
		if(dealerHand.getBlackjacktotalValue() == 21)
			return true;
		return false;
	}
	/**
	 * gets the number of cards to show as a dealer only shows one card on the first turn
	 * @return the arraylist of cards to show
	 */
	public ArrayList<Card> cardsToShow()
	{
		if(turn == 0)
		{
			ArrayList<Card> temp = new ArrayList<Card>();
			temp.add(this.dealerHand.getCards().get(0));
			return temp;
		}
		else
		{
			return this.dealerHand.getCards();
		}
	}
	/**
	 * gets the blacjack score of the dealer
	 * @return the integer value
	 */
	public int getBlackJackScore()
	{
		return this.dealerHand.getBlackjacktotalValue();
	}
	/**
	 * is called when a player hits or requests another card to be dealt to their hand
	 * @param newCard the new card to add
	 */
	public void giveCard(Card newCard)
	{
		dealerHand.addCard(newCard);
	}
	/**
	 * makes the dealer have his second turn
	 */
	public void nextTurn()
	{
		this.turn++;
	}
	public void clearHand()
	{
		this.dealerHand.emptyHand();
	}
}

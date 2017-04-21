/**
 * 
 */
package board;

import java.io.Serializable;

/**
 * @author mjschaub
 * the deck class with logic to shuffle all the cards
 */
public class Deck implements Serializable
{

    private Card[] deck;   
    private int usedCards; 
    
    /**
     * the public constructor that creates all 52 cards in a deck and adds them to the Card array
     */
    public Deck() 
    {
       
       deck = new Card[52];
       int cardIdx = 0;
       for(int suit = 0; suit < 4; suit++)
          for(int value = 1; value < 14; value++)
             deck[cardIdx++] = new Card(value,suit);
         
       this.usedCards = 0;
    }
    /**
     * the algorithm to shuffle the cards, working from the end of the array 
     * and choosing a random spot for the card, swapping it to that position.
     * Called a fisher-yates shuffle
     */
    public void shuffle() 
    {
        for(int i = 51;i > 0;i--) 
        {
            int rand = (int)(Math.random()*(i+1));
            Card temp = this.deck[i];
            this.deck[i] = this.deck[rand];
            this.deck[rand] = temp;
        }
        usedCards = 0;
    }
    /**
     * gets the number of cards left in the deck depending on the number of used cards
     * @return the integer amount of cards left
     */
    public int cardsLeft() 
    {

        return 52 - usedCards;
    }
    /**
     * deals a card to either a player or the dealer
     * @return the card object being dealt
     */
    public Card dealCard() 
    {
        
        if(usedCards == 52)
           shuffle();
        return deck[usedCards++];
    }
    /**
     * gets the array of cards
     * @return the card objects
     */
    public Card[] getDeck()
    {
    	return this.deck;
    }
    

}


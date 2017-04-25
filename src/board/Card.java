/**
 * 
 */
package board;

import java.io.Serializable;

/**
 * @author mjschaub
 * the card class that has the logic for all 52 cards in a deck
 */
public class Card
{

	private final int suit;
	private final int value;  
	/*
	 values:
	1:  "Ace"
	2: 	"2"
	3:  "3"
	4:  "4"
	5: 	"5"
	6: 	"6"
	7: 	"7"
	8: 	"8"
	9: 	"9"
	10:	"10"
	11:	"Jack"
	12:	"Queen"
	13:	"King"
	
	suits:
	0: spades
	1: hearts
	2: diamonds
	3: clubs
	 */
   /**
    * the constructor for a card
    * @param value the number value of a card(see above)
    * @param suit the suit number(see above)
    */
	public Card(int value, int suit) 
	{
		this.value = value;
		this.suit = suit;
	}
	/**
	 * To get the image string With unicode for this specific card
	 * @return the string of the file name
	 */
	public String getImageString()
	{
		String imageString = "<html>"; //"K"+Character.toString((char)0x2660); king of spades
		
		if(value < 11 && value > 1)
			imageString+= value+"<br>";
		else if(value == 1)
			imageString+="A<br>";
		else if(value == 11)
			imageString+="J<br>";
		else if(value == 12)
			imageString+="Q<br>";
		else if(value == 13)
			imageString+="K<br>";
		if(suit == 0)
			imageString+=Character.toString((char)0x2660);
		else if(suit == 1)
			imageString+=Character.toString((char)0x2661);
		else if(suit == 2)
			imageString+=Character.toString((char)0x2662);
		else
			imageString+=Character.toString((char)0x2663);
		imageString+="</html>";
		return imageString;
	}
	/**
	 * gets a card's suit
	 * @return the integer representation of the suit of this card
	 */
	public int getSuit() 
	{
		return suit;
	}
	/**
	 * gets the type of card it is
	 * @return the integer represenation of the face of the card
	 */
	public int getValue() 
	{
		return value;
	}
	
	public int getSplitValue()
	{
		if(value < 11)
			return value;
		else if(value == 11)
			return 10;
		else if(value == 12)
			return 10;
		else
			return 10;
	}
	
}

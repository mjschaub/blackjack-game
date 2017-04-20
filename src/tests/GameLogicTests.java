/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import players.Dealer;
import players.Player;

import board.Board;
import board.Card;
import board.Deck;

/**
 * @author mjschaub
 * the tests to make sure the game logic is working as intended
 */
public class GameLogicTests 
{

	private Board testBoard;
	private Player testPlayer;
	private Dealer testDealer;
	private Deck testDeck;
	/**
	 * sets up the testing environment with a board, player, dealer, and deck
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		testBoard = new Board();
		testPlayer = new Player(0,"john",1000,20,null,0);
		testDealer = new Dealer();
		testDeck = new Deck();
	}
	/**
	 * tests the board functionality and makes sure we can do basic functionality with it that isn't involved in game loop logic
	 */
	@Test
	public void testBoard() 
	{
		testBoard.addPlayer("matt", 500, 20,null,0);
		testBoard.addPlayer("jack", 1000, 30,null,0);
		assertEquals(testBoard.getNumPlayers(), 2);
		assertNotEquals(testBoard.getPlayerByID(1),null);
		assertEquals(testBoard.getPlayerByID(1).getName(),"jack");
		assertEquals(testBoard.getTurn(),0);
		testBoard.changeTurns();
		assertEquals(testBoard.getTurn(),1);
		testBoard.changeTurns();
		assertEquals(testBoard.getTurn(),-1);
		testBoard.playerHasLeft(1);
		assertEquals(testBoard.getNumPlayers(), 1);
		
		
	}
	/**
	 * tests that player functionality is working correctly and has its hand and money amounts working
	 */
	@Test
	public void testPlayer()
	{
		Card testCardOne = new Card(8,0);
		Card testCardTwo = new Card(6,1);
		assertEquals(testPlayer.getName(), "john");
		assertEquals((int)testPlayer.getMoney(), 1000);
		testPlayer.hit(testCardOne);
		assertEquals(testPlayer.getBlackjackScore(),8);
		testPlayer.hit(testCardTwo);
		assertEquals(testPlayer.getBlackjackScore(),14);
		testPlayer.setWon(0);
		testPlayer.changeMoneyAmount();
		assertEquals((int)testPlayer.getMoney(),980);
		testPlayer.prepForNewGame();
		assertEquals(testPlayer.getBlackjackScore(), 0);
		
		
	}
	/**
	 * tests that the dealer is working properly
	 */
	@Test
	public void testDealer()
	{
		Card testCardOne = new Card(13,0);
		Card testCardTwo = new Card(1,1);
		testDealer.giveCard(testCardOne);
		testDealer.giveCard(testCardTwo);
		assertEquals(testDealer.cardsToShow().size(),1);
		assertEquals(testDealer.hasBlackjack(), true);
		assertEquals(testDealer.getBlackJackScore(), 21);
		testDealer.nextTurn();
		assertEquals(testDealer.cardsToShow().size(),2);
		
		
	}
	/**
	 * tests that the deck is being created properly and well shuffled and that dealing cards works
	 */
	@Test
	public void testDeck()
	{
		assertEquals(testDeck.getDeck().length, 52);
		assertEquals(testDeck.cardsLeft(), 52);
		Deck unShuffledDeck = new Deck();
		Card[] tempDeck = unShuffledDeck.getDeck();
		testDeck.shuffle();
		Card[] tempDeck2 = testDeck.getDeck();
		int numCardsShuffled = 0;
		for(int i = 0; i < tempDeck.length; i++)
			if(tempDeck[i].getValue() != tempDeck2[i].getValue() || tempDeck[i].getSuit() != tempDeck2[i].getSuit())
				numCardsShuffled++;
		
		assertNotEquals(numCardsShuffled, 0);
		testDeck.shuffle();
		Card[] tempDeck3 = testDeck.getDeck();
		numCardsShuffled = 0;
		for(int i = 0; i < tempDeck.length; i++)
			if(tempDeck[i].getValue() != tempDeck3[i].getValue() || tempDeck[i].getSuit() != tempDeck3[i].getSuit())
				numCardsShuffled++;
		assertNotEquals(numCardsShuffled, 0);
		testDeck.shuffle();
		Card[] tempDeck4 = testDeck.getDeck();
		numCardsShuffled = 0;
		for(int i = 0; i < tempDeck.length; i++)
			if(tempDeck[i].getValue() != tempDeck4[i].getValue() || tempDeck[i].getSuit() != tempDeck4[i].getSuit())
				numCardsShuffled++;
		assertNotEquals(numCardsShuffled, 0);
		testDeck.shuffle();
		Card[] tempDeck5 = testDeck.getDeck();
		numCardsShuffled = 0;
		for(int i = 0; i < tempDeck.length; i++)
			if(tempDeck[i].getValue() != tempDeck5[i].getValue() || tempDeck[i].getSuit() != tempDeck5[i].getSuit())
				numCardsShuffled++;
		assertNotEquals(numCardsShuffled, 0);
		Card dealtCard = testDeck.dealCard();
		Card dealtCardTwo = testDeck.dealCard();
		assertEquals(testDeck.cardsLeft(),50);
		System.out.println("two dealt cards:"+dealtCard.getValue()+","+dealtCard.getSuit()+" and "+dealtCardTwo.getValue()+","+dealtCardTwo.getSuit());
		
		
	}

}

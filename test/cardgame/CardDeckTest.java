/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardgame;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sevabaskin
 */
public class CardDeckTest {
    
    public CardDeckTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    
     @Test
     public void should_push_and_pop_cards() {
         CardDeck testCardDeck = new CardDeck(3);
         Card testCard = new Card(3);
         testCardDeck.push(testCard);
         Assert.assertEquals(3, testCardDeck.pop().getValue());
     }
    // FIXME: RETEST. Shitty test. Make sure top is same after unshift && length is ++
    @Test
    public void should_unshift_cards() {
        CardDeck testCardDeck = new CardDeck(3);
        Card testCardOne = new Card(3);
        Card testCardTwo = new Card(4);
        testCardDeck.push(testCardOne);
        testCardDeck.unshift(testCardTwo);
        testCardDeck.pop();
        Assert.assertEquals(4, testCardDeck.pop().getValue());
    }
    @Test
    public void should_throw_stackOverflowException_when_pushing_and_unshifting_a_card_into_a_full_deck() {
        CardDeck testCardDeck = new CardDeck(1);
        Card testCardOne = new Card(3);
        Card testCardTwo = new Card(4);
        testCardDeck.push(testCardOne);
        try {
            testCardDeck.push(testCardTwo);
            fail( "Missing exception" );
        } catch (StackOverflowException e){
            assertTrue(e.getMessage().equals("Card deck is full. You cannot add more cards."));
        }
        try {
            testCardDeck.unshift(testCardTwo);
            fail( "Missing exception" );
        } catch (StackOverflowException e){
            assertTrue(e.getMessage().equals("Card deck is full. You cannot add more cards."));
        }
    }

    @Test
    public void should_throw_stackUnderflowException_when_poping_from_an_empty_array() {
        CardDeck testCardDeck = new CardDeck(1);
        try {
            testCardDeck.pop();
            fail( "Missing exception" );
        } catch (StackUnderflowException e){
            assertTrue(e.getMessage().equals("Card deck is empty. Cannot draw cards from the deck."));
        }
    }
}

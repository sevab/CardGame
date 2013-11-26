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
     public void should_push_cards_and_throw_stack_overflow_exception() {
        CardDeck testCardDeck = new CardDeck(3);
        Assert.assertEquals(0, testCardDeck.getSize());
        for (int i=0; i<3; i++) {
            testCardDeck.push(new Card(i));
            Assert.assertEquals(i+1, testCardDeck.getSize());
        }
        try {
            testCardDeck.push(new Card(3));
            fail( "Missing exception" );
        } catch (StackOverflowException e){
            assertTrue(e.getMessage().equals("Card deck is full. You cannot add more cards."));
        }
     }
     @Test
     public void should_pop_cards_and_throw_stack_underflow_exception() {
        CardDeck testCardDeck = new CardDeck(3);
        for (int i=0; i<3; i++)
            testCardDeck.push(new Card(i));
        for (int i=2; i > 0; i--) {
            Assert.assertEquals(i, testCardDeck.pop().getValue());
            Assert.assertEquals(i, testCardDeck.getSize());
        }

     }

    // FIXME: RETEST. Shitty test. Make sure top is same after unshift && length is ++
    @Test
    public void should_unshift_cards() {
        CardDeck testCardDeck = new CardDeck(3);
        for (int i=0; i<3; i++) {
            testCardDeck.unshift(new Card(i));
            Assert.assertEquals(i+1, testCardDeck.getSize());
        }
        try {
            testCardDeck.push(new Card(3));
            fail( "Missing exception" );
        } catch (StackOverflowException e){
            assertTrue(e.getMessage().equals("Card deck is full. You cannot add more cards."));
        }
        for (int i=0; i<3; i++) {
           Assert.assertEquals(i, testCardDeck.pop().getValue());
           Assert.assertEquals((2-i), testCardDeck.getSize());
        }
    }
    @Test
    public void should_throw_stackOverflowException_when_pushing_and_unshifting_a_card_into_a_full_deck() {
        CardDeck testCardDeck = new CardDeck(1);
        Card testCardOne = new Card(3);
        Card testCardTwo = new Card(4);
        testCardDeck.push(testCardOne);
        try {
            testCardDeck.unshift(testCardTwo);
            fail( "Missing exception" );
        } catch (StackOverflowException e){
            assertTrue(e.getMessage().equals("Card deck is full. You cannot add more cards."));
        }
    }
}

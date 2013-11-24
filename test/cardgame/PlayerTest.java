/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardgame;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sevabaskin
 */
public class PlayerTest {
    
    public PlayerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

     @Test
     public void should_be_able_to_create_a_player_and_receive_cards() {
        Player testPlayer = new Player(3);
        Card testCardOne = new Card(3);
        testPlayer.push(testCardOne);
     }
     @Test
     public void should_throw_stackOverflowException_when_pushing_a_card_into_a_full_hand() {
        Player testPlayer = new Player(1);
        Card testCardOne = new Card(3);
        Card testCardTwo = new Card(4);
        testPlayer.push(testCardOne);
        try {
            testPlayer.push(testCardTwo);
            fail( "Missing exception" );
        } catch (StackOverflowException e){
            assertTrue(e.getMessage().equals("Player's hand is full. You cannot take more cards."));
        }
    }
}

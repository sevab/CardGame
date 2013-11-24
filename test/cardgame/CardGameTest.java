package cardgame;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sevabaskin
 */
public class CardGameTest {
    
    public CardGameTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }


    @Test
    public void should_end_the_game_when_the_player_wins_right_away() {
        CardDeck testCardDeck = new CardDeck(4);
        for (int i=0; i<4; i++)
            testCardDeck.push(new Card(3));
        CardGame testGame = new CardGame(1, 2, testCardDeck);
        testGame.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
        // Assert.assertEquals(1, testGame.whoWon().getPlayerIndex());
    }
}

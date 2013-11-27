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
        // System.out.println("Game 1:");
        // CardDeck testCardDeck = new CardDeck(4);
        // for (int i=0; i<4; i++)
        //     testCardDeck.push(new Card(3));
        // CardGame testGame = new CardGame(1, 2, testCardDeck);
        // testGame.start();
        // try {
        //     Thread.sleep(10);
        // } catch (InterruptedException e) {}
        // Assert.assertEquals(1, testGame.whoWon().getPlayerIndex());
    }
    // @Test
    // public void should_not_be_won_immediatelly_when_all_cards_are_different() {
    //     System.out.println("Game 2:");
    //     CardDeck testCardDeck = new CardDeck(4);
    //     for (int i=0; i<4; i++)
    //         testCardDeck.push(new Card(i));
    //     CardGame testGame = new CardGame(1, 2, testCardDeck);
    //     testGame.start();
    //     try {
    //         Thread.sleep(10);
    //     } catch (InterruptedException e) {}
    //     // Assert.assertEquals(1, testGame.whoWon().getPlayerIndex());
    // }
    // @Test
    // public void should_notify_other_players_to_terminate_once_someone_wins() {
    //     System.out.println("Game 3:");
    //     int num_of_players = 4;
    //     int num_of_cards = 2;
    //     CardDeck testCardDeck = new CardDeck(num_of_cards*num_of_players*2);
    //     for (int i=0; i<num_of_cards*num_of_players*2; i++)
    //         testCardDeck.push(new Card(3));
    //     CardGame testGame = new CardGame(num_of_players, num_of_cards, testCardDeck);
    //     testGame.start();
    //     try {
    //         Thread.sleep(10000);
    //     } catch (InterruptedException e) {}
    //     // Assert.assertEquals(1, testGame.whoWon().getPlayerIndex());
    // }
    // @Test
    // public void should_allow_only_one_player_to_win() {
        // it'd be good to somehow test that the first Player to fire the event is in fact the one who wins
        // Even thought that it is obvious from the logs.
        // One way is to have CardGame record all those who fired the event, and then compare the end winner
        // with the first player it has recorded to fire the event
    // }
    // @Test Game
    // public void should_end_at_some_point_when_given_enough_equal_cards_for_someone_to_win
    

}

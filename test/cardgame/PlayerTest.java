/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class PlayerTest {
    
    public PlayerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    // fix Player initializers by providing a CardGame;
    // do through an external method:
        // CardDeck testCardDeck = new CardDeck(4);
        // for (int i=0; i<5; i++)
        //     testCardDeck.push(new Card(3));
        // CardGame testCardGame = new CardGame();


     @Test
     public void should_be_able_to_accept_and_store_cards_through_the_push_method_and_throw_stackoverflowexception() {
        // init
        CardDeck initialCardDeck, discardDeck, drawDeck;
        initialCardDeck = new CardDeck(0,0);
        discardDeck = new CardDeck(1,0);
        drawDeck = new CardDeck(2,0);
        int playerIndex = 1;
        int handSize = 3;
        int strategy = 1;
        CardGame testGame = new CardGame(0, 0, initialCardDeck);
        Player player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);

        for (int i=0; i<3; i++){
            player.push(new Card(i));
            Assert.assertEquals(i+1, player.getSize());
        }

        try {
            player.push(new Card(3));
            fail( "Missing exception" );
        } catch (StackOverflowException e){
            assertTrue(e.getMessage().equals("Player's hand is full. You cannot take more cards."));
        }

        
     }
     @Test
     public void should_be_able_to_delete_cards_at_an_index_and_return_that_card() {
        CardDeck initialCardDeck = new CardDeck(0, 0);
        CardDeck discardDeck = new CardDeck(1, 0);
        CardDeck drawDeck = discardDeck; //= new CardDeck(0);
        int playerIndex = 1;
        int handSize = 3;
        int strategy = 1;
        CardGame testGame = new CardGame(0, 0, initialCardDeck);
        Player player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);

        for (int i=0; i<3; i++)
            player.push(new Card(i));

        for (int i=2; i >= 0; i--) {
            Assert.assertEquals(i, player.delete_at(i).getValue());
            Assert.assertEquals(i, player.getSize());
        }
        // attempt deleting from an empty array
        try {
            player.delete_at(0);
            fail( "Missing exception" );
        } catch (StackUnderflowException e){
            assertTrue(e.getMessage().equals("Player's hands are empty. Nothing to delete."));
        }
        // attempt deleting from an empty index
        player.push(new Card(1)); // make sure player's hands aren't empty
        try {
            player.delete_at(1);
            fail( "Missing exception" );
        } catch (Exception e){
            assertTrue(e.getMessage().equals("Nothing exists at this index."));
        }
        // Deletes the last item well:
        handSize = 1;
        player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);
        Assert.assertEquals(0, player.getSize());
        player.push(new Card(1));
        Assert.assertEquals(1, player.getSize());
        player.delete_at(0);
        Assert.assertEquals(0, player.getSize());
     }

    @Test
    public void should_tell_if_has_a_winning_combination_of_cards() {
        CardDeck initialCardDeck = new CardDeck(0,0);
        CardDeck discardDeck = new CardDeck(1,0);
        CardDeck drawDeck = discardDeck; //= new CardDeck(0);
        int playerIndex = 1;
        int handSize = 3;
        int strategy = 1;
        CardGame testGame = new CardGame(0, 0, initialCardDeck);
        Player player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);

        assertFalse(player.hasWinningCombo() == true);

        // filling up with equal, but unpreffered cards, doesn't count as winning
        for (int i=0; i<3; i++)
            player.push(new Card(i));
        assertFalse(player.hasWinningCombo() == true);

        // filling up only partially with equal and preffered cards doesn't count eaither
        player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);
        for (int i=0; i<1; i++)
            player.push(new Card(1));
        assertFalse(player.hasWinningCombo() == true);
        // should win when have hand-1 of prefered cards (extra slot left for intermediate card)
        player.push(new Card(1));
        assertTrue(player.hasWinningCombo() == true);
        // when the hand is full of preferred cards (incl. intermediate slot), the combo doesn't count
        player.push(new Card(1));
        assertFalse(player.hasWinningCombo() == true);
     }
    @Test
    public void should_discard_cards_as_expected_according_to_strategy_1() {
        // should not allow discarding cards when not full
        CardDeck initialCardDeck = new CardDeck(0,0);
        CardDeck discardDeck = new CardDeck(1,0);
        CardDeck drawDeck = discardDeck; //= new CardDeck(0);
        int playerIndex = 1;
        int handSize = 3;
        int strategy = 1;
        CardGame testGame = new CardGame(0, 0, initialCardDeck);
        Player player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);

        for (int i=0; i<2; i++)
            player.push(new Card(1));

        try {
            player.discardACard();
            fail( "Missing exception" );
        } catch (Exception e){
            assertTrue(e.getMessage().equals("Can only discard cards when the hand is full."));
        }
        // should discard a preffered card when full full of preffered cards
        player.push(new Card(1));
        // System.out.println(player.discardACard());
        Assert.assertEquals(3, player.getSize());
        Assert.assertEquals(1, player.discardACard().getValue());
        Assert.assertEquals(2, player.getSize());
        // should discard first instance of unpreffered card
        player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);
        for (int i=0; i<3; i++)
            player.push(new Card(i));
        Assert.assertEquals(3, player.getSize());
        Assert.assertEquals(0, player.discardACard().getValue());
        Assert.assertEquals(2, player.getSize());
        // same test, different combo (useless test):
        player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);
        player.push(new Card(1));
        player.push(new Card(1));
        player.push(new Card(2));
        Assert.assertEquals(3, player.getSize());
        Assert.assertEquals(2, player.discardACard().getValue());
        Assert.assertEquals(2, player.getSize());
        // same test, different combo (useless test):
        player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);
        player.push(new Card(1));
        player.push(new Card(9));
        player.push(new Card(1));
        Assert.assertEquals(3, player.getSize());
        Assert.assertEquals(9, player.discardACard().getValue());
        Assert.assertEquals(2, player.getSize());
        // same test, different combo (useless test):
        player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);
        player.push(new Card(1));
        player.push(new Card(1));
        player.push(new Card(8));
        Assert.assertEquals(3, player.getSize());
        Assert.assertEquals(8, player.discardACard().getValue());
        Assert.assertEquals(2, player.getSize());
        // try discarding from a 1-size player (+1 interim slot)
        player = new Player(testGame, playerIndex, 2, strategy, drawDeck, discardDeck);
        player.push(new Card(1));
        player.push(new Card(2));
        Assert.assertEquals(2, player.getSize());
        Assert.assertEquals(2, player.discardACard().getValue());
        Assert.assertEquals(1, player.getSize());

    }
    // public void should_wait_when_a_deck_is_empty
    @Test
    public void should_wait_and_try_again_if_a_deck_is_empty_when_discarding() {
        CardDeck initialCardDeck = new CardDeck(0,0);
        CardDeck discardDeck = new CardDeck(1,1);
        CardDeck drawDeck = new CardDeck(2,1);
        int playerIndex = 1;
        int handSize = 3;
        int strategy = 1;
        CardGame testGame = new CardGame(0, 0, initialCardDeck);
        Player player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);

        for (int i=0; i<2; i++)
            player.push(new Card(i));

        player.start();
        Assert.assertEquals(2, player.getSize());
        Assert.assertEquals(0, discardDeck.getSize());
        // Assert.assertEquals(0, drawDeck.getSize());
        drawDeck.push(new Card(2));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
        Assert.assertEquals(2, player.getSize());
        // Assert.assertEquals(0, drawDeck.getSize()); // card taken
        Assert.assertEquals(1, discardDeck.getSize());
    }
    @Test
    public void should_fire_a_player_won_event_and_terminate_the_game_when_wins() {
        System.out.println("Test 6/7");
        CardDeck initialCardDeck = new CardDeck(0,0);
        CardDeck discardDeck = new CardDeck(1,2);
        CardDeck drawDeck = new CardDeck(2,2);
        int playerIndex = 1;
        int handSize = 3;
        int strategy = 1;
        CardGame testGame = new CardGame(0, 0, initialCardDeck);
        Player player = new Player(testGame, playerIndex, handSize, strategy, drawDeck, discardDeck);

        player.push(new Card(0));
        player.push(new Card(0));

        player.start();
        Assert.assertEquals(2, player.getSize());
        Assert.assertEquals(0, discardDeck.getSize());
        assertFalse(testGame.isOver());
        drawDeck.push(new Card(1));
        drawDeck.push(new Card(1));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        Assert.assertEquals(2, player.getSize());
        Assert.assertEquals(0, drawDeck.getSize()); // cards taken
        Assert.assertEquals(2, discardDeck.getSize());
        assertTrue(testGame.isOver());
    }
    @Test
    public void should_not_interfere_with_other_player_threads() {
        System.out.println("Test 7/7");
        CardDeck initialCardDeck = new CardDeck(0,0);
        CardDeck discardDeck = new CardDeck(1,2); // discard Deck for player2, but plays role of a draw deck for player1
        CardDeck drawDeck = new CardDeck(2,2);    // draw Deck for player2, but plays a role of a discard deck for player1
        int playerIndex = 1;
        int handSize = 3;
        int strategy = 1;
        CardGame testGame = new CardGame(0, 0, initialCardDeck);
        Player player1 = new Player( testGame, 2, handSize, strategy, drawDeck, discardDeck );
        Player player2 = new Player( testGame, 1, handSize, strategy, discardDeck, drawDeck );

        // initialize both players with unrelated cards
        player1.push(new Card(0));
        player1.push(new Card(0));
        player2.push(new Card(0));
        player2.push(new Card(0));
        
        // start both players
        player1.start();
        player2.start();

        // assert
        Assert.assertEquals(2, player1.getSize());
        Assert.assertEquals(2, player2.getSize());
        Assert.assertEquals(0, discardDeck.getSize());
        Assert.assertEquals(0, drawDeck.getSize());
        assertFalse(testGame.isOver());

        // push two cards preffered only by player2
        drawDeck.push(new Card(2));
        drawDeck.push(new Card(2));

        // wait till the game ends
        while(!testGame.isOver()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}    
        }
        // send out shutdown notices once the game is over
        player1.gameOverEventHandler( new GameOverEvent(testGame) );
        player2.gameOverEventHandler( new GameOverEvent(testGame) );
        Assert.assertEquals(2, testGame.getWinner().getPlayerIndex());
        // drawDeck.push(new Card(1));

    }

    // test giving two players each other's cards. See which one wins first
    // test with several players?
    // test that a player never wins when given random cards? (useless)

    // @Test deadlock situation where the only player went through the whole array and didn't find anything?
}

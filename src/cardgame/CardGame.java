/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardgame;

import java.util.EventListener;


/**
 *
 * @author sevabaskin
 */
public class CardGame extends Thread implements CardGameListener {

	private final int numberOfPlayers;
	private final int numberOfCards;
	private final Player[] players;
	private final CardDeck[] cardDecks;
	private CardDeck initialDeck;
	private boolean gameOver = false; // volatile?

    CardGame(int numberOfPlayers, int numberOfCards, CardDeck initialDeck) {
        validateDeck(initialDeck, numberOfPlayers, numberOfCards);
        this.initialDeck = initialDeck;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfCards = numberOfCards;
        this.players = new Player[numberOfPlayers];
        this.cardDecks = new CardDeck[numberOfPlayers];
    }

	public void run() {
        // init players & decks
        for (int i=0; i < this.numberOfPlayers; i++) {
        	this.players[i] = new Player(this, i+1, this.numberOfCards);
        	cardDecks[i] = new CardDeck(this.numberOfCards);
        }

        // distribute cards among players
        for (int i=0; i < this.numberOfCards; i++) {
        	for (int j=0; j < this.numberOfPlayers; j++)
        		this.players[j].push(this.initialDeck.pop());
        }
        // distribute cards among decks
        for (int i=0; i < this.numberOfCards; i++) {
        	for (int j=0; j < this.numberOfPlayers; j++)
        		this.cardDecks[j].push(this.initialDeck.pop());
        }


    	for (Player player : this.players)
    		player.start();
    }



    public synchronized void playerWonEventHandler(PlayerWonEvent event) {
		// make sure all subsequent PlayerWonEvents aren't accepted or do change the state of the game
		if (!gameOver) {
			Object winningPlayer = event.getSource();
			if (winningPlayer instanceof Player) {
				this.gameOver = true; // record winner?
		        System.out.println("Game won by Player " + ((Player) winningPlayer).getPlayerIndex() );
			    System.out.println("Game Over!");
			}
		    for (Player player : this.players)
		    	player.gameOverEventHandler( new GameOverEvent(this) );
		}
    }







    void validateDeck(CardDeck deck, int numberOfPlayers, int numberOfCards) {
    	if ( deck.getSize() < 2 * numberOfPlayers * numberOfCards)
    		throw new RuntimeException("Insufficient number of cards in the initial deck. Please, import a larger deck");
    }



    // Player getWinner() {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }
    
}

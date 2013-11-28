/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardgame;

import java.util.EventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author sevabaskin
 */
public class CardGame extends Thread implements CardGameListener {

	private final int numberOfPlayers;
	private final int handSize;
	private final Player[] players;
	private final CardDeck[] cardDecks;
	private CardDeck initialDeck;
	private boolean gameOver = false; // volatile?
    private Player winner = null;

    CardGame(int numberOfPlayers, int handSize, CardDeck initialDeck) {
    	//FEATURE: validate deck if it's possible to win with that deck given player num? (e.g. 1 player && num of preffered cards < k)
        validateDeck(initialDeck, numberOfPlayers, handSize);
        // TODO: validate numberOfPlayers && handSize > 0
        this.initialDeck = initialDeck;
        this.numberOfPlayers = numberOfPlayers;
        this.handSize = handSize;
        this.players = new Player[numberOfPlayers];
        this.cardDecks = new CardDeck[numberOfPlayers];
    }

	public void run() {
        // init players & decks
        // for (int i=0; i < this.numberOfPlayers; i++) {
        // 	this.players[i] = new Player(this, i+1, this.handSize);
        // 	cardDecks[i] = new CardDeck(this.handSize);
        // }
        for (int i=0; i < this.numberOfPlayers; i++) {
        	// each deck should be able to hold at least twice the number of cards
        	// research if can minimize this furhter
        	cardDecks[i] = new CardDeck(this.handSize*2);
        }
        for (int i=0; i < this.numberOfPlayers; i++) {
            // maybe a better way to register the game would be through the player.addGame(CardGame game) method, rather than passing directly? Same with decks? More readable.
        	// handSize+1 because player should be able to hold k+1 cards
        	// cardDecks[(i+this.numberOfPlayers-1)%this.numberOfPlayers] gives a unique reference to a cardDeck in a circular fashion
        	// TODO: store strategy in this.strategy
        	this.players[i] = new Player(this, i+1, this.handSize+1, 1, cardDecks[i], cardDecks[(i+this.numberOfPlayers-1)%this.numberOfPlayers]);
        }



        // distribute cards among players
        for (int i=0; i < this.handSize; i++) {
        	for (int j=0; j < this.numberOfPlayers; j++) {
        		this.players[j].push(this.initialDeck.pop());
        	}
        }
        // distribute cards among decks
        for (int i=0; i < this.handSize; i++) {
        	for (int j=0; j < this.numberOfPlayers; j++) {
        		this.cardDecks[j].push(this.initialDeck.pop());
        	}
        }


    	for (Player player : this.players)
    		player.start();
    }



    public synchronized void playerWonEventHandler(PlayerWonEvent event) {
		// make sure all subsequent PlayerWonEvents aren't accepted or do change the state of the game
		if (!gameOver) {
			Object winningPlayer = event.getSource();
			if (winningPlayer instanceof Player) {
				this.gameOver = true;
                this.winner = (Player) winningPlayer;
		        System.out.println("Game won by Player " + ((Player) winningPlayer).getPlayerIndex() );
			    System.out.println("Game Over!");
			}
            System.out.println("\nSending shutdown notices...\n");
		    for (Player player : this.players)
		    	player.gameOverEventHandler( new GameOverEvent(this) );
		}
    }

    void validateDeck(CardDeck deck, int numberOfPlayers, int handSize) {
    	if ( deck.getSize() < 2 * numberOfPlayers * handSize)
    		throw new RuntimeException("Insufficient number of cards in the initial deck. Please, import a larger deck");
    }
    synchronized boolean isOver() {
        return this.gameOver;
    }
    synchronized Player getWinner() {
        if (isOver())
            return this.winner;
        throw new RuntimeException("No winner yet - The game hasn't ended yet.");
    }










	public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("\n Usage Error: CardGame number_of_players number_of_cards_per_player\n");
            System.exit(1);
        }

        // initialize to something, otherwise compiler complains
        int numberOfPlayers = -1;
        int handSize = -1;
        // Validate args are ints
        try {
            numberOfPlayers = Integer.parseInt(args[0]);
            handSize = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("\nUsage Error: CardGame number_of_players number_of_cards_per_player"); 
            System.err.println("       Arguements number_of_players and number_of_cards_per_player must be integers larger than 0\n");
            System.exit(1);
        }
        // Validate args are larger than 1
        if (numberOfPlayers < 1 || handSize < 1) {
            System.err.println("\nUsage Error: CardGame number_of_players number_of_cards_per_player"); 
            System.err.println("       Arguements number_of_players and number_of_cards_per_player must be integers larger than 0\n");
            System.exit(1);
        }

        // Get & validate file
        String fileName = null;
        File f = new File("fake/path");
        while (!f.exists()) {        
            System.out.print("Please enter the path to the card deck: ");
            try {
                fileName = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                f = new File(fileName);
                if(!f.exists()) {
                    System.out.println("It doesn't look like this file exists..");
                }
            } catch (IOException e) {
                System.out.println("Oops..somethign went wrong.");
                System.exit(1);
            }
        }
        // Once file received
        System.out.println("We're loading the file in...");
        System.out.println("File: " + fileName);
        System.out.println("Number of Players: " + numberOfPlayers + ", Hand Size: " + handSize);
        // /Users/sevabaskin/Dropbox/2nd Year/Java/CW2/CardGame/test/cardgame/testDeck.txt
        // CardDeck initialDeck = new CardDeck[linesInAFile]



        // System.out.println("Game 1:");
        // CardDeck testCardDeck = new CardDeck(18);
        // for (int i=0; i<18; i++)
        //     testCardDeck.push(new Card(3));
        // CardGame testGame = new CardGame(3, 3, testCardDeck);
        // testGame.start();
        // try {
        //     Thread.sleep(10);
        // } catch (InterruptedException e) {}
	}
}

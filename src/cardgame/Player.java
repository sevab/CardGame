// TODO: decide what needs to be synchronized and what's not
// TODO: may need to implement addListener & removeListener
//       in order to be able to de-refenrence the listener (i.e. game).
//       Though the players will terminate before the game's end, so maybe no need?
package cardgame;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author xxx
 */
public class Player extends Thread implements PlayerListener {

    private Card[] cardsArray;
    private int top;
    private int playerIndex;
    private CardGame cardGame;
    private volatile boolean gameOver = false; // volatile?
    private volatile boolean gamePaused = false; // volatile?
    private CardDeck discardDeck;
    private CardDeck drawDeck;
    private int strategy;
    private File output_file;
    
    // FIXME: volatile? Yes or No
    Player(CardGame cardGame, int playerIndex, int handSize, int strategy, CardDeck drawDeck, CardDeck discardDeck) {
        this.playerIndex = playerIndex;
        this.cardsArray = new Card[handSize];   
        this.cardGame = cardGame;
        this.top = 0;
        this.discardDeck = discardDeck;
        this.drawDeck = drawDeck;
        this.strategy = strategy;
        this.output_file = new File("game_output/player" + playerIndex + "_output.txt");
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " I am player " + this.playerIndex);
        try {
            logAction("Player " + this.playerIndex + " has joined the game");
            logAction("player " + this.playerIndex + " initial hand is " + getHandAsString());
        } catch (IOException e) {}
        // make sure initialised properly (e.g. have all cards)?
        // DRY TODO: extract into verifyIfWon() method
        if (hasWinningCombo()) { // set game over to false? but still wait to get the command from the game
            firePlayerWonEvent( new PlayerWonEvent(this) );
            this.gameOver = true; // don't wait for GameOverEvent, shut down immediately and stop bombarding CardGame with winning events 
        }
        while (!this.gameOver) {
            System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " starts a loop");
            if (this.gamePaused) {
                System.out.println("player " + this.playerIndex + " paused and waiting...");
                this.cardGame.confirmPlayerState( new PlayerStateEvent(this), "pause" );
                // syncronized this or syncronized the game?
                // also, this.wait , or this.cardGame.game?
                synchronized (this) {
                    try { wait(); } catch (InterruptedException e) {}
                }
                System.out.println("player " + this.playerIndex + " resumed...");
                this.cardGame.confirmPlayerState( new PlayerStateEvent(this), "resume" );
            }
            
            while(!isFull()) {
                try {
                    System.out.println("deck " + this.drawDeck.getDeckIndex() + " has " + this.drawDeck.getSize() + " cards left. The cards are: {" + this.drawDeck.toString() + "}. Player " + this.playerIndex);
                    push(this.drawDeck.pop()); // acquires a lock on drawDeck
                    try {
                        logAction("player " + this.playerIndex + " draws a " + top().getValue() + " from deck " + this.drawDeck.getDeckIndex());
                        System.out.println("deck " + this.drawDeck.getDeckIndex() + " has " + this.drawDeck.getSize() + " cards left. The cards are: {" + this.drawDeck.toString() + "}. Player " + this.playerIndex);
                    } catch (IOException e) {}
                } catch (StackUnderflowException e) { // wait if draw stack is empty:
                    System.out.println("deck " + this.drawDeck.getDeckIndex() + " has " + this.drawDeck.getSize() + " cards left. The cards are: {" + this.drawDeck.toString() + "}. Player " + this.playerIndex);
                    System.out.println("deck " + this.discardDeck.getDeckIndex() + " has " + this.discardDeck.getSize() + " cards left. The cards are: {" + this.discardDeck.toString() + "}. Player " + this.playerIndex);
                    // System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " hangs");
                    try {
                        sleep(1);
                        if (this.gameOver) { // in case another player leaves and the draw deck is empty, avoid getting stuck in an infinite loop waiting for a card to appear 
                            System.out.println("\n\n\n\nNo one told me the game is over...\n\n\n\n\n");
                            return;   
                        }
                    } catch (InterruptedException ex) {  }
                }
            }
            System.out.println("deck " + this.discardDeck.getDeckIndex() + " has " + this.discardDeck.getSize() + " cards left. The cards are: {" + this.discardDeck.toString() + "}. Player " + this.playerIndex);
            Card discardedCard = discardACard();
            this.discardDeck.unshift(discardedCard); // acquire a lock on discardDeck
            try {
                logAction("player " + this.playerIndex + " discards a " + discardedCard.getValue() + " to deck " + this.discardDeck.getDeckIndex());
                logAction("player " + this.playerIndex + " current hand is " + getHandAsString());
                System.out.println("deck " + this.discardDeck.getDeckIndex() + " has " + this.discardDeck.getSize() + " cards left. The cards are: {" + this.discardDeck.toString() + "}. Player " + this.playerIndex);
            } catch (IOException e) {}
            if (hasWinningCombo()) {
                System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " about to fire PlayerWonEvent");
                firePlayerWonEvent( new PlayerWonEvent(this) );
                System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " completed firing PlayerWonEvent");
                this.gameOver = true; // don't wait for GameOverEvent, shut down immediately and stop bombarding CardGame with winning events
            }
            System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " completed a loop");    
        }
        System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " fully exited");
    }

    Card discardACard() {
        if (!isFull()) // TODO: move to the run method?
            throw new RuntimeException("Can only discard cards when the hand is full.");
        Card cardToDiscard = null;
        if (this.strategy == 1) {
            // unshift an unpreferred card in a FIFO order:
            for (int i=0; i < this.top; i++) {
                // if card is not preferred or it is not last (in which case we don't care the card's value since we'll need to get rid of one card anyways)
                if ( !preferresCard(this.cardsArray[i]) || (i == this.top-1)) {
                    cardToDiscard = delete_at(i).getCopy();
                    break;
                }
            }
        } else if (this.strategy == 2) {
            throw new RuntimeException("Strategy 2 is not implemented yet.");
        }
        return cardToDiscard;
    }

    void firePlayerWonEvent(PlayerWonEvent event) {
        // check if gameOver or fire anyway and maybe be the first or get ignored if gameover? yeah, probably no need to check
        System.out.println("Player " + this.getPlayerIndex() + " has fired PlayerWonEvent event");
        this.cardGame.playerWonEventHandler(event);
    }

    public void gameOverEventHandler(GameOverEvent event) {
        // verify the source is the same as this.cardGame? But who else...
        // Object source = event.getSource(); if (source instanceof CardGame)
        this.gameOver = true;
        // move code below after the while(!gameOver) loop
        try {
            logAction("player " + this.playerIndex + " final hand is " + getHandAsString());
            logAction("player " + this.playerIndex + " exits");
        } catch(IOException e) {}
        // print out this.cardsArray
    }

    public void pausePlayerEventHandler(GameStateEvent event) {
        synchronized (this) {
            this.gamePaused = true;    
        }
        System.out.println("player " + this.playerIndex + " is pausing... ");
    }

    public void resumePlayerEventHandler(GameStateEvent event) {
        synchronized (this) {
            this.gamePaused = false;
            this.notify();
        }
        System.out.println("player " + this.playerIndex + " is resuming... ");
    }

    void push(Card newCard) throws StackOverflowException {
        if (this.top < this.cardsArray.length) {
            this.cardsArray[this.top] = newCard;
            this.top++;
        } else {
            throw new StackOverflowException("Player's hand is full. You cannot take more cards.");
        }
    }

    int getPlayerIndex() {
        return this.playerIndex;
    }

    Card delete_at(int index) {
        if (this.isEmpty())
            throw new StackUnderflowException("Player's hands are empty. Nothing to delete.");
        if (this.cardsArray[index] == null)
            throw new RuntimeException("Nothing exists at this index.");
        Card cardToDelete = this.cardsArray[index].getCopy();

        Card[] temp = new Card[this.cardsArray.length];
        for(int i = 0; i < index; i++)
            temp[i] = cardsArray[i];
        this.top--;
        for(int i = index; i < this.top; i++)
            temp[i] = cardsArray[i+1];
        temp[this.cardsArray.length-1] = null;
        this.cardsArray = temp;
        return cardToDelete;
    }

    boolean hasWinningCombo() {
        if (this.top != this.cardsArray.length-1 ) // only full-1 hands are accepted
            return false;
        boolean flag = true;
        for(int i = 0; i < this.top && flag; i++) {
            if (!preferresCard(this.cardsArray[i])) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    boolean preferresCard(Card card) {
        return (card.getValue() == this.playerIndex);
    }
    boolean isFull() {
        return (this.top == this.cardsArray.length);
    }
    int getSize() {
        return this.top;
    }
    boolean isEmpty() {
        return (this.top == 0);
    }
    Card top() throws StackUnderflowException {
        if (this.isEmpty())
            throw new StackUnderflowException();
        return this.cardsArray[this.top - 1].getCopy();
    }
    String getHandAsString() {
        System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " does the hand method");    
        if (this.isEmpty())
            throw new StackUnderflowException(); // or just return an empty string?
        String hand = " "; // why space?
        Card fail = this.cardsArray[0]; // WTF?
        System.out.println(hand);
        for (int i=0; i<this.top; i++) {
            hand = hand + " " + this.cardsArray[i].getValue();
        }
        return hand;
    }
    private void logAction(String action) throws IOException {
        System.out.println(action);
        Helper.appendLineToFile( output_file, action);
    }
}

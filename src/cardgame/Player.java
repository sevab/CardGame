package cardgame;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;


/**
 *
 * @author xxx
 */
public class Player extends Thread implements PlayerListener {

    private Card[] cardsArray;
    private int top;
    private int playerIndex;
    private CardGame cardGame;
    private volatile boolean gameOver = false;
    private volatile boolean gamePaused = false;
    private CardDeck discardDeck;
    private CardDeck drawDeck;
    private int strategy;
    private File output_file;
    private Card preferredCard;
    
    Player(CardGame cardGame, int playerIndex, int handSize, int strategy, CardDeck drawDeck, CardDeck discardDeck) {
        this.playerIndex = playerIndex;
        this.cardsArray = new Card[handSize];   
        this.cardGame = cardGame;
        this.top = 0;
        this.discardDeck = discardDeck;
        this.drawDeck = drawDeck;
        this.strategy = strategy;
        // !!!!!! DELETE IN PRODUCTION:
        this.preferredCard = new Card(this.playerIndex);
        this.output_file = new File("game_output/player" + playerIndex + "_output.txt");
    }

    public void run() {
        logAction("Player " + this.playerIndex + " has joined the game");
        logAction("player " + this.playerIndex + " initial hand is " + getHandAsString());
        setPrefferedCard();
        verifyIfWon();
        while (!this.gameOver) {
            // DRY: verifyIfPaused()?
            if (this.gamePaused) {
                System.out.println("player " + this.playerIndex + " paused and waiting...");
                this.cardGame.confirmPlayerState( new PlayerStateEvent(this), "pause" );
                synchronized (this) {
                    try { this.wait(); } catch (InterruptedException e) {}
                }
                System.out.println("player " + this.playerIndex + " resumed...");
                this.cardGame.confirmPlayerState( new PlayerStateEvent(this), "resume" );
            }
            // draw a card
            while(!isFull()) {
                try {
                    push(this.drawDeck.pop()); // acquires a lock on drawDeck
                    logAction("player " + this.playerIndex + " draws a " + top().getValue() + " from deck " + this.drawDeck.getDeckIndex());
                } catch (StackUnderflowException e) { // wait if draw stack is empty:
                    try { sleep(1); } catch (InterruptedException ex) {}
                    if (this.gameOver) { // in case another player leaves and the draw deck is empty, avoid getting stuck in an infinite loop waiting for a card to appear 
                        firePlayerTerminatedEvent();
                        return;
                    }
                }
            }
            // discard a card
            Card discardedCard = discardACard(); // how come no exception is thrown that descardedCard has been defined already?
            this.discardDeck.unshift(discardedCard); // acquire a lock on discardDeck
            logAction("player " + this.playerIndex + " discards a " + discardedCard.getValue() + " to deck " + this.discardDeck.getDeckIndex());
            logAction("player " + this.playerIndex + " current hand is " + getHandAsString());
            verifyIfWon();
        }
        firePlayerTerminatedEvent();
    }


    void setPrefferedCard(){
        if (this.strategy == 1) {
            this.preferredCard = new Card(this.playerIndex);
        } else if (this.strategy == 2) {
            this.preferredCard = mostCommonCard();
        }
    }

    Card discardACard() {
        if (!isFull()) // TODO: move to the run method?
            throw new RuntimeException("Can only discard cards when the hand is full.");
        Card cardToDiscard = null;
        computePreferredCard();
        // to avoid game stalling unshift an unpreferred card in a FIFO order:
        for (int i=0; i < this.top; i++) {
            // if card is not preferred or it is not last (in which case we don't care the card's value since we'll need to get rid of one card anyways)
            if ( !this.cardsArray[i].equals(this.preferredCard) || (i == this.top-1)) {
                cardToDiscard = delete_at(i).getCopy();
                break;
            }
        }
        return cardToDiscard;
    }

    void computePreferredCard() {
        if (this.strategy == 2) {
            // recompute preferredCard if the newly drawn card is different from the last preferredCard
            if (!top().equals(this.preferredCard)) {
                this.preferredCard = mostCommonCard();
            }
        }
    }

    Card mostCommonCard() {
        Card previous = this.cardsArray[0];
        Card mostCommonCard = this.cardsArray[0];
        int count = 1;
        int maxCount = 1;

        for (int i = 1; i < this.top; i++) {
            if (this.cardsArray[i].equals(previous)){
                count++;
            } else {
                if (count > maxCount) {
                    mostCommonCard = this.cardsArray[i-1];
                    maxCount = count;
                }
                previous = this.cardsArray[i];
                count = 1;
            }
        }
        if (count > maxCount) {
            return this.cardsArray[this.top-1];
        } else {
            return mostCommonCard;
        }
    }


    void verifyIfWon() {
        if (hasWinningCombo())
            firePlayerWonEvent();
    }
    void firePlayerWonEvent() {
        this.gameOver = true; // don't wait for GameOverEvent, shut down immediately and stop bombarding CardGame with winning events
        this.cardGame.playerWonEventHandler( new PlayerWonEvent(this) );
    }
    void firePlayerTerminatedEvent() {
        logAction("player " + this.playerIndex + " final hand is " + getHandAsString());
        logAction("player " + this.playerIndex + " exits");
        this.cardGame.confirmPlayerTerminated(new PlayerStateEvent(this));
    }

    public void gameOverEventHandler(GameOverEvent event) {
        this.gameOver = true;
    }

    public void pausePlayerEventHandler(GameStateEvent event) {
        this.gamePaused = true;
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
        for(int i = 1; i < this.top; i++) {
            if(!this.cardsArray[0].equals(this.cardsArray[i])) {
                flag = false;
                break;
            }
        }
        return flag;
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
        // System.out.println(Thread.currentThread().getName() + " a.k.a. player " + this.playerIndex + " does the hand method");    
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
    // no need to declare IOExceptio if we are catching it. In fact, dry out it here, instead of doing it above.
    private void logAction(String action) {
        System.out.println(action);
        Helper.appendLineToFile( output_file, action);
    }
}

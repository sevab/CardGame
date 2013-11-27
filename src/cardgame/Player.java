// TODO: decide what needs to be synchronized and what's not
// TODO: may need to implement addListener & removeListener
//       in order to be able to de-refenrence the listener (i.e. game).
//       Though the players will terminate before the game's end, so maybe no need?
package cardgame;

/**
 *
 * @author xxx
 */
public class Player extends Thread implements PlayerListener {

    private Card[] cardsArray;
    private int top;
    private int playerIndex;
    private CardGame cardGame;
    private boolean gameOver = false; // volatile?
    private CardDeck discardDeck;
    private CardDeck drawDeck;
    private int strategy;
    
    // FIXME: volatile? Yes or No
    Player(CardGame cardGame, int playerIndex, int handSize, int strategy, CardDeck drawDeck, CardDeck discardDeck) {
        this.playerIndex = playerIndex;
        this.cardsArray = new Card[handSize];   
        this.cardGame = cardGame;
        this.top = 0;
        this.discardDeck = discardDeck;
        this.drawDeck = drawDeck;
        this.strategy = strategy;
    }

    public void run() {
        // make sure initialised properly (e.g. have all cards)?
        // DRY TODO: extract into verifyIfWon() method
        if (hasWinningCombo()) { // set game over to false? but still wait to get the command from the game
            firePlayerWonEvent( new PlayerWonEvent(this) );
            this.gameOver = true; // don't wait for GameOverEvent, shut down immediately and stop bombarding CardGame with winning events 
        }
        while (!gameOver) {
            while(!isFull()) {
                try {
                    push(this.drawDeck.pop()); // acquires a lock on drawDeck
                } catch (StackUnderflowException e) { // wait if draw stack is empty:
                    try {
                        sleep(1);
                    } catch (InterruptedException ex) {  }
                }
            }
            this.discardDeck.unshift(discardACard()); // acquire a lock on discardDeck
            if (hasWinningCombo()) {
                firePlayerWonEvent( new PlayerWonEvent(this) );
                this.gameOver = true; // don't wait for GameOverEvent, shut down immediately and stop bombarding CardGame with winning events
            }
        }   
    }

    Card discardACard() {
        if (!isFull()) // TODO: move to the run method?
            throw new RuntimeException("Can only discards cards when the hand is full.");
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
        System.out.println("Player " + this.getPlayerIndex() + " has fired event");
        this.cardGame.playerWonEventHandler(event);
    }

    public void gameOverEventHandler(GameOverEvent event) {
        // verify the source is the same as this.cardGame? But who else...
        // Object source = event.getSource(); if (source instanceof CardGame)
        this.gameOver = true;
        System.out.println("Player " + this.playerIndex + " is leaving the game");
        // print out this.cardsArray
    }


    synchronized void push(Card newCard) throws StackOverflowException {
        if (this.top < this.cardsArray.length) {
            this.cardsArray[this.top] = newCard;
            this.top++;
        } else {
            throw new StackOverflowException("Player's hand is full. You cannot take more cards.");
        }
    }

    synchronized int getPlayerIndex() {
        return this.playerIndex;
    }

    synchronized Card delete_at(int index) {
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

    synchronized boolean hasWinningCombo() {
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
    synchronized boolean preferresCard(Card card) {
        return (card.getValue() == this.playerIndex);
    }
    synchronized boolean isFull() {
        return (this.top == this.cardsArray.length);
    }
    synchronized int getSize() {
        return this.top;
    }
    synchronized boolean isEmpty() {
        return (this.top == 0);
    }

}

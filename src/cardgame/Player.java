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
        if (hasWinningCombo())
            firePlayerWonEvent( new PlayerWonEvent(this) );
            // set game over to false? but still wait to get the command from the game
        while (!gameOver) {
            while(!isFull()) {
                try {
                    push(this.drawDeck.pop());
                } catch (StackUnderflowException e) { // wait if draw stack is empty:
                    try {
                        sleep(1);
                    } catch (InterruptedException ex) {  }
                }
            }
            this.discardDeck.unshift(discardACard());
            if (hasWinningCombo())
                firePlayerWonEvent( new PlayerWonEvent(this) );
        }   
    }

    Card discardACard() {
        Card cardToDiscard = null;
        if (this.strategy == 1) {
            // pop an unpreffered card in a FIFO order:
            for (int i=0; i < this.top; i++) {
                // if card is not preffered or it is not last (in which case we don't care the card's value since we'll need to get rid of one card anyways)
                if ((this.cardsArray[i].getValue() != this.playerIndex) || (i == this.top)) {
                    cardToDiscard = delete_at(i);
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
        // TODO: test scenario deleting the last item?
        // if (this.isEmpty())
        //     throw new StackUnderflowException();
        if (this.cardsArray[index] == null)
            throw new RuntimeException("Nothing exists at this index.");
        Card cardToDelete = this.cardsArray[index].getCopy();

        Card[] temp = new Card[this.cardsArray.length];
        for(int i = 0; i < index; i++)
            temp[i] = cardsArray[i];
        // FIXME: or loop till top?
        for(int i = index; i < this.cardsArray.length-1; i++)
            temp[i] = cardsArray[i+1];
        temp[this.cardsArray.length-1] = null;
        this.top--;
        this.cardsArray = temp;
        return cardToDelete;
    }
    synchronized boolean hasWinningCombo() {
        if (!isFull()) return false;
        boolean flag = true;
        Card first = cardsArray[0];
        for(int i = 1; i < this.top && flag; i++) {
            if (!cardsArray[i].equals(first)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    synchronized boolean isFull() {
        return (this.top == this.cardsArray.length);
    }




}

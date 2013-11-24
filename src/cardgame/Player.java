// TODO: decide what needs to be synchronized and what's not
// TODO: may need to implement addListener & removeListener
//       in order to be able to de-refenrence the listener (i.e. game).
//       Though the players will terminate before the game's end, so maybe no need?
package cardgame;

/**
 *
 * @author xxx
 */
public class Player extends Thread {

    private Card[] cardsArray;
    private int top;
    private int playerIndex;
    private CardGame cardGame;
    
    // FIXME: volatile? Yes or No
    Player(CardGame cardGame, int playerIndex, int handSize) {
         this.playerIndex = playerIndex;
         this.cardsArray = new Card[handSize];   
         this.cardGame = cardGame;
         this.top = 0;
    }

    public void run() {
        // make sure initialised properly (e.g. have all cards)?
        if (hasWinningCombo()) {
            firePlayerWonEvent( new PlayerWonEvent(this) );
        }
    }


    void firePlayerWonEvent(PlayerWonEvent event) {
        System.out.println("Player " + this.getPlayerIndex() + " has fired event");
        this.cardGame.playerWonEventHandler(event);
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
    synchronized boolean hasWinningCombo() {
        if (!isFull()) return false;
        boolean flag = true;
        Card first = cardsArray[0];
        for(int i = 1; i < this.cardsArray.length && flag; i++) {
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

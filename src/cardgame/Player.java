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
    
    // FIXME: volatile? Yes or No
    Player(CardGame cardGame, int playerIndex, int handSize) {
         this.playerIndex = playerIndex;
         this.cardsArray = new Card[handSize];   
         this.cardGame = cardGame;
         this.top = 0;
    }

    public void run() {
        // make sure initialised properly (e.g. have all cards)?
        while (!gameOver) {
            if (hasWinningCombo())
                firePlayerWonEvent( new PlayerWonEvent(this) );
        }
        
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

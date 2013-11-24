package cardgame;

/**
 *
 * @author xxx
 */
public class Player {

    private Card[] cardsArray;
    private int top;
    
    // FIXME: volatile? Yes or No
    Player(int handSize) {
         this.cardsArray = new Card[handSize];   
         this.top = 0;
    }
    synchronized void push(Card newCard) throws StackOverflowException {
        if (this.top < this.cardsArray.length) {
            this.cardsArray[this.top] = newCard;
            this.top++;
        } else {
            throw new StackOverflowException("Player's hand is full. You cannot take more cards.");
        }
    }
}

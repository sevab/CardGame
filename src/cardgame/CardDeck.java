/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardgame;

/**
 *
 * @author sevabaskin
 */
public class CardDeck {

    private Card[] cardsArray;
    private int top;
    
    // FIXME: volatile? Yes or No
    CardDeck(int sizeOfCardDeck) {
         this.cardsArray = new Card[sizeOfCardDeck];   
         this.top = 0;
    }
    
    // TODO: maybe will need to add newCard.getCopy() due to concurrency & immutability issues
    synchronized void push(Card newCard) throws StackOverflowException {
        if (this.top < this.cardsArray.length) {
            this.cardsArray[this.top] = newCard;
            this.top++;
        } else {
            throw new StackOverflowException("Card deck is full. You cannot add more cards.");
        }
    }

    synchronized Card pop() throws StackUnderflowException {
        if (this.isEmpty()) {
            throw new StackUnderflowException("Card deck is empty. Cannot draw cards from the deck.");
        }
        this.top--;
        Card temp = this.cardsArray[this.top].getCopy();
        this.cardsArray[this.top] = null;
        return temp;
    }
	
	// TODO: maybe will need to add newCard.getCopy() due to concurrency & immutability issues
    synchronized void unshift(Card newCard) throws StackOverflowException {
    	if (this.top < this.cardsArray.length) {
    		for (int i = this.top-1; 0 <= i; i--)
    			this.cardsArray[i+1] = this.cardsArray[i];
    		this.cardsArray[0] = newCard.getCopy();
            this.top++;
    	}  else {
            throw new StackOverflowException("Card deck is full. You cannot add more cards.");
        }
    }
    synchronized int getSize() {
        return this.top;
    }
    
    synchronized boolean isEmpty() {
        return (this.top == 0);
    }
}

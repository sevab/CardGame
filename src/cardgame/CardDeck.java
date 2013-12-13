package cardgame;

public class CardDeck {

    private Card[] cardsArray;
    private int top;
    private int deckIndex;
    
    CardDeck(int deckIndex, int sizeOfCardDeck) {
         this.deckIndex = deckIndex;
         this.cardsArray = new Card[sizeOfCardDeck];   
         this.top = 0;
    }
    
    // storing the copy of newCard, since the object may be collected later on by garbage collector
    void push(Card newCard) throws StackOverflowException {
        if (this.top < this.cardsArray.length) {
            this.cardsArray[this.top] = newCard.getCopy();
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
    int getSize() {
        return this.top;
    }
    
    boolean isEmpty() {
        return (this.top == 0);
    }
    int getDeckIndex() {
        return this.deckIndex;
    }
    Card top() throws StackUnderflowException {
        if (this.isEmpty())
            throw new StackUnderflowException();
        return this.cardsArray[this.top - 1].getCopy();
    }
    // test
    public String toString() {
        String result = "";
        if (this.isEmpty()) return "none";
        for (int i=0; i<this.top; i++)
            result = result + this.cardsArray[i].getValue() + " ";
        return result;
    }
}
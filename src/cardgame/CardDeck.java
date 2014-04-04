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
    

   /**
    * 
    * adds newCard to the end of the cardsArray
    * 
    * @param newCard
    * @throws StackOverflowException if the deck is full
    */
    void push(Card newCard) throws StackOverflowException {
        if (this.top < this.cardsArray.length) {
            this.cardsArray[this.top] = newCard.getCopy();
            this.top++;
        } else {
            throw new StackOverflowException("Card deck is full. You cannot add more cards.");
        }
    }

    /**
     * 
     * Deletes and returns the last card from the cardsArray
     * 
     * @return the last card from the cardsArray
     * @throws StackUnderflowException if the deck is empty
     */
    synchronized Card pop() throws StackUnderflowException {
        if (this.isEmpty()) {
            throw new StackUnderflowException("Card deck is empty. Cannot draw cards from the deck.");
        }
        this.top--;
        Card temp = this.cardsArray[this.top].getCopy();
        this.cardsArray[this.top] = null;
        return temp;
    }
	

    /**
     * adds newCard to the beginning of the cardsArray shifting existing cards by one
     * 
     * @param newCard
     * @throws StackOverflowException if the deck is full
     */
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
    /**
     * 
     * @return the number of elements currently stored in the cardsArray
     */
    int getSize() {
        return this.top;
    }
    
    boolean isEmpty() {
        return (this.top == 0);
    }
    /**
     * 
     * @return the index name of the current deck
     */
    int getDeckIndex() {
        return this.deckIndex;
    }
    /**
     * 
     * @return the copy of the last card in the cardsArray
     * @throws StackUnderflowException  if empty
     */
    Card top() throws StackUnderflowException {
        if (this.isEmpty())
            throw new StackUnderflowException();
        return this.cardsArray[this.top - 1].getCopy();
    }

    /**
     * 
     * @return the contents of the cardsArray as a string
     */
    public String toString() {
        String result = "";
        if (this.isEmpty()) return "none";
        for (int i=0; i<this.top; i++)
            result = result + this.cardsArray[i].getValue() + " ";
        return result;
    }
}
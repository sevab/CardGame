package cardgame;

public class Card {

    final private int cardValue;
    /**
     * 
     * @param cardValue card's face vale
     */
    Card(int cardValue) {
        this.cardValue = cardValue;
    }
    /**
     * 
     * @return the value of the card
     */
    int getValue() {
        return cardValue;
    }

    /**
     * 
     * @param otherCard
     * @return whether or not this card has the same face value as otherCard
     */
    boolean equals(Card otherCard) {
    	return (this.cardValue == otherCard.getValue());
    }
    /**
     * 
     * @return a new instance of the card with the same face value
     */
    Card getCopy() {
        return new Card(this.cardValue);
    }
    
}

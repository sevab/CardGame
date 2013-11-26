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
public class Card {
    
    final private int cardValue;
    
    Card(int cardValue) {
        this.cardValue = cardValue;
    }

    int getValue() {
        return cardValue;
    }

    boolean equals(Card otherCard) {
    	return (this.cardValue == otherCard.getValue());
    }
    // TODO: test?
    Card getCopy() {
        return new Card(this.cardValue);
    }
    
}

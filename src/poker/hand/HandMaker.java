/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.hand;

import poker.card.Card;
import poker.game.Deck;
import poker.game.Game;

/**
 *
 * @author rothi
 */
public class HandMaker {
    protected Card[] cards;
    protected int[] value;
    protected Game game;
    private int playerPosition;

    public HandMaker(Deck d, Game game, int playerPosition) {
        this.game = game;
        this.playerPosition = playerPosition;
        cards = new Card[7];
        value = new int[6];
        setCards(game.getNumberOfTableCards());
    }

    public void printHand() {
        for (int i = 0; i < game.getNumberOfCards(); i++)
            System.out.println(cards[i]);
    }
    
    public void setCards(int tableCards) {
        for (int i = 0; i < 3; i++)
        {
            cards[i] = game.tableCards.get(i);
        }
        if(playerPosition == 1)
            for (int i = 0; i < 2; i++)
            {
                cards[i + 3] = game.playerCards.get(i);
            }
        else if(playerPosition == 2)
            for (int i = 0; i < 2; i++)
            {
                cards[i + 3] = game.playerCards.get(i + 2);
            }
        for (int i = 0; i < 2; i++)
        {
            if(tableCards == 4 || tableCards == 5) {
                cards[i + 5] = game.tableCards.get(i + 3);
            }
        }
    }
    
    public Card[] getCards() {
        return cards;
    }

    public String getCombination() {
        switch(value[0])
        {
            case 1:
                return "high card";
            case 2:
                return "pair of " + Card.rankAsString(value[1]) + "\'s";   
            case 3:
                return "two pair " + Card.rankAsString(value[1]) + " " + Card.rankAsString(value[2]);    
            case 4:
                return "three of a kind " + Card.rankAsString(value[1]) + "\'s";      
            case 5:
                return Card.rankAsString(value[1]) + " high straight";
            case 6:
                return "flush";    
            case 7:
                return "full house " + Card.rankAsString(value[1]) + " over " + Card.rankAsString(value[2]); 
            case 8:
                return "four of a kind " + Card.rankAsString(value[1]);
            case 9:
                return "straight flush " + Card.rankAsString(value[1]) + " high";
            default:
                return "Error with cards in hand!";
        }
    }
    
    public int compareTo(HandMaker that) {
        for (int i = 0; i < 6; i++)
        {
            if (this.value[i] > that.value[i])
                return 1;
            else if (this.value[i] < that.value[i])
                return -1;
        }
        return 0;
    }
}
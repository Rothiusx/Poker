/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.card;

/**
 *
 * @author rothi
 */
public class Card{
    private int rank;
    private int suit;

    private static String[] suits = {"H", "S", "D", "C"};
    private static String[] ranks  = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    public static String rankAsString(int rank) {
        return ranks[rank];
    }

    public Card(int suit, int rank)
    {
        this.rank = rank;
        this.suit = suit;
    }

    public @Override String toString()
    {
          return ranks[rank] /*+ " of "*/ + suits[suit];
    }

    public int getRank() {
         return rank;
    }

    public int getSuit() {
        return suit;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this tmplate file, choose Tools | Templates
 * and open the tmplate in the editor.
 */
package poker.game;

import poker.card.Card;
import java.util.Random;
import java.util.ArrayList;

/**
 *
 * @author rothi
 */
public class Deck {
    private final ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        randomizeDeck();
    }

    private void addCards() {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 13; j++)
             {
               cards.add(new Card(i, j));
             }
        }
    }
    
    private void randomizeDeck() {
        addCards();
        
        Random rand = new Random();
        Card tmp;
        int x, y;
        int size = cards.size() - 1;

        for (int i = 0; i < 100; i++)
        {
            x = rand.nextInt(size);
            y = rand.nextInt(size);

            tmp = cards.get(y);
            cards.set(y , cards.get(x));
            cards.set(x, tmp);
        }        
    }
    public Card drawFromDeck()
    {       
        return cards.remove(cards.size() - 1);
    }

    public int getTotalCards()
    {
        return cards.size();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}
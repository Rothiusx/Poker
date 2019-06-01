/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.game;

import poker.hand.Hand;
import poker.card.Card;
import java.util.ArrayList;
import poker.player.Player;

/**
 *
 * @author rothi
 */
public class Game extends Deck {
    public ArrayList<Card> tableCards = null;
    public ArrayList<Card> playerCards = null;
    private boolean start;
    private int numberOfCards;
    private int hiddenCards = 2;
    private Hand hand1, hand2;
    private final Player p1;
    private final Player p2;
    
    public Game(Player p1, Player p2) {
        this.start = false;
        this.p1 = p1;
        this.p2 = p2;
    }
    
    public boolean gameStarted() {
        return start;
    }
    
    public int getNumberOfCards() {
       return numberOfCards - hiddenCards;
    }
    
    public int getNumberOfTableCards() {
        return tableCards.size() - hiddenCards;
    }
    
    public void startGame() {
        tableCards = new ArrayList<>();
        playerCards = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            tableCards.add(drawFromDeck());
        }
        for (int i = 0; i < 4; i++) {
            playerCards.add(drawFromDeck());
        }
        hiddenCards = 2;
        numberOfCards = tableCards.size() + (playerCards.size() / 2);
        start = true;
    }
    
    public void nextCard() {
        if(hiddenCards > -1) {
            hiddenCards--;
            numberOfCards = tableCards.size() + (playerCards.size() / 2);
        }
    }
    
    public void evaluateHand() {
        hand1 = new Hand(new Deck(), this, 1);
        hand2 = new Hand(new Deck(), this, 2);
        evaluateHands();
        //printHands();
    }
    
    public String printWinner() {
        switch (hand1.compareTo(hand2)) {
            case 1:
                return p1.getName() + " with " + hand1.getCombination();
            case -1:
                return p2.getName() + " with " + hand2.getCombination();
            case 0:
                return "game is a tie with " + hand1.getCombination();
        }
        return null;
    }
    
    public Player getWinner() {
        switch (hand1.compareTo(hand2)) {
            case 1:
                return p1;
            case -1:
                return p2;
            case 0:
                return null;
        }
        return null;
    }
    
    public Player getLooser() {
        if(getWinner() != p1)
            return p1;
        else
            return p2;
    }
    
    public void evaluateHands() {
        hand1.evaluateHand();
        hand2.evaluateHand();
    }
    
    public void printHands() {
        System.out.println("Number of cards: " + getNumberOfCards());
        System.out.println("Current Hands are for\nPlayer1:");
        hand1.printHand();
        System.out.println("Player2:");
        hand2.printHand();
    }
}
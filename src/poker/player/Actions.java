/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.player;

import poker.exceptions.EmptyBankException;

/**
 *
 * @author rothi
 */
public class Actions {
    protected int bank;
    private int currentBet;
    private int lastBet;

    public int makeBet(int bet) {
        if(bet <= bank) {
            this.currentBet += bet;
            this.bank -= bet;
            this.lastBet = bet;
            return bet;
        }
        else
            throw new EmptyBankException();
    }    
    
    public int getCurrentBet() {
        return currentBet;
    }
    
    public int getLastBet() {
        return lastBet;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int prize) {
        this.bank += prize;
    }
    
    public void resetBet() {
        currentBet = 0;
        lastBet = 0;
    }
}
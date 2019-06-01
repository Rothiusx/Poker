/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.player;

import poker.profiles.ProfileLoader;
import java.io.IOException;
import poker.exceptions.ProfileFormatException;

/**
 *
 * @author rothi
 */
public class Player extends Actions {

    private String name;
    private int wins;
    private int loses;
    private int ties;

    public Player(String name, int bank) {
        super.bank = bank;
        this.name = name;
        this.wins = 0;
        this.loses = 0;
        this.ties = 0;
    }
    
    public Player(String fileName) throws IOException {
        try {
            ProfileLoader pl = new ProfileLoader(fileName);
            super.bank = pl.getBank();
            this.name = pl.getName();
            this.wins = pl.getWins();
            this.loses = pl.getLoses();
            this.ties = pl.getLoses();                 
        } catch (ProfileFormatException ex) {
            System.err.println(ex.getMessage());
            ex.getMessage();
        }
    }

    public String getName() {
        return name;
    }
    
    public void addWin() {
        wins++;
    }
    
    public void addLose() {
        loses++;
    }
        
    public void addTie() {
        ties++;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
            
        sb.append(getName()).append("\n");
        sb.append(getBank()).append("\n");
        sb.append(wins).append("\n").append(loses).append("\n").append(ties);
        
        return sb.toString();
    }
}
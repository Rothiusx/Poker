/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.profiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import poker.exceptions.ProfileFormatException;

/**
 *
 * @author rothi
 */
public class ProfileLoader {
    private BufferedReader br = null;
    private final String fileName;
    private String name;
    private String bank;
    private String wins;
    private String loses;
    private String ties;

    public ProfileLoader(String fileName) throws IOException {
        this.fileName = fileName;
        getProfile();
    }

    public void getProfile() throws IOException {
        try {
            br = new BufferedReader(new FileReader("./profiles/" + fileName));
            
            while(br.ready()) {
                name = br.readLine();
                bank = br.readLine();
                wins = br.readLine();
                loses = br.readLine();
                ties = br.readLine();
                break;
            }
            if(br.readLine() != null)
                throw new ProfileFormatException();
            
            
        } catch (FileNotFoundException ex) {
            System.out.println("Wrong file name with player profile!");
            ex.getMessage();
        } finally {
            if(br != null) {
                br.close();
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getBank() {
        return Integer.parseInt(bank);
    }

    public int getWins() {
        return Integer.parseInt(wins);
    }

    public int getLoses() {
        return Integer.parseInt(loses);
    }

    public int getTies() {
        return Integer.parseInt(ties);
    }
}
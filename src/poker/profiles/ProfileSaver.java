/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.profiles;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import poker.player.Player;

/**
 *
 * @author rothi
 */
public class ProfileSaver {
    private PrintStream ps = null;
    Player player;

    public ProfileSaver(Player player) {
        this.player = player;
    }

    public void saveProfile() throws IOException {
        try {
            ps = new PrintStream(new FileOutputStream("./profiles/" + player.getName() + ".txt"));
            System.setOut(ps);
            System.out.println(player);
            ps.close();
        } catch (IOException ex) {
            System.out.println("Wrong file name with player profile!");
            ex.getMessage();
        } finally {
            if(ps != null) {
                ps.close();
            }
        }
    }
}
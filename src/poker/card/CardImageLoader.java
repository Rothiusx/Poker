/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.card;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author rothi
 */
public class CardImageLoader {

    private Card card;
    
    public CardImageLoader(Card card) {
        this.card = card;
    }
    
    public CardImageLoader() {
        this.card = null;
    }
    
    public BufferedImage getCardImage(){
        BufferedImage img = null;
        try {
            if(card != null) {
                img = ImageIO.read(new File("./cards/" + card + ".png"));
            }
            else {
                img = ImageIO.read(new File("./cards/back.png"));
            }
            return img;
        } catch (IOException ex) {
            System.err.println("File with requested image of a card does not exist or is corrupted!");
            return null;
        } 
    }
}
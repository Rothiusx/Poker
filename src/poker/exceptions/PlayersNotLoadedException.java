/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.exceptions;

/**
 *
 * @author rothi
 */
public class PlayersNotLoadedException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Players not loaded properly!";
    }     
}

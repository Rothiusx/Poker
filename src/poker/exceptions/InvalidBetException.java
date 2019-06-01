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
public class InvalidBetException extends RuntimeException{
    @Override
    public String getMessage() {
        return "You need to bet more than that!";
    } 
}
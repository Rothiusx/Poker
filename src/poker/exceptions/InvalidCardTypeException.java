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
public class InvalidCardTypeException extends Exception{
    @Override
    public String getMessage() {
        return "Invalid card type while printing cards!";
    } 
}
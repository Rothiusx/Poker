/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.exceptions;

import java.io.IOException;

/**
 *
 * @author rothi
 */
public class ProfileFormatException extends IOException{
    @Override
    public String getMessage() {
        return "Wrong file format while loading profile";
    } 
}
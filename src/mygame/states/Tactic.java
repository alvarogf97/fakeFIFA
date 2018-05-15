/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.states;

/**
 *
 * @author Alvaro
 */
public interface Tactic {
    
    /*
    *   return 0 if Libero
    *   return 1 if Staccatto
    *   return 2 if Catenacho
    *   return 3 to Restart positions
    */
   
    public abstract int getTactic();
}

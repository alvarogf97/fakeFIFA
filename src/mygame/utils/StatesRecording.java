/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.utils;


import com.jme3.math.Vector3f;
import mygame.models.Ball;
import mygame.models.Player;

/**
 *
 * @author Alvaro
 */
public class StatesRecording {
    
    private Vector3f velocity;
    
    public StatesRecording(){
        
    }
    
    public void saveState(Player player){
        this.velocity = player.getFisicas().getLinearVelocity().clone();
    }
    
    public void saveState(Ball ball){
        this.velocity = ball.getPhysics().getLinearVelocity().clone();
    }
    
    public void restoreState(Player player){
        player.getFisicas().setLinearVelocity(velocity);
    }
    
    public void restoreState(Ball ball){
        ball.getPhysics().setLinearVelocity(velocity);
    }
    
}

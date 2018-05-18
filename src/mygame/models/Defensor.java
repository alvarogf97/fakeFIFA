/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import mygame.controllers.DefensorController;

/**
 *
 * @author Alvaro
 */
public class Defensor extends Player{
    
    private boolean right;
    
    public Defensor(Material mat, Team team, Vector3f position, boolean right) {
        super(mat, team, position);
        
        this.right = right;
        DefensorController controller = new DefensorController(this);
        this.box.addControl(controller);
        
    }
    
    public boolean isRight(){
        boolean res;
        if(this.team.getTerrain() == 0){
            res = this.right;
        }else{
            res = !this.right;
        }
        return res;
    }
    
    
    
    
    
}

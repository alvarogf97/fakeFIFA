/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.controllers;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import mygame.models.Player;

/**
 *
 * @author Alvaro
 */
public class DefensorController extends AbstractControl{
    
    Player player;
    
    public DefensorController(Player player){
        this.player = player;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
       switch(this.player.getTactic()){
           case 0:  toDoInLiberoTactic(tpf);
                    break;
           case 1:  toDoInStaccattoTactic(tpf);
                    break;
           case 2:  toDoInCatenachoTactic(tpf);
                    break;
           default: backToHome(tpf);
                    break;
       }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
     
    }
    
    private void toDoInLiberoTactic(float tpf){
        
    }
    
    private void toDoInStaccattoTactic(float tpf){
        
    }
    
    private void toDoInCatenachoTactic(float tpf){
        
    }
    
    private void backToHome(float tpf){
        if(!this.player.isInInitialPosition()){
                //come back to init position
                Vector3f direction = player.getInitPosition().subtract(player.getGeometry().getWorldTranslation()).normalize();
                Ray rayo = new Ray(player.getGeometry().getWorldTranslation(), direction);
                CollisionResults results_mates = new CollisionResults();
                CollisionResults results_oponents = new CollisionResults();
                
                player.getTeam().getOponents().collideWith(rayo, results_oponents);
                player.getTeam().getMates().collideWith(rayo, results_mates);
                if((results_mates.size()>0 && avant(results_mates.getClosestCollision().getGeometry()))
                  ||results_oponents.size()>0 && avant(results_oponents.getClosestCollision().getGeometry())){
                    Vector3f esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x+10,0,0).normalize();
                    player.getFisicas().setLinearVelocity(esquiva.mult(player.VELOCITY_TO_COME_BACK*tpf));
                }else{
                   player.getFisicas().setLinearVelocity(direction.mult(player.VELOCITY_TO_COME_BACK*tpf)); 
                }
            }else{
                //la paramos
                player.getFisicas().clearForces();
                player.getFisicas().setLinearVelocity(Vector3f.ZERO);
            }
    }
    
    private boolean avant(Geometry geom){
        return geom.getWorldTranslation().distance(player.getGeometry().getWorldTranslation())>10;
    }
    
}

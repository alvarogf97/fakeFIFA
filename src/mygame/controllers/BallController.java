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
import mygame.models.Ball;

/**
 *
 * @author Alvaro
 */
public class BallController extends AbstractControl{
    
    private Ball ball;
    private float time;
    
    public BallController(Ball ball){
        
        this.ball = ball;
        this.time = 0;
        
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        if(!ball.isNeededRestart()){
            CollisionResults results_front = new CollisionResults();
        
            ball.getGeometry().collideWith(ball.getGoalHelper().getGoal_front().getModelBound(), results_front);
            if(results_front.size()>0){
                ball.getMatcher().addGoalToFrontTeam();
                ball.setNeededRestart(true);
            }

            CollisionResults results_back = new CollisionResults();

            ball.getGeometry().collideWith(ball.getGoalHelper().getGoal_back().getModelBound(), results_back);
            if(results_back.size()>0){
                ball.getMatcher().addGoalToBackTeam();
                ball.setNeededRestart(true);
            }
        }else{
            this.backToHome(tpf);
        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    
    }
    
    private void backToHome(float tpf){
        if(!this.ball.isInInitialPosition()){
                //come back to init position
                Vector3f direction = ball.getInitPosition().subtract(ball.getGeometry().getWorldTranslation()).normalize();
                Ray rayo = new Ray(ball.getGeometry().getWorldTranslation(), direction);
                CollisionResults results_teamA = new CollisionResults();
                CollisionResults results_teamB = new CollisionResults();
                
                ball.getTeamB().collideWith(rayo, results_teamB);
                ball.getTeamA().collideWith(rayo, results_teamA);
                if((results_teamA.size()>0 && !avant(results_teamA.getClosestCollision().getGeometry()))
                  ||results_teamB.size()>0 && !avant(results_teamB.getClosestCollision().getGeometry())){
                    Vector3f esquiva = new Vector3f(ball.getGeometry().getWorldTranslation().x+10,0,0).normalize();
                    ball.getPhysics().setLinearVelocity(esquiva.mult(ball.VELOCITY_TO_COME_BACK*tpf));
                }else{
                   ball.getPhysics().setLinearVelocity(direction.mult(ball.VELOCITY_TO_COME_BACK*tpf)); 
                }
            }else{
                //la paramos
                ball.getPhysics().clearForces();
                ball.getPhysics().setLinearVelocity(Vector3f.ZERO);
                this.time = 0;
            }
    }
    
    private boolean avant(Geometry geom){
        return geom.getWorldTranslation().distance(ball.getGeometry().getWorldTranslation())>10;
    }
    
}

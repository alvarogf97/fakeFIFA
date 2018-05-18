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
import mygame.models.Defensor;
import mygame.utils.Vector3fUtilities;

/**
 *
 * @author Alvaro
 */
public class DefensorController extends AbstractControl{
    
    Defensor player;
    
    public DefensorController(Defensor player){
        this.player = player;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
       /*
        *       =============================================================
        *      ||       DECISIONES EN FUNCION DE LA TACTICA DEL EQUIPO      ||
        *       =============================================================
        */
                switch(this.player.getTactic()){
                    case 0:  if(player.myTeamHaveBall()){
                                 toDoInLiberoTacticWhithBall(tpf);
                             }else{
                                 toDoInLiberoTacticWhithoutBall(tpf);
                             }
                             break;
                             
                    case 1:  if(player.myTeamHaveBall()){
                                 toDoInStaccattoTacticWithBall(tpf);
                             }else{
                                 toDoInStaccattoTacticWithoutBall(tpf);
                             }
                             break;
                             
                    case 2:  if(player.myTeamHaveBall()){
                                 toDoInCatenachoTacticWithBall(tpf);
                             }else{
                                 toDoInCatenachoTacticWithoutBall(tpf);
                             }
                             break;
                             
                    default: backToHome(tpf);
                             break;
                }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
     
    }
    
    /*
    *       =============================================================
    *      ||                       TACTICA: LIBERO                     ||
    *       =============================================================
    */
    
            private void toDoInLiberoTacticWhithoutBall(float tpf){

                /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
                */
                        if(this.player.getGeometry().getWorldTranslation().distance
                            (this.player.getBall().getGeometry().getWorldTranslation()) 
                                    < this.player.ROUNDED_AREA){

                                this.player.getBall().getPhysics().clearForces();
                                this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
                                this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);
                                this.player.setHasBall(true);

                /*
                *       =============================================================
                *      ||   SI NO, INTENTO ROBARLA SIEMPRE QUE PUEDA IR POR ELLA    ||
                *       =============================================================
                */
                        }else if(canGoToBallInLibero()){
                                Vector3f direction = whereIsBallIn4Secs().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                if(hasObstacle(direction)){
                                    //this.player.getFisicas().setLinearVelocity(this.player.getFisicas().getLinearVelocity().mult(0.75f));
                                    Vector3f esquiva;
                                    this.player.getFisicas().clearForces();
                                    if(dodgeSideRight()){
                                        esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x+10,0,0).normalize();
                                    }else{
                                        esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x-10,0,0).normalize();
                                    }
                                    this.player.getFisicas().applyCentralForce(esquiva.mult(5));
                                }else{
                                    if(Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY){
                                        this.player.getFisicas().applyCentralForce(direction.mult(5));
                                    }  
                                }
                /*
                *       ==============================================================================
                *      ||   SI NO, IRE A MI POSICIÓN INICIAL Y ESPERARE A PODER IR A POR LA PELOTA   ||
                *       ==============================================================================
                */
                        }else{
                            this.backToHome(tpf);
                        }
            }


            private void toDoInLiberoTacticWhithBall(float tpf){
                /*
                *       =============================================================
                *      ||               SI SOY YO QUIEN TIENE LA PELOTA             ||
                *       =============================================================
                */
                        if(player.hasBall()){

                    /*
                    *       =============================================================
                    *      ||          SI PIERDO LA PELOTA TENGO QUE DECIRLO            ||
                    *       =============================================================
                    */
                            if(this.player.getGeometry().getWorldTranslation().distance
                            (this.player.getBall().getGeometry().getWorldTranslation()) 
                                > this.player.ROUNDED_AREA){

                                this.player.setHasBall(false); // he perdido la pelota

                            }else{

                        /*
                        *       =============================================================
                        *      ||      DEBO DECIDIR SI PASARLA O MOVERME CON LA PELOTA      ||
                        *       =============================================================
                        */

                                // limpio las fuerzas de la pelota
                                this.player.getBall().getPhysics().clearForces();
                                this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
                                this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);

                                /*
                                *       =============================================================
                                *      ||             SI DECIDO PASARLA -> APRENDO                  ||
                                *       =============================================================
                                */

                                        Vector3f dirPase = this.player.getTeam().mates.getChild(4).getWorldTranslation().subtract(this.player.getBall().getGeometry().getWorldTranslation()).normalize();
                                        this.player.getBall().getPhysics().applyImpulse(dirPase.mult(15), Vector3f.ZERO);

                                /*
                                *       =============================================================
                                *      ||             SI DECIDO MOVERME -> APRENDO                  ||
                                *       =============================================================
                                */

                                        //aprendiendo a moverme con la pelota

                    }

                /*
                *       =============================================================
                *      ||   SI NO TENGO LA PELOTA PERO MI EQUIPO SI, ME DESMARCO    ||
                *       =============================================================
                */
                }else{

                    /*
                    *       =============================================================
                    *      ||                 APRENDIENDO A DESMARCARME                 ||
                    *       =============================================================
                    */

                }
            }

    /*
    *       =============================================================
    *      ||                      /TACTICA: LIBERO                     ||
    *       =============================================================
    *
    *       -------------------------------------------------------------
    *
    *       =============================================================
    *      ||                    TACTICA: STACCATTO                     ||
    *       =============================================================
    */
    
            private void toDoInStaccattoTacticWithoutBall(float tpf){

            }

            private void toDoInStaccattoTacticWithBall(float tpf){

            }
    
    /*
    *       =============================================================
    *      ||                    /TACTICA: STACCATTO                    ||
    *       =============================================================
    *
    *       -------------------------------------------------------------
    *
    *       =============================================================
    *      ||                    TACTICA: CATENACHO                     ||
    *       =============================================================
    */
    
            private void toDoInCatenachoTacticWithoutBall(float tpf){

            }
            
            private void toDoInCatenachoTacticWithBall(float tpf){

            }
    
    /*
    *       =============================================================
    *      ||                    /TACTICA: STACCATTO                    ||
    *       =============================================================
    *
    *       -------------------------------------------------------------
    *
    *       =============================================================
    *      ||                     TACTICA: RESTARTING                   ||
    *       =============================================================
    */
    
            private void backToHome(float tpf){
                if(!this.player.isInInitialPosition()){
                        //come back to init position
                        Vector3f direction = player.getInitPosition().subtract(player.getGeometry().getWorldTranslation()).normalize();

                        if(hasObstacle(direction)){
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
    
    /*
    *       =============================================================
    *      ||                    /TACTICA: RESTARTING                   ||
    *       =============================================================
    *
    *       -------------------------------------------------------------
    *
    *       =============================================================
    *      ||                     METODOS PRIVADOS                      ||
    *       =============================================================
    */
    
            private boolean hasObstacle(Vector3f direction){
                Ray rayo = new Ray(player.getGeometry().getWorldTranslation(), direction);
                CollisionResults results_mates = new CollisionResults();
                CollisionResults results_oponents = new CollisionResults();
                player.getTeam().getOponents().collideWith(rayo, results_oponents);
                player.getTeam().getMates().collideWith(rayo, results_mates);

                return (results_mates.size()>0 && avant(results_mates.getClosestCollision().getGeometry()))
                          ||results_oponents.size()>0 && avant(results_oponents.getClosestCollision().getGeometry());
            }

            private boolean avant(Geometry geom){
                return geom.getWorldTranslation().distance(player.getGeometry().getWorldTranslation())>=4;
            }

            private boolean canGoToBallInLibero(){
                boolean res;
                if(this.player.getTeam().getTerrain() == 0){
                    if(this.player.isRight()){
                       res = whereIsBallIn4Secs().z < 0
                               && whereIsBallIn4Secs().x > 0; 
                    }else{
                       res = whereIsBallIn4Secs().z < 0
                               && whereIsBallIn4Secs().x < 0; 
                    }
                }else{
                    if(this.player.isRight()){
                       res = whereIsBallIn4Secs().z > 0
                               && whereIsBallIn4Secs().x > 0; 
                    }else{
                       res = whereIsBallIn4Secs().z > 0
                               && whereIsBallIn4Secs().x < 0; 
                    }
                }
                return res;
            }
            
            private boolean dodgeSideRight(){
                boolean res;
                if(this.player.getTeam().getTerrain() == 0){
                    res = this.player.isRight();
                }else{
                    res = !this.player.isRight();
                }
                return res;
            }
            
            private Vector3f whereIsBallIn4Secs(){
                Vector3f pos = this.player.getBall().getGeometry().getWorldTranslation();
                Vector3f velocity = this.player.getBall().getPhysics().getLinearVelocity();
                float x = pos.x + velocity.x*0.15f*16*0.5f;
                float y = pos.y;
                float z = pos.z + velocity.z*0.15f*16*0.5f;
                return new Vector3f(x,y,z);
            }

    /*      
    *       =============================================================
    *      ||                     /METODOS PRIVADOS                      ||
    *       =============================================================
    */
    
   
    
}

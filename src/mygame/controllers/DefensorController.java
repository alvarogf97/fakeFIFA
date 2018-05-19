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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.models.Defensor;
import mygame.models.Player;
import mygame.utils.Vector3fUtilities;
import weka.core.*;
import weka.classifiers.trees.*;

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
                                try {
                                    toDoInLiberoTacticWhithBall(tpf);
                                } catch (IOException ex) {
                                    Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (Exception ex) {
                                    Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                                }
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
                                    this.player.getFisicas().applyCentralForce(direction.mult(5));
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


            private void toDoInLiberoTacticWhithBall(float tpf) throws IOException, Exception{
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
                                Player pToPass = this.player.getTeam().whoIsBetterToPassTheBall();

                                /*
                                *       =============================================================
                                *      ||             SI DECIDO PASARLA -> APRENDO                  ||
                                *       =============================================================
                                */

                                        if(!pToPass.equals(this.player)){
                                            
                                            this.aprenderPasar(pToPass); //fase de aprendizaje
                                            
                                            
                                        }
                                        

                                /*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                                */

                                        else{
                                            // EN LIBERO PREGUNTO QUE ES MEJOR SI MOVER HACIA DELANTE O HACIA ATRAS
                                            
                                            Vector3f direction;
                                            
                                            if(this.player.getTeam().getNumberOfMaterInMyTerrain()>=3){
                                                //hacia delante
                                                direction = this.player.getTeam().getEnemyGoal().getMiddlePosition().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                            }else{
                                                //hacia atras
                                                direction = this.player.getTeam().getGoalkeeper().getGeometry().getWorldTranslation().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                            }
                                            
                                            if(hasObstacle(direction)){
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
                                                this.player.getBall().getPhysics().applyImpulse(direction.mult(10), Vector3f.ZERO);
                                                }  
                                            }
                                        }

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

                return (results_mates.size()>1 && avant(results_mates.getCollision(1).getGeometry()))
                          ||results_oponents.size()>0 && avant(results_oponents.getClosestCollision().getGeometry());
            }
            
            private boolean hasObstacle(Vector3f direction, float distance){
                Ray rayo = new Ray(player.getGeometry().getWorldTranslation(), direction);
                CollisionResults results_mates = new CollisionResults();
                CollisionResults results_oponents = new CollisionResults();
                player.getTeam().getOponents().collideWith(rayo, results_oponents);
                player.getTeam().getMates().collideWith(rayo, results_mates);

                return (results_mates.size()>1 && results_mates.getClosestCollision().getDistance() < distance)
                          || (results_oponents.size()>0 && results_oponents.getClosestCollision().getDistance() < distance);
            }

            private boolean avant(Geometry geom){
                float distance = geom.getWorldTranslation().distance(player.getGeometry().getWorldTranslation());
                return distance <=15;
            }

            private boolean canGoToBallInLibero(){
                boolean res;
                if(this.player.getTeam().getTerrain() == 0){
                    if(this.player.isRight()){
                       res = whereIsBallIn4Secs().z < 0
                               && whereIsBallIn4Secs().x >= 0; 
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
                               && whereIsBallIn4Secs().x <= 0; 
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
            
            private void aprenderPasar(Player pToPass) throws FileNotFoundException, IOException, Exception{
                
                Vector3f direction = pToPass.getGeometry().getWorldTranslation().subtract(this.player.getBall().getGeometry().getWorldTranslation()).normalize();
                float distance = pToPass.getGeometry().getWorldTranslation().distance(this.player.getGeometry().getWorldTranslation());
                int modulo = (new Random().nextInt(Defensor.PASAR_MAX)) + Defensor.PASAR_MIN;
                
                
                if(isCorrect(direction, distance, modulo)){
                    //ficheros
                    String pasar = System.getProperty("user.dir") + "/resources/arrf/pasar.arff";

                    //instances
                    Instances casosDePrueba = new Instances(new BufferedReader(new FileReader(pasar)));

                    casosDePrueba.setClassIndex(4);

                    M5P conocimiento = new M5P();


                    Instance instance = new Instance(casosDePrueba.numAttributes());

                    instance.setDataset(casosDePrueba);

                    instance.setValue(0, direction.x);
                    instance.setValue(1, direction.y);
                    instance.setValue(2, direction.z);
                    instance.setValue(3, distance);
                    instance.setValue(4, modulo);
                    casosDePrueba.add(instance);
                    conocimiento.buildClassifier(casosDePrueba);
                }
                
                this.disparar(modulo, direction, distance);
            }
            
            private boolean isCorrect(Vector3f direction, float distancia, int modulo){
                boolean res;
                Vector3f pos = this.player.getBall().getGeometry().getWorldTranslation();
                Vector3f velocity = (direction.mult(modulo)).divide(1);
                float x = pos.x + velocity.x*0.15f*16*0.5f;
                float y = pos.y;
                float z = pos.z + velocity.z*0.15f*16*0.5f;
                Vector3f posFinalIn4Secs = new Vector3f(x,y,z);
                if(posFinalIn4Secs.distance(pos) <= distancia +1 || posFinalIn4Secs.distance(pos) >= distancia -1){
                    res = true;
                }else{
                    res = false;
                }
                return res;
            }
            
            private void disparar(int module, Vector3f direction, float distancia){
                if(this.hasObstacle(direction, distancia)){
                    Vector3f arriba = new Vector3f(direction.x, direction.y+10, direction.z).normalize();
                    this.player.getBall().getPhysics().applyImpulse(arriba.mult(module), Vector3f.ZERO);
                }else{
                    this.player.getBall().getPhysics().applyImpulse(direction.mult(module), Vector3f.ZERO);
                }
            }

    /*      
    *       =============================================================
    *      ||                     /METODOS PRIVADOS                      ||
    *       =============================================================
    */
    
   
    
}

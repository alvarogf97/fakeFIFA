/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.controllers;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.models.Defensor;
import mygame.models.Goalkeeper;
import mygame.models.Player;
import mygame.shots.ShotType;
import mygame.states.Catenacho;
import mygame.states.Libero;
import mygame.states.Restarting;
import mygame.states.Staccatto;
import mygame.terrain.Goal;
import mygame.trainings.PassTraining;
import mygame.trainings.StopBallTraining;
import mygame.utils.PlayerUtilities;

/**
 *
 * @author Sergio
 */
public class GoalkeeperController extends AbstractControl{
    
    private Goalkeeper player;
    private StopBallTraining stopBallTraining;
    private PassTraining passTraining;
    private Goal porteria;
    
    private boolean restarted = false;
    
    private boolean derecha = false;
    private boolean predecir = false;
    private float instanteT = 0;
    private Vector3f vector = new Vector3f();
    private ShotType tipoDisparo;
    
    private float time_between_pass = 0f;
   
    
    public GoalkeeperController(Goalkeeper player, Goal porteria) throws FileNotFoundException, IOException{
        this.player = player;
        this.stopBallTraining = new StopBallTraining(player);
        this.passTraining = new PassTraining(player);
        this.porteria = porteria;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        if(!this.player.isPaused()){
        
            /*
            *       =========================================
            *      ||                TACTICS                ||
            *       =========================================
            */

            if(player.getTeam().getTacticType() == 3){
                this.backToHome(tpf);
            }else if(player.getTeam().getTacticType() != 1 && player.getTeam().getOponentGoals() - player.getTeam().getMyGoals() >= 4){
                player.getTeam().setTactic(new Staccatto());
            }else if(player.getTeam().getTacticType() != 2 && player.getTeam().getMyGoals() - player.getTeam().getOponentGoals() >= 2){
                player.getTeam().setTactic(new Catenacho());
            }

            if(restarted == true && player.getBall().isNeededRestart()){
                restarted = false;
                player.getTeam().setTactic(new Restarting());
            }

            if(restarted == false && player.getTeam().everybodyInInitialPosition()){
                player.getBall().setReady();
                player.getTeam().setTactic(new Libero());
                restarted = true;
            }

            if(restarted == false && player.getTeam().getTacticType() == 3 && !player.getBall().isNeededRestart()){
                player.getTeam().setTactic(new Libero());
            }

                /*
                *       =========================================
                *      ||               PASS BALL               ||
                *       =========================================
                */
                
            //si pierdo la pelota tengo que decirlo
            if(this.player.getGeometry().getWorldTranslation().distance
                            (this.player.getBall().getGeometry().getWorldTranslation()) 
                                > Defensor.ROUNDED_AREA){

                                this.player.setHasBall(false); // he perdido la pelota

            }

            if(time_between_pass <= 0 && player.getBall().getGeometry().getWorldTranslation().distance(player.getGeometry().getWorldTranslation()) <= 2.5f){
                try {    
                    this.player.getBall().getPhysics().clearForces();
                    this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
                    this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);
                    this.player.getFisicas().clearForces();
                    this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                    this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
                    this.player.setHasBall(true);

                    Player pasar = player.getTeam().whoIsBetterToPassTheBall();

                    //passTraining.learn(pasar);
                    passTraining.useKnowledge(player.getTeam().whoIsBetterToPassTheBall());

                    time_between_pass = 0.5f;
                } catch (Exception ex) {
                    Logger.getLogger(GoalkeeperController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else if(time_between_pass >= 0f){
                time_between_pass -= tpf;
            }

            /*
            *       =========================================
            *      ||               STOP BALL               ||
            *       =========================================
            */

            if (predecir == false && instanteT <= 0) {
                if (player.getBall().getGeometry().getWorldTranslation().distance(spatial.getWorldTranslation()) < 80) {
                    if (porteria.getLeftPosition().x <= spatial.getWorldTranslation().x) {
                        if (player.getBall().getGeometry().getWorldTranslation().x < spatial.getWorldTranslation().x) {
                            derecha();
                            derecha = true;
                        }// else {
    //                        player.getFisicas().clearForces();
    //                        player.getFisicas().setLinearVelocity(Vector3f.ZERO);
    //                        player.getFisicas().setAngularDamping(0);
                        //}
                    }
                    if (porteria.getRightPosition().x >= spatial.getWorldTranslation().x && !derecha) {
                        if (player.getBall().getGeometry().getWorldTranslation().x > spatial.getWorldTranslation().x) {
                            izquierda();
                        } //else {
    //                        player.getFisicas().clearForces();
    //                        player.getFisicas().setLinearVelocity(Vector3f.ZERO);
    //                        player.getFisicas().setAngularDamping(0);
                        //}
                    }
                    derecha = false;
                    //spatial.lookAt(player.getBall().getGeometry().getWorldTranslation(), Vector3f.UNIT_Y);
                }else{
                    this.backToHome(tpf);
                }
            } else if (predecir == true) {
                if (instanteT >= 0) {
                    //player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                    Vector3f direccion = vector.subtract(spatial.getWorldTranslation());
                    float distancia = vector.distance(spatial.getWorldTranslation());
                    float velocidadRequerida = (distancia) / instanteT;
                    //PREDICCION
                    float fuerza = 0; //= this.stopBallTraining.useKnowledge(vector.clone(), instanteT, tipoDisparo);
                    if(vector.x <= porteria.getRightPosition().x+1 && vector.x >= porteria.getLeftPosition().x-1 && vector.y <= porteria.getHeight()+0.4f && vector.y >= 1.58f){
                        if(tipoDisparo.equals(ShotType.BAJO)){
                            fuerza = 470 * velocidadRequerida * 1.18f;
                            player.getFisicas().applyImpulse(direccion.normalize().mult(fuerza), Vector3f.UNIT_Y);
                        }else{
                            fuerza = 455*velocidadRequerida;
                            Vector3f dir = direccion.normalize().mult(fuerza);
                            dir.y-=500*0.2f;
                            player.getFisicas().applyImpulse(dir, Vector3f.UNIT_Y);
                        }
                        //APRENDIZAJE
                        this.stopBallTraining.learn(vector.clone(), instanteT, tipoDisparo, fuerza);
                    }
                }
                predecir = false;

            } else if (predecir == false && instanteT > 0) {
                instanteT -= tpf;
            }
        
        }else{
                this.player.getFisicas().clearForces();
                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
            }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private void izquierda(){
        player.getFisicas().applyCentralForce(new Vector3f(1700,0,0));
    }
    
    private void derecha(){
        player.getFisicas().applyCentralForce(new Vector3f(-1700,0,0));
    }
    
    public void predecirPosPelota(ShotType shot){
        tipoDisparo = shot;
        predecir = true;
        float g = 9.81f;
        float h = player.getBall().getGeometry().getWorldTranslation().getY();
        Vector3f velocidad = player.getBall().getPhysics().getLinearVelocity();
        float v = (float)Math.sqrt(Math.pow(player.getBall().getPhysics().getLinearVelocity().z, 2) + Math.pow(player.getBall().getPhysics().getLinearVelocity().y, 2) + Math.pow(player.getBall().getPhysics().getLinearVelocity().x, 2));
        //float alfa = new Vector3f(fisicasPelota.getLinearVelocity().x,0,fisicasPelota.getLinearVelocity().z).normalize().angleBetween(fisicasPelota.getLinearVelocity().normalize());
        float alfa = new Vector3f(velocidad.x,0,velocidad.z).normalize().angleBetween(velocidad.normalize());
        float betta = new Vector3f(velocidad.x,0,velocidad.z).normalize().angleBetween(new Vector3f(0,0,1));
        if(velocidad.x < 0)
            betta*= -1;
        
        vector.z = spatial.getWorldTranslation().z; // posicion z en la que queremos saber en que puntos X,Y esta la pelota.
        instanteT = (vector.z-player.getBall().getGeometry().getWorldTranslation().z)/(v*(float)Math.cos(alfa)*(float)Math.cos(betta));
        
        vector.x = player.getBall().getGeometry().getWorldTranslation().x + v*(float)Math.cos(alfa)*(float)Math.sin(betta)*instanteT;

        vector.y = h + v*(float)Math.sin(alfa)*instanteT - (g*(float)Math.pow(instanteT, 2))/2;
        if(vector.y < 1.58f){
            vector.y = 1.58f;
        }
    }
    
    private void backToHome(float tpf) {
        if (!this.player.isInInitialPosition()) {
            Vector3f direction = player.getInitPosition().subtract(player.getGeometry().getWorldTranslation()).normalize();
            if (PlayerUtilities.hasObstacle(this.player, direction)) {
                Vector3f esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                player.getFisicas().setLinearVelocity(esquiva.mult(Player.VELOCITY_TO_COME_BACK * tpf));
            } else {
                player.getFisicas().setLinearVelocity(direction.mult(Player.VELOCITY_TO_COME_BACK * tpf));
            }
        } else {
            //la paramos
            player.getFisicas().clearForces();
            player.getFisicas().setLinearVelocity(Vector3f.ZERO);
            player.getFisicas().setAngularVelocity(Vector3f.ZERO);
        }
    }
    
}

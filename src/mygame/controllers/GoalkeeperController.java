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
import mygame.models.Goalkeeper;
import mygame.shots.ShotType;
import mygame.states.Catenacho;
import mygame.states.Libero;
import mygame.states.Restarting;
import mygame.states.Staccatto;
import mygame.terrain.Goal;
import mygame.trainings.StopBallTraining;

/**
 *
 * @author Sergio
 */
public class GoalkeeperController extends AbstractControl{
    
    private Goalkeeper player;
    private StopBallTraining stopBallTraining;
    private Goal porteria;
    
    private boolean restarted = false;
    
    private boolean derecha = false;
    private boolean predecir = false;
    private float instanteT = 0;
    private Vector3f vector = new Vector3f();
    private ShotType tipoDisparo;
    
    private Vector3f direccion;
    private float velocidadRequerida;
    
    public GoalkeeperController(Goalkeeper player, Goal porteria) throws FileNotFoundException, IOException{
        this.player = player;
        this.stopBallTraining = new StopBallTraining(player);
        this.porteria = porteria;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        /*
        *       =========================================
        *      ||                TACTICS                ||
        *       =========================================
        */
        
        if(player.getTeam().getTacticType() != 1 && player.getTeam().getTacticType() != 3 && player.getTeam().getOponentGoals() - player.getTeam().getMyGoals() >= 2){
            player.getTeam().setTactic(new Staccatto());
        }else if(player.getTeam().getTacticType() != 2 && player.getTeam().getTacticType() != 3 && player.getTeam().getMyGoals() - player.getTeam().getOponentGoals() >= 2){
            player.getTeam().setTactic(new Catenacho());
        }
        
        if(player.getBall().isNeededRestart()){
            restarted = false;
            player.getTeam().setTactic(new Restarting());
        }
        
        if(restarted == false && player.getTeam().everybodyInInitialPosition()){
            player.getBall().setReady();
            System.out.println("equipo: " + this.player.getTeam().getTeamName() + " -> listo");
            restarted = true;
        }
        
        if(player.getTeam().getTacticType() == 3 && !player.getBall().isNeededRestart()){
            player.getTeam().setTactic(new Libero());
        }
        
        /*
        *       =========================================
        *      ||               STOP BALL               ||
        *       =========================================
        */
        
        if (predecir == false && instanteT <= 0) {
            if (player.getBall().getGeometry().getWorldTranslation().distance(spatial.getWorldTranslation()) < 40) {
                if (porteria.getLeftPosition().x <= spatial.getWorldTranslation().x - 1) {
                    if (player.getBall().getGeometry().getWorldTranslation().x < spatial.getWorldTranslation().x) {
                        derecha();
                        derecha = true;
                    } else {
                        player.getBall().getPhysics().clearForces();
                    }
                }
                if (porteria.getRightPosition().x >= spatial.getWorldTranslation().x + 1 && !derecha) {
                    if (player.getBall().getGeometry().getWorldTranslation().x > spatial.getWorldTranslation().x) {
                        izquierda();
                    } else {
                        player.getBall().getPhysics().clearForces();
                    }
                }
                derecha = false;
                spatial.lookAt(player.getBall().getGeometry().getWorldTranslation(), Vector3f.UNIT_Y);
            }
        } else if (predecir == true) {
            if (instanteT >= 0) {
                direccion = vector.subtract(spatial.getWorldTranslation());
                float distancia = vector.distance(spatial.getWorldTranslation());
                velocidadRequerida = (distancia) / instanteT;
                //PREDICCION
                float fuerza; // = this.stopBallTraining.useKnowledge(vector.clone(), instanteT, tipoDisparo);
                if(vector.x <= 6 && vector.x >= -6 && vector.y <= porteria.getHeight()+0.4f && vector.y >= 1.58f){
                    if(tipoDisparo.equals(ShotType.BAJO)){
                        fuerza = 500 * velocidadRequerida * 1.18f;
                        player.getFisicas().applyImpulse(direccion.normalize().mult(fuerza), Vector3f.UNIT_Y);
                    }else{
                        fuerza = 600*velocidadRequerida;
                        Vector3f dir = direccion.normalize().mult(fuerza);
                        dir.y+=500*2.5f;
                        player.getFisicas().applyImpulse(direccion.normalize().mult(fuerza), Vector3f.UNIT_Y);
                    }
                    //APRENDIZAJE
                    this.stopBallTraining.learn(vector.clone(), instanteT, tipoDisparo, fuerza);
                }
            }
            predecir = false;
            
        } else if (predecir == false && instanteT > 0) {
            instanteT -= tpf;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private void izquierda(){
        player.getFisicas().applyCentralForce(new Vector3f(3000,0,0));
    }
    
    private void derecha(){
        player.getFisicas().applyCentralForce(new Vector3f(-3000,0,0));
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
    
}

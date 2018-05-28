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
import mygame.models.Midfield;
import mygame.models.Player;
import mygame.trainings.PassTraining;
import mygame.utils.PlayerUtilities;
import mygame.utils.Vector3fUtilities;

/**
 *
 * @author Alvaro
 */
public class MidfieldController extends AbstractControl {
    private float timeball=5;
    private Midfield player;
    private PassTraining passTraining;

    public MidfieldController(Midfield player) throws IOException, FileNotFoundException {
        this.player = player;
        this.passTraining = new PassTraining(player);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //Actualizo el tiempo para saber si tengo que ir a por la pelota o no
        timeball+=tpf;
        
        if(this.player.myTeamHaveBall()){
            timeball=0;
        }
        
        /*
        *       =============================================================
        *      ||       DECISIONES EN FUNCION DE LA TACTICA DEL EQUIPO      ||
        *       =============================================================
         */
        switch (this.player.getTactic()) {
            case 0:
                if (player.myTeamHaveBall()) {
                    try {
                        toDoInLiberoTacticWhithBall(tpf);
                    } catch (Exception ex) {
                        Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    toDoInLiberoTacticWhithoutBall(tpf);
                }
                break;

            case 1:
                if (player.myTeamHaveBall()) {
                    try {
                        toDoInStaccattoTacticWithBall(tpf);
                    } catch (Exception ex) {
                        Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    toDoInStaccattoTacticWithoutBall(tpf);
                }
                break;

            case 2:
                if (player.myTeamHaveBall()) {
                    try {
                        toDoInCatenachoTacticWithBall(tpf);
                    } catch (Exception ex) {
                        Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    toDoInCatenachoTacticWithoutBall(tpf);
                }
                break;
            default:
                backToHome(tpf);
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
    *           EL CENTROCAMPISTA SE MOVERA ENTRE LAS POSICIONES -50 Y 50
    *           NO VA A TIRAR A PORTERIA, SOLO LA VA A PASAR A LOS DELANTEROS
    *           
     */
    private void toDoInLiberoTacticWhithoutBall(float tpf) {
        /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
         */
        if (this.player.getGeometry().getWorldTranslation().distance(this.player.getBall().getGeometry().getWorldTranslation())
                < Midfield.ROUNDED_AREA) {

            this.player.getBall().getPhysics().clearForces();
            this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
            this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);
            this.player.getFisicas().clearForces();
            this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
            this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
            this.player.setHasBall(true);
            /*
                *       =============================================================
                *      ||   SI NO, INTENTO ROBARLA SIEMPRE QUE PUEDA IR POR ELLA    ||
                *       =============================================================
             */
        } else if (canGoToBallInLibero()) {
            Vector3f direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            if (PlayerUtilities.hasObstacle(this.player, direction)) {
                //this.player.getFisicas().setLinearVelocity(this.player.getFisicas().getLinearVelocity().mult(0.75f));
                Vector3f esquiva;
                this.player.getFisicas().clearForces();

                esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();

                this.player.getFisicas().applyCentralForce(esquiva.mult(5));
            } else {
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(direction.mult(5));
                }

            }
            /*
                *       ==============================================================================
                *      ||   SI NO, IRE A MI POSICIÓN INICIAL Y ESPERARE A PODER IR A POR LA PELOTA   ||
                *       ==============================================================================
             */
        } else {
            if (!this.player.isInInitialPosition()) {
                this.backToHome(tpf);
            } else {
                this.player.getFisicas().clearForces();
                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
            }
        }

    }

    private void toDoInLiberoTacticWhithBall(float tpf) throws IOException, Exception {

        /*
                *       =============================================================
                *      ||               SI SOY YO QUIEN TIENE LA PELOTA             ||
                *       =============================================================
         */
        if (player.hasBall()) {

            /*
                    *       =============================================================
                    *      ||          SI PIERDO LA PELOTA TENGO QUE DECIRLO            ||
                    *       =============================================================
             */
            if (this.player.getGeometry().getWorldTranslation().distance(this.player.getBall().getGeometry().getWorldTranslation())
                    > Midfield.ROUNDED_AREA) {

                this.player.setHasBall(false); // he perdido la pelota
            } else {

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
                if (!pToPass.equals(this.player)) {

                    // para la fase de entrenamiento
                    this.passTraining.learn(pToPass);

                    //funcionamiento entrenado
                    //this.passTraining.useKnowledge(pToPass);
                }/*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                 */ else {
                    // EN LIBERO PREGUNTO QUE ES MEJOR SI MOVER HACIA DELANTE O HACIA ATRAS

                    Vector3f direction;

                    if (this.player.getTeam().getNumberOfMaterInMyTerrain() >= 3 || isTooBack()) {
                        //hacia delante
                        direction = this.player.getTeam().getEnemyGoal().getMiddlePosition().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                    } else {
                        //hacia atras
                        direction = this.player.getTeam().getGoalkeeper().getGeometry().getWorldTranslation().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                    }

                    if (PlayerUtilities.hasObstacle(this.player, direction)) {
                        Vector3f esquiva;
                        this.player.getFisicas().clearForces();

                        esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();

                        this.player.getFisicas().applyCentralForce(esquiva.mult(5));
                        this.player.getBall().getPhysics().applyImpulse(esquiva.mult(5), Vector3f.ZERO);
                    } else {
                        if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                            this.player.getFisicas().applyCentralForce(direction.mult(5));
                            this.player.getBall().getPhysics().applyImpulse(direction.mult(5), Vector3f.ZERO);
                        }
                    }
                }

            }

            /*
                *       =============================================================
                *      ||   SI NO TENGO LA PELOTA PERO MI EQUIPO SI, ME DESMARCO    ||
                *       =============================================================
             */
        } else {

            /*
                    *       =============================================================
                    *      ||                 APRENDIENDO A DESMARCARME                 ||
                    *       =============================================================
             */
 /*
                            Vector3f directionToStandOut = this.player.getBestDirectionToStandOut();
                            if(Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY){
                                this.player.getFisicas().applyCentralForce(directionToStandOut.mult(5));
                            } */
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
    *           EL CENTROCAMPISTA SIEMPRE VA A IR A POR LA PELOTA Y VA A TIRAR A 
    *           PORTERIA SIEMPRE QUE TENGA POSIBILIDAD
     */
    private void toDoInStaccattoTacticWithoutBall(float tpf) {
        /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
         */
        if (this.player.getGeometry().getWorldTranslation().distance(this.player.getBall().getGeometry().getWorldTranslation())
                < this.player.ROUNDED_AREA) {

            this.player.getBall().getPhysics().clearForces();
            this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
            this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);
            this.player.getFisicas().clearForces();
            this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
            this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
            this.player.setHasBall(true);

            /*
                *       =============================================================
                *      ||   SI NO, INTENTO ROBARLA SIEMPRE QUE PUEDA IR POR ELLA    ||
                *       =============================================================
             */
        } else if (canGoToBallInStaccatto()) {
            Vector3f direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            if (PlayerUtilities.hasObstacle(this.player, direction)) {
                //this.player.getFisicas().setLinearVelocity(this.player.getFisicas().getLinearVelocity().mult(0.75f));
                Vector3f esquiva;
                this.player.getFisicas().clearForces();
                if (dodgeSideRight()) {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                } else {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x - 10, 0, 0).normalize();
                }
                this.player.getFisicas().applyCentralForce(esquiva.mult(5));
            } else {
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(direction.mult(5));
                }

            }
            /*
                *       ==============================================================================
                *      ||   SI NO, IRE A MI POSICIÓN INICIAL Y ESPERARE A PODER IR A POR LA PELOTA   ||
                *       ==============================================================================
             */
        } else {
            if (!this.player.isInInitialPosition()) {
                this.backToHome(tpf);
            } else {
                this.player.getFisicas().clearForces();
                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
            }
        }
    }

    private void toDoInStaccattoTacticWithBall(float tpf) throws Exception {
        
        
        
        /*
                *       =============================================================
                *      ||               SI SOY YO QUIEN TIENE LA PELOTA             ||
                *       =============================================================
         */
        if (player.hasBall()) {

            /*
                    *       =============================================================
                    *      ||          SI PIERDO LA PELOTA TENGO QUE DECIRLO            ||
                    *       =============================================================
             */
            if (this.player.getGeometry().getWorldTranslation().distance(this.player.getBall().getGeometry().getWorldTranslation())
                    > Defensor.ROUNDED_AREA) {

                this.player.setHasBall(false); // he perdido la pelota

            } else {

                /*
                        *       =============================================================
                        *      ||      DEBO DECIDIR SI PASARLA, TIRAR A PUERTA O MOVERME CON LA PELOTA      ||
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
                if (!pToPass.equals(this.player)) {

                    // para la fase de entrenamiento
                    this.passTraining.learn(pToPass);

                    //funcionamiento entrenado
                    //this.passTraining.useKnowledge(pToPass);
                }
                /*
                                *       =============================================================
                                *      ||                   SI DECIDO TIRAR A PUERTA                       ||
                                *       =============================================================
                 */ 
                
                
                
                /*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                 */ 
                
                else {
                    // EN STACCATTO SIEMPRE ME MUEVO HACIA DELANTE

                    Vector3f direction;
                    direction = this.player.getTeam().getEnemyGoal().getMiddlePosition().subtract(this.player.getGeometry().getWorldTranslation()).normalize();

                    if (PlayerUtilities.hasObstacle(this.player, direction)) {
                        Vector3f esquiva;
                        this.player.getFisicas().clearForces();
                        
                        esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                        
                        this.player.getFisicas().applyCentralForce(esquiva.mult(5));
                        this.player.getBall().getPhysics().applyImpulse(esquiva.mult(5), Vector3f.ZERO);
                    } else {
                        if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                            this.player.getFisicas().applyCentralForce(direction.mult(5));
                            this.player.getBall().getPhysics().applyImpulse(direction.mult(5), Vector3f.ZERO);
                        }
                    }
                }

            }

        } else {

            /*
                    *       =============================================================
                    *      ||                 APRENDIENDO A DESMARCARME                 ||
                    *       =============================================================
             */
 /*
                            Vector3f directionToStandOut = this.player.getBestDirectionToStandOut();
                            if(Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY){
                                this.player.getFisicas().applyCentralForce(directionToStandOut.mult(5));
                            } */
        }
    }

    /*
    *       =============================================================
    *      ||                     /TACTICA STACATTO                      ||
    *       =============================================================
     */
    /*
    *       =============================================================
    *      ||                     TACTICA CATENACHO                      ||
    *       =============================================================
     */
    
    
     private void toDoInCatenachoTacticWithoutBall(float tpf){
                /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
                */
                        if(this.player.getGeometry().getWorldTranslation().distance
                            (this.player.getBall().getGeometry().getWorldTranslation()) 
                                    < Midfield.ROUNDED_AREA){

                                this.player.getBall().getPhysics().clearForces();
                                this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
                                this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);
                                this.player.getFisicas().clearForces();
                                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
                                this.player.setHasBall(true);

                /*
                *       =============================================================
                *      ||   SI NO, INTENTO ROBARLA SIEMPRE QUE PUEDA IR POR ELLA    ||
                *       =============================================================
                */
                        }else if(canGoToBallInCatenacho()){
                                Vector3f direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                if(PlayerUtilities.hasObstacle(this.player,direction)){
                                    //this.player.getFisicas().setLinearVelocity(this.player.getFisicas().getLinearVelocity().mult(0.75f));
                                    Vector3f esquiva;
                                    this.player.getFisicas().clearForces();
                                    
                                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x+10,0,0).normalize();
                                    
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
                            if(!this.player.isInInitialPosition()){
                                this.backToHome(tpf);
                            }else{
                                this.player.getFisicas().clearForces();
                                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
                            }
                        }
            }
            
            private void toDoInCatenachoTacticWithBall(float tpf) throws Exception{
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
                                > Defensor.ROUNDED_AREA){

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
                                            
                                            // para la fase de entrenamiento
                                            this.passTraining.learn(pToPass);
                                            
                                            //funcionamiento entrenado
                                            this.passTraining.useKnowledge(pToPass);
                                            
                                            
                                        }
                                        

                                /*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                                */

                                        else{
                                            // EN LIBERO CATENACHO SIEMPRE HACIA ATRAS
                                            
                                            Vector3f direction;
                                            direction = this.player.getTeam().getGoalkeeper().getGeometry().getWorldTranslation().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                            
                                            
                                            if(PlayerUtilities.hasObstacle(this.player,direction)){
                                                Vector3f esquiva;
                                                this.player.getFisicas().clearForces();
                                                if(dodgeSideRight()){
                                                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x+10,0,0).normalize();
                                                }else{
                                                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x-10,0,0).normalize();
                                                }
                                                this.player.getFisicas().applyCentralForce(esquiva.mult(5)); 
                                                this.player.getBall().getPhysics().applyImpulse(esquiva.mult(5), Vector3f.ZERO);
                                            }else{
                                              if(Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY){
                                                    this.player.getFisicas().applyCentralForce(direction.mult(5));
                                                    this.player.getBall().getPhysics().applyImpulse(direction.mult(5), Vector3f.ZERO);
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
                    
                            /*
                            Vector3f directionToStandOut = this.player.getBestDirectionToStandOut();
                            if(Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY){
                                this.player.getFisicas().applyCentralForce(directionToStandOut.mult(5));
                            } */

                }
            }
    
    
    
    
    /*
    *       =============================================================
    *      ||                     METODOS PRIVADOS                      ||
    *       =============================================================
     */
    /**
     * Devuelve si se puede ir a buscar la pelota en la tactica libero
     *
     * @return Verdadero si el centrocampista puede ir a por la pelota
     */
    private boolean canGoToBallInLibero() {
        boolean res = false;
        
        if (this.player.getGeometry().getWorldTranslation().z < 75 && this.player.getGeometry().getWorldTranslation().z > -75 && !myteamhastheball2secs()) {
            if(this.player.getTeam().getTerrain()==0){
                res = PlayerUtilities.WhereShouldIGo(player).z < 100 && PlayerUtilities.WhereShouldIGo(player).z > -75;
            }else{
                res = PlayerUtilities.WhereShouldIGo(player).z > -100 && PlayerUtilities.WhereShouldIGo(player).z < 75;
            }
        }

        return res;
    }

    private boolean canGoToBallInStaccatto() {
        return !myteamhastheball2secs();
    }
    
    private boolean canGoToBallInCatenacho(){
                boolean res;
                
                if(this.player.getTeam().getTerrain() == 0){
                    res = PlayerUtilities.WhereShouldIGo(player).z < 25 /*&& !myteamhastheball2secs()*/;
                }else{
                    res = PlayerUtilities.WhereShouldIGo(player).z > -25 && !myteamhastheball2secs();   
                }
                return res;
            }

    /**
     * Hace un calculo de donde se encontrara la pelota en 4 segundos
     *
     * @return La posicion donde estará la pelota dentro de 4 segundos
     *
     */
    

    private boolean dodgeSideRight() {
        return true;
    }

    /**
     * Hace que el jugador en cuestion vuelva a su posiciion inicial
     *
     * @param tpf Tiempo entre ejecucioón: Se usa para calcular la velocidad a
     * la que el jugador debe volver a su posicion
     */
    private void backToHome(float tpf) {
        
        if (!this.player.isInInitialPosition()) {
            Vector3f direction = player.getInitPosition().subtract(player.getGeometry().getWorldTranslation()).normalize();
            if (PlayerUtilities.hasObstacle(this.player, direction)) {
                Vector3f esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                player.getFisicas().setLinearVelocity(esquiva.mult(player.VELOCITY_TO_COME_BACK * tpf));
            } else {
                player.getFisicas().setLinearVelocity(direction.mult(player.VELOCITY_TO_COME_BACK * tpf));
            }
        } else {
            //la paramos
            timeball=5;
            player.getFisicas().clearForces();
            player.getFisicas().setLinearVelocity(Vector3f.ZERO);
        }
    }

    private boolean isTooBack() {
        boolean res;
        if (this.player.getTeam().getTerrain() == 0) {
            res = this.player.getGeometry().getWorldTranslation().z <= -75;
        } else {
            res = this.player.getGeometry().getWorldTranslation().z >= 75;
        }
        return res;
    }

    private boolean myteamhastheball2secs(){
        boolean res=false;
        
        
        if(timeball<2){
            res=true;
        }

        return res;
    }
    
    
    
}

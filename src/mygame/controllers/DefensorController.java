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
import mygame.models.Player;
import mygame.trainings.PassTraining;
import mygame.utils.PlayerUtilities;
import mygame.utils.Vector3fUtilities;

/**
 *
 * @author Alvaro
 */
public class DefensorController extends AbstractControl{
    
    private Defensor player;
    private PassTraining passTraining;
    
    public DefensorController(Defensor player) throws FileNotFoundException, IOException{
        this.player = player;
        passTraining = new PassTraining(player);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(!this.player.isPaused()){
       /*
        *       =============================================================
        *      ||       DECISIONES EN FUNCION DE LA TACTICA DEL EQUIPO      ||
        *       =============================================================
        */
                switch(this.player.getTactic()){
                    case 0:  if(player.myTeamHaveBall()){
                                try {
                                    toDoInLiberoTacticWhithBall(tpf);
                                } catch (Exception ex) {
                                    Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                             }else{
                                 toDoInLiberoTacticWhithoutBall(tpf);
                             }
                             break;
                             
                    case 1:  if(player.myTeamHaveBall()){
                                try {
                                    toDoInStaccattoTacticWithBall(tpf);
                                } catch (Exception ex) {
                                    Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                             }else{
                                 toDoInStaccattoTacticWithoutBall(tpf);
                             }
                             break;
                             
                    case 2:  if(player.myTeamHaveBall()){
                                try {
                                    toDoInCatenachoTacticWithBall(tpf);
                                } catch (Exception ex) {
                                    Logger.getLogger(DefensorController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                             }else{
                                 toDoInCatenachoTacticWithoutBall(tpf);
                             }
                             break;
                             
                    default: backToHome(tpf);
                             break;
                }
        }else{
                this.player.getFisicas().clearForces();
                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
            }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
     
    }
    
    /*
    *       =============================================================
    *      ||                       TACTICA: LIBERO                     ||
    *       =============================================================
    *           SOLO IRA A POR LA PELOTA UNO DE LOS DEFENSAS, EL MAS
    *            CERCANO A ELLA, ADEMAS LOS DEFENSAS PODRÁN MOVERSE
    *          DESDE SU CAMPO HASTA 25 M MAS ALLA DE LA LINEA DE SAQUE
    */
    
            private void toDoInLiberoTacticWhithoutBall(float tpf){

                /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
                */
                        if(this.player.getGeometry().getWorldTranslation().distance
                            (this.player.getBall().getGeometry().getWorldTranslation()) 
                                    < Defensor.ROUNDED_AREA){

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
                        }else if(canGoToBallInLibero()){
                                Vector3f direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                if(PlayerUtilities.hasObstacle(this.player,direction)){
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
                            if(!this.player.isInInitialPosition()){
                                this.backToHome(tpf);
                            }else{
                                this.player.getFisicas().clearForces();
                                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
                            }
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
                                            //this.passTraining.useKnowledge(pToPass);
                                            
                                            
                                        }
                                        

                                /*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                                */

                                        else{
                                            // EN LIBERO PREGUNTO QUE ES MEJOR SI MOVER HACIA DELANTE O HACIA ATRAS
                                            
                                            Vector3f direction;
                                            
                                            if(this.player.getTeam().getNumberOfMaterInMyTerrain()>=3 || isTooBack()){
                                                //hacia delante
                                                direction = this.player.getTeam().getEnemyGoal().getMiddlePosition().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                            }else{
                                                //hacia atras
                                                direction = this.player.getTeam().getGoalkeeper().getGeometry().getWorldTranslation().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                            }
                                            
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
    *      ||                      /TACTICA: LIBERO                     ||
    *       =============================================================
    *
    *       -------------------------------------------------------------
    *
    *       =============================================================
    *      ||                    TACTICA: STACCATTO                     ||
    *       =============================================================
    *           AMBOS DEFENSAS IRAN A BUSCAR LA PELOTA Y A APOYAR A 
    *            LOS DELANTERO, ADEMAS LOS DEFENSAS PODRÁN MOVERSE
    *          DESDE SU CAMPO HASTA 75 M MAS ALLA DE LA LINEA DE SAQUE
    */
    
            private void toDoInStaccattoTacticWithoutBall(float tpf){
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
                                this.player.getFisicas().clearForces();
                                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
                                this.player.setHasBall(true);

                /*
                *       =============================================================
                *      ||   SI NO, INTENTO ROBARLA SIEMPRE QUE PUEDA IR POR ELLA    ||
                *       =============================================================
                */
                        }else if(canGoToBallInStaccatto()){
                                Vector3f direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
                                if(PlayerUtilities.hasObstacle(this.player,direction)){
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
                            if(!this.player.isInInitialPosition()){
                                this.backToHome(tpf);
                            }else{
                                this.player.getFisicas().clearForces();
                                this.player.getFisicas().setLinearVelocity(Vector3f.ZERO);
                                this.player.getFisicas().setAngularVelocity(Vector3f.ZERO);
                            }
                        }
            }

            private void toDoInStaccattoTacticWithBall(float tpf) throws Exception{
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
                                            //this.passTraining.useKnowledge(pToPass);
                                            
                                            
                                        }
                                        

                                /*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                                */

                                        else{
                                            // EN STACCATTO SIEMPRE ME MUEVO HACIA DELANTE
                                            
                                            Vector3f direction;
                                            direction = this.player.getTeam().getEnemyGoal().getMiddlePosition().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
             
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
    *      ||                    /TACTICA: STACCATTO                    ||
    *       =============================================================
    *
    *       -------------------------------------------------------------
    *
    *       =============================================================
    *      ||                    TACTICA: CATENACHO                     ||
    *       =============================================================
    *           AMBOS DEFENSAS IRAN A BUSCAR LA PELOTA Y A APOYAR A 
    *             LOS DELANTERO, LOS DEFENSAS SOLO PODRAN MOVERSE
    *                               EN SU CAMPO
    */
    
            private void toDoInCatenachoTacticWithoutBall(float tpf){
                /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
                */
                        if(this.player.getGeometry().getWorldTranslation().distance
                            (this.player.getBall().getGeometry().getWorldTranslation()) 
                                    < Defensor.ROUNDED_AREA){

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
                                            //this.passTraining.useKnowledge(pToPass);
                                            
                                            
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
                        Vector3f direction = player.getInitPosition().subtract(player.getGeometry().getWorldTranslation()).normalize();
                        if(PlayerUtilities.hasObstacle(this.player,direction)){
                            Vector3f esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x+10,0,0).normalize();
                            player.getFisicas().setLinearVelocity(esquiva.mult(Player.VELOCITY_TO_COME_BACK*tpf));
                        }else{
                           player.getFisicas().setLinearVelocity(direction.mult(Player.VELOCITY_TO_COME_BACK*tpf)); 
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
    
            private boolean canGoToBallInLibero(){
                boolean res;
                if(this.player.getTeam().getTerrain() == 0){
                    res = PlayerUtilities.WhereShouldIGo(player).z < 25
                               && (this.player.getTeam().nearestDefensorBall().equals(this.player) 
                               || this.player.getBall().getGeometry().getWorldTranslation().z<=-40)
                               && !this.player.getBall().isInInitialPosition();
                }else{
                    res = PlayerUtilities.WhereShouldIGo(player).z > -25
                               && (this.player.getTeam().nearestDefensorBall().equals(this.player)
                               || this.player.getBall().getGeometry().getWorldTranslation().z>=40)
                               && !this.player.getBall().isInInitialPosition();
                    
                }
                return res;
            }
            
            private boolean canGoToBallInStaccatto(){
                boolean res;
                if(this.player.getTeam().getTerrain() == 0){
                    res = PlayerUtilities.WhereShouldIGo(player).z < 75
                               && !this.player.getBall().isInInitialPosition();
                }else{
                    res = PlayerUtilities.WhereShouldIGo(player).z > -75
                               && !this.player.getBall().isInInitialPosition();
                    
                }
                return res;
            }
            
            private boolean canGoToBallInCatenacho(){
                boolean res;
                if(this.player.getTeam().getTerrain() == 0){
                    res = PlayerUtilities.WhereShouldIGo(player).z < 0
                               && !this.player.getBall().isInInitialPosition();
                }else{
                    res = PlayerUtilities.WhereShouldIGo(player).z > 0
                               && !this.player.getBall().isInInitialPosition();
                    
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
            
            private boolean isTooBack() {
                boolean res;
                if (this.player.getTeam().getTerrain() == 0) {
                    res = this.player.getGeometry().getWorldTranslation().z <= -50;
                } else {
                    res = this.player.getGeometry().getWorldTranslation().z >= 50;
                }
                return res;
            }
            
    /*      
    *       =============================================================
    *      ||                     /METODOS PRIVADOS                      ||
    *       =============================================================
    */

    
    
   
    
}

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
import com.jme3.scene.control.AbstractControl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.models.Defensor;
import mygame.models.Leading;
import mygame.models.Player;
import mygame.trainings.PassTraining;
import mygame.trainings.ShootTraining;
import mygame.utils.PlayerUtilities;
import mygame.utils.Vector3fUtilities;

/**
 *
 * @author Alvaro
 */
public class LeadingController extends AbstractControl {

    float timeball = 0;
    private Leading player;
    private PassTraining passTraining;
    private ShootTraining shootTraining;
    private Player companion;
    private Player middleCompanion;
    Vector3f middlePosition;
    boolean aprendiendo = false;
    float time_between_shot = 0;

    public LeadingController(Leading player) throws FileNotFoundException, IOException {
        this.player = player;
        passTraining = new PassTraining(player);
        shootTraining = new ShootTraining(player);
        middlePosition = this.player.getTeam().getEnemyGoal().getMiddlePosition();
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        if(!this.player.isPaused()){

            if (this.player.isRight()) {
                companion = this.player.getTeam().getLeading_left();
            } else {
                companion = this.player.getTeam().getLeading_right();
            }
            middleCompanion = this.player.getTeam().getMidfield();

            timeball += tpf;

            if (this.player.hasBall() || this.companion.hasBall()) {
                timeball = 0;
            }

            /*
            *       =============================================================
            *      ||       DECISIONES EN FUNCION DE LA TACTICA DEL EQUIPO      ||
            *       =============================================================
             */
            if (time_between_shot < 3) {
                time_between_shot += tpf;
            }

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
    *           SOLO IRA A POR LA PELOTA UNO DE LOS DELANTEROS, EL MAS
    *            CERCANO A ELLA, ADEMAS LOS DELANTEROS PODRÁN MOVERSE
    *          DESDE SU CAMPO HASTA 25 M MAS ALLA DE LA LINEA DE SAQUE
     */
    private void toDoInLiberoTacticWhithoutBall(float tpf) {

        /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
         */
        if (this.player.getGeometry().getWorldTranslation().distance(this.player.getBall().getGeometry().getWorldTranslation())
                < Leading.ROUNDED_AREA) {

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
            Vector3f direction;

            if (!entrePelotayPorteria(middlePosition.subtract(this.player.getGeometry().getWorldTranslation()))) {
                direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            } else {
                direction = this.player.getBall().getGeometry().getWorldTranslation().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            }
            if (PlayerUtilities.hasObstacle(this.player, direction)) {
                //this.player.getFisicas().setLinearVelocity(this.player.getFisicas().getLinearVelocity().mult(0.75f));
                Vector3f esquiva;
                this.player.getFisicas().clearForces();
                if (dodgeSideRight()) {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                } else {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x - 10, 0, 0).normalize();
                }
                this.player.getFisicas().applyCentralForce(esquiva.mult(6));
            } else {
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < Leading.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(direction.mult(6));
                }
            }

            /*
                *       ==============================================================================
                *      ||   SI NO, IRE A MI POSICIÓN INICIAL Y ESPERARE A PODER IR A POR LA PELOTA   ||
                *       ==============================================================================
             */
        } else {
            if (puedoDesmarcarme()) {
                /*
                    *       =============================================================
                    *      ||                 APRENDIENDO A DESMARCARME                 ||
                    *       =============================================================
                 */

                Vector3f directionToStandOut = this.player.getBestDirectionToStandOut();
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(directionToStandOut.mult(2.5f));
                }
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
    }

    private void toDoInLiberoTacticWhithBall(float tpf) throws IOException, Exception {
        /*
                *       =============================================================
                *      ||               SI SOY YO QUIEN TIENE LA PELOTA             ||
                *       =============================================================
         */
        if (player.hasBall()) {
            Vector3f posBall = this.player.getBall().getGeometry().getWorldTranslation();

            /*
                    *       =============================================================
                    *      ||          SI PIERDO LA PELOTA TENGO QUE DECIRLO            ||
                    *       =============================================================
             */
            if (this.player.getGeometry().getWorldTranslation().distance(posBall)
                    > Leading.ROUNDED_AREA) {

                this.player.setHasBall(false); // he perdido la pelota
            } else if (time_between_shot >= 3) {

                /*
                        *       =============================================================
                        *      ||      DEBO DECIDIR SI PASARLA O MOVERME CON LA PELOTA      ||
                        *       =============================================================
                 */
                // limpio las fuerzas de la pelota
                this.player.getBall().getPhysics().clearForces();
                this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
                this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);

                if (this.player.isRight()) {
                    companion = this.player.getTeam().getLeading_left();
                } else {
                    companion = this.player.getTeam().getLeading_right();
                }

                /*
                                *       =============================================================
                                *      ||             SI ESTOY CERCA DE PROTERIA -> CHUTO           ||
                                *       =============================================================
                 */
                if (this.closeToGoal(middlePosition, posBall) && !PlayerUtilities.hasObstacle(this.player, middlePosition.subtract(posBall).normalize())) {
                    if (aprendiendo) {
                        // para la fase de entrenamiento
                        this.shootTraining.learn(this.player.getTeam().getEnemyGoal());
                    } else {
                        //funcionamiento entrenado
                        this.shootTraining.useKnowledge(this.player.getTeam().getEnemyGoal());
                    }
                    time_between_shot = 0;
                } /*
                                *       =============================================================
                                *      ||           SI MI COMPAÑERO ESTÁ MÁS CERCA -> PASO         ||
                                *       =============================================================
                 */ else if (!nearestToGoal(companion)) {
                    if (aprendiendo) {
                        // para la fase de entrenamiento
                        this.passTraining.learn(companion);
                    } else {
                        //funcionamiento entrenado
                        this.passTraining.useKnowledge(companion);
                    }

                }   else if (!nearestToGoal(middleCompanion)){
                    if (aprendiendo) {
                        // para la fase de entrenamiento
                        this.passTraining.learn(middleCompanion);
                    } else {
                        //funcionamiento entrenado
                        this.passTraining.useKnowledge(middleCompanion);
                    }

                }
                 /*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                 */ else {
                    // EN LIBERO SIEMPRE VOY HACIA ADELANTE                                            
                    Vector3f direction;
                    direction = middlePosition.subtract(this.player.getGeometry().getWorldTranslation()).normalize();

                    if (PlayerUtilities.hasObstacle(this.player, direction)) {
                        Vector3f esquiva;
                        this.player.getFisicas().clearForces();
                        if (dodgeSideRight()) {
                            esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                        } else {
                            esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x - 10, 0, 0).normalize();
                        }
                        this.player.getFisicas().applyCentralForce(esquiva.mult(7));
                        this.player.getBall().getPhysics().applyImpulse(esquiva.mult(7), Vector3f.ZERO);
                    } else {
                        if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                            this.player.getFisicas().applyCentralForce(direction.mult(7));
                            this.player.getBall().getPhysics().applyImpulse(direction.mult(7), Vector3f.ZERO);
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
    *           PERMANEZCO EN EL ÁREA ENEMIGA,NADA MÁS TOQUE EL BALÓN CHUTO
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
            Vector3f direction;

            if (!entrePelotayPorteria(middlePosition.subtract(this.player.getGeometry().getWorldTranslation()))) {
                direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            } else {
                direction = this.player.getBall().getGeometry().getWorldTranslation().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            }
            if (PlayerUtilities.hasObstacle(this.player, direction)) {
                //this.player.getFisicas().setLinearVelocity(this.player.getFisicas().getLinearVelocity().mult(0.75f));
                Vector3f esquiva;
                this.player.getFisicas().clearForces();
                if (dodgeSideRight()) {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                } else {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x - 10, 0, 0).normalize();
                }
                this.player.getFisicas().applyCentralForce(esquiva.mult(6));
            } else {
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < Leading.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(direction.mult(6));
                }
            }
            /*
                *       ==============================================================================
                *      ||   SI NO, IRE A MI POSICIÓN INICIAL Y ESPERARE A PODER IR A POR LA PELOTA   ||
                *       ==============================================================================
             */
        } else {
            if (puedoDesmarcarme()) {
                /*
                    *       =============================================================
                    *      ||                 APRENDIENDO A DESMARCARME                 ||
                    *       =============================================================
                 */

                Vector3f directionToStandOut = this.player.getBestDirectionToStandOut();
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(directionToStandOut.mult(2.5f));
                }
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
                        *      ||                  SOLO CHUTO EN STACCATTO                 ||
                        *       =============================================================
                 */
                // limpio las fuerzas de la pelota
                this.player.getBall().getPhysics().clearForces();
                this.player.getBall().getPhysics().setLinearVelocity(Vector3f.ZERO);
                this.player.getBall().getPhysics().setAngularVelocity(Vector3f.ZERO);

                if (aprendiendo) {
                    // para la fase de entrenamiento
                    this.shootTraining.learn(this.player.getTeam().getEnemyGoal());
                } else {
                    //funcionamiento entrenado
                    this.shootTraining.useKnowledge(this.player.getTeam().getEnemyGoal());
                }

            }

        } else {

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
    *           LOS DELANTEROS PODRÁN PESCAR EL BALÓN EN SU CAMPO
     */
    private void toDoInCatenachoTacticWithoutBall(float tpf) {
        /*
                *       =============================================================
                *      ||          SI LA PELOTA PASA POR MI LADO LA HE ROBADO       ||
                *       =============================================================
         */
        if (this.player.getGeometry().getWorldTranslation().distance(this.player.getBall().getGeometry().getWorldTranslation())
                < Player.ROUNDED_AREA) {

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
        } else if (canGoToBallInCatenacho()) {
            Vector3f direction;

            if (!entrePelotayPorteria(middlePosition.subtract(this.player.getGeometry().getWorldTranslation()))) {
                direction = PlayerUtilities.WhereShouldIGo(player).subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            } else {
                direction = this.player.getBall().getGeometry().getWorldTranslation().subtract(this.player.getGeometry().getWorldTranslation()).normalize();
            }
            if (PlayerUtilities.hasObstacle(this.player, direction)) {
                //this.player.getFisicas().setLinearVelocity(this.player.getFisicas().getLinearVelocity().mult(0.75f));
                Vector3f esquiva;
                this.player.getFisicas().clearForces();
                if (dodgeSideRight()) {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                } else {
                    esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x - 10, 0, 0).normalize();
                }
                this.player.getFisicas().applyCentralForce(esquiva.mult(6));
            } else {
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < Leading.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(direction.mult(6));
                }
            }
            /*
                *       ==============================================================================
                *      ||   SI NO, IRE A MI POSICIÓN INICIAL Y ESPERARE A PODER IR A POR LA PELOTA   ||
                *       ==============================================================================
             */
        } else {
            if (puedoDesmarcarme()) {
                /*
                    *       =============================================================
                    *      ||                 APRENDIENDO A DESMARCARME                 ||
                    *       =============================================================
                 */

                Vector3f directionToStandOut = this.player.getBestDirectionToStandOut();
                if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                    this.player.getFisicas().applyCentralForce(directionToStandOut.mult(2.5f));
                }
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
    }

    private void toDoInCatenachoTacticWithBall(float tpf) throws Exception {
        /*
                *       =============================================================
                *      ||               SI SOY YO QUIEN TIENE LA PELOTA             ||
                *       =============================================================
         */
        if (player.hasBall()) {
            Vector3f posBall = this.player.getBall().getGeometry().getWorldTranslation();

            /*
                    *       =============================================================
                    *      ||          SI PIERDO LA PELOTA TENGO QUE DECIRLO            ||
                    *       =============================================================
             */
            if (this.player.getGeometry().getWorldTranslation().distance(posBall)
                    > Leading.ROUNDED_AREA) {

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
                                *      ||             SI ESTOY CERCA DE PROTERIA -> CHUTO           ||
                                *       =============================================================
                 */
                if (this.closeToGoal(middlePosition, posBall) && !PlayerUtilities.hasObstacle(this.player, middlePosition.subtract(posBall))) {
                    if (aprendiendo) {
                        // para la fase de entrenamiento
                        this.shootTraining.learn(this.player.getTeam().getEnemyGoal());
                    } else {
                        //funcionamiento entrenado
                        this.shootTraining.useKnowledge(this.player.getTeam().getEnemyGoal());
                    }

                } /*
                                *       =============================================================
                                *      ||           SI MI COMPAÑERO ESTÁ MÁS CERCA -> PASO         ||
                                *       =============================================================
                 */ else if (!pToPass.equals(this.player)) {
                    if (aprendiendo) {
                        // para la fase de entrenamiento
                        this.passTraining.learn(pToPass);
                    } else {
                        //funcionamiento entrenado
                        this.passTraining.useKnowledge(pToPass);
                    }

                }   else if (!nearestToGoal(middleCompanion)){
                    if (aprendiendo) {
                        // para la fase de entrenamiento
                        this.passTraining.learn(middleCompanion);
                    } else {
                        //funcionamiento entrenado
                        this.passTraining.useKnowledge(middleCompanion);
                    }

                }
                 /*
                                *       =============================================================
                                *      ||                   SI DECIDO MOVERME                       ||
                                *       =============================================================
                 */ else {
                    // EN LIBERO SIEMPRE VOY HACIA ADELANTE                                            
                    Vector3f direction;
                    direction = middlePosition.subtract(this.player.getGeometry().getWorldTranslation()).normalize();

                    if (PlayerUtilities.hasObstacle(this.player, direction)) {
                        Vector3f esquiva;
                        this.player.getFisicas().clearForces();
                        if (dodgeSideRight()) {
                            esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x + 10, 0, 0).normalize();
                        } else {
                            esquiva = new Vector3f(player.getGeometry().getWorldTranslation().x - 10, 0, 0).normalize();
                        }
                        this.player.getFisicas().applyCentralForce(esquiva.mult(7));
                        this.player.getBall().getPhysics().applyImpulse(esquiva.mult(7), Vector3f.ZERO);
                    } else {
                        if (Vector3fUtilities.module(this.player.getFisicas().getLinearVelocity()) < this.player.MAX_LINEAR_VELOCITY) {
                            this.player.getFisicas().applyCentralForce(direction.mult(7));
                            this.player.getBall().getPhysics().applyImpulse(direction.mult(7), Vector3f.ZERO);
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
    *      ||                    /TACTICA: STACCATTO                    ||
    *       =============================================================
    *
    *       -------------------------------------------------------------
    *
    *       =============================================================
    *      ||                     TACTICA: RESTARTING                   ||
    *       =============================================================
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
    private boolean canGoToBallInLibero() {
        boolean res;
        if (this.player.getTeam().getTerrain() == 0) {
            res = PlayerUtilities.WhereShouldIGo(player).z > 0
                    && (this.player.getTeam().nearesLeading().equals(this.player) || this.player.getTeam().haveBall())
                    && !this.player.getBall().isInInitialPosition();
        } else {
            res = PlayerUtilities.WhereShouldIGo(player).z < 0
                    && (this.player.getTeam().nearesLeading().equals(this.player) || this.player.getTeam().haveBall())
                    && !this.player.getBall().isInInitialPosition();

        }
        return res;
    }

    private boolean canGoToBallInStaccatto() {
        boolean res;
        if (this.player.getTeam().getTerrain() == 0) {
            res = PlayerUtilities.WhereShouldIGo(player).z > 25
                    && !this.player.getBall().isInInitialPosition();
        } else {
            res = PlayerUtilities.WhereShouldIGo(player).z < 25
                    && !this.player.getBall().isInInitialPosition();

        }
        return res;
    }

    private boolean canGoToBallInCatenacho() {
        boolean res;
        if (this.player.getTeam().getTerrain() == 0) {
            res = PlayerUtilities.WhereShouldIGo(player).z > -25
                    && this.player.getTeam().nearesLeading().equals(this.player)
                    && !this.player.getBall().isInInitialPosition();
        } else {
            res = PlayerUtilities.WhereShouldIGo(player).z < 25
                    && this.player.getTeam().nearesLeading().equals(this.player)
                    && !this.player.getBall().isInInitialPosition();

        }
        return res;
    }

    private boolean dodgeSideRight() {
        boolean res;
        if (this.player.getTeam().getTerrain() == 0) {
            res = this.player.isRight();
        } else {
            res = !this.player.isRight();
        }
        return res;
    }

    private boolean closeToGoal(Vector3f posPorteria, Vector3f posPlayer) {
        return posPorteria.distance(posPlayer) < 40 && (posPlayer.z < 95 && posPlayer.z > -95);
    }

    private boolean nearestToGoal(Player companion) {
        float distMe = middlePosition.distance(this.player.getGeometry().getWorldTranslation());
        float distComp = middlePosition.distance(companion.getGeometry().getWorldTranslation());

        return distMe < distComp;
    }

    private boolean entrePelotayPorteria(Vector3f direction) {
        Ray rayo = new Ray(this.player.getBall().getGeometry().getWorldTranslation(), direction);
        CollisionResults results = new CollisionResults();
        this.player.getGeometry().collideWith(rayo, results);

        return results.size() > 0;
    }

    /*      
    *       =============================================================
    *      ||                     /METODOS PRIVADOS                      ||
    *       =============================================================
     */
    private boolean puedoDesmarcarme() {
        return mycompanionhasball2secs();
    }

    public boolean mycompanionhasball2secs() {
        boolean res = false;

        if (timeball < 3) {
            res = true;
        }

        return res;
    }

}

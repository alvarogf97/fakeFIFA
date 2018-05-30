/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.controllers;

import com.jme3.input.controls.ActionListener;
import mygame.models.Ball;
import mygame.models.Team;
import mygame.terrain.Timer;

/**
 *
 * @author Alvaro
 */
public class PauseController implements ActionListener{

    private Team team_A;
    private Team team_B;
    private Timer timer;
    private Ball ball;
    private boolean paused;
    
    public PauseController(Team team_A, Team team_B, Ball ball, Timer timer){
        this.team_A = team_A;
        this.team_B = team_B;
        this.ball = ball;
        this.timer = timer;
        this.paused = false;
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("space") && isPressed){
            if(!paused){
                //se pausa
                this.team_A.pauseTeam();
                this.team_B.pauseTeam();
                this.ball.pause();
                this.timer.pause();
                this.paused = true;
            }else{
                //se recupera el estado
                this.team_A.resumeTeam();
                this.team_B.resumeTeam();
                this.ball.resume();
                this.timer.resume();
                this.paused = false;
            }
        }
    }
    
}

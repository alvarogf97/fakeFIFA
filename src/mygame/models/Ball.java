/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import mygame.controllers.BallController;
import mygame.terrain.GoalHelper;
import mygame.terrain.Matcher;
import mygame.utils.StatesRecording;

/**
 *
 * @author Alvaro
 */
public class Ball {
    
    public final int VELOCITY_TO_COME_BACK = 2000;
    
    private Geometry spehere;
    private RigidBodyControl physics; 
    private GoalHelper goalHelper;
    private Matcher matcher;
    private volatile int ready = 0;
    private boolean neededRestart;
    private Vector3f init_position;
    private Node teamA;
    private Node teamB;
    private Team team_A;
    private Team team_B;
    private boolean paused;
    private StatesRecording state;
    
    public Ball(Material mat, Vector3f position, GoalHelper goalHelper, Matcher matcher, Node teamA, Node teamB){
    
        this.paused = false;
        this.teamA = teamA;
        this.teamB = teamB;
        this.neededRestart = false;
        this.goalHelper = goalHelper;
        this.matcher = matcher;
        this.init_position = position;
        this.state = new StatesRecording();
        
        spehere = new Geometry("ball", new Sphere(32, 32, 0.3f));
        spehere.setMaterial(mat);
        spehere.move(position);
        physics = new RigidBodyControl(1f);
        
        // change physics parameter if it is necessary
        
        spehere.addControl(physics);
        
        BallController controller = new BallController(this);
        this.spehere.addControl(controller);
    }
    
    public Geometry getGeometry(){
        return spehere;
    }
    
    public RigidBodyControl getPhysics(){
        return physics;
    }

    public GoalHelper getGoalHelper() {
        return goalHelper;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public synchronized boolean isNeededRestart() {
        return neededRestart;
    }

    public synchronized void setNeededRestart(boolean neededRestart) {
        this.neededRestart = neededRestart;
    }
    
    public synchronized void setReady(){
        this.ready++;
        if(this.ready==2){
            this.ready = 0;
            this.neededRestart = false;
        }
    }
    
    public Vector3f getInitPosition(){
        return this.init_position;
    }
    
    public boolean isInInitialPosition(){
        return this.init_position.distance(this.spehere.getWorldTranslation())<=0.75f;
    }

    public Node getTeamA() {
        return teamA;
    }

    public void setTeamA(Node teamA) {
        this.teamA = teamA;
    }

    public Node getTeamB() {
        return teamB;
    }

    public void setTeamB(Node teamB) {
        this.teamB = teamB;
    }

    public void setTeam_A(Team team_A) {
        this.team_A = team_A;
    }

    public void setTeam_B(Team team_B) {
        this.team_B = team_B;
    }

    public Team getTeam_A() {
        return team_A;
    }

    public Team getTeam_B() {
        return team_B;
    }
    
    public boolean isPaused(){
        return this.paused;
    }
    
    public void pause(){
        this.paused = true;
        state.saveState(this);
    }
    
    public void resume(){
        this.paused = false;
        state.restoreState(this);
    }
    
    
    
    
    
    
    
}

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
    
    public Ball(Material mat, Vector3f position, GoalHelper goalHelper, Matcher matcher, Node teamA, Node teamB){
    
        this.teamA = teamA;
        this.teamB = teamB;
        this.neededRestart = false;
        this.goalHelper = goalHelper;
        this.matcher = matcher;
        this.init_position = position;
        
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
        return this.init_position.equals(this.spehere.getWorldTranslation());
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
    
    
    
    
    
}

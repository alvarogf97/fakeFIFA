/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.concurrent.Semaphore;
import mygame.states.Tactic;

/**
 *
 * @author Alvaro
 */
public class Team {
    
    private String teamName;
    private Tactic tactic;
    private Player defensor_left;
    private Player defensor_right;
    private Player goalkeeper;
    private Player leading_left;
    private Player leading_right;
    private Player midfield;
    private Semaphore semaphore;
    
    // team nodes
    
    public Node oponents;
    public Node mates;
    public Ball ball;
    
    public Team(Material mat, String teamName, Node oponents, Node mates, Ball ball, BulletAppState states, Vector3f [] positions){
        
        this.mates = mates;
        this.oponents = oponents;
        this.ball = ball;
        this.teamName = teamName;
        semaphore = new Semaphore(1, true);
        
        this.defensor_left = new Defensor(mat, this, positions[0]);
        this.defensor_right = new Defensor(mat, this, positions[1]);
        this.midfield = new Midfield(mat, this, positions[2]);
        this.goalkeeper = new Goalkeeper(mat, this, positions[3]);
        this.leading_left = new Leading(mat, this, positions[4]);
        this.leading_right = new Leading(mat, this, positions[5]);
        
        //adding players to mates node
        
        mates.attachChild(this.defensor_left.getGeometry());
        mates.attachChild(this.defensor_right.getGeometry());
        mates.attachChild(this.goalkeeper.getGeometry());
        mates.attachChild(this.leading_left.getGeometry());
        mates.attachChild(this.leading_right.getGeometry());
        mates.attachChild(this.midfield.getGeometry());
        
        //adding players to states
        
        states.getPhysicsSpace().add(this.defensor_left.getFisicas());
        states.getPhysicsSpace().add(this.defensor_right.getFisicas());
        states.getPhysicsSpace().add(this.goalkeeper.getFisicas());
        states.getPhysicsSpace().add(this.leading_left.getFisicas());
        states.getPhysicsSpace().add(this.leading_right.getFisicas());
        states.getPhysicsSpace().add(this.midfield.getFisicas());
        
    }
    
    public void setTactic(Tactic tactic){
        try{
            semaphore.acquire();
            this.tactic = tactic; 
        } catch (InterruptedException ex) {
            throw new RuntimeException("Error de concurrencia en el acceso a la táctica");
        }finally{
            semaphore.release();
        }
        
    }
    
    public int getTacticType(){
        int tacticType = -1;
        try{
            semaphore.acquire();
            tacticType = tactic.getTactic();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Error de concurrencia en el acceso a la táctica");
        }finally{
            semaphore.release();
        }
        return tacticType;
    }
    
    public synchronized boolean haveBall(){
        return (this.defensor_left.hasBall() || this.defensor_right.hasBall() ||
                this.goalkeeper.hasBall() || this.midfield.hasBall() ||
                this.leading_left.hasBall() || this.leading_right.hasBall());
    }
    
    public String getTeamName(){
        return this.teamName;
    }

    public Node getOponents() {
        return oponents;
    }

    public void setOponents(Node oponents) {
        this.oponents = oponents;
    }

    public Node getMates() {
        return mates;
    }

    public void setMates(Node mates) {
        this.mates = mates;
    }
    
    
    
}

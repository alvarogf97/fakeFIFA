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
import com.jme3.scene.shape.Box;

/**
 *
 * @author Alvaro
 */
public abstract class Player {
    
    public final int VELOCITY_TO_COME_BACK = 1000;
    protected Team team;
    protected Geometry box;
    protected RigidBodyControl physics;
    private boolean hasBall;
    protected Node oponents;
    protected Node mates;
    protected Ball ball;
    protected Vector3f init_position;
    
    
    protected Player(Material mat, Team team, Vector3f position){
        
        this.init_position = position;
        this.oponents = team.oponents; 
        this.mates = team.mates;
        this.ball = team.ball;
        
        this.team = team;
        box = new Geometry(team.getTeamName(), new Box(1, 1, 1));
        box.setMaterial(mat);
        box.move(position);
        hasBall = false;
        
        physics = new RigidBodyControl(1);
        box.addControl(physics); 
    }
    
    public Geometry getGeometry(){
        return box;
    }
    
    public boolean myTeamHaveBall(){
        return team.haveBall();
    }
    
    public synchronized void changeHaveBall(boolean change){
        this.hasBall = change;
    }
    
    public synchronized boolean hasBall(){
        return this.hasBall;
    }
    
    public RigidBodyControl getFisicas(){
        return physics;
    }
    
    public Vector3f getInitPosition(){
        return this.init_position;
    }
    
    public boolean isInInitialPosition(){
        return this.init_position.equals(box.getWorldTranslation());
    }
    
    public int getTactic(){
        return this.team.getTacticType();
    }
    
    public Team getTeam(){
        return this.team;
    }
    
    
    
    
    
}

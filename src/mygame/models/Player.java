/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author Alvaro
 */
public abstract class Player {
    
    public final int VELOCITY_TO_COME_BACK = 2500;
    public final int MAX_LINEAR_VELOCITY = 4;
    public final int ROUNDED_AREA = 2;
    
    protected Team team;
    protected Geometry box;
    protected RigidBodyControl physics;
    private boolean hasBall;
    protected Node oponents;
    protected Node mates;
    protected Ball ball;
    protected Vector3f init_position;
    protected Vector3f [] directions;
    
    
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
        
        directions = new Vector3f[]{new Vector3f(1,0,0),
                                    new Vector3f(1,0,1),
                                    new Vector3f(0,0,1),
                                    new Vector3f(-1,0,1),
                                    new Vector3f(-1,0,0),
                                    new Vector3f(-1,0,-1),
                                    new Vector3f(0,0,-1),
                                    new Vector3f(1,0,-1)};
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
    
    public Ball getBall(){
        return this.ball;
    }
    
    public void setHasBall(boolean value){
        this.hasBall = value;
    }
    
    @Override
    public boolean equals(Object o){
        boolean res = false;
        if(o instanceof Player){
            Player p = (Player) o;
            res = this.init_position.equals(p.getInitPosition());
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.init_position);
        return hash;
    }
    
    public float getPropability(){
        return this.distNearestMate() + this.distToEnemyGoal() + this.getEnemyNumberIn10m();
    }
    
    private float distToEnemyGoal(){
        return this.box.getWorldTranslation().distance(this.team.getEnemyGoal().getMiddlePosition());
    }
    
    private float distNearestMate(){
        float dist1 = this.box.getWorldTranslation().distance(this.team.getDefensor_left().getGeometry().getWorldTranslation());
        float dist2 = this.box.getWorldTranslation().distance(this.team.getDefensor_right().getGeometry().getWorldTranslation());
        float dist3 = this.box.getWorldTranslation().distance(this.team.getMidfield().getGeometry().getWorldTranslation());
        float dist4 = this.box.getWorldTranslation().distance(this.team.getLeading_left().getGeometry().getWorldTranslation());
        float dist5 = this.box.getWorldTranslation().distance(this.team.getLeading_right().getGeometry().getWorldTranslation());
        return getMin(new Float []{dist1, dist2, dist3, dist4, dist5});
    }
    
    private float getMin(Float [] values){
        float minValue = Float.MAX_VALUE;
        for(Float value : values){
            if(value != 0 && value < minValue){
                minValue = value;
            }
        }
        return minValue;
    }
    
    
    private int getEnemyNumberIn10m(){
        int enemyNumber = 0;
        for(Vector3f direction : directions){
            CollisionResults results = new CollisionResults();
            Ray rayo = new Ray(box.getWorldTranslation(), direction);
            this.team.getOponents().collideWith(rayo, results);
            Iterator<CollisionResult> iter = results.iterator();
            while(iter.hasNext()){
                if(iter.next().getGeometry().getWorldTranslation().distance(this.box.getWorldTranslation())<=10){
                    enemyNumber++;
                }
            }
        }
        return enemyNumber;
    }
    
    
    
}

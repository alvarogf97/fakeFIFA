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
import mygame.utils.PlayerUtilities;

/**
 *
 * @author Alvaro
 */
public abstract class Player {
    
    public static final int VELOCITY_TO_COME_BACK = 2500;
    public static final int MAX_LINEAR_VELOCITY = 10;
    public static final int ROUNDED_AREA = 2;
    public static final int PASAR_MIN = 5;
    public static final int PASAR_MAX = 25;
    
    protected Team team;
    protected Geometry box;
    protected RigidBodyControl physics;
    private boolean hasBall;
    protected Node oponents;
    protected Node mates;
    protected Ball ball;
    protected Vector3f init_position;
    protected Vector3f [] directions;
    protected String filePasarName;
    protected String fileStopBall;
    
    
    protected Player(Material mat, Team team, Vector3f position, String filePasarName, String fileStopBall){
        
        this.fileStopBall = fileStopBall;
        this.filePasarName = filePasarName;
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
    
    protected Player(Material mat, Team team, Vector3f position, String filePasarName, String fileStopBall, int weight){
        
        this.fileStopBall = fileStopBall;
        this.filePasarName = filePasarName;
        this.init_position = position;
        this.oponents = team.oponents; 
        this.mates = team.mates;
        this.ball = team.ball;
        
        this.team = team;
        box = new Geometry(team.getTeamName(), new Box(1, 1, 1));
        box.setMaterial(mat);
        box.move(position);
        hasBall = false;
        
        physics = new RigidBodyControl(weight);
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
        //calculamos en un area
        return this.init_position.distance(box.getWorldTranslation())<=2;
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
    
    public Vector3f getBestDirectionToStandOut(){
        Vector3f res = directions[1];
        float min = Float.MAX_VALUE;
        for (Vector3f direction : this.directions){
            float prob = this.getProbabilityDirection(direction);
            if(min > prob){
                min = prob;
                res = direction;
            }
        }
        return res;
    }
    
    private float getProbabilityDirection(Vector3f direction){
        Vector3f velocity = direction.mult(5);
        Vector3f posInicial = box.getWorldTranslation();
        Vector3f posFinal = new Vector3f(posInicial.x + velocity.x, posInicial.y + velocity.y, posInicial.z + velocity.z);
        return this.distNearestMate(posFinal) + 
                this.distToEnemyGoal(posFinal) + 
                this.getEnemyNumberIn10m(posFinal); 
    }
    
    public float getPropability(){
        return  
                this.distToEnemyGoal(this.box.getWorldTranslation()) + 
                this.getEnemyNumberIn10m(this.box.getWorldTranslation());
    }
    
    private float distToEnemyGoal(Vector3f position){
        return position.distance(this.team.getEnemyGoal().getMiddlePosition());
    }
    
    private float distNearestMate(Vector3f position){
        float dist1 = position.distance(this.team.getDefensor_left().getGeometry().getWorldTranslation());
        float dist2 = position.distance(this.team.getDefensor_right().getGeometry().getWorldTranslation());
        float dist3 = position.distance(this.team.getMidfield().getGeometry().getWorldTranslation());
        float dist4 = position.distance(this.team.getLeading_left().getGeometry().getWorldTranslation());
        float dist5 = position.distance(this.team.getLeading_right().getGeometry().getWorldTranslation());
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
    
    private int getEnemyNumberIn10m(Vector3f position){
        int enemyNumber = 0;
        for(Vector3f direction : directions){
            CollisionResults results = new CollisionResults();
            Ray rayo = new Ray(box.getWorldTranslation(), direction);
            this.team.getOponents().collideWith(rayo, results);
            Iterator<CollisionResult> iter = results.iterator();
            while(iter.hasNext()){
                if(iter.next().getGeometry().getWorldTranslation().distance(position)<=10){
                    enemyNumber++;
                }
            }
        }
        return enemyNumber;
    }

    public void pass(int module, Vector3f direction, float distancia){
        if (PlayerUtilities.hasObstacle(this, direction, distancia)) {
            Vector3f arriba = new Vector3f(direction.x, direction.y + 0.7f, direction.z).normalize();
            this.getBall().getPhysics().applyImpulse(arriba.mult(module), Vector3f.ZERO);
        } else {
            this.getBall().getPhysics().applyImpulse(direction.mult(module), Vector3f.ZERO);
        }
    }

    public String getFilePasarName() {
        return filePasarName;
    }
    
    public void shoot(int module, Vector3f direction){
            this.getBall().getPhysics().applyImpulse(direction.mult(module), Vector3f.ZERO);    
    }
    
     public String getFileStopBallName(){
        return fileStopBall;
    }
  
    
    
}

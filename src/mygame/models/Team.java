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
import java.io.IOException;
import java.util.concurrent.Semaphore;
import mygame.states.Libero;
import mygame.states.Tactic;
import mygame.terrain.Goal;
import mygame.terrain.Matcher;

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
    private Goal enemyGoal;
    private Semaphore semaphore;
    private int terrain; //0 if MADRID 1 if BARCELONA
    private Matcher matcher;
    
    // team nodes
    
    public Node oponents;
    public Node mates;
    public Ball ball;
    
    public Team(Material mat, String teamName, Node oponents, Node mates, Ball ball, BulletAppState states, Vector3f [] positions, int terrain, Goal enemyGoal, Matcher matcher, String [] filesPasar, String [] filesChutar, Goal goal) throws IOException{
        
        this.matcher = matcher;
        this.enemyGoal = enemyGoal;
        this.terrain = terrain;
        this.mates = mates;
        this.oponents = oponents;
        this.ball = ball;
        this.teamName = teamName;
        this.tactic = new Libero();
        semaphore = new Semaphore(1, true);
        
        this.defensor_left = new Defensor(mat, this, positions[0],false,filesPasar[0]);
        this.defensor_right = new Defensor(mat, this, positions[1],true,filesPasar[1]);
        this.midfield = new Midfield(mat, this, positions[2],filesPasar[2]);
        this.goalkeeper = new Goalkeeper(mat, this, positions[3],filesPasar[3],filesPasar[6],goal);
        this.leading_left = new Leading(mat, this, positions[4], false, filesPasar[4], filesChutar[0]);
        this.leading_right = new Leading(mat, this, positions[5],true, filesPasar[5], filesChutar[1]);
        
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
    
    public boolean everybodyInInitialPosition(){
        return this.getDefensor_left().isInInitialPosition() && this.getDefensor_right().isInInitialPosition()
                && this.getGoalkeeper().isInInitialPosition() && this.getLeading_left().isInInitialPosition()
                && this.getLeading_right().isInInitialPosition() && this.getMidfield().isInInitialPosition() && this.ball.isInInitialPosition();
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
    
    public int getTerrain(){
        return this.terrain;
    }
    
    /*
    *       =============================================================
    *      ||      DEVUELVE EL MEJOR JUGADOR AL QUE PASARLE LA PELOTA   ||
    *      ||         SI EL JUGADOR DEVUELTO SOY YO, ME MUEVO           ||
           ||         EL JUGADO DEVUELTO NUNCA SERA EL PORTERO          ||
    *       =============================================================
    */
    public Player whoIsBetterToPassTheBall(){
        //distancia compañero mas cercano
        //distancia porteria enemiga
        //numero oponentes en 10m
        Player [] arrayP = new Player[]{this.defensor_left, this.defensor_right, this.leading_left, this.leading_right, this.midfield};
        Player res = null;
        float min = Float.MAX_VALUE;
        for(Player p : arrayP){
            float prob = p.getPropability();
            if(prob < min){
                res = p;
                min = prob;
            }
        }
        return res;
    }

    public Player getDefensor_left() {
        return defensor_left;
    }

    public Player getDefensor_right() {
        return defensor_right;
    }

    public Player getGoalkeeper() {
        return goalkeeper;
    }

    public Player getLeading_left() {
        return leading_left;
    }

    public Player getLeading_right() {
        return leading_right;
    }

    public Player getMidfield() {
        return midfield;
    }

    public Goal getEnemyGoal() {
        return enemyGoal;
    }
    
    public int getMyGoals(){
        int num;
        if(this.terrain == 0){
           num = this.matcher.getGoals_team_back();
        }else{
           num = this.matcher.getGoals_team_front();
        }
        return num;
    }
    
    public int getOponentGoals(){
        int num;
        if(this.terrain == 0){
            num = this.matcher.getGoals_team_front();
        }else{
            num = this.matcher.getGoals_team_back();
        }
        return num;
    }
    
    public int getNumberOfMaterInMyTerrain(){
        int num = 0;
        
        if(this.terrain == 0){
            if(this.defensor_left.getGeometry().getWorldTranslation().z < 0) num++;
            if(this.defensor_right.getGeometry().getWorldTranslation().z < 0) num++;
            if(this.leading_left.getGeometry().getWorldTranslation().z < 0) num++;
            if(this.leading_right.getGeometry().getWorldTranslation().z < 0) num++;
            if(this.midfield.getGeometry().getWorldTranslation().z < 0) num++;
        }else{
            if(this.defensor_left.getGeometry().getWorldTranslation().z > 0) num++;
            if(this.defensor_right.getGeometry().getWorldTranslation().z > 0) num++;
            if(this.leading_left.getGeometry().getWorldTranslation().z > 0) num++;
            if(this.leading_right.getGeometry().getWorldTranslation().z > 0) num++;
            if(this.midfield.getGeometry().getWorldTranslation().z > 0) num++;
        }
        
        return num;
    }
    
    public synchronized Player nearestDefensorBall(){
        Player p;
        float distance_1 = this.defensor_left.getGeometry().getWorldTranslation().distance(this.ball.getGeometry().getWorldTranslation());
        float distance_2 = this.defensor_right.getGeometry().getWorldTranslation().distance(this.ball.getGeometry().getWorldTranslation());
        
        if(distance_1 >= distance_2){
            p = this.defensor_right;
        }else{
            p = this.defensor_left;
        }
        
        return p;
    }
    
    public synchronized Player nearesLeading(){
        Player p;
        float distance_1 = this.leading_left.getGeometry().getWorldTranslation().distance(this.ball.getGeometry().getWorldTranslation());
        float distance_2 = this.leading_right.getGeometry().getWorldTranslation().distance(this.ball.getGeometry().getWorldTranslation());
        
        if(distance_1 >= distance_2){
            p = this.leading_right;
        }else{
            p = this.leading_left;
        }
        
        return p;
    }
    
    
    
    
    
    
    
    
    
}

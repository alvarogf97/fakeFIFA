/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import java.io.IOException;
import mygame.controllers.GoalkeeperController;
import mygame.shots.ShotType;
import mygame.terrain.Goal;

/**
 *
 * @author Alvaro
 */
public class Goalkeeper extends Player{
    
    private GoalkeeperController controller;
    
    public Goalkeeper(Material mat, Team team, Vector3f position, String filePasarName, String fileStopBall, Goal goal) throws IOException {
        super(mat, team, position, filePasarName,fileStopBall,500);
        this.fileStopBall = fileStopBall;
        this.controller = new GoalkeeperController(this, goal);
        this.box.addControl(controller);
    }
    
    public void predict(ShotType type){
        controller.predecirPosPelota(type);
    }
   
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import java.io.IOException;
import mygame.controllers.LeadingController;

/**
 *
 * @author Alberto
 */
public class Leading extends Player{
    
    private boolean right;
    private String fileChutarName;
    
    public Leading(Material mat, Team team, Vector3f position, boolean right, String filePasarName, String fileChutarName) throws IOException {
        super(mat, team, position, filePasarName,null);
        this.fileChutarName = fileChutarName;
        this.right = right;
        LeadingController controller = new LeadingController(this);
        this.box.addControl(controller);
    }
    
    public boolean isRight(){
        return this.right;
    }
        
    public String getFileChutarName() {
        return fileChutarName;
    }
} 

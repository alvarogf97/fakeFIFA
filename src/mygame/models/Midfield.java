/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.models;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import java.io.IOException;
import mygame.controllers.MidfieldController;

/**
 *
 * @author Alvaro
 */
public class Midfield extends Player{
    private String fileChutarName;
    public Midfield(Material mat, Team team, Vector3f position, String filePasarName, String chutar) throws IOException {
        super(mat, team, position,filePasarName,null);
        fileChutarName=chutar;
        MidfieldController control= new MidfieldController(this);
        this.box.addControl(control);
        
        System.out.println(chutar);
    }
    
    public String getFileChutarName(){
        return fileChutarName;
    }
    
}

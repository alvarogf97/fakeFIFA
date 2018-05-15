/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.terrain;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;


public class Grass{
    
    private Node campo;
    
    public Grass(float ancho, float largo){
        super();
        Box b = new Box(ancho, 0.5f, largo);
        Geometry cesped = new Geometry("campo",b);
        campo = new Node();
        campo.attachChild(cesped);
    }
    
    public Node getNode(){
        return campo;
    }
}

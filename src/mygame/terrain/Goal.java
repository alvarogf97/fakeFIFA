/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mygame.terrain;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;


public class Goal {
    
    private Node porteria;
    private Geometry larguero;
    private Geometry barraIzq;
    private Geometry barraDcha;
    
    private final float alto;
    
    public Goal(int calidad1, int calidad2, float anchoCilindros,float alto, float largo){
        Cylinder mallaLarguero = new Cylinder(calidad1, calidad2, anchoCilindros, largo+2*anchoCilindros, true);
        Cylinder mallaBarraIzq = new Cylinder(calidad1, calidad2, anchoCilindros, alto, true);
        Cylinder mallaBarraDcha = new Cylinder(calidad1, calidad2, anchoCilindros, alto, true);
        this.alto = alto;
        
        larguero = new Geometry("larguero", mallaLarguero);
        barraIzq = new Geometry("barraIzq", mallaBarraIzq);
        barraDcha = new Geometry("barraDcha", mallaBarraDcha);
        
        //creamos el nodo que manejara el rootNode
        porteria = new Node();
        porteria.attachChild(larguero);
        porteria.attachChild(barraIzq);
        porteria.attachChild(barraDcha);
        
        //situamos los componentes
        barraIzq.rotate((float)Math.PI/2f,0,0);
        barraDcha.rotate((float)Math.PI/2f,0,0);
        larguero.rotate(0,(float)Math.PI/2f,0);
        
        barraIzq.move(0,alto/2,0);
        barraDcha.move(largo,alto/2,0);
        larguero.move(largo/2,alto,0);
    }
    
    public Node getNode(){
        return porteria;
    }
    
    public Vector3f getLeftPosition(){
        return barraIzq.getWorldTranslation();
    }
    
    public Vector3f getRightPosition(){
        return barraDcha.getWorldTranslation();
    }
    
    public float getHeight(){
        return alto;
    }
}

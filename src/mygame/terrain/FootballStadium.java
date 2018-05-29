/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Sergio
 */
public class FootballStadium {
    private static final float LARGO_CAMPO = 100f;
    private static final float ANCHO_CAMPO = 67f;

    private static final float RADIO_BARRAS_PORTERIAS = 0.4f;

    private static final float LARGO_PORTERIA = 20f;
    private static final float ALTO_PORTERIA = 8f;
    
    private Node campoDeFutbol;
    private Goal porteria1;
    private Goal porteria2;
    private Grass cesped;
    
    /*private RigidBodyControl fisicaPorteria1, fisicaPorteria2, fisicaSuelo, fisicaInvPorteria1, fisicaInvPorteria2,
            fisicaLateral1, fisicaLateral2;*/
    
    public FootballStadium(AssetManager assetManager){
        porteria1 = new Goal(30, 30, RADIO_BARRAS_PORTERIAS, ALTO_PORTERIA, LARGO_PORTERIA);
        porteria2 = new Goal(30, 30, RADIO_BARRAS_PORTERIAS, ALTO_PORTERIA, LARGO_PORTERIA);
        cesped = new Grass(ANCHO_CAMPO, LARGO_CAMPO);
        Node porteriaNode1 = porteria1.getNode();
        Node porteriaNode2 = porteria2.getNode();
        Node cespedNode = cesped.getNode();

        //Creamos el campo de futbol y le anadimos los componentes
        campoDeFutbol = new Node();
        campoDeFutbol.attachChild(porteriaNode1);
        campoDeFutbol.attachChild(porteriaNode2);
        campoDeFutbol.attachChild(cespedNode);

        //Anadimos un material al campo de futbol completo
        Material matPorteria = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matPorteria.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.dds"));
        porteriaNode1.setMaterial(matPorteria);
        porteriaNode2.setMaterial(matPorteria);
        
        Material matCesped = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matCesped.setTexture("DiffuseMap", assetManager.loadTexture("Materials/campoFutbol.jpg"));
        cespedNode.setMaterial(matCesped);

        //Movemos las figuras para que queden ordenadas
        porteriaNode1.move(-LARGO_PORTERIA/2, 0, LARGO_CAMPO-RADIO_BARRAS_PORTERIAS-3); //-3 para equilibrar algo mas alante
        porteriaNode2.move(-LARGO_PORTERIA/2, 0, -LARGO_CAMPO+RADIO_BARRAS_PORTERIAS+3); //3 para equilibrar algo mas alante
        cespedNode.move(0, 0.1f, 0);
        
        //Paredes invisibles
        Material matInvisible = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matInvisible.setColor("Color", new ColorRGBA(0, 0, 0, 0f));
        matInvisible.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        //Paredes EXTRA
        /*
        Geometry pared_front_1 = new Geometry("pared_front_1",new Box(ANCHO_CAMPO/2 -LARGO_PORTERIA/4,25,1));
        Geometry pared_back_1 = new Geometry("pared_front_1",new Box(ANCHO_CAMPO/2 -LARGO_PORTERIA/4,25,1)); 
        Geometry pared_front_2 = new Geometry("pared_front_1",new Box(ANCHO_CAMPO/2 -LARGO_PORTERIA/4,25,1));
        Geometry pared_back_2 = new Geometry("pared_front_1",new Box(ANCHO_CAMPO/2 -LARGO_PORTERIA/4,25,1));
        Geometry pared_front_lateral_1 = new Geometry("pared_front_1",new Box(0.5f,25,10));
        Geometry pared_front_lateral_2 = new Geometry("pared_front_1",new Box(0.5f,25,10));
        Geometry pared_back_lateral_1 = new Geometry("pared_front_1",new Box(0.5f,25,10));
        Geometry pared_back_lateral_2 = new Geometry("pared_front_1",new Box(0.5f,25,10));
        
        pared_front_1.move(-(ANCHO_CAMPO +LARGO_PORTERIA/2)/2,1,LARGO_CAMPO-10);
        pared_front_1.setMaterial(matInvisible);
        pared_front_2.move((ANCHO_CAMPO +LARGO_PORTERIA/2)/2,1,LARGO_CAMPO-10);
        pared_front_2.setMaterial(matInvisible);
        pared_front_lateral_1.move(porteria1.getLeftPosition().x,1,LARGO_CAMPO);
        pared_front_lateral_1.setMaterial(matInvisible);
        pared_front_lateral_2.move(porteria1.getRightPosition().x,1,LARGO_CAMPO);
        pared_front_lateral_2.setMaterial(matInvisible);
        
        pared_back_1.move(-(ANCHO_CAMPO +LARGO_PORTERIA/2)/2,1,-(LARGO_CAMPO-10));
        pared_back_1.setMaterial(matInvisible);
        pared_back_2.move((ANCHO_CAMPO +LARGO_PORTERIA/2)/2,1,-(LARGO_CAMPO-10));
        pared_back_2.setMaterial(matInvisible);
        pared_back_lateral_1.move(porteria1.getLeftPosition().x,1,-LARGO_CAMPO);
        pared_back_lateral_1.setMaterial(matInvisible);
        pared_back_lateral_2.move(porteria1.getRightPosition().x,1,-LARGO_CAMPO);
        pared_back_lateral_2.setMaterial(matInvisible);
        
        campoDeFutbol.attachChild(pared_front_1);campoDeFutbol.attachChild(pared_back_1);
        campoDeFutbol.attachChild(pared_front_2);campoDeFutbol.attachChild(pared_back_2);
        campoDeFutbol.attachChild(pared_front_lateral_1);campoDeFutbol.attachChild(pared_front_lateral_2);
        campoDeFutbol.attachChild(pared_back_lateral_1);campoDeFutbol.attachChild(pared_back_lateral_2);
        */
        //Paredes EXTRA
        
        Box b = new Box(ANCHO_CAMPO,25,1);
        Box b2 = new Box(LARGO_CAMPO,25,1);
        Geometry invPorteria1 = new Geometry("invPorteria1", b);Geometry invPorteria2 = new Geometry("invPorteria2", b);
        Geometry lateral1 = new Geometry("lateral1", b2);Geometry lateral2 = new Geometry("lateral2", b2);
        
        invPorteria1.setMaterial(matInvisible);invPorteria2.setMaterial(matInvisible);lateral1.setMaterial(matInvisible);lateral2.setMaterial(matInvisible);
        
        invPorteria1.setLocalTranslation(new Vector3f(cespedNode.getWorldTranslation().clone().setZ(porteria1.getNode().getWorldTranslation().z+2.9f)));
        invPorteria2.setLocalTranslation(new Vector3f(cespedNode.getWorldTranslation().clone().setZ(porteria2.getNode().getWorldTranslation().z-3.1f)));
        lateral1.setLocalTranslation(new Vector3f().setX(ANCHO_CAMPO-0.1f)); //para que no se salga la pelota del campo el 0.1
        lateral2.setLocalTranslation(new Vector3f().setX(-ANCHO_CAMPO+0.1f));
        lateral1.rotate(0, (float)Math.PI/2, 0);
        lateral2.rotate(0, (float)Math.PI/2, 0);
        /*invPorteria1.move(0, b.getYExtent(), 0);
        invPorteria2.move(0, b.getYExtent(), 0);
        lateral1.move(0, b2.getYExtent(), 0);
        lateral2.move(0, b2.getYExtent(), 0);*/
        
        campoDeFutbol.attachChild(invPorteria1);campoDeFutbol.attachChild(invPorteria2);campoDeFutbol.attachChild(lateral1);campoDeFutbol.attachChild(lateral2);
        
    }
    
    public Node getNode(){
        return campoDeFutbol;
    }
    
    public Goal getPorteria1(){
        return porteria1;
    }
    
    public Goal getPorteria2(){
        return porteria2;
    }
    
    public float getHeight(){
        return porteria1.getHeight();
    }
    
}

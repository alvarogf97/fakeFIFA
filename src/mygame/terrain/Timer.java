/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 *
 * @author Alvaro
 */
public class Timer {
    
    private final int FONT_SIZE = 2;

    private float time;
    
    private BitmapFont guiFont;
    private BitmapText text;
    private Node guiNode;
    private boolean enabled;
    
    public Timer(AssetManager assetManager, AppSettings settings, Node guiNode){
        
        this.time = 0;
        this.guiNode = guiNode;
        this.enabled = true;
        
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize() * FONT_SIZE);
        text.setColor(ColorRGBA.Red);
        text.setText("time: " + time);
        float X= settings.getWidth()*0.7f;
        float Y= settings.getHeight()*0.95f;
        text.setLocalTranslation( X, Y, 0);
        guiNode.attachChild(text);
        
    }
    
    public void addTime(float tpf){
        
        if(this.isEnabled()){
            this.time += tpf;
            int hours = (int)this.time / 3600;
            int minutes = (int)(this.time % 3600) / 60;
            int seconds = (int)this.time % 60;
            text.setText("time: " + hours +":"+minutes+":"+seconds);
        }
    }
    
    public float getTime(){
        return this.time;
    }
    
    public void dettachFromParent(){
        this.guiNode.detachChild(text);
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public synchronized void pause(){
        this.enabled = false;
    }
    
    public synchronized void resume(){
        this.enabled = true;
    }
    
    
    
}

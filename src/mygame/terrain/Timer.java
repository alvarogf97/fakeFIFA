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
    
    public Timer(AssetManager assetManager, AppSettings settings, Node guiNode){
        
        this.time = 0;
        
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize() * FONT_SIZE);
        text.setColor(ColorRGBA.Red);
        text.setText("time: " + time);
        float X= settings.getWidth()*0.5f;
        float Y= settings.getHeight()*0.95f;
        text.setLocalTranslation( X, Y, 0);
        guiNode.attachChild(text);
        
    }
    
    public void addTime(float tpf){
        
        this.time += tpf;
        text.setText("time: " + time);
        
    }
    
}

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
public class Matcher {
    
    private final int FONT_SIZE = 2;
    
    private int goals_team_front;
    private int goals_team_back;
    
    private BitmapFont guiFont;
    private BitmapText text;
    
    public Matcher(AssetManager assetManager, AppSettings settings, Node guiNode){
        
        goals_team_front = 0;
        goals_team_back = 0;
        
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize() * FONT_SIZE);
        text.setColor(ColorRGBA.Red);
        text.setText(goals_team_front + "  /  " + goals_team_back);
        float X= settings.getWidth()*0.05f;
        float Y= settings.getHeight()*0.95f;
        text.setLocalTranslation( X, Y, 0);
        guiNode.attachChild(text);
        
    }
    
    public void addGoalToFrontTeam(){
        
        this.goals_team_front ++;
        text.setText(goals_team_front + "  /  " + goals_team_back);
        
    }
    
    public void addGoalToBackTeam(){
        
        this.goals_team_back ++;
        this.text.setText(goals_team_front + "  /  " + goals_team_back);
        
    }
    
}

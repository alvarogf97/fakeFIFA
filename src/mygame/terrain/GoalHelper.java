/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Alvaro
 */
public class GoalHelper {
    
    private Geometry goal_front;
    private Geometry goal_back;
    
    public GoalHelper(AssetManager assetManager){
        
        Box malle_goal_front = new Box(9,8,0.1f);
        Box malle_goal_back = new Box(9,8,0.1f);
        
        
        Material matInvisible = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matInvisible.setColor("Color", new ColorRGBA(0, 0, 0, 0f));
        matInvisible.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        

        BoundingBox bound_goal_front = new BoundingBox();
        BoundingBox bound_goal_back = new BoundingBox();
        
        malle_goal_front.setBound(bound_goal_front);
        malle_goal_front.updateBound();
        
        malle_goal_back.setBound(bound_goal_back);
        malle_goal_back.updateBound();
        
        goal_front = new Geometry("goal_front", malle_goal_front);
        goal_back = new Geometry("goal_back", malle_goal_back);
        
        goal_front.setMaterial(matInvisible);
        goal_back.setMaterial(matInvisible);
        
        goal_front.move(new Vector3f(0,0,-90));
        goal_back.move(new Vector3f(0,0,90));
        
        bound_goal_front.setCenter(goal_front.getWorldTranslation());
        bound_goal_back.setCenter(goal_back.getWorldTranslation());
    }
    
    public Geometry getGoal_front(){
        return this.goal_front;
    }
    
    public Geometry getGoal_back(){
        return this.goal_back;
    }
    
}

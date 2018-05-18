/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.utils;

import com.jme3.math.Vector3f;

/**
 *
 * @author Alvaro
 */
public class Vector3fUtilities {
    
    public static float module(Vector3f vector){
        double x_2 = Math.pow(vector.x, 2);
        double y_2 = Math.pow(vector.y, 2);
        double z_2 = Math.pow(vector.z, 2);
        return (float) Math.sqrt(x_2+y_2+z_2);
    }
    
}

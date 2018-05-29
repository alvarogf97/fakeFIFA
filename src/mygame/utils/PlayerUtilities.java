/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.utils;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.Iterator;
import mygame.models.Player;

/**
 *
 * @author Alvaro
 */
public class PlayerUtilities {
    
    public static boolean hasObstacle(Player player, Vector3f direction){
        Ray rayo = new Ray(player.getGeometry().getWorldTranslation(), direction);
        CollisionResults results_mates = new CollisionResults();
        CollisionResults results_oponents = new CollisionResults();
        player.getTeam().getOponents().collideWith(rayo, results_oponents);
        player.getTeam().getMates().collideWith(rayo, results_mates);

        return (results_mates.size() > 1 && !avant(player,results_mates.getCollision(1).getGeometry()))
                || results_oponents.size() > 0 && !avant(player,results_oponents.getClosestCollision().getGeometry());
    }

    public static boolean hasObstacle(Player player, Vector3f direction, float distance) {
        Ray rayo = new Ray(player.getGeometry().getWorldTranslation(), direction);
        CollisionResults results_mates = new CollisionResults();
        CollisionResults results_oponents = new CollisionResults();
        player.getTeam().getOponents().collideWith(rayo, results_oponents);
        player.getTeam().getMates().collideWith(rayo, results_mates);

        return (results_mates.size() > 1 && results_mates.getCollision(1).getDistance() < distance)
                || (results_oponents.size() > 0 && results_oponents.getClosestCollision().getDistance() < distance);
    }
    
    public int numberOfOponents(Player player, Vector3f direction, int radius){
        int enemyNumber = 0;
            CollisionResults results = new CollisionResults();
            Ray rayo = new Ray(player.getGeometry().getWorldTranslation(), direction);
            player.getTeam().getOponents().collideWith(rayo, results);
            Iterator<CollisionResult> iter = results.iterator();
            while(iter.hasNext()){
                if(iter.next().getGeometry().getWorldTranslation().distance(player.getGeometry().getWorldTranslation())<=radius){
                    enemyNumber++;
                }
            }
        return enemyNumber;
    }
    
    private static boolean avant(Player player, Geometry geom){
        float distance = geom.getWorldTranslation().distance(player.getGeometry().getWorldTranslation());
        return distance >= 5;
    }
    
    public static Vector3f WhereShouldIGo(Player player){
        if(player.getTeam().getTerrain() == 0){
            if(player.getBall().getGeometry().getWorldTranslation().z>=player.getBall().getGeometry().getWorldTranslation().z
                    && player.getBall().getGeometry().getWorldTranslation().z>=-60){
                Vector3f pos = player.getBall().getGeometry().getWorldTranslation();
                Vector3f velocity = player.getBall().getPhysics().getLinearVelocity();
                float x = pos.x + velocity.x * 0.15f * -4 * 0.5f;
                float y = pos.y;
                float z = pos.z + velocity.z * 0.15f * -4 * 0.5f;
                return new Vector3f(x, y, z);
            }else{
                Vector3f pos = player.getBall().getGeometry().getWorldTranslation();
                Vector3f velocity = player.getBall().getPhysics().getLinearVelocity();
                float x = pos.x + velocity.x * 0.15f * 9 * 0.5f;
                float y = pos.y;
                float z = pos.z + velocity.z * 0.15f * 9 * 0.5f;
                return new Vector3f(x, y, z);
            }
        }else{
            if(player.getBall().getGeometry().getWorldTranslation().z<=player.getBall().getGeometry().getWorldTranslation().z
                    && player.getBall().getGeometry().getWorldTranslation().z<=60){
                Vector3f pos = player.getBall().getGeometry().getWorldTranslation();
                Vector3f velocity = player.getBall().getPhysics().getLinearVelocity();
                float x = pos.x + velocity.x * 0.15f * -4 * 0.5f;
                float y = pos.y;
                float z = pos.z + velocity.z * 0.15f * -4 * 0.5f;
                return new Vector3f(x, y, z);
            }else{
                Vector3f pos = player.getBall().getGeometry().getWorldTranslation();
                Vector3f velocity = player.getBall().getPhysics().getLinearVelocity();
                float x = pos.x + velocity.x * 0.15f * 9 * 0.5f;
                float y = pos.y;
                float z = pos.z + velocity.z * 0.15f * 9 * 0.5f;
                return new Vector3f(x, y, z);
            }
        }
    }
    
}

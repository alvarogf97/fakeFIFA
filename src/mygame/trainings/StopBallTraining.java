/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.trainings;

import com.jme3.math.Vector3f;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.models.Player;
import mygame.shots.ShotType;
import weka.core.Instance;

/**
 *
 * @author Sergio
 */
public class StopBallTraining extends Training{
    
    public StopBallTraining(Player player) throws FileNotFoundException, IOException {
        super(player, 6, "stopBall");
    }

    /** this method is used to learn how to stop the ball
     * @param studyObjects [0] is the Vector3f position of the ball when it is at the same Z position than the goalkeeper
     *                     [1] is the time the ball needs to be at the same Z position than the goalkepper
     *                     [2] is the type of shot the player did
     *                     [3] is the force needed to stop the ball
    */
    @Override
    public void learn(Object... studyObjects) {
        Vector3f posPelota = (Vector3f) studyObjects[0];
        Vector3f posPortero = this.player.getGeometry().getWorldTranslation();
        
        Vector3f direction = posPelota.subtract(posPortero).normalize();
        double distance = posPelota.distance(posPortero);
        Float time = (Float) studyObjects[1];
        ShotType shot = (ShotType) studyObjects[2];
        float fuerza = 0; // Obtenemos segun la formula la fuerza que hay que ejercer

        Instance instance = new Instance(casosDePrueba.numAttributes());

        instance.setDataset(casosDePrueba);

        instance.setValue(0, direction.x);
        instance.setValue(1, direction.y);
        instance.setValue(2, direction.z);
        instance.setValue(3, distance);
        instance.setValue(4, time);
        instance.setValue(5, ShotType.valueOf(shot));
        instance.setValue(6, fuerza);
            
        StopBallThread thread = new StopBallThread(player,time,this,instance);
        thread.start();

    }
    
    /**
     * Used by StopBallThread class to save the instances if they fullfilled the objective
     * @param instance
     * @throws Exception 
     */
    protected synchronized void saveInKnowledge(Instance instance) throws Exception{
        casosDePrueba.add(instance);
        conocimiento.buildClassifier(casosDePrueba);
        this.saveData(instance);
    }
    
    /** 
     * this method
     * @param studyObjects [0] is the Vector3f position of the ball when it is at the same Z position than the goalkeeper
     *                     [1] is the time the ball needs to be at the same Z position than the goalkepper
     *                     [2] is the type of shot the player did
     * @return the prediction done by IA
    */
    @Override
    public float useKnowledge(Object... studyObjects) {  
        try {
            super.useKnowledge(studyObjects);
        } catch (Exception ex) {
            Logger.getLogger(StopBallTraining.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector3f posPelota = (Vector3f) studyObjects[0];
        Vector3f posPortero = this.player.getGeometry().getWorldTranslation();
        
        Vector3f direction = posPelota.subtract(posPortero).normalize();
        double distance = posPelota.distance(posPortero);
        Double time = (Double) studyObjects[1];
        ShotType shot = (ShotType) studyObjects[2];
        float fuerza = 0;
        
        Instance instance = new Instance(casosDePrueba.numAttributes());

        instance.setDataset(casosDePrueba);
        
        instance.setValue(0, direction.x);
        instance.setValue(1, direction.y);
        instance.setValue(2, direction.z);
        instance.setValue(3, distance);
        instance.setValue(4, time);
        instance.setValue(5, ShotType.valueOf(shot));
        
        try {
            fuerza = (float)conocimiento.classifyInstance(instance);
        } catch (Exception ex) {
            Logger.getLogger(PassTraining.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return fuerza;
    }

    @Override
    protected boolean isCorrect(Object... studyObjects) {
        return true;
    }
    
}

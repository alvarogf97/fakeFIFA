/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.trainings;

import com.jme3.math.Vector3f;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.models.Defensor;
import mygame.models.Player;
import mygame.shots.ShotType;
import weka.classifiers.trees.M5P;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Alvaro
 */
public class PassTraining {
    
    private String pathFile;
    private Player player;
    private Instances casosDePruebaPasarPelota;
    private M5P conocimientoPasar;
    private boolean build;
    
    public PassTraining(Player player) throws FileNotFoundException, IOException{
        this.pathFile = System.getProperty("user.dir") + "/src/resources/arff/pasar/" + player.getFilePasarName() + ".arff";
        this.player = player;
        this.casosDePruebaPasarPelota = new Instances(new BufferedReader(new FileReader(pathFile)));
        casosDePruebaPasarPelota.setClassIndex(4);
        this.conocimientoPasar = new M5P();
        build = false;
    }
    
    public void learn(Player pToPass) throws Exception{
        
        Vector3f direction = pToPass.getGeometry().getWorldTranslation().subtract(this.player.getBall().getGeometry().getWorldTranslation()).normalize();
        float distance = pToPass.getGeometry().getWorldTranslation().distance(this.player.getGeometry().getWorldTranslation());
        int modulo = (new Random().nextInt(Defensor.PASAR_MAX)) + Defensor.PASAR_MIN;

        if (isCorrect(direction, distance, modulo)) {

            Instance instance = new Instance(casosDePruebaPasarPelota.numAttributes());

            instance.setDataset(casosDePruebaPasarPelota);

            instance.setValue(0, direction.x);
            instance.setValue(1, direction.y);
            instance.setValue(2, direction.z);
            instance.setValue(3, distance);
            instance.setValue(4, modulo);
            casosDePruebaPasarPelota.add(instance);
            conocimientoPasar.buildClassifier(casosDePruebaPasarPelota);
            
            this.saveData();
        }

        this.player.pass(modulo, direction, distance);
    }
    
    public void useKnowledge(Player pToPass) throws Exception{
        
        if(!build){
            this.buildKnowldegeClassifier();
        }
        
        int modulo = 0;
        Vector3f direction = pToPass.getGeometry().getWorldTranslation().subtract(this.player.getBall().getGeometry().getWorldTranslation()).normalize();
        float distance = pToPass.getGeometry().getWorldTranslation().distance(this.player.getGeometry().getWorldTranslation());
        
        Instance instance = new Instance(casosDePruebaPasarPelota.numAttributes());

        instance.setDataset(casosDePruebaPasarPelota);
        
        instance.setValue(0, direction.x);
        instance.setValue(1, direction.y);
        instance.setValue(2, direction.z);
        instance.setValue(3, distance);
        
        try {
            modulo = Math.round((float)conocimientoPasar.classifyInstance(instance));
        } catch (Exception ex) {
            Logger.getLogger(PassTraining.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        player.pass(modulo, direction, distance);
    }
    
    private void saveData(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.pathFile))) {
            writer.write(casosDePruebaPasarPelota.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("error al escribir en el fichero");
        }
    }
    
    private boolean isCorrect(Vector3f direction, float distancia, int modulo){
        boolean res;
        Vector3f pos = this.player.getBall().getGeometry().getWorldTranslation();
        Vector3f velocity = (direction.mult(modulo)).divide(1);
        float x = pos.x + velocity.x * 0.15f * 16 * 0.5f;
        float y = pos.y;
        float z = pos.z + velocity.z * 0.15f * 16 * 0.5f;
        Vector3f posFinalIn4Secs = new Vector3f(x, y, z);
        res = posFinalIn4Secs.distance(pos) <= distancia + 1 || posFinalIn4Secs.distance(pos) >= distancia - 1;
        return res;
    }
    
    private void buildKnowldegeClassifier() throws Exception{
        this.conocimientoPasar.buildClassifier(casosDePruebaPasarPelota);
        this.build = true;
    }
}

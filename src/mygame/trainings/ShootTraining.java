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
import mygame.models.Leading;
import mygame.models.Player;
import mygame.shots.ShotType;
import mygame.terrain.Goal;
import weka.classifiers.trees.M5P;
import weka.core.Instance;
import weka.core.Instances;
/**
 *
 * @author Alberto Ramírez Mena
 */
public class ShootTraining {
    private String pathFile;
    private Player player;
    private Instances casosDePruebaChutar;
    private M5P conocimientoChutar;
    private boolean build;
    private boolean alto = false;
    private final static int CHUTAR_MAX = 40;
    private final static int CHUTAR_MIN = 20;
    
    public ShootTraining(Leading player) throws FileNotFoundException, IOException{
        this.pathFile = System.getProperty("user.dir") + "/src/resources/arff/chutar/" + player.getFileChutarName() + ".arff";
        this.player = player;
        this.casosDePruebaChutar = new Instances(new BufferedReader(new FileReader(pathFile)));
        casosDePruebaChutar.setClassIndex(4);
        this.conocimientoChutar = new M5P();
        build = false;
    }
    
    public void learn(Goal goal) throws Exception{
        Vector3f objetivo = this.aimShoot(goal);
        Vector3f direction = objetivo.subtract(this.player.getBall().getGeometry().getWorldTranslation()).normalize();
        float distance = objetivo.distance(this.player.getGeometry().getWorldTranslation());
        int modulo = (new Random().nextInt(CHUTAR_MAX-CHUTAR_MIN)) + CHUTAR_MIN;

        if (isCorrect(direction, distance, modulo)) {

            Instance instance = new Instance(casosDePruebaChutar.numAttributes());

            instance.setDataset(casosDePruebaChutar);

            instance.setValue(0, direction.x);
            instance.setValue(1, direction.y);
            instance.setValue(2, direction.z);
            instance.setValue(3, distance);
            instance.setValue(4, modulo);
            casosDePruebaChutar.add(instance);
            conocimientoChutar.buildClassifier(casosDePruebaChutar);
            
            this.saveData(instance);
        }

        this.player.shoot(modulo, direction);
        if(alto)
            player.getTeam().predictBall(ShotType.ALTO);
        else
            player.getTeam().predictBall(ShotType.BAJO);
    }
    
    public void useKnowledge(Goal goal) throws Exception{
        
        if(!build){
            this.buildKnowldegeClassifier();
        }
        
        Vector3f objetivo = this.aimShoot(goal);
        int modulo = 0;
        Vector3f direction = objetivo.subtract(this.player.getBall().getGeometry().getWorldTranslation()).normalize();
        float distance = objetivo.distance(this.player.getGeometry().getWorldTranslation());
        
        Instance instance = new Instance(casosDePruebaChutar.numAttributes());

        instance.setDataset(casosDePruebaChutar);
        
        instance.setValue(0, direction.x);
        instance.setValue(1, direction.y);
        instance.setValue(2, direction.z);
        instance.setValue(3, distance);
        
        try {
            modulo = Math.round((float)conocimientoChutar.classifyInstance(instance));
        } catch (Exception ex) {
            Logger.getLogger(PassTraining.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.player.shoot(modulo, direction);
        if(alto)
            player.getTeam().predictBall(ShotType.ALTO);
        else
            player.getTeam().predictBall(ShotType.BAJO);
    }
    
    private void saveData(Instance instance){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.pathFile,true))) {
            writer.write("\n" + instance.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("error al escribir en el fichero");
        }
    }
    
    private void buildKnowldegeClassifier() throws Exception{
        this.conocimientoChutar.buildClassifier(casosDePruebaChutar);
        this.build = true;
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
    
    private Vector3f aimShoot(Goal goal){
        float x;
        float y;
        if(new Random().nextBoolean()){
            x = goal.getRightPosition().x - new Random().nextInt(5);
        } else {
            x = goal.getLeftPosition().x + new Random().nextInt(5);
        } 
        if(new Random().nextBoolean()){
            y = 0;
            alto = false;
        }else{
            y = goal.getHeight();
            alto = true;
        }
        
        float z = goal.getMiddlePosition().z;
        if(z > 0)
            z = +100;
        else
            z = -100;
        
        return new Vector3f(x, y, z);
    }
}

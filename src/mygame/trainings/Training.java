/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.trainings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import mygame.models.Player;
import weka.classifiers.trees.M5P;
import weka.core.Instances;

/**
 *
 * @author Sergio
 */
public abstract class Training {
    
    private String pathFile;
    
    Player player;
    Instances casosDePrueba;
    M5P conocimiento;
    boolean build;
    
    public Training(Player player, int classIndex, String packageName) throws FileNotFoundException, IOException{
        this.pathFile = System.getProperty("user.dir") + "/src/resources/arff/" + packageName + "/" + player.getFileStopBallName() + ".arff";
        this.player = player;
        this.casosDePrueba = new Instances(new BufferedReader(new FileReader(pathFile)));
        casosDePrueba.setClassIndex(classIndex);
        this.conocimiento = new M5P();
        build = false;
    }
    
    public abstract void learn(Object... studyObjects) throws Exception;
    
    public float useKnowledge(Object... studyObjects) throws Exception {
        if(!build){
            this.buildKnowldegeClassifier();
        }
        return 0;
    }
    
    /**
     * this function is used in the method learn() as a heuristic to know if the objective is fulfilled
     * @param studyObjects the objects needed by the class
     * @return true if the objetive is fulfilled, otherwise false.
     */
    protected abstract boolean isCorrect(Object... studyObjects);
    
    protected void saveData(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.pathFile))) {
            writer.write(casosDePrueba.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error al escribir en el fichero de salida.");
        }
    }
    
    protected void buildKnowldegeClassifier() throws Exception{
        this.conocimiento.buildClassifier(casosDePrueba);
        this.build = true;
    }
    
}

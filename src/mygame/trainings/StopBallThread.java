/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.trainings;

import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.models.Player;
import weka.core.Instance;

/**
 *
 * @author Sergio
 */
class StopBallThread extends Thread {
    
    private int milis;
    private Player player;
    private StopBallTraining training;
    private Instance instance;
    
    public StopBallThread(Player goalkeeper, Double time, StopBallTraining training, Instance instance){
        int milis = (int) (time*1000 + 1000);
        player = goalkeeper;
        this.training = training;
        this.instance = instance;
    }
    
    
    @Override
    public void run(){
        int golesAntes = player.getTeam().getOponentGoals();
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ex) {
            Logger.getLogger(StopBallThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(golesAntes == player.getTeam().getOponentGoals() + 1){
            try {
                training.saveInKnowledge(instance);
            } catch (Exception ex) {
                Logger.getLogger(StopBallThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}

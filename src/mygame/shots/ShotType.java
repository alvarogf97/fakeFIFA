/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.shots;

/**
 *
 * @author Sergio
 */
public enum ShotType {
    ALTO, BAJO;

    public static String valueOf(ShotType shot) {
        switch(shot){
            case ALTO:
                return "alto";
            case BAJO:
                return "bajo";
            default:
                return null;
        }
    }
}
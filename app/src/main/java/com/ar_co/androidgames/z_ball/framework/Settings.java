package com.ar_co.androidgames.z_ball.framework;

import android.util.Log;

import com.ar_co.androidgames.z_ball.interfaces.FileIO;

import java.io.IOException;
import java.io.Serializable;

public class Settings implements Serializable{
    private static final int VERSION = 3;
    private float score;
    private float time;
    private int platforms;
    private int obstacles;
    private int tries = 0;
    private static final int ATTEMPTS = 10;

    private static Settings settings;

    private Settings(){
        score = 0;
    }

    public static Settings getSettings(FileIO fileIO){
        if(settings == null){
            try{
                settings = (Settings) fileIO.readObject("settings");
            }catch(IOException e){
                settings = new Settings();
            }

            if(settings == null){
                settings = new Settings();
            }
        }
        return settings;
    }

    public static GameActivity getActivity(GLGame game){
        return game;
    }

    public void save(FileIO fileIO){
        try{
            fileIO.writeObject(settings, "settings");
        }catch (IOException e) {
            tries++;
            if(tries < ATTEMPTS) {
                save(fileIO);
            }else{
                throw new RuntimeException("Failed to save");
            }
        }
    }

    public void pushHighScore(float score) {
        if (this.score < score) {
            this.score = score;
        }
    }

    public float getHighScore(){
        return score;
    }

}

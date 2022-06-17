package com.ar_co.androidgames.z_ball.game.controllers;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Controller;

public class ScoreSystem {

    private static final float SCORE_BASE = 0.01f;
    private static final float TIME_STAMP = 0.1f;

    private static ScoreSystem scoreSystem;
    private float score = 0;
    private float time = 0;

    private int obstacles = 0;
    private int platforms = 0;

    private Controller scoreMonitor;

    private ScoreSystem(){
        scoreMonitor = new Controller(TIME_STAMP){
            @Override
            public void update(){
                score += SCORE_BASE;
            }
        };
    }

    public static ScoreSystem getInstance(){
        if(scoreSystem == null){
            scoreSystem = new ScoreSystem();
        }
        return scoreSystem;
    }

    public void update(float deltaTime){
        time += deltaTime;
        scoreMonitor.update(deltaTime);
    }

    public void countObstacle(){
        obstacles++;
    }

    public void countPlatform(){
        platforms++;
    }

    public float getScore() {
        return score;
    }

    public float getTime(){
        return time;
    }

    public int getPlatformCount(){
        return platforms;
    }

    public int getObstacleCount(){
        return obstacles;
    }

    public void dispose(){
        Log.i("ScoreSystem", "Disposed");
        score = 0;
        time = 0;

        obstacles = 0;
        platforms = 0;
        scoreSystem = null;
    }

}

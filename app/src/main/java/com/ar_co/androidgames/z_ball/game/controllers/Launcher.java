package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.Ball;

public class Launcher {

    private static final float LAUNCH_SPEED = 0.001f;

    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private Ball ball;
    private Controller launch;

    private boolean launching;

    //private float launchX;
    //private float launchY;

    public Launcher(Ball ball, float width, float height){
        this.ball = ball;
        this.WORLD_WIDTH = width;
        this.WORLD_HEIGHT = height;

        setLaunch();
    }

    public void prepareLaunch(float x, float y){
        launching = true;
        ball.setX(x);
        ball.setY(y);
    }

    public void launch(float deltaTime){
        launch.update(deltaTime);
    }

    public boolean isLaunching(){
        return launching;
    }

    public boolean hasLaunched(){
        return ball.getX() > 0 && ball.getX2() < WORLD_WIDTH;
    }

    private void setLaunch(){
        launch = new Controller(LAUNCH_SPEED){
            @Override
            public void update(){
                if(hasLaunched()){
                    launching = false;
                }
                ball.advance();
            }
        };
    }
}

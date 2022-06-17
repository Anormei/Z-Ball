package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.Random;

public class Trail extends Model {

    private static final float SPEED = 0.02f;
    private static final float ROTATE_SPEED = 0.01f;
    private static Random r = new Random();

    private static PoolHandler poolHandler = new PoolHandler();

    private Controller resizer;
    private Controller rotater;

    private boolean expired;
    private int angle;

    private Trail(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/trail"));
        createControllers();
    }

    private void createControllers(){
        resizer = new Controller(SPEED){
            @Override
            public void update(){
                setWidth(width + 1);
                setHeight(height + 1);
                setCoord(x - 0.5f, y - 0.5f);
                if(width > 60f){
                    if(alpha < 1.0f){
                        setAlpha(alpha + 0.01f);
                    }else{
                        expired = true;
                    }
                }
            }
        };

        rotater = new Controller(ROTATE_SPEED){
            @Override
            public void update(){
                rotate((angle++ % 360f));
            }
        };
    }

    public static Trail newInstance(GLGame game){
        if(!poolHandler.isInitialized(game)){
            final GLGame g = game;
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Trail createObject() {
                    return new Trail(g);
                }
            }, 64);
        }

        Trail trail = (Trail)poolHandler.getPool().newObject();
        trail.angle = r.nextInt(361);
        trail.rotate(trail.angle);
        trail.expired = false;
        trail.setAlpha(0.3f);
        trail.setWidth(20f);
        trail.setHeight(20f);
        return trail;
    }

    public static void remove(Trail trail){
        poolHandler.getPool().free(trail);
    }

    public void update(float deltaTime){
        resizer.update(deltaTime);
        rotater.update(deltaTime);
    }

    public boolean isExpired(){
        return expired;
    }
}

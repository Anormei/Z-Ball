package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.Random;

public class LaunchDust extends Model {

    private static final float VELOCITY = 1f;
    private static final float SPEED = 0.005f;
    private static final float FADE_SPEED = 0.02f;
    private static final float MAX_TIMER = 0.5f;

    private static PoolHandler poolHandler = new PoolHandler();
    private static Random r = new Random();

    private float velocityX;
    private float velocityY;

    private float timer;
    private boolean expired;

    private Controller advancer;
    private Controller fader;

    private int rotation;

    public LaunchDust(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/lsparkle"));
        setControllers();
    }

    private void setControllers(){
        advancer = new Controller(SPEED){
            @Override
            public void update(){
                setX(x + velocityX);
                setY(y + velocityY);
                rotate(rotation);
                rotation = (rotation + 1) % 360;
            }
        };

        fader = new Controller(FADE_SPEED){
            @Override
            public void update(){
                if(alpha < 1f){
                    setAlpha(alpha + 0.01f);
                }else{
                    expired = true;
                }
            }
        };
    }

    public static LaunchDust newInstance(GLGame game){
        if(!poolHandler.isInitialized(game)){
            final GLGame g = game;
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public LaunchDust createObject() {
                    return new LaunchDust(g);
                }
            }, 1000);
        }

        LaunchDust ld = (LaunchDust)poolHandler.getPool().newObject();
        float xVelocity = r.nextFloat() * 2f;
        float yVelocity = 2f - xVelocity;
        xVelocity = r.nextBoolean() ? -xVelocity : xVelocity;
        yVelocity = r.nextBoolean() ? -yVelocity : yVelocity;

        ld.velocityX = xVelocity;
        ld.velocityY = yVelocity;

        ld.setAlpha(r.nextFloat());
        ld.timer = 0;
        ld.expired = false;

        ld.rotation = r.nextInt(361);

        ld.setVisibility(true);
        ld.setAlpha(0.2f);

        return ld;
    }

    public static void remove(LaunchDust ex){
        poolHandler.getPool().free(ex);
    }

    public void update(float deltaTime){
        timer += deltaTime;
        advancer.update(deltaTime);
        if(timer >= MAX_TIMER){
            fader.update(deltaTime);
        }
    }

    public boolean isExpired(){
        return expired;
    }

}

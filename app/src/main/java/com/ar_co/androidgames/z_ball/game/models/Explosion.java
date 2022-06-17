package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.Random;

public class Explosion extends Model {

    private static final float VELOCITY = 1f;
    private static final float SPEED = 0.003f;
    private static final float MAX_TIMER = 0.66f;

    private static PoolHandler poolHandler = new PoolHandler();
    private static Random r = new Random();

    public float[] tint = new float[3];

    private float velocityX;
    private float velocityY;

    private float timer;
    private boolean expired;

    private Controller advancer;

    public Explosion(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/explosion"));
        setControllers();
    }

    private void setControllers(){
        advancer = new Controller(SPEED){
            @Override
            public void update(){
                setX(x + velocityX);
                setY(y + velocityY);
            }
        };
    }

    public static Explosion newInstance(GLGame game){
        if(!poolHandler.isInitialized(game)){
            final GLGame g = game;
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Explosion createObject() {
                    return new Explosion(g);
                }
            }, 1000);
        }

        Explosion ex = (Explosion)poolHandler.getPool().newObject();
        float xVelocity = r.nextFloat() * 2f;
        float yVelocity = 2f - xVelocity;
        xVelocity = r.nextBoolean() ? -xVelocity : xVelocity;
        yVelocity = r.nextBoolean() ? -yVelocity : yVelocity;

        ex.velocityX = xVelocity;
        ex.velocityY = yVelocity;

        ex.setAlpha(r.nextFloat());
        ex.timer = 0;
        ex.expired = false;

        ex.setVisibility(true);

        //change hue
        /*if(levelUp) {
            int rgb1 = r.nextInt(3);
            int rgb2 = r.nextInt(3);
            int rgb3 = r.nextInt(3);
            ex.tint[rgb1] = 1.0f;
            while (rgb2 == rgb1) {
                rgb2 = r.nextInt(3);
            }
            ex.tint[rgb2] = r.nextFloat();
            while (rgb3 == rgb1 || rgb3 == rgb2) {
                rgb3 = r.nextInt(3);
            }
            ex.tint[rgb3] = 0;
        }else{
            for(int i = 0; i < ex.tint.length; i++){
                ex.tint[i] = 0;
            }
        }*/
        return ex;
    }

    public static void remove(Explosion ex){
        poolHandler.getPool().free(ex);
    }

    public void update(float deltaTime){
        timer += deltaTime;
        advancer.update(deltaTime);
        if(timer >= MAX_TIMER){
            kill();
        }
    }

    public boolean isExpired(){
        return expired;
    }

    private void kill() {
        setVisibility(false);
        expired = true;
    }

}

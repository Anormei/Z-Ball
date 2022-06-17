package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.interfaces.Pixmap;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Body;

import java.util.Random;

public class Obstacle extends Model {

    private static final PoolHandler poolHandler = new PoolHandler();
    private static final Random random = new Random();

    private static final float MIN_SPEED = 0.0045f;
    private static final float MAX_SPEED = 0.0085f;
    private static final float GLOW_SPEED = 0.02f;

    private float r;

    private Controller advancer;
    //private Controller glow;

    public Obstacle(GLGame game){
        super(game);
        createControllers();
    }

    private void createControllers(){
        advancer = new Controller(0){
            @Override
            public void update(){
                setY(y + 1);
            }
        };

        /*glow = new Controller(GLOW_SPEED){

            private boolean s;
            private float timer = 0;

            @Override
            public void update(){

                if(!s) {
                    timer += GLOW_SPEED;
                }

                if(timer > 1f) {
                    if (r < 0.6f && !s) {
                        r += 0.01f;
                    } else {
                        s = true;
                        timer = 0;
                    }
                }

                if(r > 0 && s){
                    r -= 0.01f;
                }else{
                    s = false;
                }

                //setSrcColor(1.0f, 1.0f - r, 1.0f - r);
            }
        };*/
    }

    public static Obstacle newInstance(GLGame game){

        if(!poolHandler.isInitialized(game)){
            final GLGame g = game;
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Model createObject() {
                    return new Obstacle(g);
                }
            }, 50);
        }

        Obstacle obstacle = (Obstacle)poolHandler.getPool().newObject();
        //obstacle.r = random.nextFloat() * 0.6f;
        obstacle.setTexture(Assets.getInstance(game).getImage("gameplay/obstacles/obstacle" + (random.nextInt(13) + 1)));
        obstacle.setWidth(obstacle.texture.width);
        obstacle.setHeight(obstacle.texture.height);
        obstacle.setSpeed((random.nextFloat() * (MAX_SPEED - MIN_SPEED)) + MIN_SPEED);

        return obstacle;
    }

    public static void remove(Obstacle obstacle){
        poolHandler.getPool().free(obstacle);
    }

    public void next(){
        setTexture(Assets.getInstance(game).getImage("gameplay/obstacles/obstacle" + (random.nextInt(13) + 1)));
        setWidth(texture.width);
        setHeight(texture.height);
    }

    public void update(float deltaTime){
        advancer.update(deltaTime);
        //glow.update(deltaTime);
    }

    public float getTint(){
        return r;
    }

    public boolean isExpired(){
        return y > 1920f;
    }

    private void setSpeed(float speed){
        advancer.TICK = speed;
    }

}

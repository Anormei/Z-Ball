package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

public class Platform extends Model {

    public static PoolHandler poolHandler = new PoolHandler();
    public static final float COOLDOWN = 4.0f;
    private static final float KILL_SPEED = 0.002f;

    private float life;
    private boolean dead;
    private Controller kill;

    private Platform(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/platform"));
        setControllers();
    }

    public static Platform newInstance(GLGame game){

        if(!poolHandler.isInitialized(game)){
            final GLGame g = game;
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Model createObject() {
                    return new Platform(g);
                }
            }, 50);
        }

        Platform platform = (Platform)poolHandler.getPool().newObject();
        platform.setAlpha(0);
        platform.life = 0;
        platform.dead = false;
        platform.setBody((int)platform.getWidth(), (int)platform.getHeight());

        return platform;
    }

    public static void remove(Platform platform){
        poolHandler.getPool().free(platform);
    }

    public void update(float deltaTime){
        life += deltaTime;

        if(life > COOLDOWN){
            kill.update(deltaTime);
        }
    }

    public boolean isExpired(){
        return life >= COOLDOWN;
    }

    public boolean isDead(){
        return dead;
    }

    private void setControllers(){
        kill = new Controller(KILL_SPEED){
            @Override
            public void update(){
                if(alpha < 1.0f) {
                    setY(y - 1);
                    setAlpha(alpha + 0.01f);
                }else{
                    dead = true;
                }
            }
        };
    }
}

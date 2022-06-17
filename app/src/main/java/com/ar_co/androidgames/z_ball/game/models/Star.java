package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.Random;

public class Star extends Model {

    private static PoolHandler poolHandler = new PoolHandler();

    private static final float OFFSET = 7.0f;
    private static final float WIDTH = 7.0f;
    private static final float HEIGHT = 7.0f;
    private static final int MAX_MOD = 12;

    private static final float MIN_SPEED = 0.005f;
    private static final float MAX_SPEED = 0.01f;

    private static final int FRAMES = 5;
    private static final float FRAME_DURATION = 0.1f;

    private static final float MIN_FLICKER = 2.0f;
    private static final float MAX_FLICKER = 10.0f;
    private static final float FLICKER_DURATION = 0.2f;

    private static final int WHITE_STAR_SPAWN_CHANCE = 80;
    private static final int PINK_STAR_SPAWN_CHANCE = 10;
    private static final int BLUE_STAR_SPAWN_CHANCE = 10;

    private static final int ANIMATED_SPAWN_CHANCE = 5;

    private static final int FLICKERING_SPAWN_CHANCE = 30;

    private static final int MAX_CHANCE = 100;


    public float speed;
    public float time;

    private int frame;

    private Controller animator;
    private Controller flickerOff;
    private Controller flickerOn;

    private boolean animated;
    private boolean flickering;
    private boolean isFlickering;
    private float flicker;

    private static Random r = new Random();

    private float regionY;

    private Star(GLGame game){
        super(game);
        //setRegion(0, 0, WIDTH, HEIGHT);
        createControllers();
    }

    public static Star newInstance(GLGame game){
        if(!Star.poolHandler.isInitialized(game)) {
            final GLGame g = game;
            Star.poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Model createObject() {
                    return new Star(g);
                }
            }, 200);
        }

        Star star = (Star)poolHandler.getPool().newObject();
        star.setConfigurations();
        float size = WIDTH * (r.nextInt(MAX_MOD) + 1);
        star.setWidth(size);
        star.setHeight(size);
        return star;
    }

    public static void remove(Star star){
        poolHandler.getPool().free(star);
    }

    private void createControllers(){

        animator = new Controller(FRAME_DURATION){
            @Override
            public void update(){
                frame++;
                frame = frame % FRAMES;
                switchRegion();
            }
        };

        flickerOff = new Controller(flicker){
            @Override
            public void update(){
                if(isFlickering){
                    return;
                }
                isFlickering = true;
                setVisibility(false);
            }
        };

        flickerOn = new Controller(FLICKER_DURATION){
            @Override
            public void update(){
                if(isFlickering){
                    setVisibility(true);
                    isFlickering = false;
                }
            }
        };
    }

    public void update(float deltaTime){
        time += deltaTime;
        if(animated){
            animator.update(deltaTime);
        }

        if(flickering){
            if(!isFlickering){
                flickerOff.update(deltaTime);
            }else {
                flickerOn.update(deltaTime);
            }
        }
    }

    private void switchRegion(){
        float regionWidth = (frame * OFFSET) + WIDTH;
        setRegion(frame * OFFSET, regionY, regionWidth, regionY + HEIGHT);
    }

    private void setConfigurations(){
        speed = (r.nextFloat() * (MAX_SPEED - MIN_SPEED)) + MIN_SPEED;
        time = 0;

        animated = roll(ANIMATED_SPAWN_CHANCE, MAX_CHANCE);
        frame = animated ? r.nextInt(FRAMES) : 0;

        flickering = !animated && roll(FLICKERING_SPAWN_CHANCE, MAX_CHANCE);
        isFlickering = r.nextBoolean();
        flicker = flickering ? (r.nextFloat() * (MAX_FLICKER - MIN_FLICKER)) + MIN_FLICKER : 0;

        setAlpha(0);

        if(!animated){
            setAlpha(r.nextInt() > 0 ? 0.5f : 0);
        }

        resetControllers();

        /*if(!roll(WHITE_STAR_SPAWN_CHANCE, MAX_CHANCE)){
            boolean isApp2 = roll(PINK_STAR_SPAWN_CHANCE, PINK_STAR_SPAWN_CHANCE + BLUE_STAR_SPAWN_CHANCE);
            if(isApp2){
                setTexture(assets.getImage(app2));
            }else{
                setTexture(assets.getImage(app3));
            }
        }else{
            setTexture(assets.getImage(app1));
        }*/

        setTexture(Assets.getInstance(game).getImage("backgrounds/stars/stars"));
        if(!roll(WHITE_STAR_SPAWN_CHANCE, MAX_CHANCE)){
            boolean which = roll(PINK_STAR_SPAWN_CHANCE, PINK_STAR_SPAWN_CHANCE + BLUE_STAR_SPAWN_CHANCE);
            if(which){
                regionY = HEIGHT;
            }else{
                regionY = 2 * HEIGHT;
            }
        }else{
            regionY = 0;
        }
        switchRegion();
    }

    private void resetControllers(){
        flickerOff.TICK = flicker;
        flickerOn.TICK = FLICKER_DURATION;

        flickerOff.time = 0;
        flickerOn.time = 0;
    }

    private boolean roll(int what, int outOf){
        return r.nextInt(outOf - 1) + 1 <= what;
    }

}

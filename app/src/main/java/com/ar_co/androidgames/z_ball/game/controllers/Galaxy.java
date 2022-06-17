package com.ar_co.androidgames.z_ball.game.controllers;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.DrawUtils;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.Nebula;
import com.ar_co.androidgames.z_ball.game.models.Star;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

public class Galaxy {

    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private static final int MIN_STARS = 200;
    private static final int MAX_STARS = 400;

    private static final float NEBULA_MIN_SPAWN_TIME = 0f;
    private static final float NEBULA_MAX_SPAWN_TIME = 10f;
    private static final int SPAWN_RATE = 64;

    private static final float NEBULA_SPEED = 0.1f;

    private GLGame game;
    private List<Star> stars = new ArrayList<>();
    private List<Star> bin = new ArrayList<>();
    private Nebula nebula;

    private Random r;
    private Controller starAdvancer;
    private Controller starSpawner;

    private Controller nebulaAdvancer;
    private Controller nebulaSpawner;

    private boolean inactive;

    private SpriteBatcher starBatcher;


    public Galaxy(GLGame game, float width, float height, boolean showNebula){
        this.game = game;
        this.WORLD_WIDTH = width;
        this.WORLD_HEIGHT = height;

        r = new Random();

        newBatch(stars, 0, 0);
        nebula = new Nebula(game, width);

        if(r.nextInt(101) <= 80){
            float min = -(nebula.getHeight());
            float max = WORLD_HEIGHT;
            nebula.setY((int)((r.nextFloat() * (max - min)) + min));
            nebula.setVisibility(showNebula);
            inactive = false;
        }else{
            nebula.setY(-nebula.getHeight());
            inactive = true;
        }

        //nebula.setY(0);
        //inactive = false;

        /*
        float min = -(nebula.getHeight());
        float max = nebula.getHeight();
        nebula.setY((int)((r.nextFloat() * (max - min)) + min));
        nebula.setVisibility(showNebula);

        if(nebula.getY() > WORLD_HEIGHT){
            inactive = true;
        }*/

        Log.i("Galaxy", "Nebula, x = " + nebula.getX() + ", y = " + nebula.getY() + ", y2 = " + nebula.getY2());
        //Log.i("Galaxy", "Galaxy type? " + inactive);
        Log.i("Galaxy", "Nebula Inactive? = " + inactive);

        createControllers();
        starBatcher = new SpriteBatcher(game, MAX_STARS * 2, 32);
    }

    private void createControllers(){
        starAdvancer = new Controller(0){
            @Override
            public void update(float deltaTime){
                for(int i = 0; i < stars.size(); i++){
                    Star star = stars.get(i);
                    star.update(deltaTime);
                    while(star.time >= star.speed) {
                        star.time -= star.speed;
                        star.setY(star.getY() + 1);
                    }
                    if(star.getY() > WORLD_HEIGHT){
                        bin.add(star);
                        Star.remove(star);
                    }
                }

                stars.removeAll(bin);
                bin.clear();
            }

        };

        starSpawner = new Controller(r.nextFloat()){
            @Override
            public void update(){
                TICK = r.nextFloat();
                if(stars.size() < MAX_STARS){
                    for(int i = 0; i < r.nextInt(SPAWN_RATE); i++){
                        Star star = Star.newInstance(game);
                        star.setX((r.nextFloat() * (WORLD_WIDTH + star.getWidth())) - star.getWidth());
                        star.setY(-star.getHeight());
                        stars.add(star);
                    }
                }
            }
        };

        nebulaAdvancer = new Controller(NEBULA_SPEED * 12.0f){
            @Override
            public void update(){
                nebula.setY(nebula.getY() + 12);
                //Log.i("Galaxy", "Nebula, x = " + nebula.getX() + ", y = " + nebula.getY() + ", y2 = " + nebula.getY2());
            }
        };

        nebulaSpawner = new Controller((r.nextFloat() * (NEBULA_MAX_SPAWN_TIME - NEBULA_MIN_SPAWN_TIME)) + NEBULA_MIN_SPAWN_TIME){
            @Override
            public void update(){
                nebula.changeBackground();
                nebula.setY(-nebula.getHeight());
                inactive = false;
                TICK = (r.nextFloat() * (NEBULA_MAX_SPAWN_TIME - NEBULA_MIN_SPAWN_TIME)) + NEBULA_MIN_SPAWN_TIME;
                Log.i("Galaxy", "Respawning...");
            }
        };
    }

    public void update(float deltaTime){
        starAdvancer.update(deltaTime);
        starSpawner.update(deltaTime);

        nebula.update(deltaTime);
        if(nebula.getY() > WORLD_HEIGHT && !inactive){
            inactive = true;
            Log.i("Galaxy", "is inactive...");
        }

        if(inactive){
            nebulaSpawner.update(deltaTime);
        }else{
            nebulaAdvancer.update(deltaTime);
        }
    }

    public void draw(float deltaTime){
        GL11 gl = game.getGLGraphics().getGL11();

        starBatcher.startBatch(Assets.getInstance(game).getImage("backgrounds/stars/stars"));
        for(int i = 0; i < stars.size(); i++) {
            starBatcher.draw(stars.get(i));
        }
        starBatcher.endBatch();

        DrawUtils.drawTint(gl, nebula, nebula.tint);
    }

    private void newBatch(List<Star> stars, float x, float y){
        for(int i = 0; i < r.nextInt(MAX_STARS - MIN_STARS) + MIN_STARS; i++){
            Star star = Star.newInstance(game);
            star.setX(((r.nextFloat() * (WORLD_WIDTH + star.getWidth())) - star.getWidth()) + x);
            star.setY((r.nextFloat() * (WORLD_HEIGHT)) + y);
            stars.add(star);
        }
    }

}

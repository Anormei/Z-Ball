package com.ar_co.androidgames.z_ball.game.models;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Boundary extends Model{

    private static final float H_SPACE = 5f;
    private static final float V_SPACE = 5f;

    private static final float BORDER_WIDTH = 245f;
    private static final float BORDER_HEIGHT = 245f;

    private static final float DEFAULT_SPEED = 0.02f;

    private float spawnY;

    private float speed;

    private List<Border> borders = new ArrayList<>();

    private Controller spawnUp;
    private Controller advanceUp;
    private Controller advanceDown;
    private Controller spawnDown;

    private Controller currAdvancer;
    private Controller currSpawner;

    private SpriteBatcher batcher;

    private int wSize;
    private int hSize;

    public Boundary(GLGame game, float x, float y, float width, float height, boolean flowdown){
        super(game, width, height);

        speed = DEFAULT_SPEED;

        setX(x);
        setY(y);
        wSize = (int)Math.ceil(width / (BORDER_WIDTH + H_SPACE));
        hSize = (int)Math.ceil(height / (BORDER_HEIGHT + V_SPACE));

        for(int h = 0; h < hSize; h++) {
            for (int w = 0; w < wSize; w++) {
                Border border = Border.newInstance(game);
                border.setX(H_SPACE + w * (BORDER_WIDTH + H_SPACE));
                if (flowdown) {
                    border.setY((getY2() - BORDER_HEIGHT) - h * (BORDER_HEIGHT + V_SPACE));
                } else {
                    border.setY(y + h * (BORDER_HEIGHT + V_SPACE));
                }

                spawnY = border.getY();
                borders.add(border);
            }

        }

        Log.i("Boundary", "Size = " + borders.size());
        //Log.i("Boundary", (flowdown ? "Flowing Down" : "Flowing Up") + ", wSize = " + wSize + ", hSize = " + hSize);

        batcher = new SpriteBatcher(game, (wSize + 2) * (hSize + 2), 32);

        createControllers();

        if(flowdown){
            currAdvancer = advanceDown;
            currSpawner = spawnDown;
        }else{
            currAdvancer = advanceUp;
            currSpawner = spawnUp;
        }

        //Log.i("Boundary", "" + getY2());
    }

    private void createControllers(){
        spawnDown = new Controller(0){
            @Override
            public void update(){
                if (borders.get(borders.size() - 1).getY() > getY() + V_SPACE){
                    float y = borders.get(borders.size() - 1).getY() - BORDER_HEIGHT - V_SPACE;
                    for (int w = 0; w < wSize; w++) {
                        Border border = Border.newInstance(game);
                        border.setX(H_SPACE + w * (BORDER_WIDTH + H_SPACE));
                        border.setY(y);

                        spawnY = border.getY();
                        borders.add(border);
                    }
                    Log.i("Boundary", "Size = " + borders.size());
                }
            }
        };

        advanceDown = new Controller(speed){
            @Override
            public void update(){
                for (Iterator<Border> iterator = borders.iterator(); iterator.hasNext();) {
                    Border border = iterator.next();
                    border.setY(border.getY() + 1);
                    if(border.getY2() > getY2()){
                        border.setRegion(0, 0, BORDER_WIDTH, getY2() - border.getY());
                        border.setHeight(getY2() - border.getY());
                    }
                    if(border.getY() > getY2()){
                        Border.remove(border);
                        iterator.remove();
                    }

                }
            }
        };

        spawnUp = new Controller(speed){
            @Override
            public void update(){
                if(borders.get(borders.size() - 1).getY() < getY2() - V_SPACE){
                    float y = borders.get(borders.size() - 1).getY() + BORDER_HEIGHT + V_SPACE;
                    for (int w = 0; w < wSize; w++) {
                        Border border = Border.newInstance(game);
                        border.setX(H_SPACE + w * (BORDER_WIDTH + H_SPACE));
                        border.setY(y);

                        spawnY = border.getY();
                        borders.add(border);
                    }
                }
            }
        };

        advanceUp = new Controller(speed){
            @Override
            public void update(){
                for (Iterator<Border> iterator = borders.iterator(); iterator.hasNext();) {
                    Border border = iterator.next();
                    border.setY(border.getY() - 1);
                    if(border.getY() < y){
                        //border.setRegion(0, border.getHeight() - (y - border.getY()), BORDER_WIDTH, BORDER_HEIGHT);
                        //border.setHeight(border.getHeight() - (y - border.getY()));
                        //border.setY(y);
                        border.setRegion(0, BORDER_HEIGHT - (border.getY2() - y), BORDER_WIDTH, BORDER_HEIGHT);
                        border.setHeight(border.getY2() - y);
                        border.setY(y);
                    }
                    if(border.getHeight() <= 0){
                        Border.remove(border);
                        iterator.remove();
                    }
                }
            }
        };
    }

    public void update(float deltaTime) {
        currSpawner.update(deltaTime);
        currAdvancer.update(deltaTime);
    }

    @Override
    public void bind(){
        batcher.startBatch(Assets.getInstance(game).getImage("gameplay/border"));
    }

    @Override
    public void draw(){
        for(int i = 0; i < borders.size(); i++){
            batcher.draw(borders.get(i));
        }
    }

    @Override
    public void unbind(){
        batcher.endBatch();
    }

    public void changeSpeed(float speed){
        this.speed = speed;
    }

    public void defaultSpeed(){
        speed = DEFAULT_SPEED;
    }

}

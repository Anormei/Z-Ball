package com.ar_co.androidgames.z_ball.game.models;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Slider extends Model {

    public static final int MOVE_LEFT = 0;
    public static final int MOVE_RIGHT = 1;

    private static final float MOD = 4.0f;
    private static final int ICON_SIZE = 12;
    private static final float SPEED = 0.01f;
    private static final float INITIAL_SPEED =  0.001f;


    private static final float WIDTH_REGION = 22f;
    private static final float HEIGHT_REGION = 20f;

    private Assets assets;
    private List<Model> icons = new ArrayList<>();
    private List<Integer> integers = new ArrayList<>();
    private int direction;

    private int tick = 0;

    private SpriteBatcher batcher;

    private Random r= new Random();

    private Texture texture;

    private Controller advancer;

    public Slider(GLGame game, float x, float y, float offset, float width,  int direction){
        super(game);
        assets = Assets.getInstance(game);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = ICON_SIZE * (HEIGHT_REGION * MOD);
        this.direction = direction;

        this.texture = assets.getImage("scoreboard/luck");

        for(int i = 0; i < ICON_SIZE; i++){
            integers.add(i);
        }

        Log.i("Slider", "x = " + x);
        while(integers.size() > 0){
            int index = r.nextInt(integers.size());
            int pos = integers.get(index);

            Model icon = new Model(game, texture, WIDTH_REGION * MOD, HEIGHT_REGION * MOD);
            icon.setRegion(pos * (WIDTH_REGION + 2f), 0, pos * (WIDTH_REGION + 2f) + WIDTH_REGION, HEIGHT_REGION);

            if(direction == MOVE_LEFT) {
                icon.setX(x + offset + (icons.size() * (WIDTH_REGION + 2f) * MOD));
                Log.i("Slider", "icon.x = " + icon.getX());
            }else{
                icon.setX((x + offset + width - ((WIDTH_REGION + 2f) * MOD)) - (icons.size() * (WIDTH_REGION + 2f) * MOD));
            }
            icon.setY(y);

            /*if(icon.getX() > x + width){
                icon.setWidth(0);
                icon.setRegion(0, 0, 0 ,0);

            }else if(icon.getX2() > x + width){
                icon.setWidth(icon.getX() - (x + width));
                float diff = (WIDTH_REGION * MOD) / (icon.getX() - (x + width));
                icon.setRegion(0, pos * WIDTH_REGION, (pos * WIDTH_REGION + WIDTH_REGION) * diff, HEIGHT_REGION);
            }*/

            icons.add(icon);
            integers.remove(index);
        }
        setControllers();

        batcher = new SpriteBatcher(game, 20, 32);
    }

    private void setControllers(){
        advancer = new Controller(INITIAL_SPEED){
            @Override
            public void update(){
                if(tick < 1060) {
                    tick++;
                }else if(tick == 1060){
                    this.TICK = SPEED;
                    tick++;
                }

                for(int i = 0; i < icons.size(); i++){
                    Model icon = icons.get(i);
                    if(direction == MOVE_LEFT){
                        icon.setX(icon.getX() - 1f);
                        if (icon.getX2() < x) {
                            icon.setX(icons.get(i > 0 ? i - 1 : icons.size() - 1).getX2() + 8f);
                        }
                    }else{
                        icon.setX(icon.getX() + 1f);
                        if (icon.getX() > x + width){
                            icon.setX(icons.get(i > 0 ? i - 1 : icons.size() - 1).getX() - ((WIDTH_REGION + 2f) * MOD));
                        }
                    }
                }
            }
        };
    }

    public void update(float deltaTime){
        advancer.update(deltaTime);
    }

    @Override
    public void setX(float x){
        for(int i = 0; i < icons.size(); i++){
            Model icon = icons.get(i);
            icon.setX(icon.getX() + (x - this.x));
        }
        super.setX(x);
    }

    @Override
    public void setY(float y){
        for(int i = 0; i < icons.size(); i++){
            Model icon = icons.get(i);
            icon.setY(icon.getY() + (y - this.y));
        }
        super.setY(y);
    }

    @Override
    public void setCoord(float x, float y){
        for(int i = 0; i < icons.size(); i++){
            Model icon = icons.get(i);
            icon.setX(icon.getX() + (x - this.x));
            icon.setY(icon.getY() + (y - this.y));
        }
        super.setCoord(x, y);
    }

    @Override
    public float getWidth(){
        return ((WIDTH_REGION + 2f) * ICON_SIZE - 2f) * MOD;
    }

    @Override
    public void bind(){
        batcher.startBatch(assets.getImage("scoreboard/luck"));
    }

    @Override
    public void draw(){
        for(int i = 0; i < icons.size(); i++){
            batcher.draw(icons.get(i));
        }
    }

    @Override
    public void unbind(){
        batcher.endBatch();
    }


}

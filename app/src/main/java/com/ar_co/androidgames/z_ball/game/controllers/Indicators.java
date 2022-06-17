package com.ar_co.androidgames.z_ball.game.controllers;

import android.graphics.RectF;
import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.Ball;
import com.ar_co.androidgames.z_ball.game.models.GuidelineModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Indicators {

    private static final float CONVEYER_SPEED = 0.005f;
    private static final float BALL_SIZE = 50;

    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private GLGame game;
    private RectF area;

    private Texture guideline;
    private float gMidW;
    private float gMidH;

    private float midX;
    private float midY;

    private float touchX;
    private float touchY;
    private boolean visible;

    private RectF edge;
    private float xEdge;
    private float yEdge;

    private float indicatorLength;

    private Controller conveyer;
    private Controller spawner;

    private List<GuidelineModel> models = new ArrayList<>();

    private SpriteBatcher batcher;

    public Indicators(GLGame game, float width, float height, RectF area){
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;

        this.game = game;

        this.area = area;
        edge = new RectF();
        guideline = Assets.getInstance(game).getImage("gameplay/guidelines");

        int size = (int)(Math.ceil(WORLD_WIDTH / (guideline.width * 2)));
        indicatorLength = size * guideline.width;
        for(int i = -1; i < size; i++){
            GuidelineModel model = GuidelineModel.newInstance(game);
            model.setPos(i * (guideline.width * 2));
            model.setVisibility(false);
            models.add(model);
        }

        Collections.reverse(models);
        midX = area.left + ((area.right - area.left)/ 2);
        midY = area.top + ((area.bottom - area.top) / 2);

        gMidW = guideline.width/2;
        gMidH = guideline.height/2;

        setControllers();
        batcher = new SpriteBatcher(game, 100, 32);
    }

    private void setControllers(){
        spawner = new Controller(0){
            @Override
            public void update(){
                if(models.size() < 0){
                    return;
                }

                if(models.get(models.size() - 1).getPos() > guideline.width){
                    GuidelineModel model = GuidelineModel.newInstance(game);
                    model.setPos(-guideline.width);
                    model.setVisibility(false);
                    models.add(model);
                }
            }
        };

        conveyer = new Controller(CONVEYER_SPEED){

            @Override
            public void update(){
                for(Iterator<GuidelineModel> iterator = models.iterator(); iterator.hasNext();){
                    GuidelineModel model = iterator.next();
                    model.setPos(model.getPos() + 1);

                    model.setVisibility(visible);

                    if(touchX >= midX && touchY < midY){
                        model.setX(model.getPos());
                        model.setY(touchY + (touchX -model.getPos()));
                    }else if(touchX < midX && touchY < midY){
                        model.setX(area.right - model.getPos());
                        model.setY((touchY + (area.right - touchX)) - model.getPos());
                    }else if(touchX >= midX && touchY >= midY){
                        model.setX(model.getPos());
                        model.setY((touchY - touchX) + model.getPos());
                    }else if(touchX < midX && touchY >= midY){
                        model.setX(area.right - model.getPos());
                        model.setY((touchY - (area.right - touchX)) + model.getPos());
                    }
                    model.setX(model.getX() - gMidW);
                    model.setY(model.getY() - gMidH);

                    if(model.getPos() > area.right + guideline.width){
                        GuidelineModel.remove(model);
                        iterator.remove();
                    }

                }

            }
        };
    }

    public void update(float deltaTime){
        spawner.update(deltaTime);
        conveyer.update(deltaTime);
    }

    public void draw(float deltaTime){
        batcher.startBatch(Assets.getInstance(game).getImage("gameplay/guidelines"));
        for(int i = 0; i < models.size(); i++){
            batcher.draw(models.get(i));
        }
        batcher.endBatch();

    }

    public void showIndicators(float x, float y){
        visible = true;
        /*if(touchX == x && touchY == y){
            return;
        }*/

        bordersWithin();

        if(x >= midX && y < midY){
            //checkTopRightEdge();
            if(x + y < midX + (midY - midX)){
                return;
            }

            if(x + y > (midY - area.top - BALL_SIZE) + midY){
                return;
            }
        }else if(x < midX && y < midY){
            if(x - y > midX - (midY - midX)){
                return;
            }

            if(x - y < (area.right - (midY - area.top - BALL_SIZE)) - midY){
                return;
            }
        }else if(x >= midX && y >= midY){
            if(x - y > (midY - area.top) - midY){
                return;
            }

            if(x - y < midX - (midY + midX)){
                return;
            }
        }else if(x < midX && y >= midY){
            //checkBottomLeftEdge();
            if(x + y < (area.right - (midY - area.top)) + midY){
                return;
            }

            if(x + y > midX + (midX + midY)){
                return;
            }
        }

        touchX = x;
        touchY = y;
    }

    public void hideIndicators(){
        visible = false;
    }

    public float getLaunchX(){
        if(touchX > midX){
            return -BALL_SIZE;
        }else{
            return area.right + BALL_SIZE;
        }
    }

    public float getLaunchY(){
        float y = 0;
        if(touchX >= midX && touchY < midY){
            y =  touchY + touchX;
        }else if(touchX < midX && touchY < midY){
            y = touchY + (area.right - touchX);
        }else if(touchX >= midX && touchY >= midY){
            y = touchY - touchX;
        }else if(touchX < midX && touchY >= midY){
            y = touchY - (area.right - touchX);
        }

        y -= gMidH;
        return y;
    }

    public void setLaunchDirection(Ball ball){
        if(touchX > midX && touchY < midY){
            ball.setDirection(Ball.Direction.UpRight);
        }else if(touchX < midX && touchY < midY){
            ball.setDirection(Ball.Direction.UpLeft);
        }else if(touchX > midX && touchY > midY){
            ball.setDirection(Ball.Direction.DownRight);
        }else if(touchX < midX && touchY > midY){
            ball.setDirection(Ball.Direction.DownLeft);
        }
    }

    private void bordersWithin(){
        if(touchX < area.left){
            touchX = area.left;
        }

        if(touchX > area.right){
            touchX = area.right;
        }

        if(touchY < area.top){
            touchY = area.top;
        }

        if(touchY > area.bottom){
            touchY = area.bottom;
        }
    }

}

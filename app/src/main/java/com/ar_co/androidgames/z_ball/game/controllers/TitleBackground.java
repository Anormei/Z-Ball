package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.TitlePattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TitleBackground {

    private static final float TICK = 0.01f;
    private static final float VERTICAL_SPACE = 28.0f;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private GLGame game;

    private List<TitlePattern> models = new ArrayList<>();
    private List<TitlePattern> bin = new ArrayList<>();
    private Texture pattern;

    private Controller advancer;
    private Controller spawner;

    private int xSize;
    private int ySize;

    public TitleBackground(GLGame game, float width, float height){
        this.game = game;
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
        Assets assets = Assets.getInstance(game);
        pattern = assets.getImage("menu/title/titlebackground");

        xSize = (int)((WORLD_WIDTH / pattern.width) + 0.5f);
        ySize = (int)((pattern.height + WORLD_HEIGHT) / pattern.height);
        for(int y = 0; y < ySize; y++){
            for(int x = 0; x < xSize; x++) {
                TitlePattern model = TitlePattern.newInstance(game);
                model.setX(pattern.width * x);
                model.setY(-pattern.height + (pattern.height * y));
                models.add(model);
            }
        }
        Collections.reverse(models);
        createControllers();
    }

    public void createControllers(){

        advancer = new Controller(TICK){

            @Override
            public void update(){
                for(int i = 0; i < models.size(); i++){
                    TitlePattern model = models.get(i);
                    model.setY(model.getY() + 1);
                    if(model.getY() > WORLD_HEIGHT){
                        bin.add(model);
                        TitlePattern.remove(model);
                    }
                }

                models.removeAll(bin);
                bin.clear();
            }
        };

        spawner = new Controller(0){

            @Override
            public void update(){
                TitlePattern model = models.get(models.size()-1);
                if(model.getY() >= 0){
                    for(int i = 0; i < xSize; i++) {
                        TitlePattern newModel = TitlePattern.newInstance(game);
                        newModel.setX(pattern.width * i);
                        newModel.setY(model.getY() - pattern.height);
                        models.add(newModel);
                    }
                }
            }
        };
    }

    public void update(float deltaTime){
        spawner.update(deltaTime);
        advancer.update(deltaTime);
    }

    public List<TitlePattern> getModels(){
        return models;
    }
}

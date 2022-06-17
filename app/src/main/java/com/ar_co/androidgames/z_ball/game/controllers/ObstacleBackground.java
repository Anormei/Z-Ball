package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.TitlePattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObstacleBackground {

    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private GLGame game;

    private List<Model> models = new ArrayList<>();

    private int xSize;
    private int ySize;

    private SpriteBatcher batcher;

    public ObstacleBackground(GLGame game, float width, float height){
        this.game = game;
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
        Assets assets = Assets.getInstance(game);
        Texture pattern = assets.getImage("gameplay/obstaclebackground");

        xSize = (int)Math.ceil(WORLD_WIDTH / pattern.width);
        ySize = (int)Math.ceil(WORLD_HEIGHT / pattern.height);
        for(int y = 0; y < ySize; y++){
            for(int x = 0; x < xSize; x++) {
                TitlePattern model = TitlePattern.newInstance(game);
                model.setX(pattern.width * x);
                model.setY(pattern.height * y);
                models.add(model);
            }
        }

        batcher = new SpriteBatcher(game, models.size() * 2, 32);
    }

    public void draw(float deltaTime){
        batcher.startBatch(Assets.getInstance(game).getImage("gameplay/obstaclebackground"));
        for(int i = 0; i < models.size(); i++){
            batcher.draw(models.get(i));
        }
        batcher.endBatch();
    }

    public List<Model> getModels(){
        return models;
    }
}

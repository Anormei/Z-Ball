package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.Explosion;

import java.util.ArrayList;
import java.util.List;

public class Gamebar{

    private GLGame game;
    private Assets assets;

    private ScoreSystem scoreSystem;
    private Gameplay gameplay;


    private Model barMain;
    private Model barLeft;
    private Model barRight;

    private StockBar stockBar;

    private Model labels;
    //private Model divider;

    private ScoreNumbers scoreNumbers;
    //private NumberWriter stockWriter;

    private float size;

    public Gamebar(GLGame game, Gameplay gameplay){
        this.game = game;
        assets = Assets.getInstance(game);

        scoreSystem = ScoreSystem.getInstance();
        this.gameplay = gameplay;

        barLeft = new Model(game, assets.getImage("gameplay/tray/barLeft"), 18f, 114f);
        barLeft.setY(53f); //+26

        barRight = new Model(game, assets.getImage("gameplay/tray/barRight"), 18f, 114f);
        barRight.setY(53f);

        barMain = new Model(game, assets.getImage("gameplay/tray/barMain"), 1080f, 126f);
        barMain.setCoord(540f, 72f);

        labels = new Model(game, assets.getImage("gameplay/tray/labels"));
        labels.setCoord(342f, 5f);

        stockBar = new StockBar(game, gameplay, 540f, 179f);

        scoreNumbers = new ScoreNumbers(game, 540f, 74f);
    }


    public void update(float deltaTime){
        scoreNumbers.update(deltaTime);
        stockBar.update(deltaTime);

        size = scoreNumbers.getSize() + (22f * 2);
        barMain.setWidth(size);
        barMain.setX(540f - (size / 2));

        barLeft.setX(barMain.getX() - barLeft.getWidth());
        barRight.setX(barMain.getX2());
    }

    public void draw(float deltaTime){
        draw(barLeft);
        draw(barRight);

        draw(labels);

        stockBar.draw(deltaTime);

        scoreNumbers.draw(deltaTime);

    }

    public void draw(Model model){
        model.bind();
        model.draw();
        model.unbind();
    }
}

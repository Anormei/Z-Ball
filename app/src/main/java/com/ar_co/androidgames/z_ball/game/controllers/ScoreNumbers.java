package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.game.Assets;

public class ScoreNumbers {

    private static final int MIN_RANGE = 4;
    private static final float SLOT_SPEED = 0.07f;
    private static final float SPACE = 64f;

    private GLGame game;
    private Assets assets;

    private ScoreSystem scoreSystem;

    private float x;
    private float y;

    private NumberWriter writer;
    private Model decimal;
    private Model meters;

    private String score;

    protected int size;
    private float currPos;


    public ScoreNumbers(GLGame game, float x, float y) {
        this.game = game;
        this.assets = Assets.getInstance(game);

        this.x = x;
        this.y = y;

        scoreSystem = ScoreSystem.getInstance();

        score = "0.00";
        size = MIN_RANGE;
        decimal = new Model(game, assets.getImage("gameplay/tray/decimal"));
        decimal.setY(y + 48f);

        meters = new Model(game, assets.getImage("gameplay/tray/meters"));
        meters.setY(y + 16f);

        writer = new NumberWriter(game, assets.getImage("gameplay/tray/scorenumbers"), 28f, 36f, 32f, 4f);
    }

    public void update(float deltaTime){
        score = String.format("%.2f", scoreSystem.getScore());
        writeScore(score);

    }

    public void writeScore(String score){
        if(score.length() > MIN_RANGE){
            size = score.length();
        }

        currPos = x - (getSize() / 2);

        writer.startWriting();
        for(int i = 0; i < score.length(); i++){
            if(score.charAt(i) != '.'){
                writer.write("" + score.charAt(i), currPos, y, 56f, 72f, NumberWriter.ALIGN_LEFT);
                currPos += 64f;
            }else{
                decimal.setX(currPos);
                decimal.setY(y + 48f);
                currPos += 32f;
            }
        }

        meters.setX(currPos);
        meters.setY(y + 16f);
    }


    public void writeScore(String score, float width, float height){
        if(score.length() > MIN_RANGE){
            size = score.length();
        }

        float change = width / 56f;

        currPos = x - ((getSize() * change)  / 2);

        writer.startWriting();
        for(int i = 0; i < score.length(); i++){
            if(score.charAt(i) != '.'){
                writer.write("" + score.charAt(i), currPos, y, width, height, NumberWriter.ALIGN_LEFT);
                currPos += 64f * change;
            }else{
                decimal.setX(currPos);
                decimal.setY(y + 48f * change);
                currPos += 32 * change;
            }
        }

        meters.setX(currPos);
        meters.setY(y + 16f * change);
    }

    public void changeSize(float by){
        decimal.setWidth(decimal.getWidth() * by);
        decimal.setHeight(decimal.getHeight() * by);

        meters.setWidth(meters.getWidth() * by);
        meters.setHeight(meters.getHeight() * by);
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    public void draw(float deltaTime){
        decimal.bind();
        decimal.draw();
        decimal.unbind();

        meters.bind();
        meters.draw();
        meters.unbind();
        writer.draw(deltaTime);
    }

    public float getSize(){
        return (size - 1) * 64f + 32f + 56f;
    }

}

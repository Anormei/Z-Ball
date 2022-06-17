package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.Heart;
import com.ar_co.androidgames.z_ball.game.models.Platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StockBar {

    private static final float V_SPACE = 6f;
    private static final float WIDTH = 66f;

    private GLGame game;
    private Assets assets;
    private Gameplay gameplay;

    private float midX;
    private float y;
    private float width;

    private List<Heart> stock = new ArrayList<>();
    private SpriteBatcher heartBatcher;

    public StockBar(GLGame game, Gameplay gameplay, float midX, float y){
        this.game = game;
        this.gameplay = gameplay;
        this.midX = midX;
        this.y = y;
        assets = Assets.getInstance(game);

        for(int i = 0; i < gameplay.getAmmoClipSize(); i++){
            Heart heart = Heart.newInstance(game);
            heart.setY(y);
            stock.add(Heart.newInstance(game));
        }

        heartBatcher = new SpriteBatcher(game, 20, 32);
    }

    public void update(float deltaTime){

        for(Iterator<Heart> iterator = stock.iterator(); iterator.hasNext();){
            Heart heart = iterator.next();
            Heart.remove(heart);
            iterator.remove();
        }

        float size = gameplay.countAmmo();
        width = (WIDTH + V_SPACE) * (size + (size != gameplay.getAmmoClipSize() ? 1 : 0))  - V_SPACE ;

        for(int i = 0; i < size; i++){
            Heart heart = Heart.newInstance(game);
            heart.setX((midX - width/2) + (i * (WIDTH + V_SPACE)));
            heart.setY(y);
            stock.add(heart);
        }

        if(size != gameplay.getAmmoClipSize()){
            Heart heart = Heart.newInstance(game);
            //int index = stock.size() > 0 ? stock.size() - 1 : 0;
            heart.setX((midX - width / 2) + stock.size() * (WIDTH + V_SPACE));
            heart.setY(y);

            heart.setAlpha(0.5f);

            Model innerHeart = heart.getInnerHeart();

            innerHeart.setAlpha(0.5f);

            float innerHeight = innerHeart.getTexture().height;
            //float lifespan = recentPlatform.getLifespan();

            innerHeart.setHeight(innerHeight * ((gameplay.getAmmo() % Gameplay.AMMO_COST) / Gameplay.AMMO_COST));
            innerHeart.setRegion(0, innerHeight - innerHeart.getHeight(), innerHeart.getWidth(), innerHeart.getTexture().height);

            innerHeart.setY(heart.getY() + 12f + (innerHeight - innerHeart.getHeight()));

            stock.add(heart);
        }

    }

    public void draw(float deltaTime){
        heartBatcher.startBatch(assets.getImage("gameplay/tray/heartcontainer"));
        for(int i = 0; i < stock.size(); i++){
            heartBatcher.draw(stock.get(i));
        }
        heartBatcher.endBatch();

        heartBatcher.startBatch(assets.getImage("gameplay/tray/heart"));
        for(int i = 0; i < stock.size(); i++){
            heartBatcher.draw(stock.get(i).getInnerHeart());
        }
        heartBatcher.endBatch();
    }
}

package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

public class TitlePattern extends Model {

    private static PoolHandler poolHandler = new PoolHandler();

    private TitlePattern(GLGame game){
        super(game, Assets.getInstance(game).getImage("menu/title/titlebackground"));
    }

    public static TitlePattern newInstance(GLGame game){
        if(!TitlePattern.poolHandler.isInitialized(game)) {
            final GLGame g = game;
            TitlePattern.poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Model createObject() {
                    return new TitlePattern(g);
                }
            }, 100);
        }

        return (TitlePattern) TitlePattern.poolHandler.getPool().newObject();
    }

    public static void remove(TitlePattern model){
        TitlePattern.poolHandler.getPool().free(model);
    }
}


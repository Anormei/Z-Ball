package com.ar_co.androidgames.z_ball.game.models;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

public class Border extends Model {

    private static final PoolHandler poolHandler = new PoolHandler();

    public Border(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/border"));
    }

    public static Border newInstance(GLGame game){

        if(!poolHandler.isInitialized(game)){
            final GLGame g = game;
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Object createObject() {
                    return new Border(g);
                }
            }, 40);
        }

        Border border = (Border) poolHandler.getPool().newObject();
        //border.setAlpha(0);
        //border.setSrcColor(1, 1, 1);
        border.setHeight(border.texture.height);
        border.setRegion(0, 0, border.getWidth(), border.getHeight());
        return border;
    }

    public static void remove(Border border){
        poolHandler.getPool().free(border);
    }
}

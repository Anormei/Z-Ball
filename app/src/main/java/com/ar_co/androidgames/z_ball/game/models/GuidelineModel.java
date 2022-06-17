package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

public class GuidelineModel extends Model {

    private static PoolHandler poolHandler = new PoolHandler();

    private float pos;

    private GuidelineModel(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/guidelines"));
    }

    public static GuidelineModel newInstance(GLGame game){
        if(!GuidelineModel.poolHandler.isInitialized(game)) {
            final GLGame g = game;
            GuidelineModel.poolHandler.setup(g, new Pool.PoolObjectFactory() {
                @Override
                public Model createObject() {
                    return new GuidelineModel(g);
                }
            }, 200);
        }

        return (GuidelineModel)GuidelineModel.poolHandler.getPool().newObject();
    }

    public static void remove(GuidelineModel model){
        poolHandler.getPool().free(model);
    }

    public void setPos(float pos){
        this.pos = pos;
    }

    public float getPos(){
        return pos;
    }

}

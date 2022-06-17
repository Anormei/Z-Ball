package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.game.Assets;

public class Heart extends Model{

    private static PoolHandler poolHandler = new PoolHandler();

    private Model innerHeart;

    public Heart(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/tray/heartcontainer"));
        innerHeart = new Model(game, Assets.getInstance(game).getImage("gameplay/tray/heart"));
    }

    public static Heart newInstance(final GLGame game){
        if(!poolHandler.isInitialized(game)){
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Heart createObject() {
                    return new Heart(game);
                }
            }, 20);
        }

        Heart heart = (Heart)poolHandler.getPool().newObject();
        heart.setAlpha(0);
        heart.innerHeart.setRegion(0, 0, heart.innerHeart.getTexture().width, heart.innerHeart.getTexture().height);
        heart.innerHeart.setWidth(heart.innerHeart.getTexture().width);
        heart.innerHeart.setHeight(heart.innerHeart.getTexture().height);
        heart.innerHeart.setAlpha(0);


        return heart;
    }

    public static void remove(Heart heart){
        poolHandler.getPool().free(heart);
    }

    public Model getInnerHeart(){
        return innerHeart;
    }

    @Override
    public void setX(float x){
        super.setX(x);
        innerHeart.setX(x + 12f);
    }

    @Override
    public void setY(float y){
        super.setY(y);
        innerHeart.setY(y + 12f);
    }
}

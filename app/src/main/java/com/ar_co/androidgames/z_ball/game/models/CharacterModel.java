package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Pool;
import com.ar_co.androidgames.z_ball.framework.PoolHandler;
import com.ar_co.androidgames.z_ball.framework.Texture;

public class CharacterModel extends Model {

    private static PoolHandler poolHandler = new PoolHandler();

    private CharacterModel(GLGame game){
        super(game);
    }

    public static CharacterModel newInstance(GLGame game, Texture texture, float width, float height){
        if(!poolHandler.isInitialized(game)) {
            final GLGame g = game;
            poolHandler.setup(game, new Pool.PoolObjectFactory() {
                @Override
                public Model createObject() {
                    return new CharacterModel(g);
                }
            }, 100);
        }

        CharacterModel characterModel = (CharacterModel)poolHandler.getPool().newObject();
        characterModel.setTexture(texture);

        characterModel.setWidth(width);
        characterModel.setHeight(height);
        return characterModel;
    }

    public static void remove(CharacterModel characterModel){
        poolHandler.getPool().free(characterModel);
    }

}




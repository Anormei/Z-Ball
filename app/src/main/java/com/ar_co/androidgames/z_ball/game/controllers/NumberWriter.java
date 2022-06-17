package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.game.models.CharacterModel;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public class NumberWriter {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_MID = 2;

    private GLGame game;
    private Texture texture;
    private float stride;

    private float width;
    private float height;

    private float alpha = 0;

    private float space;

    private List<Model> characters = new ArrayList<>();

    private SpriteBatcher batcher;

    public NumberWriter(GLGame game, Texture texture, float width, float height, float stride, float space){
        this.game = game;
        this.space = space;

        this.texture = texture;
        this.width = width;
        this.height = height;
        this.stride = stride;

        batcher = new SpriteBatcher(game, 100, 32);
    }

    public void startWriting(){
        for(int i = 0; i < characters.size(); i++){
            CharacterModel.remove((CharacterModel) characters.get(i));
        }
        characters.clear();
    }

    public void write(String number, float x, float y, int type){
        if(type == ALIGN_MID){
            x -= (((width + space) * number.length()) / 2.0f);
        }

        int value;
        Model character;

        for(int i = 0; i < number.length(); i++) {
            value = number.charAt(i) - '0';

            character = CharacterModel.newInstance(game, texture, width, height);
            character.setAlpha(alpha);
            writeCharacter(character, x + (i * (width + space)), y, value);
            characters.add(character);
        }
    }

    public void write(String number, float x, float y, float w, float h, int type){
        if(type == ALIGN_MID){
            x -= (((width + space) * number.length()) / 2.0f);
        }

        int value;
        Model character;

        for(int i = 0; i < number.length(); i++) {
            value = number.charAt(i) - '0';

            character = CharacterModel.newInstance(game, texture, w, h);
            character.setAlpha(alpha);
            writeCharacter(character, x + (i * (width + space)), y, value);
            characters.add(character);
        }
    }

    public void draw(float deltaTime){
        GL11 gl = game.getGLGraphics().getGL11();

        batcher.startBatch(texture);
        for(int i = 0; i < characters.size(); i++){
            batcher.draw(characters.get(i));
        }
        batcher.endBatch();
    }

    public void setAlpha(float alpha){
        this.alpha = alpha;
    }

    public List<Model> getCharacters(){
        return characters;
    }

    private void writeCharacter(Model model, float x, float y, int value){
        model.setCoord(x, y);
        model.setRegion(stride * value, 0, (stride * value) + width, height);
    }

}

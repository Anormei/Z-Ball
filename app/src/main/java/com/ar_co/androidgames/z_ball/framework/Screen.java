package com.ar_co.androidgames.z_ball.framework;

import com.ar_co.androidgames.z_ball.interfaces.Game;

public abstract class Screen {
    private final Game GAME;

    public Screen(Game game){
        GAME = game;
    }

    public Game getGame(){
        return GAME;
    }

    public abstract void onStart();

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();

    public void rawDraw(float deltaTime){}

    public void draw(Model model){
        model.bind();
        model.draw();
        model.unbind();
    }

    public boolean onBackPressed(){
        return false;
    }
}

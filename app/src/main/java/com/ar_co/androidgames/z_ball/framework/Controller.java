package com.ar_co.androidgames.z_ball.framework;


public abstract class Controller {

    public float TICK;
    public float time = 0;

    public Controller(float tick){
        this.TICK = tick;
    }

    public void update(float deltaTime){
        time += deltaTime;
        if(TICK > 0) {
            while (time >= TICK) {
                time -= TICK;
                update();
            }
        }else{
            update();
        }
    }

    public void update(){

    }

    public void draw(GLGraphics glGraphics){

    }

}

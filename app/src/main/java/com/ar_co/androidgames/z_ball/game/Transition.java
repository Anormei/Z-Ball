package com.ar_co.androidgames.z_ball.game;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Camera2D;
import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Screen;
import com.ar_co.androidgames.z_ball.interfaces.Game;
import com.ar_co.androidgames.z_ball.interfaces.Input;

import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public class Transition extends Screen {

    Screen prevScreen;
    Screen nextScreen;

    private final float TICK = 0.004f;
    private boolean next = false;
    private boolean finish = false;

    private Model screen;
    private Controller transition;

    private GLGame game;
    private Camera2D camera2D;

    private float[] rgb;

    public Transition(GLGame g, Screen prevScreen, Screen nextScreen, float[] rgb){
        super(g);
        this.game = g;
        this.rgb = rgb;
        this.prevScreen = prevScreen;
        this.nextScreen = nextScreen;
        nextScreen.onStart();
    }

    @Override
    public void onStart(){
        screen = new Model(game, 1080f, 1920f);
        screen.setCoord(0, 0);
        screen.setAlpha(1);
        screen.setSrcColor(rgb[0], rgb[1], rgb[2]);
        camera2D = new Camera2D(game.getGLGraphics(), 1080f, 1920f);
        createControllers();
    }

    private void createControllers(){
        transition = new Controller(TICK){
            @Override
            public void update(){

                if(!next) {
                    if (screen.getAlpha() > 0) {
                        screen.setAlpha(screen.getAlpha() - 0.01f);
                    } else {
                        prevScreen.pause();
                        prevScreen.dispose();

                        nextScreen.onStart();
                        nextScreen.resume();
                        nextScreen.update(0);

                        Log.i("Intro", "Transition = " + screen.getAlpha());
                        next = true;
                    }
                }

                if(next) {
                    if (screen.getAlpha() < 1.0f) {
                        screen.setAlpha(screen.getAlpha() + 0.01f);
                    }else{
                        if(!finish){
                            getGame().setScreen(nextScreen);
                            finish = true;
                        }
                    }
                }

            }

        };
    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEventList = getGame().getInput().getTouchEvents();
        if(next){
            nextScreen.update(deltaTime);
        }else{
            prevScreen.update(deltaTime);
        }

        transition.update(deltaTime);
    }

    @Override
    public void present(float deltaTime) {
        //Graphics g = getGame().getGraphics();
        GL11 gl = game.getGLGraphics().getGL11();



        gl.glClearColor(0, 0.0784313f, 0.0039215f, 1);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
        camera2D.setViewportAndMatrices();

        gl.glEnable(GL11.GL_BLEND);
        gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL11.GL_TEXTURE_2D);

        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);


        if(next){
            nextScreen.rawDraw(deltaTime);
        }else{
            prevScreen.rawDraw(deltaTime);
        }

        screen.bind();
        screen.draw();
        screen.unbind();
        //g.drawRect(0, 0, 1081, 1921,(Color.argb(alpha, 0, 0, 0)));

        gl.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL11.GL_COLOR_ARRAY);
    }

    @Override
    public boolean onBackPressed(){
        return true;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose(){

    }
}

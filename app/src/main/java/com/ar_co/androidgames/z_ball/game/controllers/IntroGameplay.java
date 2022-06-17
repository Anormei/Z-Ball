package com.ar_co.androidgames.z_ball.game.controllers;

import android.graphics.RectF;
import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Camera2D;
import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.animations.ExitAnimation;
import com.ar_co.androidgames.z_ball.game.animations.IntroAnimation;
import com.ar_co.androidgames.z_ball.game.models.Ball;
import com.ar_co.androidgames.z_ball.interfaces.Input;

import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public class IntroGameplay {

    private static final float IDLE_SPEED = 0.5f;

    private GLGame game;
    private Assets assets;

    private Camera2D camera2D;
    private Ball ball;
    private Model hold;

    private Indicators indicators;

    private boolean isReady;
    private boolean launch;

    public Launcher launcher;

    private Controller idle;

    private Gameplay gameplay;

    public IntroGameplay(GLGame game, Camera2D camera2D, Ball ball, float x, float y, float width, float height, Gameplay gameplay){
        this.game = game;
        assets = Assets.getInstance(game);

        this.gameplay = gameplay;

        RectF rect = new RectF(x, y, width, height);

        this.ball = ball;
        indicators = new Indicators(game, camera2D.frustumWidth, camera2D.frustumHeight, rect);
        launcher = new Launcher(ball, camera2D.frustumWidth, camera2D.frustumHeight);

        this.camera2D = camera2D;

        hold = new Model(game, assets.getImage("gameplay/hold"));
        hold.setCoord(65f, 930f);

        setControllers();

    }

    public void setControllers(){

        idle = new Controller(IDLE_SPEED){

            boolean visible = false;

            @Override
            public void update(){
                if(visible){
                    hold.setAlpha(1.0f);
                }else {
                    hold.setAlpha(0);
                }
                visible = !visible;
            }
        };

    }

    public void update(float deltaTime, List<Input.TouchEvent> touchEvents) {
        readTouchEvents(touchEvents);

        indicators.update(deltaTime);

        if(!isReady){
            idle.update(deltaTime);
        }

        if(launch){
            launcher.launch(deltaTime);
            gameplay.updateDusts(deltaTime);
        }



    }


    public void draw(float deltaTime){
        GL11 gl = game.getGLGraphics().getGL11();

        draw(hold);
        indicators.draw(deltaTime);
    }

    public void readTouchEvents(List<Input.TouchEvent> touchEvents){
        if(launch){
            return;
        }
        for (int i = 0; i < touchEvents.size(); i++) {
            Input.TouchEvent touchEvent = touchEvents.get(i);

            if (touchEvent.type == Input.TouchEvent.TOUCH_DOWN) {
                isReady = true;
                hold.setAlpha(1.0f);
            }

            if (touchEvent.type == Input.TouchEvent.TOUCH_DRAGGED) {
                hold.setAlpha(1.0f);
                isReady = true;
                indicators.showIndicators(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));
            }

            if (touchEvent.type == Input.TouchEvent.TOUCH_UP) {
                indicators.hideIndicators();
                launch = true;

                launcher.prepareLaunch(indicators.getLaunchX(), indicators.getLaunchY());
                indicators.setLaunchDirection(ball);
                assets.playSound("launch");

                gameplay.placeDusts();
            }
        }
    }

    public boolean isFinished(){
        return launcher.hasLaunched();
    }

    private void draw(Model model){
        model.bind();
        model.draw();
        model.unbind();
    }

}

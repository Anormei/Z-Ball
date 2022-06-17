package com.ar_co.androidgames.z_ball.game.screens;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Camera2D;
import com.ar_co.androidgames.z_ball.framework.FPSCounter;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.controllers.Galaxy;
import com.ar_co.androidgames.z_ball.game.controllers.Gamebar;
import com.ar_co.androidgames.z_ball.game.controllers.IntroGameplay;
import com.ar_co.androidgames.z_ball.game.controllers.ObstacleHandler;
import com.ar_co.androidgames.z_ball.game.controllers.ScoreSystem;
import com.ar_co.androidgames.z_ball.game.controllers.Gameplay;
import com.ar_co.androidgames.z_ball.game.controllers.Scoreboard;
import com.ar_co.androidgames.z_ball.game.models.Boundary;
import com.ar_co.androidgames.z_ball.interfaces.FileIO;
import com.ar_co.androidgames.z_ball.interfaces.Game;
import com.ar_co.androidgames.z_ball.interfaces.Graphics;
import com.ar_co.androidgames.z_ball.interfaces.Input;
import com.ar_co.androidgames.z_ball.framework.Settings;
import com.ar_co.androidgames.z_ball.framework.Screen;
import com.ar_co.androidgames.z_ball.game.models.Ball;
import com.ar_co.androidgames.z_ball.game.models.Obstacle;
import com.ar_co.androidgames.z_ball.game.models.Platform;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

public class ZBallScreen extends Screen {

    private enum GameState{
        Ready,
        Running,
        Pause,
        GameOver
    }

    private static final float WORLD_WIDTH = 1080.0f;
    private static final float WORLD_HEIGHT = 1920.0f;

    private GLGame game;
    private GameState gameState;

    private Assets assets;
    private Camera2D camera2D;

    private ScoreSystem scoreSystem;

    private IntroGameplay introGameplay;
    private Gameplay gameplay;
    private ObstacleHandler obstacleHandler;
    private Scoreboard scoreboard;

    private Gamebar gamebar;

    private Galaxy galaxy;

    private Boundary topBoundary;
    private Boundary bottomBoundary;

    private FPSCounter fpsCounter = new FPSCounter();

    public ZBallScreen(Game g){
        super(g);
        this.game = (GLGame) g;
    }

    @Override
    public void onStart(){
        assets = Assets.getInstance(game);
        camera2D = new Camera2D(game.getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);

        galaxy = new Galaxy(game, WORLD_WIDTH, WORLD_HEIGHT, true);

        scoreSystem = ScoreSystem.getInstance();

        gameplay = new Gameplay(game, WORLD_WIDTH, WORLD_HEIGHT);
        introGameplay = new IntroGameplay(game, camera2D, gameplay.getBall(), 0, 328f, WORLD_WIDTH, 1794f, gameplay);

        obstacleHandler = new ObstacleHandler(game, gameplay);
        scoreboard = new Scoreboard(game, camera2D);

        gamebar = new Gamebar(game, gameplay);
        topBoundary = new Boundary(game, 0, 0, 1080, 250, true);
        bottomBoundary = new Boundary(game, 0, 1815, 1080, 105, false);
        gameState = GameState.Ready;
    }

    @Override
    public void update(float deltaTime){
        List<Input.TouchEvent> touchEvents = getGame().getInput().getTouchEvents();
        galaxy.update(deltaTime);

        if(gameState == GameState.Ready){
            introGameplay.update(deltaTime, touchEvents);
            if(introGameplay.isFinished()){
                gameState = GameState.Running;
                gameplay.started = true;
            }
        }else if(gameState == GameState.Running) {
            updateRunning(touchEvents, deltaTime);
        }else if(gameState == GameState.GameOver){
            scoreboard.update(deltaTime, touchEvents);
        }
        obstacleHandler.update(deltaTime);
        gamebar.update(deltaTime);
        topBoundary.update(deltaTime);
        bottomBoundary.update(deltaTime);
    }

    private void updateRunning(List<Input.TouchEvent> touchEvents, float deltaTime){
        for(int i = 0; i < touchEvents.size(); i++){
            Input.TouchEvent touchEvent = touchEvents.get(i);
                if(touchEvent.type == Input.TouchEvent.TOUCH_DOWN){
                    gameplay.addPlatform(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));
                }
            }

        if(!gameplay.isGameOver()){
            scoreSystem.update(deltaTime);
        }
        gameplay.update(deltaTime);

        if(gameplay.isExpired()){
            gameState = GameState.GameOver;

            Settings settings = Settings.getSettings(game.getFileIO());
            scoreboard.setScore(scoreSystem.getScore(), settings.getHighScore());

            settings.pushHighScore(scoreSystem.getScore());
            settings.save(game.getFileIO());
        }
    }

    @Override
    public void present(float deltaTime) {
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

        galaxy.draw(deltaTime);

        gameplay.draw(deltaTime);
        obstacleHandler.draw(deltaTime);
        gameplay.drawExplosions();
        topBoundary.bind();
        topBoundary.draw();
        topBoundary.unbind();

        bottomBoundary.bind();
        bottomBoundary.draw();
        bottomBoundary.unbind();
        if(gameState == GameState.Running) {
            gamebar.draw(deltaTime);
        }

        if(gameState == GameState.GameOver) {
            scoreboard.draw(deltaTime);
        }

        if(gameState == GameState.Ready){
            introGameplay.draw(deltaTime);
        }

        gl.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL11.GL_COLOR_ARRAY);

        fpsCounter.logFrame();
    }

    @Override
    public void rawDraw(float deltaTime){
        galaxy.draw(deltaTime);

        gameplay.draw(deltaTime);
        //obstacleHandler.draw(deltaTime);
        gameplay.drawExplosions();
        /*topBoundary.bind();
        topBoundary.draw();
        topBoundary.unbind();

        bottomBoundary.bind();
        bottomBoundary.draw();
        bottomBoundary.unbind();*/
        if(gameState == GameState.Running) {
            //gamebar.draw(deltaTime);
        }

        if(gameState == GameState.GameOver) {
            scoreboard.draw(deltaTime);
        }

        if(gameState == GameState.Ready){
            introGameplay.draw(deltaTime);
        }
    }

    @Override
    public boolean onBackPressed(){
        synchronized(this){
            if (gameState == GameState.GameOver) {
                game.transitionScreen(this, new MenuScreen(game), new float[]{0, 0.07843137254f, 0.00392156862f});
            } else if (gameState == GameState.Running) {
                super.onBackPressed();
            }
            return true;
        }
    }

    @Override
    public void pause(){
        if(gameState == GameState.Running){
            gameState = GameState.Pause;
        }
    }

    @Override
    public void resume(){
        if(gameState == GameState.Pause){
            gameState = GameState.Running;
        }
    }

    @Override
    public void dispose(){
        scoreSystem.dispose();
    }
}

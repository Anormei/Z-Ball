package com.ar_co.androidgames.z_ball.game.screens;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Camera2D;
import com.ar_co.androidgames.z_ball.framework.DrawUtils;
import com.ar_co.androidgames.z_ball.framework.FPSCounter;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Screen;
import com.ar_co.androidgames.z_ball.framework.Settings;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.controllers.Galaxy;
import com.ar_co.androidgames.z_ball.game.controllers.NumberWriter;
import com.ar_co.androidgames.z_ball.game.controllers.ScoreNumbers;
import com.ar_co.androidgames.z_ball.game.controllers.TitleBackground;
import com.ar_co.androidgames.z_ball.game.models.MenuButton;
import com.ar_co.androidgames.z_ball.interfaces.Game;
import com.ar_co.androidgames.z_ball.interfaces.Input;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

public class MenuScreen extends Screen {

    private static final float WORLD_WIDTH = 1080.0f;
    private static final float WORLD_HEIGHT = 1920.0f;

    private GLGame game;
    private Assets assets;
    private Camera2D camera2d;

    private Galaxy galaxy;

    private Model hiscoreLabel;

    private ScoreNumbers hiscoreNumbers;

    private Model titleMain;
    private Model titleInner;
    private TitleBackground titleBackground;

    private MenuButton playButton;
    private SpriteBatcher batcher;

    private Settings settings;
    private float hiscore;
    private boolean shown = false;

    private Random random = new Random();
    private FPSCounter fpsCounter = new FPSCounter();

    private boolean go = false;

    public MenuScreen(Game g){
        super(g);
        Log.i("MenuScreen", "Initialized");
        game = ((GLGame)getGame());
    }

    @Override
    public void onStart(){
        assets = Assets.getInstance(game);
        camera2d = new Camera2D(game.getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);

        galaxy = new Galaxy(game, WORLD_WIDTH, WORLD_HEIGHT, true);

        settings = Settings.getSettings(game.getFileIO());

        hiscoreLabel = new Model(game, assets.getImage("menu/hiscore/hiscorelabel"));
        hiscoreLabel.setCoord(351.0f, 32.0f);

        this.hiscoreNumbers = new ScoreNumbers(game, 540f, 91f){
            @Override
            public void update(float deltaTime){
                writeScore(String.format("%.2f", hiscore), 42f, 54f);
            }
        };

        hiscoreNumbers.changeSize(0.75f);

        titleMain = new Model(game, assets.getImage("menu/title/titlemain"));
        titleMain.setCoord(68.0f, 363.0f);

        titleInner = new Model(game, assets.getImage("menu/title/titleinner"));
        titleInner.setCoord(88.0f, 379.0f);

        titleBackground = new TitleBackground(game, WORLD_WIDTH, WORLD_HEIGHT);

        playButton = new MenuButton(game, camera2d, assets.getImage("menu/buttons/playbutton"), 0, 999f, 1080f, 174f,
                new MenuButton.OnClickListener() {
                    private boolean clicked;

                    @Override
                    public void onClick() {
                        if(clicked){
                            return;
                        }

                        getGame().transitionScreen(MenuScreen.this, new ZBallScreen(getGame()), new float[]{0, 0.07843137254f, 0.00392156862f});
                        assets.playSound("play");
                        clicked = true;
                    }
                });

        batcher = new SpriteBatcher(game, 100, 32);
    }

    @Override
    public void update(float deltaTime){
        List<Input.TouchEvent> touchEvents = getGame().getInput().getTouchEvents();

        galaxy.update(deltaTime);

        hiscore = settings.getHighScore();
        hiscoreNumbers.update(deltaTime);

        titleBackground.update(deltaTime);

        playButton.readTouchEvents(touchEvents);
    }

    @Override
    public void present(float deltaTime){
        GL11 gl = game.getGLGraphics().getGL11();

        gl.glClearColor(0, 0.0784313f, 0.0039215f, 1);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
        camera2d.setViewportAndMatrices();

        gl.glEnable(GL11.GL_BLEND);
        gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL11.GL_TEXTURE_2D);

        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        galaxy.draw(deltaTime);

        //draw(hiscoreBorder);
        draw(hiscoreLabel);
        //draw(playcountLabel);

        hiscoreNumbers.draw(deltaTime);

        draw(titleMain);
        //draw(titleOutline);
        draw(titleInner);

        DrawUtils.drawStencil(gl);
        draw(titleInner);

        DrawUtils.drawOverlay(gl);
        batcher.startBatch(Assets.getInstance(game).getImage("menu/title/titlebackground"));
        for (Model model : titleBackground.getModels()){
            if(model.getX2() >= titleInner.getX() && model.getY2() >= titleInner.getY() && model.getX() <= titleInner.getX2() && model.getY() <= titleInner.getY2())
            batcher.draw(model);
        }
        batcher.endBatch();

        DrawUtils.finishStencil(gl);

        draw(playButton);

        gl.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL11.GL_COLOR_ARRAY);

        fpsCounter.logFrame();
    }

    @Override
    public void rawDraw(float deltaTime){
        GL11 gl = game.getGLGraphics().getGL11();
        galaxy.draw(deltaTime);

        //draw(hiscoreBorder);
        draw(hiscoreLabel);
        //draw(playcountLabel);

        hiscoreNumbers.draw(deltaTime);

        draw(titleMain);
        //draw(titleOutline);
        draw(titleInner);

        DrawUtils.drawStencil(gl);
        draw(titleInner);

        DrawUtils.drawOverlay(gl);
        batcher.startBatch(Assets.getInstance(game).getImage("menu/title/titlebackground"));
        for (Model model : titleBackground.getModels()){
            if(model.getX2() >= titleInner.getX() && model.getY2() >= titleInner.getY() && model.getX() <= titleInner.getX2() && model.getY() <= titleInner.getY2())
                batcher.draw(model);
        }
        batcher.endBatch();

        DrawUtils.finishStencil(gl);

        draw(playButton);

    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }

    @Override
    public void dispose(){

    }

}

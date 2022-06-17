package com.ar_co.androidgames.z_ball.game;

import com.ar_co.androidgames.z_ball.framework.AndroidAudio;
import com.ar_co.androidgames.z_ball.framework.AndroidFileIO;
import com.ar_co.androidgames.z_ball.framework.AndroidTouchHandler;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.GLGraphics;
import com.ar_co.androidgames.z_ball.framework.Screen;
import com.ar_co.androidgames.z_ball.game.screens.MenuScreen;
import com.ar_co.androidgames.z_ball.interfaces.Audio;
import com.ar_co.androidgames.z_ball.interfaces.FileIO;
import com.ar_co.androidgames.z_ball.interfaces.Graphics;
import com.ar_co.androidgames.z_ball.interfaces.Input;

public class ZBallGame extends GLGame {

    private GLGraphics mGraphics;
    private Audio mAudio;
    private Input mInput;
    private FileIO mFileIO;
    private Screen mCurrentScreen;

    @Override
    public void setupGame(){
        super.setupGame();
        mGraphics = new GLGraphics(glView);
        mAudio = new AndroidAudio(this);
        mInput = new AndroidTouchHandler(glView, 1, 1);
        mFileIO = new AndroidFileIO(this);
    }

    @Override
    public Graphics getGraphics(){
        throw new IllegalStateException("Use GL");
    }

    @Override
    public GLGraphics getGLGraphics(){
        return mGraphics;
    }

    @Override
    public Audio getAudio(){
        return mAudio;
    }

    @Override
    public Input getInput(){
        return mInput;
    }

    @Override
    public FileIO getFileIO(){
        return mFileIO;
    }

    @Override
    public void createScreen(Screen screen){
        if(screen == null){
            throw new IllegalArgumentException("Null pointer exception");
        }
        mCurrentScreen.pause();
        mCurrentScreen.dispose();
        screen.onStart();
        screen.resume();
        screen.update(0);
        mCurrentScreen = screen;
    }

    @Override
    public void setScreen(Screen screen){
        if(screen == null){
            throw new IllegalArgumentException("Null pointer exception");
        }
        mCurrentScreen.pause();
        mCurrentScreen.dispose();
        screen.resume();
        screen.update(0);
        mCurrentScreen = screen;
    }

    @Override
    public void transitionScreen(Screen prevScreen, Screen nextScreen, float[] rgb){
        if(prevScreen == null || nextScreen == null){
            throw new IllegalArgumentException("Null pointer exception");
        }
        Screen screen = new Transition(this, prevScreen, nextScreen, rgb);
        screen.onStart();
        screen.resume();
        screen.update(0);
        mCurrentScreen = screen;
    }

    @Override
    public Screen getCurrentScreen(){
        return mCurrentScreen;
    }

    @Override
    public Screen getStartScreen(){
        mCurrentScreen = new MenuScreen(this);
        mCurrentScreen.onStart();
        return mCurrentScreen;
    }

    @Override
    public void onBackPressed(){
        if(getCurrentScreen() == null || !getCurrentScreen().onBackPressed()){
            super.onBackPressed();
        }
    }

}

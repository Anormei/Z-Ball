package com.ar_co.androidgames.z_ball.framework;

import android.opengl.GLSurfaceView;

import com.ar_co.androidgames.z_ball.game.Assets;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer, GLGame.StateHandler{
    enum GLState{
        Initialized,
        Running,
        Paused,
        Finished,
        Idle
    }

    private GLGame game;
    private GLState state = GLState.Initialized;
    private Object stateChanged = new Object();
    private long startTime = System.nanoTime();
    private Assets assets;

    public GLRenderer(GLGame game){
        this.game = game;
        assets = Assets.getInstance(game);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config){
        game.getGLGraphics().setGL(gl);

        synchronized(stateChanged){
            if(state == GLState.Initialized){
                assets.loadAssets(game);
                game.getStartScreen();
            }else{
                assets.reloadAssets(game);
                VerticesReloader.getInstance().reload();
            }
            state = GLState.Running;
            game.getCurrentScreen().resume();
            startTime = System.nanoTime();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height){

    }

    @Override
    public void onDrawFrame(GL10 gl){
        GLState state = null;

        synchronized(stateChanged){
            state = this.state;
        }

        if(state == GLState.Running){
            float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            game.getCurrentScreen().update(deltaTime);
            game.getCurrentScreen().present(deltaTime);
        }

        if(state == GLState.Paused){
            game.getCurrentScreen().pause();
            synchronized(stateChanged){
                this.state = GLState.Idle;
                stateChanged.notifyAll();
            }
        }

        if(state == GLState.Finished){
            game.getCurrentScreen().pause();
            game.getCurrentScreen().dispose();
            synchronized(stateChanged){
                this.state = GLState.Idle;
                stateChanged.notifyAll();
            }
        }
    }

    @Override
    public void changeState(GLState state){
        this.state = state;
    }

    @Override
    public Object getSync(){
        return stateChanged;
    }

}

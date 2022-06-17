package com.ar_co.androidgames.z_ball.framework;

import android.opengl.GLSurfaceView;

import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.interfaces.Game;

import java.util.ArrayList;
import java.util.List;

public abstract class GLGame extends GameActivity implements Game {

    interface StateHandler{
        void changeState(GLRenderer.GLState state);
        Object getSync();
    }

    interface DisposalUnit{
        void dispose();
    }

    protected GLSurfaceView glView;
    private StateHandler stateHandler;
    private List<DisposalUnit> disposalUnits = new ArrayList<>();
    @Override
    public void setupGame(){
        GLRenderer renderer = new GLRenderer(this);
        stateHandler = renderer;

        glView = new GLSurfaceView(this);
        glView.setEGLConfigChooser(8, 8, 8, 8, 0, 8);
        glView.setRenderer(renderer);
        addView(glView);
    }


    @Override
    public void onResume(){
        super.onResume();
        glView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        synchronized(stateHandler.getSync()){
            if(isFinishing()){
                stateHandler.changeState(GLRenderer.GLState.Finished);
            }else{
                stateHandler.changeState(GLRenderer.GLState.Paused);
            }

            while(true){
                try {
                    stateHandler.getSync().wait();
                    break;
                }catch(InterruptedException e){

                }
            }
        }
        glView.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(isFinishing()) {
            for(int i = 0; i < disposalUnits.size(); i++){
                disposalUnits.get(i).dispose();
            }
            disposalUnits.clear();
            disposalUnits = null;
            Assets.getInstance(this).unloadAssets(this);
            VerticesReloader.getInstance().unload();
        }
    }

    public void addDisposalUnit(DisposalUnit disposalUnit){
        if(disposalUnits.contains(disposalUnit)){
            return;
        }
        disposalUnits.add(disposalUnit);
    }

    public abstract GLGraphics getGLGraphics();

}

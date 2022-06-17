package com.ar_co.androidgames.z_ball.framework;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class GLGraphics {

    private GLSurfaceView glView;
    private GL10 gl;

    public GLGraphics(GLSurfaceView glView){
        this.glView = glView;
    }

    public int getWidth(){
        return glView.getWidth();
    }

    public int getHeight(){
        return glView.getHeight();
    }

    public void setGL(GL10 gl){
        this.gl = (GL11)gl;
    }

    public GL10 getGL10(){
        return gl;
    }

    public GL11 getGL11(){
        if(gl instanceof GL11){
            return (GL11)gl;
        }else{
            return null;
        }
    }
}

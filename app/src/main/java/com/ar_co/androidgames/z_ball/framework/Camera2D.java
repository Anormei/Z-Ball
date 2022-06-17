package com.ar_co.androidgames.z_ball.framework;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Camera2D {

    public float zoom;
    public final float frustumWidth;
    public final float frustumHeight;
    private final GLGraphics glGraphics;

    private float x;
    private float y;

    public Camera2D(GLGraphics glGraphics, float frustumWidth, float frustumHeight) {
        this.glGraphics = glGraphics;
        this.frustumWidth = frustumWidth;
        this.frustumHeight = frustumHeight;
        x = frustumWidth / 2;
        y = frustumHeight / 2;
        this.zoom = 1.0f;
    }

    public void setViewportAndMatrices() {
        GL11 gl = glGraphics.getGL11();
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(
                x - frustumWidth * zoom / 2,
                x + frustumWidth * zoom / 2,
                y + frustumHeight * zoom / 2,
                y - frustumHeight * zoom / 2,
                1, -1
                );
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public float touchX(float xTouch){
        float result = (xTouch / (float)glGraphics.getWidth()) * frustumWidth * zoom;
        return (result + x) - (frustumWidth * zoom / 2);
    }

    public float touchY(float yTouch){
        float result = (yTouch / (float)glGraphics.getHeight()) * frustumHeight * zoom;
        return (result + y) - (frustumHeight * zoom / 2);
    }

    public void focus(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getLeftFrustum(){
        return x - (frustumWidth * zoom / 2);
    }

    public float getRightFrustum(){
        return x + (frustumWidth * zoom / 2);
    }

    public float getTopFrustum(){
        return y - (frustumHeight * zoom / 2);
    }

    public float getBottomFrustum(){
        return y + (frustumHeight * zoom / 2);
    }

    public void reset(){
        x = frustumWidth / 2;
        y = frustumHeight / 2;
        zoom = 1.0f;
    }
}


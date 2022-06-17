package com.ar_co.androidgames.z_ball.game.models;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

public class Nebula extends Model {

    private static final int NUM_BACKGROUNDS = 16;
    private static final float BACKGROUND_WIDTH = 90.0f;
    private static final float TINT_CHANGE_SPEED = 0.5f;
    private final float MOD;

    public float[] tint = new float[]{0, 0, 0, 0.6f};
    private float[] scale = new float[2];

    public float scaleX;
    public float scaleY;

    private Texture[] backgrounds;

    private Random r;
    private Controller tintAlphaScaler;
    private boolean increase = true;

    public Nebula(GLGame game, float width) {
        super(game);
        MOD = (width / BACKGROUND_WIDTH);
        r = new Random();
        Assets a = Assets.getInstance(game);
        backgrounds = new Texture[NUM_BACKGROUNDS];
        for(int i = 0; i < NUM_BACKGROUNDS; i++){
            backgrounds[i] = a.getImage("backgrounds/background" + i);
        }
        createControllers();
        changeBackground();
    }

    private void createControllers(){
        tintAlphaScaler = new Controller(TINT_CHANGE_SPEED){
            private float timer = 0;
            @Override
            public void update(){
                if(increase) {
                    timer += TICK;
                    if(timer > 1f) {
                        setAlpha(alpha + 0.05f);
                        if(alpha >= 0.3f){
                            setAlpha(0.3f);
                            increase = false;
                            timer = 0;
                        }
                    }
                }else{
                    setAlpha(alpha - 0.05f);
                    if(alpha <= 0){
                        setAlpha(0);
                        increase = true;
                    }
                }
            }
        };
    }


    public void update(float deltaTime){
        //tintAlphaScaler.update(deltaTime);
    }

    public void changeBackground(){
        Texture background = backgrounds[r.nextInt(NUM_BACKGROUNDS)];
        setTexture(background);
        setWidth((float) background.width * MOD);
        setHeight((float) background.height * MOD);

        setX(0);

        scale[0] = r.nextBoolean() ? 1 : -1;
        scale[1] = r.nextBoolean() ? 1 : -1;

        //scale[0] = -1;
        //scale[1] = -1;

        //scale[0] = scale[0] == 1 ? scale[0] : scale[0] - 1;
        //scale[1] = scale[1] == 1 ? scale[1] : scale[1] - 1;
        Log.i("Nebula", "scale[0] (Horizontal) = " + scale[0] + ", scale[1] (Vertical) = " + scale[1]);
        //change hue
        int rgb1 = r.nextInt(3);
        int rgb2 = r.nextInt(3);
        int rgb3 = r.nextInt(3);

        srcColor[rgb1] = 1.0f;
        while(rgb2 == rgb1){
            rgb2 = r.nextInt(3);
        }
        srcColor[rgb2] = r.nextFloat();
        while(rgb3 == rgb1 || rgb3 == rgb2){
            rgb3 = r.nextInt(3);
        }
        srcColor[rgb3] = 0;
    }

    @Override
    public void draw(){
        if(!visible){
            return;
        }
        GL11 gl = glGraphics.getGL11();
        gl.glLoadIdentity();
        //scaleX = x;
        //scaleY = y;

        if(scale[0] == -1){
            //scaleX += x + width;
            gl.glTranslatef(x + x + width, 0, 0);
        }
        if(scale[1] == -1){
            gl.glTranslatef(0, y + y + height, 0);
        }

        //gl.glTranslatef(scaleX, scaleY, 0);
        gl.glScalef(scale[0], scale[1], 1);

        vertices.draw(GL11.GL_TRIANGLES, 0, 6);
        gl.glLoadIdentity();
    }

    /*@Override
    public void refresh(){
        verticesBuffer[0] = sides[0];
        verticesBuffer[1] = sides[1];

        verticesBuffer[8] = sides[2];
        verticesBuffer[9] = sides[1];

        verticesBuffer[16] = sides[0];
        verticesBuffer[17] = sides[3];

        verticesBuffer[24] = sides[2];
        verticesBuffer[25] = sides[3];

        for(int i = 2; i < verticesBuffer.length; i += 8) {
            verticesBuffer[i] = 1;
            verticesBuffer[i+1] = 1;
            verticesBuffer[i+2] = 1;
            verticesBuffer[i+3] = alpha;
        }

        verticesBuffer[6] = region[0];
        verticesBuffer[7] = region[1];
        verticesBuffer[14] = region[2];
        verticesBuffer[15] = region[1];
        verticesBuffer[22] = region[0];
        verticesBuffer[23] = region[3];
        verticesBuffer[30] = region[2];
        verticesBuffer[31] = region[3];

        vertices.setVertices(verticesBuffer, 0, 32);
    }

    private void flip(int align){
        //reset
        if(align == HORIZONTAL){
            setSides(-width - 1, 0, 0, height - 1);
        }else if(align == VERTICAL){
            setSides(0, -height - 1, width - 1, 0);
        }else if(align == HORIZONTAL_VERTICAL){
            setSides(-width - 1, -height - 1, 0, 0);
        }else{
            setSides(0, 0, width - 1, height - 1);
        }
    }

    private void setSides(float x, float y, float width, float height){
        sides[0] = x;
        sides[1] = y;
        sides[2] = width;
        sides[3] = height;
    }*/

}

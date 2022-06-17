package com.ar_co.androidgames.z_ball.framework;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL11;

public class Texture {

    private static final int MIN_FILTER = GL11.GL_NEAREST;
    private static final int MAG_FILTER = GL11.GL_NEAREST;
    private static final Canvas canvas = new Canvas();
    private static final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

    public int width;
    public int height;

    public int po2Width;
    public int po2Height;

    private AssetManager assets;
    private GLGraphics glGraphics;
    private String file;
    private int id;
    private Body body;


    public Texture(GLGame game, String file){
        this.glGraphics = game.getGLGraphics();
        assets = game.getAssets();
        this.file = file;
        load();
    }

    public void load(){
        GL11 gl = glGraphics.getGL11();
        int[] textureIds = new int[1];
        gl.glGenTextures(1, textureIds, 0);
        id = textureIds[0];

        InputStream in = null;

        try{
            in = assets.open(file);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            po2Width = getPowerOf2(width);
            po2Height = getPowerOf2(height);

            gl.glBindTexture(GL11.GL_TEXTURE_2D, id);
            if(po2Width != width || po2Height != height){
                Bitmap po2Bitmap = Bitmap.createBitmap(po2Width, po2Height, Bitmap.Config.ARGB_8888);
                canvas.setBitmap(po2Bitmap);
                canvas.drawBitmap(bitmap, 0, 0, paint);
                GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, po2Bitmap, 0);

                po2Bitmap.recycle();
                po2Bitmap = null;
            }else{
                GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);
            }

            gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, MIN_FILTER);
            gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, MAG_FILTER);
            gl.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            body = new Body(bitmap);
            bitmap.recycle();
            bitmap = null;
        }catch(IOException e){
            throw new RuntimeException("Couldn't load texture '" + file + "'", e);
        }finally{
            if(in != null){
                try{
                    in.close();
                }catch(IOException e){

                }
            }
        }
    }

    public void reload(){
        load();
    }

    public void bind(){
        GL11 gl = glGraphics.getGL11();
        gl.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public void dispose(){
        GL11 gl = glGraphics.getGL11();
        gl.glBindTexture(GL11.GL_TEXTURE_2D, id);
        int[] textureIds = {id};
        gl.glDeleteTextures(1, textureIds, 0);

        assets = null;
        body = null;
        glGraphics = null;
    }

    public Body getBody(){
        return body;
    }

    private int getPowerOf2(float length){
        int po2 = 2;
        while(po2 < length){
            po2 *= 2;
        }

        return po2;
    }

}

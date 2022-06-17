package com.ar_co.androidgames.z_ball.framework;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class Body {

    private boolean[] area;

    private static Rect srcRect = new Rect();
    private static Rect trgtRect = new Rect();
    private static Rect interceptedArea = new Rect();
    private static float[] pointOfCollision = new float[2];

    public Body(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        area = new boolean[width*height];
        for(int i = 0; i < pixels.length; i++){
            area[i] = pixels[i] != 0;
        }
    }

    public Body(int width, int height){
        area = new boolean[height * width];

        for(int i = 0; i < height * width; i++) {
            area[i] = true;
        }
    }

    public boolean[] getArea(){
        return area;
    }

    public static Rect getIntersection(){
        return interceptedArea;
    }

    public static boolean isTouching(Model src, Model trgt){
        srcRect.set((int)src.getX(), (int)src.getY(), (int)src.getX2(), (int)src.getY2());
        trgtRect.set((int)trgt.getX(), (int)trgt.getY(), (int)trgt.getX2(), (int)trgt.getY2());

        if(!interceptedArea.setIntersect(srcRect, trgtRect)){
            return false;
        }

        boolean[] srcArea = src.getBody().getArea();
        boolean[] trgtArea = trgt.getBody().getArea();

        for(int y = interceptedArea.top; y < interceptedArea.bottom; y++){
            for(int x = interceptedArea.left; x < interceptedArea.right; x++){
                if(srcArea[((int)src.getWidth() * (y - srcRect.top)) + (x - srcRect.left)] && trgtArea[((int)trgt.getWidth() * (y - trgtRect.top)) + (x - trgtRect.left)]){
                    pointOfCollision[0] = x;
                    pointOfCollision[1] = y;
                    return true;
                }
            }
        }
        return false;
    }

    public static float[] getPointOfCollision(){
        return pointOfCollision;
    }

    public static Rect getRect(Model src){
        srcRect.set((int)src.getX(), (int)src.getY(), (int)src.getX2(), (int)src.getY2());
        return srcRect;
    }

}

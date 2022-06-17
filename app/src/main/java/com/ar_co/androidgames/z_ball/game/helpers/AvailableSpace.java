package com.ar_co.androidgames.z_ball.game.helpers;

import com.ar_co.androidgames.z_ball.framework.Pool;

public class AvailableSpace {

    private static Pool<AvailableSpace> pool;

    private float x;
    private float width;

    private AvailableSpace(){

    }

    public static AvailableSpace newInstance(){

        if(pool == null){
            pool = new Pool<>(new Pool.PoolObjectFactory<AvailableSpace>() {
                @Override
                public AvailableSpace createObject() {
                    return new AvailableSpace();
                }
            }, 100);
        }

        return pool.newObject();
    }

    public static void remove(AvailableSpace availableSpace){
        pool.free(availableSpace);
    }

    public void setX(float x){
        this.x = x;
    }

    public void setWidth(float width){
        this.width = width;
    }

    public float getX(){
        return x;
    }

    public float getWidth(){
        return width;
    }


}

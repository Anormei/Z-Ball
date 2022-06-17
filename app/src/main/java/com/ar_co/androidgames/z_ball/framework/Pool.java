package com.ar_co.androidgames.z_ball.framework;

import java.util.ArrayList;
import java.util.List;

public class Pool<T>{

    public interface PoolObjectFactory<T>{
        T createObject();
    }

    private final List<T> FREE_OBJECTS;
    private final PoolObjectFactory<T> FACTORY;
    private final int MAX_SIZE;

    public Pool(PoolObjectFactory<T> factory, int maxSize){
        FACTORY = factory;
        MAX_SIZE = maxSize;
        FREE_OBJECTS = new ArrayList<>(maxSize);
    }

    public T newObject(){
        T object = null;

        if(FREE_OBJECTS.isEmpty()){
            object = FACTORY.createObject();
        }else{
            object = FREE_OBJECTS.remove(FREE_OBJECTS.size() - 1);
        }

        return object;
    }

    public void free(T object){
        if(FREE_OBJECTS.size() < MAX_SIZE)
            FREE_OBJECTS.add(object);
    }

    public void clear(){
        FREE_OBJECTS.clear();
    }

}

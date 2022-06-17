package com.ar_co.androidgames.z_ball.framework;

import java.util.ArrayList;
import java.util.List;

public class VerticesReloader {

    public interface Reloader{
        void reloadBuffers();
    }

    private static VerticesReloader verticesReloader;
    private List<Reloader> reloaders = new ArrayList<>();

    private VerticesReloader(){

    }

    public static VerticesReloader getInstance(){
        if(verticesReloader == null){
            verticesReloader = new VerticesReloader();
        }

        return verticesReloader;
    }

    public void addReloader(VerticesReloader.Reloader reloader){
        this.reloaders.add(reloader);
    }

    public void reload(){
        for(int i = 0; i < reloaders.size(); i++){
            reloaders.get(i).reloadBuffers();
        }
    }

    public void unload(){
        reloaders.clear();
    }
}

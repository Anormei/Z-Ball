package com.ar_co.androidgames.z_ball.game;

import android.content.res.AssetManager;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.interfaces.Audio;
import com.ar_co.androidgames.z_ball.interfaces.Sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assets {

    private static final String IMAGE_FOLDER = "images";
    private static final String AUDIO_FOLDER = "audio";

    private static Assets assets;
    private boolean loaded;

    private AssetManager a;

    private Map<String, Texture> imageMap;
    private Map<String, Sound> audioMap;

    public static Assets getInstance(GLGame game){
        if(assets == null) {
            assets = new Assets(game);
        }
        return assets;
    }

    private Assets(GLGame game){
        a = game.getAssets();
        imageMap = new HashMap<>();
        audioMap = new HashMap<>();
    }

    public void loadAssets(GLGame game){
        if(loaded){
            return;
        }
        Audio audio = game.getAudio();

        List<String> files;
        files = getImageFiles();

        String path;
        int index;
        String file;

        for(int i = 0; i < files.size(); i++){
            path = files.get(i);
            file = path;
            index = file.lastIndexOf('.');

            if(index >= 0){
                file = path.substring(0, index);
            }
            imageMap.put(file, new Texture(game, path));
        }

        files = getAudioFiles();

        for(int i = 0; i < files.size(); i++){
            path = files.get(i);
            file = path;
            index = file.lastIndexOf('.');

            if(index >= 0){
                file = path.substring(0, index);
            }
            audioMap.put(file, audio.newSound(path));
        }

        loaded = true;
    }

    public void reloadAssets(GLGame game){
        if(!loaded){
            return;
        }

        for(String file : imageMap.keySet()){
            imageMap.get(file).reload();
        }

    }

    public void unloadAssets(GLGame game){
        if(!loaded){
            return;
        }

        for(String file : imageMap.keySet()){
            imageMap.get(file).dispose();
        }

        imageMap.clear();

        game.getAudio().dispose();
        audioMap.clear();

        imageMap = null;
        audioMap = null;
        a = null;

        loaded = false;
        assets = null;

    }

    public Texture getImage(String file){
        return imageMap.get(IMAGE_FOLDER + "/" + file);
    }

    public void playSound(String file) {
        audioMap.get(AUDIO_FOLDER + "/" + file).play(1);
    }

    private List<String> getImageFiles(){
        List<String> list = new ArrayList<>();
        getAssetFiles(IMAGE_FOLDER, list);
        return list;
    }

    private List<String> getAudioFiles(){
        List<String> list = new ArrayList<>();
        getAssetFiles(AUDIO_FOLDER, list);
        return list;
    }

    private boolean getAssetFiles(String path, List<String> list){
        String[] arr;

        try{
            arr = a.list(path);
            if(arr.length > 0){
                for(String file : arr){
                    if(!getAssetFiles(path + (!path.equals("") ? "/" : "") + file, list)){
                        return false;
                    }
                }
            }else{
                list.add(path);
            }
        }catch (IOException e){
            return false;
        }
        return true;
    }

}

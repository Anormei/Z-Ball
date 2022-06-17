package com.ar_co.androidgames.z_ball.framework;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ar_co.androidgames.z_ball.interfaces.Audio;
import com.ar_co.androidgames.z_ball.interfaces.Sound;

import java.io.IOException;

public class AndroidAudio implements Audio {

    private SoundPool mSoundPool;
    private AssetManager mAssets;

    public AndroidAudio(Context context){
        mAssets = context.getAssets();
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public Sound newSound(String fileName) {
        try{
            //AssetFileDescriptor assetFileDescriptor = mAssets.openFd("audio/" + fileName);
            AssetFileDescriptor assetFileDescriptor = mAssets.openFd(fileName);
            int soundId = mSoundPool.load(assetFileDescriptor, 1);
            return new AndroidSound(mSoundPool, soundId);
        }catch(IOException e){
            throw new RuntimeException("Could not load sound file: '" + fileName + "'" + e);
        }
    }

    @Override
    public void dispose(){
        mSoundPool.release();
        mSoundPool = null;
    }

}

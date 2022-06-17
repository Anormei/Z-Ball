package com.ar_co.androidgames.z_ball.framework;

import android.media.SoundPool;

import com.ar_co.androidgames.z_ball.interfaces.Sound;

public class AndroidSound implements Sound {

    private int mSoundId;
    private SoundPool mSoundPool;

    public AndroidSound(SoundPool soundPool, int soundId){
        mSoundPool = soundPool;
        mSoundId = soundId;
    }

    @Override
    public void play(float volume){
        mSoundPool.play(mSoundId, volume, volume, 0, 0, 1);
    }

    @Override
    public void dispose(){
        mSoundPool.unload(mSoundId);
    }
}

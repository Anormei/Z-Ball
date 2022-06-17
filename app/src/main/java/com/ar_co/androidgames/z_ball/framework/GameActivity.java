package com.ar_co.androidgames.z_ball.framework;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ar_co.androidgames.z_ball.R;
import com.ar_co.androidgames.z_ball.interfaces.FileIO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.IOException;

public abstract class GameActivity extends AppCompatActivity{

    private static final String WAKE_LOCK = "WakeLock";
    private static final int REQUEST_LEADERBOARD = 0;
    private static final int RC_SIGN_IN = 1;

    private static final int CHANGE_AD_STATE = 0;
    private static final int SHOW_INTERSTITIAL = 1;
    private static final int SHOW_LEADERBOARD = 2;
    private static final int RATE = 3;

    private RelativeLayout mMain;
    private Handler mHandler;
    private PowerManager.WakeLock mWakeLock;
    private boolean mResolvingConnectionFailure = false;

    private long hiscore;

    private FileIO fileIO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics resolution = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(resolution);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        fileIO = new AndroidFileIO(this);

        mMain = (RelativeLayout) findViewById(R.id.main);

        setupGame();

        AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest interstitialRequest = new AdRequest.Builder()
                .build();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, WAKE_LOCK);
    }

    @Override
    public void onResume(){
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    public void onPause(){
        super.onPause();
        mWakeLock.release();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    protected void addView(View view){
        mMain.addView(view);
    }

    protected abstract void setupGame();

}

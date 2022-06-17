package com.ar_co.androidgames.z_ball.interfaces;

import android.graphics.Bitmap;

import com.ar_co.androidgames.z_ball.interfaces.Graphics.PixmapFormat;

public interface Pixmap {

    int getWidth();

    int getHeight();

    PixmapFormat getFormat();

    Bitmap getBitmap();

    void dispose();
}

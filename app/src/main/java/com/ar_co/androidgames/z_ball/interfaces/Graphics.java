package com.ar_co.androidgames.z_ball.interfaces;

public interface Graphics{
    enum PixmapFormat{
        ARGB8888, ARGB4444, RGB565
    }

    interface Options{
        Options setAlpha(int alpha);
        Options setOverlay(int r, int g, int b, int intensity);
        Options setHue(int degrees);
        Options setSaturation(int value);
        void process();
        void reset();
    }

    Pixmap newPixmap(String fileName, PixmapFormat format);

    void clear(int color);

    void drawPixel(int x, int y, int color);

    void drawLine(int x, int y, int x2, int y2, int length);

    void drawRect(int x, int y, int width, int height, int color);

    void drawElipse(int x, int y, int width, int height, int color, int strokeWidth);

    void drawPixmap(Pixmap pixmap, int x, int y);

    void drawPixmap(Pixmap pixmap, int x, int y, Options options);

    void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight);

    void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight, Options options);

    int getWidth();

    int getHeight();

    Options options();

}

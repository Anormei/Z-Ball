package com.ar_co.androidgames.z_ball.interfaces;

import java.io.IOException;

public interface FileIO {
    Object readObject(String filename) throws IOException;
    void writeObject(Object object, String filename) throws IOException;
}

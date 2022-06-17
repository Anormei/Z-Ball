package com.ar_co.androidgames.z_ball.framework;

import android.app.Activity;
import android.content.Context;

import com.ar_co.androidgames.z_ball.interfaces.FileIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AndroidFileIO implements FileIO {
    Context mContext;

    public AndroidFileIO(Context context){
        mContext = context;
    }

    @Override
    public Object readObject(String filename) throws IOException{
        ObjectInputStream objectIn = null;
        Object object = null;

        try{
            FileInputStream fileIn = mContext.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();
        }catch(FileNotFoundException e){
            throw new IOException();
        }catch(ClassNotFoundException e){
            throw new IOException();
        }finally{
            if(objectIn != null){
                try{
                    objectIn.close();
                }catch(IOException e){

                }
            }
        }

        return object;
    }

    @Override
    public void writeObject(Object object, String filename) throws IOException{
        ObjectOutputStream objectOut = null;

        try {
            FileOutputStream fileOut = mContext.getApplicationContext().openFileOutput(filename, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();
        }finally{
            if(objectOut != null){
                try{
                    objectOut.close();
                }catch(IOException e){

                }
            }
        }
    }
}

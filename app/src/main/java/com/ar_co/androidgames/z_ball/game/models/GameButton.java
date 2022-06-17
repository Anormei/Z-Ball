package com.ar_co.androidgames.z_ball.game.models;

import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Button;
import com.ar_co.androidgames.z_ball.framework.Camera2D;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.framework.Vertices;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.interfaces.Input;

import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public class GameButton extends Model {

    public interface OnClickListener{
        void onClick();
    }

    private Camera2D camera2d;

    private Button button;
    private OnClickListener listener;

    private Assets a;

    private Texture label;
    private Texture labelDown;

    public GameButton(GLGame game, Camera2D camera2d, Texture label, Texture labelDown, OnClickListener listener){
        super(game, label);
        a = Assets.getInstance(game);
        this.label = label;
        this.labelDown = labelDown;
        this.camera2d = camera2d;
        button = new Button(glGraphics, x, y, width, height);
        //setX((width / 2) - (float) (label.width / 2));
        //setY(y + (height / 2) - (float) (label.height / 2));

        this.listener = listener;
        setListener();
    }

    public void readTouchEvents(List<Input.TouchEvent> touchEvents){
        for(int i = 0; i < touchEvents.size(); i++){
            Input.TouchEvent touch = touchEvents.get(i);
            if(touch.pointerId == 0) {
                button.readTouchEvent(touch.type, camera2d.touchX(touch.x), camera2d.touchY(touch.y));
            }
        }

    }

    private void setListener(){
        button.attachListener(new Button.Listener() {
            @Override
            public void onTouchDown() {
                a.playSound("buttondown");
                setTexture(labelDown);
                Log.i("GameButton", "Clicked");
            }

            @Override
            public void onTouchUp() {
                a.playSound("buttonup");
                listener.onClick();
                setTexture(label);
            }

            @Override
            public void onDrag() {
                setTexture(labelDown);
            }

            @Override
            public void onDefault(){
                a.playSound("buttonup");
                setTexture(label);
            }
        });
    }

    @Override
    public void setCoord(float x, float y){
        super.setCoord(x, y);
        button.x = x;
        button.y = y;
        refreshButton();
    }

    @Override
    public void setX(float x){
        super.setX(x);
        refreshButton();
    }

    @Override
    public void setY(float y){
        super.setY(y);
        refreshButton();
    }

    @Override
    public void setWidth(float width){
        super.setWidth(width);
        refreshButton();
    }

    @Override
    public void setHeight(float height){
        super.setHeight(height);
        refreshButton();
    }

    private void refreshButton(){
        button.x = x;
        button.y = y;
        button.width = x + width;
        button.height = y + height;
    }

}

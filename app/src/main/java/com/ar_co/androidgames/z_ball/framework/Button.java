package com.ar_co.androidgames.z_ball.framework;

import com.ar_co.androidgames.z_ball.interfaces.Input;

public class Button {

    public static abstract class Listener{
        public void onTouchUp(){

        }

        public void onTouchDown(){

        }

        public void onDrag(){

        }

        public void onDefault(){

        }
    }

    public float x;
    public float y;
    public float width;
    public float height;

    private boolean inside;
    private boolean isDown;

    private GLGraphics glGraphics;
    private Listener listener;

    public Button(GLGraphics glGraphics, float x, float y, float width, float height){
        this.glGraphics = glGraphics;
        this.x = x;
        this.y = y;
        this.width = x + width;
        this.height = y + height;
    }

    public void readTouchEvent(int touchEvent, float touchX, float touchY){
        if(touchX >= x && touchX <= width && touchY >= y && touchY <= height){
            if(touchEvent == Input.TouchEvent.TOUCH_DOWN && !isDown){
                inside = true;
                isDown = true;
                if(listener != null){
                    listener.onTouchDown();
                }
            }

            if(touchEvent == Input.TouchEvent.TOUCH_DRAGGED && isDown){
                if(listener != null){
                    listener.onDrag();
                }
            }

            if(touchEvent == Input.TouchEvent.TOUCH_UP && isDown){
                isDown = false;
                if(listener != null){
                    listener.onTouchUp();
                }
            }
                //return true;
            }else{
                if(inside && isDown) {
                    isDown = false;
                    inside = false;
                    if (listener != null) {
                        listener.onDefault();
                    }
                }
            }

    }



    public void attachListener(Listener listener){
        this.listener = listener;
    }
}

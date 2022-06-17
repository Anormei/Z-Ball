package com.ar_co.androidgames.z_ball.framework;

import android.view.MotionEvent;
import android.view.View;

import com.ar_co.androidgames.z_ball.interfaces.Input;

import java.util.ArrayList;
import java.util.List;

public class AndroidTouchHandler implements Input, View.OnTouchListener{

    private static final int MAX_TOUCH_POINTS = 10;
    private boolean[] mIsTouched = new boolean[MAX_TOUCH_POINTS];
    private int[] mTouchX = new int[MAX_TOUCH_POINTS];
    private int[] mTouchY = new int[MAX_TOUCH_POINTS];
    private int[] mId = new int[MAX_TOUCH_POINTS];

    private Pool<TouchEvent> mTouchEventPool;
    private List<TouchEvent> mTouchEvents = new ArrayList<>();
    private List<TouchEvent> mTouchEventsBuffer = new ArrayList<>();

    private float mScaleX;
    private float mScaleY;

    public AndroidTouchHandler(View v, float scaleX, float scaleY){
        Pool.PoolObjectFactory factory = new Pool.PoolObjectFactory() {
            @Override
            public Object createObject() {
                return new Input.TouchEvent();
            }
        };

        mTouchEventPool = new Pool<>(factory, 100);
        v.setOnTouchListener(this);
        mScaleX = scaleX;
        mScaleY = scaleY;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        synchronized(this){
            int action = event.getActionMasked();
            int pointerIndex = event.getActionIndex();
            int pointerCount = event.getPointerCount();

            Input.TouchEvent touchEvent;

            for(int i = 0; i < MAX_TOUCH_POINTS; i++){
                if(i >= pointerCount){
                    mIsTouched[i] = false;
                    mId[i] = -1;
                    continue;
                }

                if(event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex){
                    continue;
                }

                switch(action){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchEvent = mTouchEventPool.newObject();
                        processTouch(touchEvent, event, Input.TouchEvent.TOUCH_DOWN);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchEvent = mTouchEventPool.newObject();
                        processTouch(touchEvent, event, TouchEvent.TOUCH_DRAGGED);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchEvent = mTouchEventPool.newObject();
                        processTouch(touchEvent, event, Input.TouchEvent.TOUCH_UP);
                        break;

                }
            }
            return true;
        }
    }

    @Override
    public boolean isTouchDown(int pointerId){
        synchronized(this){
            int idIndex = getIdIndex(pointerId);
            if(checkIndex(idIndex))
                return false;
            else
                return mIsTouched[idIndex];
        }
    }

    @Override
    public int getTouchX(int pointerId){
        synchronized(this){
            int idIndex = getIdIndex(pointerId);
            if(checkIndex(idIndex))
                return 0;
            else
                return mTouchX[idIndex];
        }
    }

    @Override
    public int getTouchY(int pointerId){
        synchronized(this){
            int idIndex = getIdIndex(pointerId);
            if(checkIndex(idIndex))
                return 0;
            else
                return mTouchY[idIndex];
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents(){
        synchronized(this){
            for(int i = 0; i < mTouchEvents.size(); i++){
                mTouchEventPool.free(mTouchEvents.get(i));
            }
            mTouchEvents.clear();
            mTouchEvents.addAll(mTouchEventsBuffer);
            mTouchEventsBuffer.clear();

            return mTouchEvents;
        }
    }

    private void processTouch(Input.TouchEvent touchEvent, MotionEvent event, int type){
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        touchEvent.type = type;
        touchEvent.pointerId = pointerId;
        touchEvent.x = mTouchX[index] = (int) (event.getX(index) * mScaleX);
        touchEvent.y = mTouchY[index] = (int) (event.getY(index) * mScaleY);
        mIsTouched[index] = true;
        mId[index] = pointerId;
        mTouchEventsBuffer.add(touchEvent);
    }

    private int getIdIndex(int pointerId){
        for(int i = 0; i < MAX_TOUCH_POINTS; i++){
            if(mId[i] == pointerId)
            return i;

        }
        return - 1;
    }

    private boolean checkIndex(int index) {
        if (index < 0 || index >= MAX_TOUCH_POINTS)
            return true;
        else
            return false;
    }



}

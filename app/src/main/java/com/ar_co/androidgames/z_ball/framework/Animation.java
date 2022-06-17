package com.ar_co.androidgames.z_ball.framework;

import android.util.Log;

import com.ar_co.androidgames.z_ball.game.Assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Animation {
    public enum AnimationState{
        Starting,
        Playing,
        Paused,
        Finished,
        Skipping
    }

    public static final float FADE_IN = 0.0f;
    public static final float FADE_OUT = 1.0f;
    private static final float ALPHA_TYPE = 255.0f;
    private static final float ALPHA_CHANGE = 1.0f / ALPHA_TYPE;

    public Assets assets;
    public volatile boolean skipping = false;

    protected final GLGame game;

    private List<Sequence> sequences = new ArrayList<>();
    private List<Float> accBuffer = new ArrayList<>();

    private AnimationState state = AnimationState.Starting;

    public Animation(GLGame game){
        this.game = game;
        assets = Assets.getInstance(game);
        onCreate();
    }

    public Animation(GLGame game, Sequence... sequences){
        this(game);
        addSequence(sequences);
        onCreate();
    }

    public void onCreate(){

    }

    public Sequence createSequence(Task... tasks){
        for(int i = 0; i < tasks.length; i++){
            tasks[i].initControllers();
        }
        return new Sequence(tasks);
    }

    public void addSequence(Sequence... sequences){
        this.sequences.addAll(Arrays.asList(sequences));
    }

    public void clearSequence(){
        sequences.clear();
    }

    public void update(float deltaTime){
        if(state == AnimationState.Starting || state == AnimationState.Playing) {
            state = AnimationState.Playing;
            for (int i = 0; i < sequences.size(); i++) {
                Sequence sequence = sequences.get(i);
                if (sequence.isFinished()) {
                    continue;
                }
                sequence.update(deltaTime);
                return;
            }
            state = AnimationState.Finished;
        }

    }

    public void draw(float deltaTime){
        for(int i = 0; i < sequences.size(); i++){
            Sequence Sequence = sequences.get(i);
            Sequence.draw(game.getGLGraphics());
            return;
        }
    }

    public void resume(){
        if(state == AnimationState.Paused)
        state = AnimationState.Playing;
    }

    public void pause(){
        state = AnimationState.Paused;
    }

    public void skip(){
        if(state == AnimationState.Skipping){
            return;
        }

        state = AnimationState.Skipping;
        for(int i = 0; i < sequences.size(); i++){
            Sequence s = sequences.get(i);
            s.skip();
        }
        state = AnimationState.Finished;
    }

    public void rewind(){
        if(state != AnimationState.Starting) {
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).reset();
            }
            state = AnimationState.Starting;
        }
    }

    public boolean isPlaying(){
        return state == AnimationState.Starting || state == AnimationState.Playing;
    }

    public boolean isPaused(){
        return state == AnimationState.Paused;
    }

    public boolean isFinished(){
        return state == AnimationState.Finished;
    }

    //Animation helpers:

    public Sequence pause(float time){

        final float expTime = time;

        return new Sequence(){

            float currTime = 0;

            @Override
            public void update(float deltaTime){
                currTime += deltaTime;
                if(currTime >= expTime){
                    finish();
                }
            }

            @Override
            public void reset(){
                currTime = 0;
            }
        };
    }

    public Task custom(TaskController... controllers){
        final TaskController[] cont = controllers;
        return new Task(){
            @Override
            public TaskController[] attachControllers(){
                return cont;
            }

            @Override
            public void resetControllers(){
                this.controllers.clear();
                this.finished.clear();
                this.controllers = Arrays.asList(attachControllers());
            }
        };
    }

    public Task playSound(String sound, float when){
        final String file = sound;
        return new Task(when){
            @Override
            public TaskController[] attachControllers(){
                return new TaskController[]{new TaskController(){

                    @Override
                    public void update(){
                        if(state != AnimationState.Skipping){
                            assets.playSound(file);
                        }
                        finish(this);
                    }

                }
                };
            }
        };
    }

    public Task place(Model model, float x, float y, float when){
        final float destX = x;
        final float destY = y;

        return new Task(model, when){

            @Override
            public TaskController[] attachControllers(){
                return new TaskController[]{new TaskController(){
                    @Override
                    public void update(){
                        model.setX(destX);
                        model.setY(destY);
                        finish(this);
                    }
                }

                };
            }

            @Override
            public void draw(GLGraphics g){
                model.bind();
                model.draw();
                model.unbind();
            }
        };
    }

    public Task show(Model model, float when){
        final float at = when;

        return new Task(model, at){
            @Override
            public TaskController[] attachControllers(){
                return new TaskController[]{new TaskController(){
                    @Override
                    public void update(){
                        model.setAlpha(0.0f);
                        finish(this);
                    }
                }};
            }
        };
    }

    public Task hide(Model model, float when){

        final float at = when;

        return new Task(model, at){
            @Override
            public TaskController[] attachControllers(){
                return new TaskController[]{new TaskController(){
                    @Override
                    public void update(){
                        model.setAlpha(1.0f);
                        finish(this);
                    }
                }};
            }
        };
    }

    public Task move(Model model, float x, float y, float x2, float y2, float when, float duration){
        model.setX((int)x);
        model.setY((int)y);
        return move(model, x2, y2, when, duration);
    }

    public Task move(Model model, float x, float y, float when, float duration) {
        final float destX = (int)x;
        final float destY = (int)y;
        final float expTime = duration;

        return new Task(model, when) {

            @Override
            public TaskController[] attachControllers() {

                return new TaskController[]{new TaskController() {

                    @Override
                    public void ready(){
                        float length = getLength(model.getX(), destX);
                        TICK = length > 0 ? expTime / length : 0;
                    }

                    @Override
                    public void update() {
                        if (model.getX() < destX) {
                            model.setX(model.getX() + 1);
                        } else if (model.getX() > destX) {
                            model.setX(model.getX() - 1);
                        }else{
                            finish(this);
                        }
                    }
                },
                        new TaskController() {
                            @Override
                            public void ready(){
                                float length = getLength(model.getY(), destY);
                                TICK = length > 0 ? expTime / length : 0;
                            }

                            @Override
                            public void update() {
                                if (model.getY() < destY) {
                                    model.setY(model.getY() + 1);
                                } else if (model.getY() > destY) {
                                    model.setY(model.getY() - 1);
                                }else{
                                    finish(this);
                                }
                            }
                        }
                };
            }

        };
    }

    public Task resize(Model model, float width, float height, float originX, float originY, float when, float duration){
        float scaleX = width / model.getWidth();
        float scaleY = height / model.getHeight();
        return scale(model, scaleX, scaleY, originX, originY, when, duration);
    }

    public Task scale(Model model, float scaleX, float scaleY, float originX, float originY, float when, float duration){

        final float width = (int)(model.getWidth() * scaleX);
        final float height = (int)(model.getHeight() * scaleY);

        final float origX = originX;
        final float origY = originY;

        final float expTime = duration;

        return new Task(model, when){

            @Override
            public TaskController[] attachControllers(){

                return new TaskController[]{new TaskController(){

                    @Override
                    public void ready(){
                        float length = getLength(model.getWidth(), width);
                        TICK = length > 0 ? expTime / length : 0;
                    }

                    @Override
                    public void update(){
                        if(model.getWidth() < width){
                            model.setWidth(model.width += 1.0f);
                        }else if(model.getWidth() > width){
                            model.setWidth(model.width -= 1.0f);
                        }else{
                            finish(this);
                            Log.i("Animation", "Finished scaling width");
                        }
                    }
                },
                        new TaskController(){

                            @Override
                            public void ready(){
                                float length = getLength(model.getHeight(), height);
                                TICK = length > 0 ? expTime / length : 0;
                            }

                            @Override
                            public void update(){
                                if(model.getHeight() < height){
                                    model.setHeight(model.height += 1.0f);
                                }else if(model.getHeight() > height){
                                    model.setHeight(model.height -= 1.0f);
                                }else{
                                    finish(this);
                                    Log.i("Animation", "Finished scaling height");
                                }
                            }
                        },

                        new TaskController(){
                            private float destX;

                            @Override
                            public void ready(){
                                //destX = (int)(model.getX() - ((width - model.getWidth()) * ((origX % model.getWidth()) / model.getWidth())));
                                destX = (int)(model.getX() - ((width - model.getWidth()) * (origX % 1.0f)));
                                float length = getLength(model.getX(), destX);
                                TICK = length > 0 ? expTime / length : 0;
                            }

                            @Override
                            public void update(){
                                if (model.getX() < destX) {
                                    model.setX(model.getX() + 1);
                                } else if (model.getX() > destX) {
                                    model.setX(model.getX() - 1);
                                } else {
                                    finish(this);
                                    Log.i("Animation", "Finished moving X");
                                }
                            }
                        },

                        new TaskController(){

                            private float destY;

                            @Override
                            public void ready(){
                                destY = (int)(model.getY() - ((height - model.getHeight()) * (origY % 1.0f)));
                                float length = getLength(model.getY(), destY);
                                TICK = length > 0 ? expTime / length : 0;
                            }

                            @Override
                            public void update(){
                                if (model.getY() < destY) {
                                    model.setY(model.getY() + 1);
                                } else if (model.getY() > destY) {
                                    model.setY(model.getY() - 1);
                                } else {
                                    finish(this);
                                    Log.i("Animation", "Finished moving Y");
                                }
                            }
                        }

                };
            }
        };
    }

    public Task accelerate(Model model, float x, float y, float when, float accelerateIn, float duration, float accelerateOut){
        final float destX = x;
        final float destY = y;
        final float accIn = accelerateIn;
        final float accOut = accelerateOut;
        final float expTime = accIn + duration + accOut;
        final float xUnit = expTime / getLength(model.getX(), destX);
        final float yUnit = expTime / getLength(model.getY(), destY);

        final float[] xAccIn = getAccMods(accIn / xUnit, true);
        final float[] xAccOut = getAccMods(accOut / xUnit, false);
        final float[] yAccIn = getAccMods(accIn / yUnit, true);
        final float[] yAccOut = getAccMods(accOut / yUnit, false);

        return new Task(model, when){

            float accTime = 0;

            @Override
            public void update(float deltaTime){
                if(time >= when) {
                    if (accTime < expTime)
                        accTime += deltaTime;
                }
                super.update(deltaTime);
            }

            @Override
            public TaskController[] attachControllers(){

                return new TaskController[]{new TaskController(){

                    float mod = xAccIn[0];
                    int index = 1;

                    @Override
                    public void ready(){
                        TICK = xUnit;
                    }

                    @Override
                    public void update(float deltaTime){

                        time += deltaTime;
                        while(time >= TICK * ((mod > 1) ? mod : 1)){
                            time -= TICK * ((mod > 1) ? mod : 1);
                            if(accTime < accIn && index < xAccIn.length) {
                                mod = xAccIn[index++];
                            }
                            else if(accTime > expTime - accIn && accTime < expTime && index < xAccOut.length){
                                mod = xAccOut[index++];
                            }else{
                                index = 0;
                            }
                            update();
                        }
                    }

                    @Override
                    public void update(){
                        if (model.getX() < destX) {
                            model.setX(model.getX() + 1);
                        } else if (model.getX() > destX) {
                            model.setX(model.getX() - 1);
                        }else{
                            finish(this);
                        }
                    }
                },
                        new TaskController(){

                            float mod = yAccIn[0];
                            int index = 1;

                            @Override
                            public void ready(){
                                TICK = yUnit;
                            }

                            @Override
                            public void update(float deltaTime){

                                time += deltaTime;
                                while(time >= TICK * ((mod > 1) ? mod : 1)){
                                    time -= TICK * ((mod > 1) ? mod : 1);
                                    if(accTime < accIn && index < yAccIn.length) {
                                        mod = yAccIn[index++];
                                    }
                                    else if(accTime > expTime - accIn && accTime < expTime && index < yAccOut.length){
                                        mod = yAccOut[index++];
                                    }else{
                                        index = 0;
                                    }
                                    update();
                                }
                            }

                            @Override
                            public void update(){
                                if (model.getY() < destY) {
                                    model.setY(model.getY() + 1);
                                } else if (model.getY() > destY) {
                                    model.setY(model.getY() - 1);
                                }else{
                                    finish(this);
                                }
                            }
                        }

                };

            }

            @Override
            public void reset(){
                super.reset();
                accTime = 0;
            }
        };
    }

    public Task fade(Model model, float alpha, float when, float duration){

        final float desAlpha = alpha % 1.0f;
        //final float desAlpha = (+alpha - ((+alpha + model.alpha) % 1.0f));
        //final float desAlpha = alpha < 0 ?
        //        model.alpha - alpha + (+alpha - ((model.alpha + alpha) % 1)) :
        //        model.alpha + alpha - ((model.alpha + alpha) % 1);
        final float expTime = duration;
        //final float unit = duration / getLength(model.alpha, desAlpha) * ALPHA_TYPE;

        return new Task(model, when){
            @Override
            public TaskController[] attachControllers() {
                return new TaskController[]{new TaskController(){

                    @Override
                    public void ready(){
                        //TICK = unit;
                        float length = getLength(model.alpha, desAlpha) * ALPHA_TYPE;
                        TICK = length > 0 ? expTime / (getLength(model.alpha, desAlpha) * ALPHA_TYPE) : 0;
                        Log.i("Animation", "TICK is (fade) = " + TICK);
                    }

                    @Override
                    public void update(){
                        if(model.alpha < desAlpha) {
                            model.setAlpha(model.alpha + ALPHA_CHANGE > desAlpha ? desAlpha : model.alpha + ALPHA_CHANGE);
                        }else if(model.alpha > desAlpha){
                            model.setAlpha(model.alpha - ALPHA_CHANGE < desAlpha ? desAlpha : model.alpha - ALPHA_CHANGE);
                        }else{
                            Log.i("Animation", "has faded");
                            finish(this);
                        }
                    }
                }

                };
            }
        };
    }

    public Task fade(Model model, float setAlpha, float alpha, float when, float duration){
        model.alpha = setAlpha;
        return fade(model, alpha, when, duration);
    }

    private float[] getAccMods(float length, boolean in){
        float size = length;
        float mod = (float) Math.sqrt(size);
        accBuffer.add(mod);
        while(mod > 1){
            mod = (float) Math.sqrt(size -= mod);
            accBuffer.add(mod);
        }
        accBuffer.add(1.0f);
        float[] arr = new float[accBuffer.size()];
        for(int i = 0; i < accBuffer.size(); i++){
            if(in) {
                arr[i] = accBuffer.get(i);
            }else{
                arr[i] = accBuffer.get(accBuffer.size() - 1 - i);
            }
        }
        accBuffer.clear();
        return arr;
    }

    private float getLength(float p1, float p2) {
        return Math.max(p1, p2) - Math.min(p1, p2);
    }


}

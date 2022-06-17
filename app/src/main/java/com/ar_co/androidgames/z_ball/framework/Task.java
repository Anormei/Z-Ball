package com.ar_co.androidgames.z_ball.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Task {

    protected Model initModel;
    protected Model model;
    protected List<TaskController> controllers = new ArrayList<>();
    protected List<TaskController> finished = new ArrayList<>();
    protected float when = 0;
    protected float time = 0;

    private boolean isReady;

    public Task(){

    }

    public Task(Model model){
        this.model = model;
        initModel = Model.copyOf(model);
    }

    public Task(float when){
        this.when = when;
    }

    public Task(Model model, float when){
        this(model);
        this.when = when;
    }

    public void initControllers(){
        controllers.addAll(Arrays.asList(attachControllers()));
    }

    public void update(float deltaTime){
        if(time < when)
        time += deltaTime;

        if(time >= when) {
            if (controllers.size() > 0) {
                for (int i = 0; i < controllers.size(); i++) {
                    TaskController controller = controllers.get(i);
                    if(!isReady){
                        controller.ready();
                    }
                    controller.update(deltaTime);

                }
                if(!isReady){
                    isReady = true;
                }
                controllers.removeAll(finished);
            }
        }
    }

    public void draw(GLGraphics g){

    }

    public void finish(TaskController c){
        if(finished.contains(c)){
            return;
        }
        finished.add(c);
    }

    public void skip(){
        for(int i = 0; i < controllers.size(); i++){
            TaskController c = controllers.get(i);
            c.ready();
            while(!hasFinished(c)){
                c.update();
            }
        }
    }

    public boolean isFinished(){
        return controllers.size() == 0;
    }

    public boolean hasFinished(Controller c){
        return finished.contains(c);
    }

    public void reset(){
        isReady = false;
        if(model != null) {
            model.copyModel(initModel);
        }
        time = 0;
        resetControllers();
    }
    public abstract TaskController[] attachControllers();

    public List<TaskController> getControllers(){
        return controllers;
    }

    protected void resetControllers(){
        finished.addAll(controllers);
        controllers.clear();
        controllers.addAll(finished);
        finished.clear();
    }


}

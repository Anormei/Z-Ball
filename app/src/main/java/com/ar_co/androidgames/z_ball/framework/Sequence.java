package com.ar_co.androidgames.z_ball.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sequence {

    public boolean finished;
    private List<Task> tasks = new ArrayList<>();
    private List<Task> finishedTasks = new ArrayList<>();

    public Sequence() {

    }

    public Sequence(Task... tasks){
        this.tasks.addAll(Arrays.asList(tasks));
    }

    public void update(float deltaTime) {
        Task task;

        if(tasks.size() == 0){
            finish();
        }

        for(int i = 0; i < tasks.size(); i++){
            task = tasks.get(i);
            task.update(deltaTime);
            if(task.isFinished()){
                finishedTasks.add(tasks.get(i));
            }
        }
        tasks.removeAll(finishedTasks);
    }

    public void draw(GLGraphics g) {

    }

    public void skip(){
        for(int i = 0; i < tasks.size(); i++){
            Task task = tasks.get(i);
            task.skip();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void finish() {
        finished = true;
    }

    public void reset(){
        finishedTasks.addAll(tasks);
        tasks.clear();
        for(int i = 0; i < finishedTasks.size(); i++){
            finishedTasks.get(i).reset();
        }
        tasks.addAll(finishedTasks);
        finishedTasks.clear();
        finished = false;
    }

}

package com.ar_co.androidgames.z_ball.game.controllers;

import android.graphics.RectF;
import android.util.Log;

import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.DrawUtils;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.game.helpers.AvailableSpace;
import com.ar_co.androidgames.z_ball.game.models.Obstacle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

public class ObstacleHandler {

    private static final int MAX_OBSTACLES = 8;
    private static final int MIN_OBSTACLES = 4;
    private static final float MAX_INTERVAL = 2f;
    private static final float BOUNDARY = 607f; //389 //607 //452
    private static final float GAP_BOUNDARY = 340f;

    private static final float SMALLEST_WIDTH = 135f;
    private static final float START_DIFFICULTY = 1.75f;
    private static final float MAX_DIFFICULTY = 1.0f;

    private static Comparator<Obstacle> c = new Comparator<Obstacle>() {
        @Override
        public int compare(Obstacle lhs, Obstacle rhs) {
            if(lhs.getX() < rhs.getX()){
                return -1;
            }else if(lhs.getX() > rhs.getX()){
                return 1;
            }else{
                return 0;
            }
        }
    };

    private GLGame game;
    private Gameplay gameplay;
    private ScoreSystem scoreSystem;

    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Obstacle> active = new ArrayList<>();

    private List<AvailableSpace> availableSpaces = new ArrayList<>();
    private List<AvailableSpace> temp = new ArrayList<>();
    private float fullWidth;

    private float difficulty;

    private Random random = new Random();
    private Controller spawner;
    private Controller difficultyIncreaser;

    private ObstacleBackground obstacleBackground;

    private RectF rect1 = new RectF();
    private RectF rect2 = new RectF();

    private float[] colorBuffer = new float[]{1.0f, 0, 0, 0.6f};

    public ObstacleHandler(GLGame game, Gameplay gameplay){
        this.game = game;
        this.gameplay = gameplay;
        scoreSystem = ScoreSystem.getInstance();

        difficulty = START_DIFFICULTY;

        obstacleBackground = new ObstacleBackground(game, 1080, 1920);

        for(int i = 0; i < random.nextInt(MAX_OBSTACLES - MIN_OBSTACLES + 1) + MIN_OBSTACLES; i++){
            Obstacle obstacle = Obstacle.newInstance(game);

            do {
                obstacle.setX((int) ((random.nextFloat() * 1080f + obstacle.getWidth()) - obstacle.getWidth()));
                obstacle.setY((int) ((random.nextFloat() * 1920f + obstacle.getHeight()) - obstacle.getHeight()));
                rect1.set(obstacle.getX(), obstacle.getY(), obstacle.getX2(), obstacle.getY2());
            }while(holdsIntersection());
            obstacles.add(obstacle);
        }
        createControllers();
    }

    private boolean holdsIntersection(){
        for(int i = 0; i < obstacles.size(); i++){
            Obstacle obstacle = obstacles.get(i);
            rect2.set(obstacle.getX(), obstacle.getY(), obstacle.getX2(), obstacle.getY2());
            if(rect1.intersect(rect2)){
                return true;
            }
        }
        return false;
    }

    private void createControllers(){
        spawner = new Controller(random.nextFloat() * MAX_INTERVAL){
            @Override
            public void update(){
                //if(obstacles.size() < MAX_OBSTACLES){
                    placeObstacle();
                //}
                TICK = random.nextFloat() * MAX_INTERVAL * difficulty;
            }
        };

        difficultyIncreaser = new Controller(0.6f){
            @Override
            public void update(){
                if(difficulty > MAX_DIFFICULTY){
                    difficulty -= 0.0075f;
                }
            }
        };
    }

    public void update(float deltaTime){
        active.clear();

        for (Iterator<Obstacle> iterator = obstacles.iterator(); iterator.hasNext();) {
            Obstacle obstacle = iterator.next();
            if(gameplay.getBall().isTouching(obstacle) && gameplay.started){
                gameplay.die();
            }

            obstacle.update(deltaTime);

            if(obstacle.getY() < BOUNDARY * (1f + ((difficulty * 0.5f) - 0.5f))){
                active.add(obstacle);
            }

            if(obstacle.isExpired()){
                Obstacle.remove(obstacle);
                iterator.remove();
                scoreSystem.countObstacle();
            }
        }

        if(!gameplay.isGameOver()) {
            spawner.update(deltaTime);
            difficultyIncreaser.update(deltaTime);
        }
    }

    public void draw(float deltaTime){
        GL11 gl = game.getGLGraphics().getGL11();

        for(int i = 0; i < obstacles.size(); i++){
            Obstacle obstacle = obstacles.get(i);

            obstacle.bind();
            obstacle.draw();
            obstacle.unbind();
        }

        DrawUtils.drawStencil(gl);
        for(int i = 0; i < obstacles.size(); i++){
            Obstacle obstacle = obstacles.get(i);

            obstacle.bind();
            obstacle.draw();
            obstacle.unbind();
        }
        DrawUtils.drawOverlay(gl);
        obstacleBackground.draw(deltaTime);
        DrawUtils.finishStencil(gl);
    }

    public void placeObstacle(){
        sortActive();
        defineFreeSpaces();

        if(!isSpace()){
            return;
        }

        Obstacle obstacle = Obstacle.newInstance(game);
        while(!isRoom(obstacle)){
            obstacle.next();
        }

        defineAvailableSpaces(obstacle);

        AvailableSpace slot = availableSpaces.get(random.nextInt(availableSpaces.size()));
        float x = slot.getX() == 0 ? -obstacle.getWidth() : slot.getX();
        float w = (slot.getX() + slot.getWidth()) >= 1080f ? slot.getWidth() + obstacle.getWidth() : slot.getWidth() - obstacle.getWidth();
        obstacle.setX(x + (int)(random.nextFloat() * w));
        obstacle.setY(-obstacle.getHeight());

        obstacles.add(obstacle);
    }

    private void sortActive(){

        /*int index = 0;
        for(int i = 0; i < active.size(); i++){
            Obstacle toReplace = active.get(i);
            for(int j = i; j < active.size(); j++){
                Obstacle obstacle = active.get(j);
                if(obstacle.getX() < toReplace.getX()){
                    toReplace = obstacle;
                    index = j;
                }

            }
            active.set(index, active.set(i, toReplace));
        }*/

        Collections.sort(active, c);

        /*String string = "";

        for(int i = 0; i < active.size(); i++){
            Obstacle obstacle = active.get(i);
            string += "obstacle" + i + " x = " + obstacle.getX() + ", ";
        }
        Log.i("ObstacleHandler", string);*/

    }

    private void defineFreeSpaces(){
        for(int i = 0; i < availableSpaces.size(); i++){
            AvailableSpace a = availableSpaces.get(i);
            AvailableSpace.remove(a);
        }
        availableSpaces.clear();

        float progress = 0;

        /*if(active.size() == 0){
            AvailableSpace a = AvailableSpace.newInstance();

            a.setX(0);
            a.setWidth(1080f);

            availableSpaces.add(a);
            return;
        }*/
        for(int i = 0; i < active.size(); i++){
            Obstacle obstacle = active.get(i);
            if(obstacle.getX() > 0 && obstacle.getX() - progress > 0) {
                AvailableSpace a = AvailableSpace.newInstance();

                a.setX(progress);
                a.setWidth(obstacle.getX() - progress);
                availableSpaces.add(a);
            }
            progress = obstacle.getX2();
        }

        if(progress < 1080f){
            AvailableSpace a = AvailableSpace.newInstance();

            a.setX(progress);
            a.setWidth(1080f - progress);
            availableSpaces.add(a);
        }

        temp.clear();
        for(int i = 0; i < availableSpaces.size(); i++){
            AvailableSpace a = availableSpaces.get(i);
            if(a.getWidth() >= GAP_BOUNDARY){
                temp.add(a);
            }
        }

        if(temp.size() > 0) {
            AvailableSpace a1 = temp.get(random.nextInt(temp.size()));
            AvailableSpace a2 = AvailableSpace.newInstance();
            float newWidth = (int) (random.nextFloat() * (a1.getWidth() - GAP_BOUNDARY * difficulty));
            a2.setX(a1.getX() + newWidth + GAP_BOUNDARY * difficulty);
            a2.setWidth((a1.getX() + a1.getWidth()) - a2.getX());
            a1.setWidth(newWidth);
            availableSpaces.add(a2);
        }
    }

    private boolean isSpace(){
        for(int i = 0; i < availableSpaces.size(); i++){
            AvailableSpace a = availableSpaces.get(i);
            fullWidth += a.getWidth();
            if(a.getWidth() >= SMALLEST_WIDTH){
                return temp.size() > 0;
            }
        }
        return false;
    }

    private boolean isRoom(Obstacle obstacle){
        for(int i = 0; i < availableSpaces.size(); i++){
            if(obstacle.getWidth() <= availableSpaces.get(i).getWidth()){
                return true;
            }
        }

        return false;
    }

    private void defineAvailableSpaces(Obstacle obstacle){
        for(Iterator<AvailableSpace> iterator = availableSpaces.iterator(); iterator.hasNext();){
            AvailableSpace a = iterator.next();
            if(a.getWidth() < obstacle.getWidth()){
                AvailableSpace.remove(a);
                iterator.remove();
            }
        }

    }

}

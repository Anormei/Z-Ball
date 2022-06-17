package com.ar_co.androidgames.z_ball.game.controllers;

import android.graphics.Rect;
import android.graphics.RectF;

import com.ar_co.androidgames.z_ball.framework.Body;
import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.SpriteBatcher;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.models.Ball;
import com.ar_co.androidgames.z_ball.game.models.Explosion;
import com.ar_co.androidgames.z_ball.game.models.LaunchDust;
import com.ar_co.androidgames.z_ball.game.models.Platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Gameplay {

    private static final float DEFAULT_BALL_SPEED = 0.00325f;
    private static final float INITIAL_BALL_SPEED = 0.001f;
    private static final float SPAWN_INTERVAL = 0.46f;
    private static final float EXPLODE_SPAWN_RATE = 0.1f;
    private static final int MIN_EXPLOSIONS = 4;
    private static final int MAX_EXPLOSIONS = 12;
    private static final int MAX_EXPLOSION_INTERVALS = 7;
    private static final int MIN_SPARKLES = 25;
    private static final int MAX_SPARKLES = 30;
    private static final float DEACCELERATE_SPEED = 0.04f;
    private static final int MIN_DUSTS = 25;
    private static final int MAX_DUSTS = 35;
    public static final float MAX_AMMO = 12.0f;
    public static final float AMMO_COST = 2.0f;

    private static Random r = new Random();

    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;
    private SpriteBatcher launchDustBatcher;
    private float ammo;

    public boolean started;

    private Assets assets;

    private GLGame game;

    private ScoreSystem scoreSystem;

    private Ball ball;
    public float ballSpeed = INITIAL_BALL_SPEED;

    //private Boundary topBoundary;
    //private Boundary bottomBoundary;

    private List<Platform> platforms = new ArrayList<>();
    private List<Trail> trails = new ArrayList<>();
    private List<Explosion> explosions = new ArrayList<>();
    private List<LaunchDust> launchDusts = new ArrayList<>();

    private Controller ballHandler;
    private Controller platformHandler;
    private Controller trailSpawner;
    private Controller explodeSpawner;
    private Controller explodeAdvancer;
    private Controller deaccelerator;

    private Launcher launcher;
    private Indicators indicators;

    private boolean gameOver = false;
    private boolean expired = false;

    private SpriteBatcher platformBatcher;
    private SpriteBatcher trailBatcher;
    private SpriteBatcher explodeBatcher;

    private int explosionCount;

    public Gameplay(GLGame game, float width, float height){
        this.game = game;
        assets = Assets.getInstance(game);

        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;

        scoreSystem = ScoreSystem.getInstance();

        ball = new Ball(game);

        launcher = new Launcher(ball, width, height);
        indicators = new Indicators(game, width, height, new RectF(0, 250f, width, 1815f));

        ammo = MAX_AMMO;

        platformBatcher = new SpriteBatcher(game, 10, 32);
        trailBatcher = new SpriteBatcher(game, 64, 32);
        explodeBatcher = new SpriteBatcher(game, 100, 32);
        launchDustBatcher = new SpriteBatcher(game, 50, 32);

        createControllers();
    }

    private void createControllers(){
        ballHandler = new Controller(ballSpeed){
            @Override
            public void update(){
                ball.advance();

                if(ball.getX2() < 0){
                    ball.setX(WORLD_WIDTH - 1f);
                }

                if(ball.getX() > WORLD_WIDTH){
                    ball.setX(-ball.getWidth() + 1f);
                }

                if(ball.getY() <= 250 || ball.getY2() >= 1815){
                    ball.reverseY();
                }

                for(Iterator<Platform> iterator = platforms.iterator(); iterator.hasNext();) {
                    Platform platform = iterator.next();
                    if(!platform.isExpired()){
                        if (ball.isTouching(platform)) {
                            assets.playSound("ballhit");

                            Rect intersect = Body.getIntersection();

                            if(intersect.height() > 1 &&
                                    (ball.getX2() >= platform.getX() && ball.getX() < platform.getX() && ball.isMovingRight()) ^
                                            (ball.getX() <= platform.getX2() && ball.getX2() > platform.getX2() && !ball.isMovingRight())){

                                ball.reverseX();
                            }

                            if(intersect.width() > 1 &&
                                    (ball.getY2() >= platform.getY() && ball.getY() < platform.getY() && ball.isMovingDown()) ^
                                            (ball.getY() <= platform.getY2() && ball.getY2() > platform.getY2() && !ball.isMovingDown())){

                                ball.reverseY();
                            }
                        }
                    }

                }

                TICK = ballSpeed;
            }
        };

        platformHandler = new Controller(0){

            @Override
            public void update(float deltaTime){
                for(Iterator<Platform> iterator = platforms.iterator(); iterator.hasNext();) {
                    Platform platform = iterator.next();

                    platform.update(deltaTime);

                    if(platform.isDead()){
                        Platform.remove(platform);
                        iterator.remove();
                        assets.playSound("platformreload");
                    }
                }
            }
        };

        trailSpawner = new Controller(SPAWN_INTERVAL){
            @Override
            public void update(){
                Trail trail = Trail.newInstance(game);
                trail.setCoord((ball.getX() + (ball.getWidth() / 2)) - trail.getWidth() / 2, (ball.getY() + (ball.getHeight() / 2)) - trail.getHeight() / 2);
                trails.add(trail);
            }
        };

        explodeSpawner = new Controller(EXPLODE_SPAWN_RATE){
            @Override
            public void update(){
                if(explosionCount < MAX_EXPLOSION_INTERVALS){
                    explosionCount++;
                    for(int i = 0; i < r.nextInt(MAX_EXPLOSIONS - MIN_EXPLOSIONS + 1) + MIN_EXPLOSIONS; i++) {
                        Explosion explosion = Explosion.newInstance(game);
                        explosion.setCoord(ball.getX(), ball.getY());
                        explosions.add(explosion);
                    }
                }
            }
        };

        explodeAdvancer = new Controller(0){
            @Override
            public void update(float deltaTime){
                for(Iterator<Explosion> iterator = explosions.iterator(); iterator.hasNext();){
                    Explosion explosion = iterator.next();
                    explosion.update(deltaTime);
                    if(explosion.isExpired()){
                        Explosion.remove(explosion);
                        iterator.remove();
                    }
                }
            }
        };

        deaccelerator = new Controller(DEACCELERATE_SPEED){
            @Override
            public void update(){
                if(ballSpeed < DEFAULT_BALL_SPEED){
                    ballSpeed += 0.0001f;
                }else{
                    ballSpeed = DEFAULT_BALL_SPEED;
                }
            }
        };
    }

    public void update(float deltaTime) {
        if(!gameOver) {
            ballHandler.update(deltaTime);
            trailSpawner.update(deltaTime);
            ammo += deltaTime;
            if(ammo > MAX_AMMO){
                ammo = MAX_AMMO;
            }
            if(ballSpeed < DEFAULT_BALL_SPEED){
                deaccelerator.update(deltaTime);
            }
        }else{
            explodeSpawner.update(deltaTime);
            explodeAdvancer.update(deltaTime);
            if(explosions.size() == 0 && trails.size() == 0 && platforms.size() == 0 && launchDusts.size() == 0){
                expired = true;
                //game.transitionScreen(game.getCurrentScreen(), new MenuScreen(game));
            }
        }

        for(Iterator<Trail> iterator = trails.iterator(); iterator.hasNext();){
            Trail trail = iterator.next();
            trail.update(deltaTime);
            if(trail.isExpired()){
                Trail.remove(trail);
                iterator.remove();
            }
        }

        if(launchDusts.size() > 0){
            updateDusts(deltaTime);
        }

        platformHandler.update(deltaTime);

    }

    public void draw(float deltaTime){
        trailBatcher.startBatch(assets.getImage("gameplay/trail"));
        for(int i = 0; i < trails.size(); i++) {
            trailBatcher.draw(trails.get(i));
        }
        trailBatcher.endBatch();

        ball.bind();
        ball.draw();
        ball.unbind();

        platformBatcher.startBatch(assets.getImage("gameplay/platform"));
        for(int i = 0; i < platforms.size(); i++){
            platformBatcher.draw(platforms.get(i));
        }
        platformBatcher.endBatch();
        launchDustBatcher.startBatch(assets.getImage("gameplay/lsparkle"));
        for(int i = 0; i < launchDusts.size(); i++){
            launchDustBatcher.draw(launchDusts.get(i));
        }
        launchDustBatcher.endBatch();
    }

    public void drawExplosions(){
        explodeBatcher.startBatch(assets.getImage("gameplay/explosion"));
        for(int i = 0; i < explosions.size(); i++){
            explodeBatcher.draw(explosions.get(i));
        }
        explodeBatcher.endBatch();
    }

    public void addPlatform(float x, float y){
        if(ammo < AMMO_COST || gameOver){
            return;
        }

        Platform platform = Platform.newInstance(game);

        platform.setX((int)(x - platform.getWidth() / 2));
        platform.setY((int)(y - platform.getHeight() / 2));

        for(int i = 0; i < platforms.size(); i++){
            Platform platform2 = platforms.get(i);
            if(platform.getY2() >= platform2.getY() && platform.getY() <= platform2.getY2()){
                if(platform.getX() == platform2.getX()){
                    platform.setX(platform.getX() + 1);
                    platform.setY(platform.getY() + 1);
                }
            }
        }

        if(platform.getX() < 0){
            platform.setX(0);
        }

        if(platform.getX2() > WORLD_WIDTH){
            platform.setX(WORLD_WIDTH - platform.getWidth());
        }

        if(platform.getX2() > ball.getX() && platform.getX() < ball.getX2()){
            float x1 = ball.getX2() - platform.getX();
            float x2 = platform.getX2() - ball.getX();

            platform.setX(x1 > x2 ? ball.getX2() + 1 : ball.getX() - platform.getWidth() - 1);
        }

        if(platform.getY2() > 250f && platform.getY() < 1815f) {
            /*for(int i = 0; i < r.nextInt(MAX_SPARKLES - MIN_SPARKLES + 1) + MIN_SPARKLES; i++){
                sparkles.add(Sparkle.newInstance(game, platform.getX() + platform.getWidth() / 2, platform.getY() + platform.getHeight() / 2));
            }*/
            assets.playSound("platform");
            platforms.add(platform);
            ammo -= AMMO_COST;
            scoreSystem.countPlatform();
        }
    }

    public void killTrail(float deltaTime){
        for(Iterator<Trail> iterator = trails.iterator(); iterator.hasNext();){
            Trail trail = iterator.next();
            trail.update(deltaTime);
            if(trail.isExpired()){
                Trail.remove(trail);
                iterator.remove();
            }
        }
    }

    public Ball getBall(){
        return ball;
    }

    public int getPlatformCount(){
        return platforms.size();
    }

    public int getAmmoClipSize(){
        return (int)Math.floor(MAX_AMMO/AMMO_COST);
    }

    public int countAmmo(){
        return (int)Math.floor(ammo/AMMO_COST);
    }

    public float getAmmo(){
        return ammo;
    }

    public void die(){
        if(gameOver){
            return;
        }
        assets.playSound("death");
        gameOver = true;
        explodeSpawner.update();
        explosionCount = 0;
        ball.setVisibility(false);
    }

    public void placeDusts(){
        for(int i = 0; i < r.nextInt(MAX_DUSTS - MIN_DUSTS + 1) + MIN_DUSTS; i++){
            LaunchDust ld = LaunchDust.newInstance(game);
            ld.setCoord(ball.getX(), ball.getY());
            launchDusts.add(ld);

        }
    }

    public void updateDusts(float deltaTime){
        for(Iterator<LaunchDust> iterator = launchDusts.iterator(); iterator.hasNext();){
            LaunchDust ld = iterator.next();
            ld.update(deltaTime);
            if(ld.isExpired()){
                LaunchDust.remove(ld);
                iterator.remove();
            }
        }
    }

    public Platform getOldestPlatform(){
        return platforms.get(0);
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public boolean isExpired(){
        return expired;
    }

}

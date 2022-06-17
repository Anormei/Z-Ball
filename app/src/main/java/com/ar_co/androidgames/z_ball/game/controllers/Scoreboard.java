package com.ar_co.androidgames.z_ball.game.controllers;

import com.ar_co.androidgames.z_ball.framework.Animation;
import com.ar_co.androidgames.z_ball.framework.Camera2D;
import com.ar_co.androidgames.z_ball.framework.Controller;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Settings;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.game.animations.RetryAnimation;
import com.ar_co.androidgames.z_ball.game.animations.ScoreboardAnimation;
import com.ar_co.androidgames.z_ball.game.models.GameButton;
import com.ar_co.androidgames.z_ball.game.models.Slider;
import com.ar_co.androidgames.z_ball.game.screens.MenuScreen;
import com.ar_co.androidgames.z_ball.game.screens.ZBallScreen;
import com.ar_co.androidgames.z_ball.interfaces.Input;

import java.util.List;

public class Scoreboard {

    public static final float INCREMENT = 0.01f;
    public static final float COUNT_TIMER = 7f;
    private static final float COUNT_TICK = 0.1f;
    private Assets assets;

    private GameButton backButton;
    private GameButton retryButton;

    private Animation scoreboardAnimation;
    private Animation retryAnimation;

    private Model scoreboardLeft;
    private Model scoreboardRight;

    private Model gameOver;

    private Model scoreLabel;
    private Model bestLabel;

    private Slider topSlider;
    private Slider bottomSlider;

    private ScoreNumbers scoreNumbers;
    private ScoreNumbers bestNumbers;

    private float currentScore = 0;
    private float score;
    private float hiscore;

    private float speed;
    private Controller incrementer;
    private Controller countSound;
    private Camera2D camera2D;

    private boolean finished;
    private boolean selected;

    public Scoreboard(GLGame game, Camera2D camera2D){
        assets = Assets.getInstance(game);


        this.scoreboardLeft = new Model(game, assets.getImage("scoreboard/scoreboardleft"));
        this.scoreboardRight = new Model(game, assets.getImage("scoreboard/scoreboardright"));

        scoreboardLeft.setWidth(scoreboardLeft.getWidth() * 8f);
        scoreboardLeft.setHeight(scoreboardLeft.getHeight() * 8f);
        scoreboardLeft.setCoord(-scoreboardLeft.getWidth(), 583f);

        scoreboardRight.setWidth(scoreboardRight.getWidth() * 8f);
        scoreboardRight.setHeight(scoreboardRight.getHeight() * 8f);
        scoreboardRight.setCoord(1080, 583f);

        scoreLabel = new Model(game, assets.getImage("scoreboard/scoreboardlabel1"));
        scoreLabel.setCoord(1080f, 639f);

        bestLabel = new Model(game, assets.getImage("scoreboard/scoreboardlabel2"));
        bestLabel.setCoord(-bestLabel.getWidth(), 900f);

        this.gameOver = new Model(game, assets.getImage("scoreboard/gameover"));
        gameOver.setCoord(160f, -88f);

        topSlider = new Slider(game, 0, 316f, 1080f, 1080f, Slider.MOVE_LEFT);
        bottomSlider = new Slider(game, 0, 1650f, 1080f, 1080f, Slider.MOVE_RIGHT);

        //topSlider.setCoord(1080f, 316f);
        //bottomSlider.setCoord(-bottomSlider.getWidth(), 1650f);

        this.scoreNumbers = new ScoreNumbers(game, scoreLabel.getX() + (scoreLabel.getWidth() / 2), scoreLabel.getY() + 136f){
            @Override
            public void update(float deltaTime){
                writeScore(String.format("%.2f", currentScore));
            }
        };
        this.bestNumbers = new ScoreNumbers(game, bestLabel.getX() + (bestLabel.getWidth() / 2), bestLabel.getY() + 120f){
            @Override
            public void update(float deltaTime){
                writeScore(String.format("%.2f", hiscore));
            }
        };
        this.camera2D = camera2D;

        final GLGame g = game;

        backButton = new GameButton(game, camera2D, assets.getImage("scoreboard/back"), assets.getImage("scoreboard/backpressed"), new GameButton.OnClickListener(){
            @Override
            public void onClick(){
                g.transitionScreen(g.getCurrentScreen(), new MenuScreen(g), new float[]{0, 0.07843137254f, 0.00392156862f});
                selected = true;
            }
        });

        backButton.setCoord(-backButton.getWidth(), 1256f);

        retryButton = new GameButton(game, camera2D, assets.getImage("scoreboard/retry"), assets.getImage("scoreboard/retrypressed"), new GameButton.OnClickListener(){
            @Override
            public void onClick(){
                g.transitionScreen(g.getCurrentScreen(), new ZBallScreen(g), new float[]{0, 0.07843137254f, 0.00392156862f});
                selected = true;
            }
        });

        retryButton.setCoord(1080f, 1256f);

        backButton.setAlpha(1);
        retryButton.setAlpha(1);

        setControllers();

        scoreboardAnimation = new Animation(game){
            @Override
            public void onCreate(){
                addSequence(
                        /*createSequence(show(scoreboard, 0), show(gameOver, 0)),
                        createSequence(place(scoreboard, 1080f, 575f, 0), place(gameOver, 64f, -112f, 0)), //418
                        createSequence(move(gameOver, 64f, 418f, 0, 1f), move(scoreboard, 176f, 575f, 0.5f, 0.4f), playSound("slide", 0), playSound("slide", 0.5f), playSound("stop", 0.4f), playSound("stop", 1f))*/
                        createSequence(move(gameOver, 160f, 437f, 0, 1f), playSound("slide", 0)),//, move(topSlider, 0, 316f, 0.7f, 0.4f), move(bottomSlider, 0, 1650f, 0.7f, 0.5f)),
                        createSequence(move(scoreboardLeft, 80, 583, 0, 0.4f), move(scoreboardRight, 952, 583, 0, 0.4f), playSound("stop", 0.4f)),
                        createSequence(move(scoreLabel, 208f, 639f, 0, 0.6f), move(bestLabel, 232f, 900f, 0, 0.6f), playSound("slide", 0), playSound("stop", 0.6f))
                );
            }
        };

        retryAnimation = new Animation(game){
            @Override
            public void onCreate(){
                addSequence(
                        createSequence(show(backButton, 0), show(retryButton, 0)),
                        createSequence(move(backButton, 80f, 1256f, 0, 0.4f), move(retryButton, 760f, 1256f, 0, 0.4f), playSound("stop", 0.4f))
                );
            }
        };

        //Settings.displayBanner(game, true);
    }

    private void setControllers(){
        incrementer = new Controller(0){
            @Override
            public void update(){
                if(currentScore > hiscore){
                    hiscore = currentScore;
                }

                if(currentScore < score){
                    currentScore += INCREMENT;
                }else{
                    finished = true;
                }
            }
        };

        countSound = new Controller(COUNT_TICK){
            @Override
            public void update(){
                if(!finished){
                    assets.playSound("count");
                }
            }
        };
    }

    public void setScore(float score, float hiscore){
        this.score = score;
        this.hiscore = hiscore;

        if(score <= 1f){
            speed = COUNT_TIMER / (1f / INCREMENT);
        }else{
            speed = COUNT_TIMER / (score / INCREMENT);
        }
        incrementer.TICK = speed;
    }

    public void update(float deltaTime, List<Input.TouchEvent> touchEvents){
        for(int i = 0; i < touchEvents.size(); i++){
            Input.TouchEvent event = touchEvents.get(i);
            if(!finished && !retryAnimation.isFinished()) {
                if (event.type == Input.TouchEvent.TOUCH_DOWN) {
                    skip();
                }
            }
        }

        if(finished && retryAnimation.isFinished() && !selected){
            backButton.readTouchEvents(touchEvents);
            retryButton.readTouchEvents(touchEvents);
            //Log.i("Scoreboard", "Finished");
        }

        scoreboardAnimation.update(deltaTime);
        if(scoreboardAnimation.isFinished() && finished){
            retryAnimation.update(deltaTime);
        }

        scoreNumbers.setX(scoreLabel.getX() + (scoreLabel.getWidth() / 2));
        scoreNumbers.setY(scoreLabel.getY() + 136f);

        bestNumbers.setX(bestLabel.getX() + (bestLabel.getWidth() / 2));
        bestNumbers.setY(bestLabel.getY() + 120f);

        scoreNumbers.update(deltaTime);
        bestNumbers.update(deltaTime);

        topSlider.update(deltaTime);
        bottomSlider.update(deltaTime);

        if(!finished) {
            incrementer.update(deltaTime);
            countSound.update(deltaTime);
        }
    }

    public void draw(float deltaTime){
        scoreboardAnimation.draw(deltaTime);
        draw(scoreboardLeft);
        draw(scoreboardRight);
        draw(scoreLabel);
        draw(bestLabel);
        draw(topSlider);
        draw(bottomSlider);
        draw(gameOver);
        draw(backButton);
        draw(retryButton);

        scoreNumbers.draw(deltaTime);
        bestNumbers.draw(deltaTime);
    }

    public void skip(){
        scoreboardAnimation.skip();
        retryAnimation.skip();
        finished = true;
        currentScore = score;
        if(currentScore > hiscore){
            hiscore = currentScore;
        }
    }

    public void draw(Model model){
        model.bind();
        model.draw();
        model.unbind();
    }
}

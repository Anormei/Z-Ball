package com.ar_co.androidgames.z_ball.game.animations;

import com.ar_co.androidgames.z_ball.framework.Animation;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;

public class ScoreboardAnimation extends Animation {

    private Model scoreboard;
    private Model gameover;

    public ScoreboardAnimation(GLGame game){
        super(game);
        scoreboard = new Model(game, assets.getImage("scoreboard/scoreboard"));
        gameover = new Model(game, assets.getImage("scoreboard/gameover"));

        scoreboard.setAlpha(1);
        gameover.setAlpha(1);

        addSequence(
                createSequence(show(scoreboard, 0), show(gameover, 0)),
                createSequence(place(scoreboard, 1080f, 575f, 0), place(gameover, 64f, -112f, 0)), //418
                createSequence(move(gameover, 64f, 418f, 0, 1f), move(scoreboard, 176f, 575f, 0.5f, 0.4f), playSound("slide", 0), playSound("slide", 0.5f), playSound("stop", 0.4f), playSound("stop", 1f))
        );
    }

    @Override
    public void draw(float deltaTime){
        scoreboard.bind();
        scoreboard.draw();
        scoreboard.unbind();

        gameover.bind();
        gameover.draw();
        gameover.unbind();
    }

    public Model getScoreboard(){
        return scoreboard;
    }
}

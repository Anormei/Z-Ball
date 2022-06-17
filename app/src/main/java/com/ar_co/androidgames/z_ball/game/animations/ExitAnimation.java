package com.ar_co.androidgames.z_ball.game.animations;

import com.ar_co.androidgames.z_ball.framework.Animation;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;

public class ExitAnimation extends Animation {

    public ExitAnimation(GLGame game, Model topEdge, Model bottomEdge){
        super(game);
        addSequence(
                createSequence(playSound("slide", 0), move(topEdge, 0, -topEdge.getHeight() * 2, 0, 0.5f), move(bottomEdge, 0, 1920 + bottomEdge.getHeight() * 2, 0, 0.5f))
        );
    }
}

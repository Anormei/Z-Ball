package com.ar_co.androidgames.z_ball.game.animations;

import com.ar_co.androidgames.z_ball.framework.Animation;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;

public class RetryAnimation extends Animation {

    private Model backButton;
    private Model retryButton;

    public RetryAnimation(GLGame game, Model backButton, Model retryButton){
        super(game);
        this.backButton = backButton;
        this.retryButton = retryButton;
        addSequence(
                createSequence(show(backButton, 0), show(retryButton, 0)),
                createSequence(move(backButton, 80f, 1256f, 0, 0.4f), move(retryButton, 760f, 1256f, 0, 0.4f))
        );
    }
}

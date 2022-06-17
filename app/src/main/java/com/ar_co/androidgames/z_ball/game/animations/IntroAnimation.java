package com.ar_co.androidgames.z_ball.game.animations;

import com.ar_co.androidgames.z_ball.framework.Animation;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;

public class IntroAnimation extends Animation {

    private Model topEdge;
    private Model bottomEdge;
    private Model message;
    private Model border;

    private float x;
    private float y;
    private float width;
    private float height;

    public IntroAnimation(GLGame game, float x, float y, float width, float height){
        super(game);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setTopEdge(Model model){
        topEdge = model;
    }

    public void setBottomEdge(Model model){
        bottomEdge = model;
    }

    public void setMessage(Model model){
        message = model;
    }

    public void setBorder(Model model){
        border = model;
    }

    public void build(){
        addSequence(
                createSequence(hide(message, 0)),
                //reveal edges and border
                createSequence(move(topEdge, x, y - topEdge.getHeight(), x, y, 0, 0.75f),
                        move(bottomEdge, x, y + height, x, y + height - topEdge.getHeight(), 0, 0.75f),
                        playSound("slide", 0)),
                createSequence(show(message, 0))

        );
    }

}

package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.game.Assets;

public class Ball extends Model {

    public enum Direction {
        UpLeft,
        UpRight,
        DownLeft,
        DownRight
    }

    public static final int VELOCITY = 1;


    private int xMove;
    private int yMove;

    public Ball(GLGame game){
        super(game, Assets.getInstance(game).getImage("gameplay/ball"));
        xMove = VELOCITY;
        yMove = VELOCITY;

        setX(-width);
        setY(-height);
    }

    public void advance(){
        setX(x + xMove);
        setY(y + yMove);
    }

    public void setDirection(Direction direction){
        if(direction == Direction.DownRight || direction == Direction.UpRight){
            xMove = VELOCITY;
        }else{
            xMove = -VELOCITY;
        }

        if(direction == Direction.DownLeft || direction == Direction.DownRight){
            yMove = VELOCITY;
        }else{
            yMove = -VELOCITY;
        }
    }

    public void moveLeft(){
        xMove = -VELOCITY;
    }

    public void moveRight(){
        xMove = VELOCITY;
    }

    public void moveUp(){
        yMove = -VELOCITY;
    }

    public void moveDown(){
        yMove = VELOCITY;
    }

    public void reverseX(){
        xMove = -xMove;
    }

    public void reverseY(){
        yMove = -yMove;
    }

    public boolean isMovingRight(){
        return xMove > 0;
    }

    public boolean isMovingDown(){
        return yMove > 0;
    }

}

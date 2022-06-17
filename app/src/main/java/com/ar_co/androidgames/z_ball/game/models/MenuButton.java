package com.ar_co.androidgames.z_ball.game.models;

import com.ar_co.androidgames.z_ball.framework.Button;
import com.ar_co.androidgames.z_ball.framework.Camera2D;
import com.ar_co.androidgames.z_ball.framework.GLGame;
import com.ar_co.androidgames.z_ball.framework.Model;
import com.ar_co.androidgames.z_ball.framework.Texture;
import com.ar_co.androidgames.z_ball.framework.Vertices;
import com.ar_co.androidgames.z_ball.game.Assets;
import com.ar_co.androidgames.z_ball.interfaces.Input;

import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public class MenuButton extends Model {

    public interface OnClickListener{
        void onClick();
    }

    private Camera2D camera2d;

    private Button button;
    private OnClickListener listener;

    private boolean highlight;
    private Vertices highlightVertices;

    private Assets a;

    public MenuButton(GLGame game, Camera2D camera2d, Texture label, float x, float y, float width, float height, OnClickListener listener){
        super(game, label);
        a = Assets.getInstance(game);
        this.camera2d = camera2d;
        button = new Button(glGraphics, x, y, width, height);
        setX((width/2) - (float)(label.width/2));
        setY(y + (height / 2) - (float) (label.height / 2));

        this.listener = listener;
        setListener();

        highlightVertices = new Vertices(game.getGLGraphics(), 4, 6, true, false);
        highlightVertices.setVertices(new float[]{
                0, 0, 1, 1, 1, 0.5f,
                width, 0, 1, 1, 1, 0.5f,
                0, height, 1, 1, 1, 0.5f,
                width, height, 1, 1, 1, 0.5f

        }, 0, 24);
        highlightVertices.setIndices(new short[]{0, 1, 2, 3, 2, 1}, 0, 6);
    }

    public void readTouchEvents(List<Input.TouchEvent> touchEvents){
        for(int i = 0; i < touchEvents.size(); i++){
            Input.TouchEvent touch = touchEvents.get(i);
            if(touch.pointerId == 0) {
                button.readTouchEvent(touch.type, camera2d.touchX(touch.x), camera2d.touchY(touch.y));
            }
        }

    }

    private void setListener(){
        button.attachListener(new Button.Listener() {
            @Override
            public void onTouchDown() {
                a.playSound("buttondown");
                highlight = true;
            }

            @Override
            public void onTouchUp() {
                a.playSound("buttonup");
                listener.onClick();
                highlight = false;
            }

            @Override
            public void onDrag() {
                highlight = true;
            }

            @Override
            public void onDefault(){
                a.playSound("buttonup");
                highlight = false;
            }
        });
    }

    @Override
    public void draw(){
        super.draw();

        GL11 gl = glGraphics.getGL11();

        if(highlight){
            gl.glLoadIdentity();
            gl.glTranslatef(button.x, button.y, 0);
            gl.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            highlightVertices.bind();
            highlightVertices.draw(GL11.GL_TRIANGLES, 0, 6);
            highlightVertices.unbind();
            gl.glLoadIdentity();
        }
    }
}

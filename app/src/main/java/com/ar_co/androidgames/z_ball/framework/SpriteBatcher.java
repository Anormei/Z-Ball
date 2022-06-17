package com.ar_co.androidgames.z_ball.framework;

import javax.microedition.khronos.opengles.GL11;

public class SpriteBatcher implements VerticesReloader.Reloader {

    private GLGraphics glGraphics;
    private int id[] = new int[2];
    private Vertices vertices;
    private short[] indicesBuffer = new short[]{0, 1, 2, 2, 3, 1};

    private int size;
    private int length;
    private int index;

    public SpriteBatcher(GLGame game, int size, int length){
        this.glGraphics = game.getGLGraphics();
        GL11 gl = glGraphics.getGL11();
        this.size = size;
        this.length = length;

        vertices = new Vertices(glGraphics, 4, 6, true, true);
        vertices.setIndices(indicesBuffer, 0, 6);

        gl.glGenBuffers(2, id, 0);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, size * length * 4, null, GL11.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
        gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, size * 6 * 2, null, GL11.GL_DYNAMIC_DRAW);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < indicesBuffer.length; j++){
                indicesBuffer[j] += 4;
            }
            vertices.setIndices(indicesBuffer, 0, 6);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
            gl.glBufferSubData(GL11.GL_ELEMENT_ARRAY_BUFFER, i * 6 * 2, vertices.indices.capacity() * 2, vertices.indices);
        }
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        VerticesReloader.getInstance().addReloader(this);
    }

    public void startBatch(Texture texture){
        GL11 gl = glGraphics.getGL11();
        gl.glLoadIdentity();
        texture.bind();
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        index = 0;
    }

    public void startBatch(){
        GL11 gl = glGraphics.getGL11();
        gl.glLoadIdentity();
        gl.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        index = 0;
    }

    public void draw(Model model){
        GL11 gl = glGraphics.getGL11();
        if(!model.visible){
            return;
        }
        index += vertices.vertices.capacity() * 4;
        gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, index, model.vertices.vertices.capacity() * 4, model.vertices.vertices);
    }

    public void endBatch(){
        GL11 gl = glGraphics.getGL11();
        gl.glVertexPointer(2, GL11.GL_FLOAT, 32, 0);
        gl.glColorPointer(4, GL11.GL_FLOAT, 32, 8);
        gl.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 24);

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
        gl.glDrawElements(GL11.GL_TRIANGLES, (index / 32 / 4) * 6, GL11.GL_UNSIGNED_SHORT, 0);
    }

    @Override
    public void reloadBuffers(){
        GL11 gl = glGraphics.getGL11();
        gl.glGenBuffers(2, id, 0);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, size * length * 4, null, GL11.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        indicesBuffer[0] = 0;
        indicesBuffer[1] = 1;
        indicesBuffer[2] = 2;
        indicesBuffer[3] = 2;
        indicesBuffer[4] = 3;
        indicesBuffer[5] = 1;

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
        gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, size * 6 * 2, null, GL11.GL_DYNAMIC_DRAW);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < indicesBuffer.length; j++){
                indicesBuffer[j] += 4;
            }
            vertices.setIndices(indicesBuffer, 0, 6);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
            gl.glBufferSubData(GL11.GL_ELEMENT_ARRAY_BUFFER, i * 6 * 2, vertices.indices.capacity() * 2, vertices.indices);
        }
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}

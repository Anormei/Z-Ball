package com.ar_co.androidgames.z_ball.framework;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

public class Vertices implements VerticesReloader.Reloader{
    final GLGraphics glGraphics;
    final boolean hasColor;
    final boolean hasTexCoords;
    final int vertexSize;
    final int maxVertices;
    final int maxIndices;
    final FloatBuffer vertices;
    final ShortBuffer indices;

    private int[] id;

    public Vertices(GLGraphics glGraphics, int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords) {
        this.glGraphics = glGraphics;
        this.hasColor = hasColor;
        this.hasTexCoords = hasTexCoords;
        this.vertexSize = (2 + (hasColor ? 4 : 0) + (hasTexCoords ? 2 : 0)) * 4;
        this.maxVertices = maxVertices;
        this.maxIndices = maxIndices;

        GL11 gl = glGraphics.getGL11();

        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        id = new int[2];
        gl.glGenBuffers(2, id, 0);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, maxVertices * vertexSize, vertices, GL11.GL_DYNAMIC_DRAW);

        if (maxIndices > 0) {
            buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();

            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
            gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, maxIndices * Short.SIZE / 8, indices, GL11.GL_DYNAMIC_DRAW);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            indices = null;
        }
        VerticesReloader.getInstance().addReloader(this);
    }

    public void reloadBuffers(){
        GL11 gl = glGraphics.getGL11();

        gl.glGenBuffers(2, id, 0);

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, maxVertices * vertexSize, vertices, GL11.GL_DYNAMIC_DRAW);

        if(indices != null){
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
            gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, maxIndices * Short.SIZE / 8, indices, GL11.GL_DYNAMIC_DRAW);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    public void setVertices(float[] vertices, int offset, int length) {
        GL11 gl = glGraphics.getGL11();

        this.vertices.clear();
        this.vertices.put(vertices, offset, length);
        this.vertices.flip();

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        gl.glBufferSubData(GL11.GL_ARRAY_BUFFER, 0, vertices.length * 4, this.vertices);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
    }

    public void setIndices(short[] indices, int offset, int length) {
        GL11 gl = glGraphics.getGL11();

        this.indices.clear();
        this.indices.put(indices, offset, length);
        this.indices.flip();

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
        gl.glBufferSubData(GL11.GL_ELEMENT_ARRAY_BUFFER, 0, indices.length * (Short.SIZE / 8), this.indices);
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void bind() {
        GL11 gl = glGraphics.getGL11();

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        gl.glVertexPointer(2, GL11.GL_FLOAT, vertexSize, 0);

        if(hasColor){
            gl.glColorPointer(4, GL11.GL_FLOAT, vertexSize, 2 * 4);
        }

        if(hasTexCoords){
            gl.glTexCoordPointer(2, GL11.GL_FLOAT, vertexSize, (hasColor ? 6 : 2) * 4);
        }

    }

    public void draw(int primitiveType, int offset, int numVertices) {
        GL11 gl = glGraphics.getGL11();

        if (indices != null) {
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
            gl.glDrawElements(primitiveType, numVertices, GL11.GL_UNSIGNED_SHORT, 0);
        } else {
            gl.glDrawArrays(primitiveType, offset, numVertices);
        }
    }

    public void unbind() {
        GL11 gl = glGraphics.getGL11();
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}

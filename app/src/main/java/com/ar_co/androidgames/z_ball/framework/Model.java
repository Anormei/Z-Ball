package com.ar_co.androidgames.z_ball.framework;

import javax.microedition.khronos.opengles.GL11;

public class Model {

    public static float TO_RADIANS = (1.0f / 180.0f) * (float) Math.PI;

    protected Texture texture;
    protected Vertices vertices;

    protected float[] verticesBuffer = new float[32];
    protected short[] indices = new short[]{0, 1, 2, 2, 3, 1};

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float[] region = new float[4];
    protected float[] srcColor = new float[]{1, 1, 1};

    protected float alpha = 0;
    protected boolean visible = true;

    protected Body body;
    protected GLGraphics glGraphics;
    protected GLGame game;

    public Model(GLGame game){
        this.game = game;
        this.glGraphics = game.getGLGraphics();
        this.vertices = new Vertices(glGraphics, 4, 6, true, true);
        vertices.setIndices(indices, 0, 6);
    }

    public Model(GLGame game, Model model){
        this(game);
        copyModel(model);
    }

    public Model(GLGame game, Texture texture){
        this(game);
        this.width = texture.width;
        this.height = texture.height;
        setTexture(texture);
    }

    public Model(GLGame game, Texture texture, float width, float height){
        this(game);
        this.width = width;
        this.height = height;
        setTexture(texture);
    }

    public Model(GLGame game, float width, float height){
        this(game);
        this.width = width;
        this.height = height;
        refresh();
        this.body = new Body((int)width, (int)height);
    }

    public static Model copyOf(Model model){
        return new Model(model.game, model);
    }

    public void copyModel(Model model){
        this.game = model.game;
        this.glGraphics = model.glGraphics;

        this.x = model.getX();
        this.y = model.getY();
        this.width = model.getWidth();
        this.height = model.getHeight();
        this.alpha = model.alpha;
        this.visible = model.visible;

        if(model.hasTexture()){
            setTexture(model.texture);
            this.body = model.getBody();
        }

        for(int i = 0; i < srcColor.length; i++) {
            srcColor[i] = model.srcColor[i];
        }

        //prob
        region[0] = model.region[0];
        region[1] = model.region[1];
        region[2] = model.region[2];
        region[3] = model.region[3];
        refresh();
    }

    public void setTexture(Texture texture){
        this.texture = texture;
        setRegion(0, 0, texture.width, texture.height);
        body = texture.getBody();
    }

    public void setSrcColor(float r, float g, float b){
        srcColor[0] = r;
        srcColor[1] = g;
        srcColor[2] = b;

        refresh();
    }

    public void setRegion(float x, float y, float width, float height){
        float w = texture.po2Width;
        float h = texture.po2Height;

        region[0] = x / w;
        region[1] = y / h;
        region[2] = width / w;
        region[3] = height / h;

        refresh();
    }

    public Texture getTexture(){
        return texture;
    }

    public Vertices getVertices(){
        return vertices;
    }

    public void refresh(){

        //Up-Left corner
        verticesBuffer[0] = x + 0.375f;
        verticesBuffer[1] = y + 0.375f;

        //Up-Right Corner
        verticesBuffer[8] = x + width + 0.375f;
        verticesBuffer[9] = y + 0.375f;

        //Bottom-Left Corner
        verticesBuffer[16] = x + 0.375f;
        verticesBuffer[17] = y + height + 0.375f;

        //Bottom-Right Corner
        verticesBuffer[24] = x + width + 0.375f;
        verticesBuffer[25] = y + height + 0.375f;

        for(int i = 2; i < verticesBuffer.length; i += 8) {
            verticesBuffer[i] = srcColor[0];
            verticesBuffer[i+1] = srcColor[1];
            verticesBuffer[i+2] = srcColor[2];
            verticesBuffer[i+3] = 1.0f - alpha;
        }

        verticesBuffer[6] = region[0];
        verticesBuffer[7] = region[1];
        verticesBuffer[14] = region[2];
        verticesBuffer[15] = region[1];
        verticesBuffer[22] = region[0];
        verticesBuffer[23] = region[3];
        verticesBuffer[30] = region[2];
        verticesBuffer[31] = region[3];

        vertices.setVertices(verticesBuffer, 0, 32);
    }

    public void bind(){
        if(!visible){
            return;
        }
        GL11 gl = glGraphics.getGL11();
        if(texture == null) {
            gl.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }else{
            texture.bind();
        }
        vertices.bind();
    }

    public void draw(){
        if(!visible){
            return;
        }
        GL11 gl = glGraphics.getGL11();

        vertices.draw(GL11.GL_TRIANGLES, 0, 6);
    }

    public void unbind(){
        if(!visible){
            return;
        }
        vertices.unbind();
    }

    public void setX(float x){
        this.x = x;
        refresh();
    }

    public void setY(float y){
        this.y = y;
        refresh();
    }

    public void setWidth(float width){
        this.width = width;
        refresh();
    }

    public void setHeight(float height){
        this.height = height;
        refresh();
    }

    public void setCoord(float x, float y){
        this.x = x;
        this.y = y;
        refresh();
    }

    public void setBody(int width, int height){
        body = new Body(width, height);
    }

    public void rotate(float angle){
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        float rad = angle * TO_RADIANS;
        float cos = (float)Math.cos(rad);
        float sin = (float)Math.sin(rad);

        float x1 = -halfWidth * cos - (-halfHeight) * sin;
        float y1 = -halfWidth * sin + (-halfHeight) * cos;
        float x2 = halfWidth * cos - (-halfHeight) * sin;
        float y2 = halfWidth * sin + (-halfHeight) * cos;
        float x3 = -halfWidth * cos - halfHeight * sin;
        float y3 = -halfWidth * sin + halfHeight * cos;
        float x4 = halfWidth * cos - halfHeight * sin;
        float y4 = halfWidth * sin + halfHeight * cos;

        x1 += x + halfWidth;
        y1 += y + halfHeight;
        x2 += x + halfWidth;
        y2 += y + halfHeight;
        x3 += x + halfWidth;
        y3 += y + halfHeight;
        x4 += x + halfWidth;
        y4 += y + halfHeight;

        verticesBuffer[0] = x1;
        verticesBuffer[1] = y1;

        verticesBuffer[8] = x2;
        verticesBuffer[9] = y2;

        verticesBuffer[16] = x3;
        verticesBuffer[17] = y3;

        verticesBuffer[24] = x4;
        verticesBuffer[25] = y4;

        vertices.setVertices(verticesBuffer, 0, 32);
    }

    public void setAlpha(float alpha){
        this.alpha = alpha;
        refresh();
    }

    public float getAlpha(){
        return alpha;
    }

    public void setVisibility(boolean visibility){
        this.visible = visibility;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getX2(){
        return x + width;
    }

    public float getY2(){
        return y + height;
    }

    public Body getBody(){
        return body;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

    public boolean isTouching(Model target) {
        return Body.isTouching(this, target);
    }

    public boolean hasTexture() {
        return texture != null;
    }

}

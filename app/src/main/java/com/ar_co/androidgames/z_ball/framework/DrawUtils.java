package com.ar_co.androidgames.z_ball.framework;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public class DrawUtils {

    private static FloatBuffer colorBuffer;

    static{
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 4);
        buffer.order(ByteOrder.nativeOrder());

        colorBuffer = buffer.asFloatBuffer();
    }

    public static void drawOverlay(GL11 gl, Model src, Model trgt){

        gl.glEnable(GL11.GL_STENCIL_TEST);
        gl.glEnable(GL11.GL_ALPHA_TEST);

        gl.glClearStencil(0);
        gl.glColorMask(false, false, false, false);
        gl.glDepthMask(false);
        gl.glAlphaFunc(GL11.GL_NOTEQUAL, 0);
        gl.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
        gl.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);

        gl.glStencilMask(0xFF);
        gl.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        src.bind();
        src.draw();
        src.unbind();

        gl.glColorMask(true, true, true, true);
        gl.glStencilMask(0x00);
        gl.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
        gl.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        gl.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        trgt.bind();
        trgt.draw();
        trgt.unbind();

        gl.glDisable(GL11.GL_STENCIL_TEST);
        gl.glDisable(GL11.GL_ALPHA_TEST);

    }

    public static void drawOverlay(GL11 gl, Model src, List<?> trgt, SpriteBatcher batcher){

        gl.glEnable(GL11.GL_STENCIL_TEST);
        gl.glEnable(GL11.GL_ALPHA_TEST);

        gl.glClearStencil(0);
        gl.glColorMask(false, false, false, false);
        gl.glDepthMask(false);
        gl.glAlphaFunc(GL11.GL_NOTEQUAL, 0);
        gl.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
        gl.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);

        gl.glStencilMask(0xFF);
        gl.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        src.bind();
        src.draw();
        src.unbind();

        gl.glColorMask(true, true, true, true);
        gl.glStencilMask(0x00);
        gl.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
        gl.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        gl.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        List<Model> models = (List<Model>)trgt;

        if(models.size() > 0) {
            batcher.startBatch(models.get(0).texture);
            for (Model model : models) {
                batcher.draw(model);
            }
            batcher.endBatch();
        }

        gl.glDisable(GL11.GL_STENCIL_TEST);
        gl.glDisable(GL11.GL_ALPHA_TEST);

    }

    public static void drawStencil(GL11 gl){

        gl.glEnable(GL11.GL_STENCIL_TEST);
        gl.glEnable(GL11.GL_ALPHA_TEST);

        gl.glClearStencil(0);
        gl.glColorMask(false, false, false, false);
        gl.glDepthMask(false);

        gl.glAlphaFunc(GL11.GL_NOTEQUAL, 0);
        gl.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
        gl.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);

        gl.glStencilMask(0xFF);
        gl.glClear(GL11.GL_STENCIL_BUFFER_BIT);

    }

    public static void drawOverlay(GL11 gl){

        gl.glColorMask(true, true, true, true);

        gl.glStencilMask(0x00);
        gl.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
        gl.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        gl.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    }

    public static void finishStencil(GL11 gl){
        gl.glDisable(GL11.GL_ALPHA_TEST);
        gl.glDisable(GL11.GL_STENCIL_TEST);
    }

    public static void drawTint(GL11 gl, Model trgt, float[] tint){
        colorBuffer.clear();
        colorBuffer.put(tint, 0, 4);
        colorBuffer.flip();

        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_COMBINE);
        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB, GL11.GL_INTERPOLATE);
        gl.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, colorBuffer);

        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_SRC0_RGB, GL11.GL_TEXTURE);
        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB, GL11.GL_CONSTANT);
        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_SRC2_RGB, GL11.GL_CONSTANT);
        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND2_RGB, GL11.GL_ONE_MINUS_SRC_ALPHA);

        trgt.bind();
        trgt.draw();
        trgt.unbind();

        gl.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
    }
}

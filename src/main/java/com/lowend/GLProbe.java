package com.lowend;

import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

/**
 * Asks the driver what it REALLY supports. Every later trick (FBO scaling, PBO uploads,
 * S3TC, point sprites...) checks these flags and falls back if missing.
 */
public final class GLProbe {
    public static String version = "?";
    public static String renderer = "?";
    public static String vendor = "?";

    public static boolean gl20, gl21, gl30;
    public static boolean fbo;          // render-to-texture (world resolution scale)
    public static boolean pbo;          // async texture uploads
    public static boolean vbo;          // vertex buffers
    public static boolean vao;          // vertex array objects
    public static boolean s3tc;         // compressed textures
    public static boolean multiDraw;    // glMultiDrawArrays (GL 1.4)
    public static boolean pointSprite;  // particles as points

    private GLProbe() {}

    public static void run(Logger log) {
        GLCapabilities c = GL.getCapabilities();
        version = String.valueOf(GL11.glGetString(GL11.GL_VERSION));
        renderer = String.valueOf(GL11.glGetString(GL11.GL_RENDERER));
        vendor = String.valueOf(GL11.glGetString(GL11.GL_VENDOR));

        gl20 = c.OpenGL20;
        gl21 = c.OpenGL21;
        gl30 = c.OpenGL30;
        fbo = c.OpenGL30 || c.GL_ARB_framebuffer_object || c.GL_EXT_framebuffer_object;
        pbo = c.OpenGL21 || c.GL_ARB_pixel_buffer_object || c.GL_EXT_pixel_buffer_object;
        vbo = c.OpenGL15 || c.GL_ARB_vertex_buffer_object;
        vao = c.OpenGL30 || c.GL_ARB_vertex_array_object;
        s3tc = c.GL_EXT_texture_compression_s3tc;
        multiDraw = c.OpenGL14;
        pointSprite = c.OpenGL20 || c.GL_ARB_point_sprite;

        log.info("==== LowEnd GL probe ====");
        log.info("GL_VERSION  : {}", version);
        log.info("GL_RENDERER : {}", renderer);
        log.info("GL_VENDOR   : {}", vendor);
        log.info("GL2.0={} GL2.1={} GL3.0={}", gl20, gl21, gl30);
        log.info("FBO={} PBO={} VBO={} VAO={} S3TC={} MultiDraw={} PointSprite={}",
                fbo, pbo, vbo, vao, s3tc, multiDraw, pointSprite);
        log.info("=========================");
    }

    /** Short one-line summary for the overlay. */
    public static String flags() {
        return (fbo ? "FBO " : "") + (pbo ? "PBO " : "") + (vbo ? "VBO " : "")
                + (vao ? "VAO " : "") + (s3tc ? "S3TC " : "") + (pointSprite ? "PS" : "");
    }
}

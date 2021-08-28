package org.applecat.game;

import org.applecat.engine.GameItem;
import org.applecat.engine.graph.Mesh;
import org.applecat.engine.graph.ShaderProgram;
import org.applecat.engine.Utils;
import org.applecat.engine.Window;
import org.applecat.engine.graph.Transformation;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Renderer {

    private ShaderProgram shaderProgram;

    private final Transformation transformation;

    private static final float FOV = (float) Math.toRadians(60.0);

    // 到近平面的距离（z-near）
    private static final float Z_NEAR = 0.01f;

    // 到远平面的距离 (z-far)
    private static final float Z_FAR = 1000.0f;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("worldMatrix");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameItem[] gameItems) {
        // 渲染之前先清理
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // 绘制mesh
        for (GameItem gameItem : gameItems) {
            // 设置对于这个 gameItem 的 worldMatrix
            Matrix4f worldMatrix =
                    transformation.getWorldMatrix(
                            gameItem.getPosition(),
                            gameItem.getRotation(),
                            gameItem.getScale());
            shaderProgram.setUniform("worldMatrix", worldMatrix);
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null)
            shaderProgram.cleanup();
    }
}

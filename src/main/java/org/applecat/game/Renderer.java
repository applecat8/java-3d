package org.applecat.game;

import org.applecat.engine.GameItem;
import org.applecat.engine.Utils;
import org.applecat.engine.Window;
import org.applecat.engine.graph.Camera;
import org.applecat.engine.graph.ShaderProgram;
import org.applecat.engine.graph.Transformation;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

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
        shaderProgram.createUniform("texture_sampler");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameItem[] gameItems, Camera camera) {
        // 渲染之前先清理
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        // 更新投影矩阵
        shaderProgram.bind();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);

        // 更新 模型视图矩形
        for (GameItem gameItem : gameItems) {
            // 设置对于这个 gameItem 的 模型视图矩阵
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("worldMatrix", modelViewMatrix);
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null)
            shaderProgram.cleanup();
    }
}

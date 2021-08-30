package org.applecat.game;

import org.applecat.engine.GameItem;
import org.applecat.engine.Utils;
import org.applecat.engine.Window;
import org.applecat.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private ShaderProgram shaderProgram;

    private final Transformation transformation;

    private static final float FOV = (float) Math.toRadians(60.0);

    // 到近平面的距离（z-near）
    private static final float Z_NEAR = 0.01f;

    // 到远平面的距离 (z-far)
    private static final float Z_FAR = 1000.0f;

    private final float specularPower;


    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        // Create uniform for material
        shaderProgram.createMaterialUniform("material");
        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");

    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameItem[] gameItems, Camera camera, Vector3f ambientLight, PointLight pointLight) {
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

        // Update Light Uniforms
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currPointLight);


        shaderProgram.setUniform("texture_sampler", 0);

        // 更新 模型视图矩形
        for (GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();
            // 设置对于这个 gameItem 的 模型视图矩阵
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null)
            shaderProgram.cleanup();
    }
}

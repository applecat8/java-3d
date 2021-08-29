package org.applecat.engine.graph;

import org.applecat.engine.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 处理转换
 */
public class Transformation {
    private final Matrix4f projectionMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f viewMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Vector3f rotation = gameItem.getRotation();
        modelViewMatrix.identity().translate(gameItem.getPosition())
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(gameItem.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

    /**
     * 正如你所看到的，我们首先需要做旋转，然后再做平移。
     * 如果我们反其道而行之，我们就不是沿着摄像机的位置旋转，而是沿着坐标原点旋转。
     */
    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotatin = camera.getRotation();

        viewMatrix.identity();

        // 先进行旋转
        viewMatrix.rotate((float) Math.toRadians(rotatin.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotatin.y), new Vector3f(0, 1, 0));

        // 然后进行移动
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        return viewMatrix;
    }
}

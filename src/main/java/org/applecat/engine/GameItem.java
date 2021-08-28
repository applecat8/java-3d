package org.applecat.engine;

import org.applecat.engine.graph.Mesh;
import org.joml.Vector3f;

/**
 * 它将持有一个模型数据即一个Mesh实例。
 * 一个GameItem实例将存储它的位置，它的旋转状态和它的比例
 */
public class GameItem {
    private final Mesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public GameItem(Mesh mesh){
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z){
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }
}
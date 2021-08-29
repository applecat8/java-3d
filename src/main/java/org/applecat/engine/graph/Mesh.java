package org.applecat.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * 模型数据
 *
 * 该类将一个位置数组作为输入，创建VBO和VAO对象以将该模型加载到显卡中。
 *
 * 管理vao和vbo
 */
public class Mesh {
    private final int vaoId;

    private final List<Integer> vboIdList;

    private final int vertexCount;

    private final Texture texture;

    public Mesh(float[] positions, float[] textCoords, int[] indices, Texture texture){

        /*
          我们必须做的第一件事是将浮点数组存储到 FloatBuffer 中。
          这主要是因为我们必须与基于 C 的 OpenGL 库接口，因此我们必须将浮点数组转换为可由库管理的东西。
         */
        FloatBuffer posBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer textCoordsBuffer = null;

        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();
            this.texture = texture;
            

            // 创建vao并绑定
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // 生成vbo 位置缓冲
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER,  posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // 生成 vbo 颜色缓冲

            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // 生成 vbo 下标缓冲
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,  indicesBuffer, GL_STATIC_DRAW);


            // 解除绑定
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if ( posBuffer != null)
                MemoryUtil.memFree( posBuffer);
            if (textCoordsBuffer != null)
                MemoryUtil.memFree(textCoordsBuffer);
            if (indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);
        }
    }

    public int getVaoId(){
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        // 删除 vbo buffers
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (Integer vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // 删除vao
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    /**
     *
     */
    public void render() {
        // Activate first texture unit
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, texture.getId());

        // 画出这个模型
        glBindVertexArray(getVaoId());
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        //恢复状态
        glBindVertexArray(0);
    }
}

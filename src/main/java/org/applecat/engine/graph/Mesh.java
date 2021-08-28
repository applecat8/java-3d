package org.applecat.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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

    private final int idxVboId;

    private final int colourVboId;

    private final int posVboId;

    private final int vertexCount;

    public Mesh(float[] positions, float[] colours, int[] indices){

        /*
          我们必须做的第一件事是将浮点数组存储到 FloatBuffer 中。
          这主要是因为我们必须与基于 C 的 OpenGL 库接口，因此我们必须将浮点数组转换为可由库管理的东西。
         */
        FloatBuffer posBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer colBuffer = null;

        try {
            vertexCount = indices.length;
            

            // 创建vao并绑定
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // 生成vbo 位置缓冲
            posVboId = glGenBuffers();
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER,  posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // 生成 vbo 颜色缓冲

            colourVboId = glGenBuffers();
            colBuffer = MemoryUtil.memAllocFloat(colours.length);
            colBuffer.put(colours).flip();
            glBindBuffer(GL_ARRAY_BUFFER, colourVboId);
            glBufferData(GL_ARRAY_BUFFER,  colBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            // 生成 vbo 下标缓冲
            idxVboId = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,  indicesBuffer, GL_STATIC_DRAW);

            // 解除绑定
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if ( posBuffer != null)
                MemoryUtil.memFree( posBuffer);
            if (colBuffer != null)
                MemoryUtil.memFree(colBuffer);
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
        glDeleteBuffers(posVboId);
        glDeleteBuffers(colourVboId);
        glDeleteBuffers(idxVboId);

        // 删除vao
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    /**
     *
     */
    public void render() {
        // 画出这个模型
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        //恢复状态
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}

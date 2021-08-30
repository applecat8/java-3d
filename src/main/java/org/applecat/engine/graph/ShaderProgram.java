package org.applecat.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * 着色器程序，用来加载外部的着色器代码
 */
public class ShaderProgram {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0)
            throw new Exception("Could not create Shader");
        uniforms = new HashMap<>();
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0)
            throw new Exception("Error create shader. Type: " + shaderType);
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);
        return shaderId;
    }

    /**
     * 从着色器中获取一个新的 uniform
     * @param uniformName 名称
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0)
            throw new Exception("Could not find uniform:" + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * 设置指定 uniform 的数据
     * @param uniformName 名字
     * @param value 数据
     */
    public void setUniform(String uniformName, Matrix4f value){
        try (MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, int value){
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String colour, Vector3f value) {
        glUniform3f(uniforms.get(colour), value.x, value.y, value.z);
    }

    public void link() throws Exception {
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0)
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));

        if (vertexShaderId != 0)
            glDetachShader(programId, vertexShaderId);

        if (fragmentShaderId != 0)
            glDetachShader(programId, fragmentShaderId);

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind(){
        glUseProgram(programId);
    }

    public void unbind(){
        glUseProgram(0);
    }

    public void cleanup(){
        unbind();
        if (programId != 0)
            glDeleteProgram(programId);
    }

}
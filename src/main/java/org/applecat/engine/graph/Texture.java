package org.applecat.engine.graph;

import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * 现在我们将创建一个新的 Texture 类，它将执行加载纹理的所有必要步骤。
 * 我们的纹理图像将位于资源文件夹中，可以作为 CLASSPATH 资源访问，并作为输入流传递给 PNGDecoder 类。
 */
public class Texture {

    private final int id;

    public Texture(int id){
        this.id = id;
    }

    public Texture(String fileName) throws Exception {
        this(loadTexture(fileName));
    }

    private static int loadTexture(String fileName) throws Exception {
        PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(fileName));
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

        glGenerateMipmap(GL_TEXTURE_2D);

        return textureId;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void cleanup (){
        glDeleteTextures(id);
    }

    public int getId(){
        return id;
    }
}

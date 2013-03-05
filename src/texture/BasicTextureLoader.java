package texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.OpenGLException;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

/**
 * User: FreezerburnVinny
 * Date: 1/1/12
 * Time: 5:59 PM
 */
public class BasicTextureLoader implements Runnable, TextureLoader {
    public SimpleTexture texture;
    public BufferedImage image;
    public int width, height;
    protected String key;
    protected boolean shouldRemoveBackground;

    public BasicTextureLoader(BufferedImage image, String key) {
        this(image, key, false);
    }

    public BasicTextureLoader(BufferedImage image, String key, boolean shouldRemoveBackground) {
        this.image = image;
        this.key = key;
        this.shouldRemoveBackground = shouldRemoveBackground;
        this.texture = null;
        this.width = -1;
        this.height = -1;
    }

    protected ByteBuffer imageToTexture(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS
        return buffer;
    }

    protected int genTextureFromBufferedImage(BufferedImage image) {
        int tex = -1;
        try {
            width = image.getWidth();
            height = image.getHeight();
            ByteBuffer imageBuffer = imageToTexture(image);
            tex = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, tex);

            // These parameters make the image actually look nice and look like the image with no artifacts.
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height,
                    0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            glDeleteTextures(tex);
            tex = -1;
        } catch (OpenGLException e) {
            e.printStackTrace();
            glDeleteTextures(tex);
            tex = -1;
        }
        return tex;
    }

    @Override
    public void run() {
        if(texture != null) {
            return;
        }
        texture = new SimpleTexture(GL_TEXTURE_2D, genTextureFromBufferedImage(image), key);
        texture.setWidth(width);
        texture.setHeight(height);
//        System.out.println(width + ", " + height + ", " + texture.getName());
    }

    @Override
    public Texture get() {
        run();
        return texture;
    }
}

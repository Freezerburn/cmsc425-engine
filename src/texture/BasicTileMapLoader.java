package texture;

import org.lwjgl.opengl.GL11;
import stuff.Utils;

/**
 * Author: FreezerburnVinny
 * Date: 1/6/12
 * Time: $(TIME}
 */
public class BasicTileMapLoader implements Runnable {
    public SimpleTexture[] textures;
    public int tilesWide, tilesHigh;
    protected BasicTextureLoader loader;
    protected int tileWidth, tileHeight;

    public BasicTileMapLoader(String file, String managerHandle, int tileWidth, int tileHeight, boolean shouldRemoveBackground) {
        loader = new BasicTextureLoader(Utils.loadImage(file), managerHandle, shouldRemoveBackground);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public SimpleTexture get(int x, int y) {
        return textures[y * tilesWide + x];
    }

    @Override
    public void run() {
        loader.run();
        SimpleTexture masterTexture = loader.texture;
        tilesWide = (int) (masterTexture.getWidth() / tileWidth);
        tilesHigh = (int) (masterTexture.getHeight() / tileHeight);
        double texWidth = tileWidth / masterTexture.getWidth();
        double texHeight = tileHeight / masterTexture.getHeight();
        textures = new SimpleTexture[tilesHigh * tilesWide];
        for (int y = 0; y < tilesHigh; y++) {
            for (int x = 0; x < tilesWide; x++) {
                textures[y * tilesWide + x] = new SimpleTexture(GL11.GL_TEXTURE_2D, masterTexture.getName(),
                        masterTexture.getManagerHandle(),
                        tileWidth, tileHeight,
                        texWidth * x, texWidth * (x + 1),
                        texHeight * y, texHeight * (y + 1));
            }
        }
    }
}

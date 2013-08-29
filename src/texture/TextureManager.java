package texture;

import stuff.Two;
import stuff.Utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGetTexImage;

/**
 * User: FreezerburnVinny
 * Date: 1/1/12
 * Time: 5:57 PM
 */
public class TextureManager {
    private static final Map<String, Texture> mTextureMap = new HashMap<>();
    private static final Map<String, Texture[]> mTextureTileMap = new HashMap<>();

    private static final Map<String, Texture> retained = new HashMap<>();
    private static final ReferenceQueue<Texture> reaped = new ReferenceQueue<>();
    private static final Map<String, Reference<Texture>> textures = new HashMap<>();
    private static final Map<Reference<Texture>, Two<Integer, String>> reapedTextureIds = new HashMap<>();
    private static final Object texturesLock = new Object();

    public static final Texture DUMMY = fromBufferedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), "DUMMY");

    public static void doMaintenance() {
        Reference<? extends Texture> ref;
        while((ref = reaped.poll()) != null) {
            Two<Integer, String> reapedData = reapedTextureIds.get(ref);
            int textureId = reapedData.one;
            String textureName = reapedData.two;
            glDeleteTextures(textureId);
            reapedTextureIds.remove(ref);
            textures.remove(textureName);
        }
    }

    public static void removeTexture(String name) {
        if(mTextureMap.containsKey(name)) {
            mTextureMap.remove(name);
        }
    }

    public static boolean doesTextureExist(String name) {
        return mTextureMap.containsKey(name);
    }

    public static void renameTexture(String oldName, String newName) {
        Texture toRename = mTextureMap.get(oldName);
        mTextureMap.remove(oldName);
        mTextureMap.put(newName, toRename);
    }

    public static Texture getTexture(String name) {
        return mTextureMap.get(name);
    }

    public static Texture[] getTileMap(String name) {
        return mTextureTileMap.get(name);
    }

    public static Texture getTileFromMap(String name, int x, int y) {
        return mTextureMap.get(name + "_" + x + "," + y);
    }

    public static Texture fromBufferedImage(BufferedImage image, String name) {
        if(textures.containsKey(name)) {
            Reference<Texture> ref = textures.get(name);
            Texture ret = ref.get();
            if(ret == null) {
                removeTexture(name);
                return fromBufferedImage(image, name);
            }
            ret.alloc();
            return ret;
        }

        BasicTextureLoader loader = new BasicTextureLoader(image, name);
        loader.run();
        mTextureMap.put(name, loader.texture);
        return loader.texture;
    }

    public static Texture[] fromString(String text, int pointSize) {
        return fromString(text, text + "_" + pointSize, pointSize);
    }

    public static Texture[] fromString(String text, String name, int pointSize) {
        Texture[] ret = new Texture[text.length()];
        char[] chars = text.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            ret[i] = fromChar(chars[i], String.valueOf(chars[i]) + "_" + pointSize, pointSize);
        }
        return ret;
    }

    public static Texture fromChar(char theChar, String name, int pointSize) {
        if(mTextureMap.containsKey(name)) {
            Texture ret = mTextureMap.get(name);
            ret.alloc();
            return ret;
        }

        char[] charData = new char[]{theChar};
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, pointSize);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage temp = gc.createCompatibleImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = temp.createGraphics();
        Rectangle2D r = font.getStringBounds(charData, 0, 1, g.getFontRenderContext());
        g.dispose();

        temp = gc.createCompatibleImage(Math.max((int)Math.ceil(r.getWidth()), 1),
                (int)Math.round(r.getHeight() * 1.25),
                BufferedImage.TYPE_INT_ARGB);
        g = temp.createGraphics();
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawChars(charData, 0, 1, 0, (int)Math.floor(r.getHeight()));
        g.dispose();
        BasicTextureLoader loader = new BasicTextureLoader(temp, name);
        loader.run();
        mTextureMap.put(name, loader.texture);
        return loader.texture;
    }

    public static Texture loadTexture(String file) {
        return loadTexture(file, false);
    }

    public static Texture loadTexture(String file, boolean removeBackground) {
        String name = file.substring(0, file.lastIndexOf("."));
        return loadTexture(name, file, removeBackground, false);
    }

    public static Texture loadTexture(String file, String name) {
        return loadTexture(file, name, false, false);
    }

    public static Texture loadTexture(String file, String name, boolean removeBackground, boolean retain) {
        if(textures.containsKey(name)) {
            Texture ret = textures.get(name).get();
            if(ret == null) {
                removeTexture(name);
                return loadTexture(file, name, removeBackground, retain);
            }
            ret.alloc();
            return ret;
        }

        TextureLoader loader = new BasicTextureLoader(Utils.loadImage(file), name, removeBackground);
        Texture fromLoader = loader.get();
        if(retain) {
            retained.put(name, fromLoader);
        }
        else {
            textures.put(name, new PhantomReference<Texture>(fromLoader, reaped));
        }
        return fromLoader;
    }

    public static Texture[] loadTileMap(String file, int width, int height) {
        return loadTileMap(file, width, height, false);
    }

    public static Texture[] loadTileMap(String file, int width, int height, boolean removeBackground) {
        String name = file.substring(0, file.lastIndexOf("."));
        return loadTileMap(name, file, width, height, removeBackground);
    }

    public static Texture[] loadTileMap(String name, String file, int width, int height) {
        return loadTileMap(name, file, width, height, false);
    }

    // Actual impl
    public static Texture[] loadTileMap(String name, String file, int width, int height, boolean removeBackground) {
        BasicTileMapLoader loader = new BasicTileMapLoader(file, name, width, height, removeBackground);
        loader.run();
        for (int y = 0; y < loader.tilesHigh; y++) {
            for (int x = 0; x < loader.tilesWide; x++) {
                String toPut = name + "_" + x + "," + y;
                mTextureMap.put(toPut, loader.get(x, y));
            }
        }
        mTextureTileMap.put(name, loader.textures);
        return loader.textures;
    }

    public static Texture[] loadTileMap(String name, TileMapLoader customLoader) {
        preloadTileMap(name, customLoader);
        return customLoader.getAll();
    }

    public static Texture loadTexture(String name, TextureLoader customLoader) {
        customLoader.run();
        mTextureMap.put(name, customLoader.get());
        return customLoader.get();
    }

    public static void preloadTexture(String file) {
        String name = file.substring(0, file.lastIndexOf("."));
        preloadTexture(name, file, false);
    }

    public static void preloadTexture(String file, boolean removeBackground) {
        String name = file.substring(0, file.lastIndexOf("."));
        preloadTexture(name, file, removeBackground);
    }

    public static void preloadTexture(String name, String file) {
        preloadTexture(name, file, false);
    }

    public static void preloadTexture(String name, String file, boolean removeBackground) {
        BasicTextureLoader loader = new BasicTextureLoader(Utils.loadImage(file), name, removeBackground);
        loader.run();
        mTextureMap.put(name, loader.texture);
    }

    public static void preloadTexture(String name, TextureLoader customLoader) {
        customLoader.run();
        mTextureMap.put(name, customLoader.get());
    }

    public static void preloadTileMap(String name, String file, int width, int height) {
        preloadTileMap(name, file, width, height, false);
    }

    public static void preloadTileMap(String name, String file, int width, int height, boolean removeBackground) {
        BasicTileMapLoader loader = new BasicTileMapLoader(file, name, width, height, removeBackground);
        loader.run();
        for (int y = 0; y < loader.tilesHigh; y++) {
            for (int x = 0; x < loader.tilesWide; x++) {
                String toPut = name + "_" + x + "," + y;
                if (name.equals("bullet")) {
                    System.out.println(toPut);
                }
                mTextureMap.put(toPut, loader.get(x, y));
            }
        }
        mTextureTileMap.put(name, loader.textures);
    }

    public static void preloadTileMap(String name, TileMapLoader customLoader) {
        customLoader.run();
        for (int y = 0; y < customLoader.getColumns(); y++) {
            for (int x = 0; x < customLoader.getRows(); x++) {
                String toPut = name + "_" + x + "," + y;
                mTextureMap.put(toPut, customLoader.get(x, y));
            }
        }
        mTextureTileMap.put(name, customLoader.getAll());
    }

    public static Texture[] getArrayFromTileMap(String name, int startx, int starty,
                                                int endx, int endy) {
        ArrayList<Texture> ret = new ArrayList<>();
        while (starty <= endy) {
            while (startx <= endx) {
                ret.add(mTextureMap.get(name + "_" + startx + "," + starty));
                startx++;
            }
            starty++;
        }
        Texture[] ret2 = new Texture[ret.size()];
        System.arraycopy(ret.toArray(), 0, ret2, 0, ret.size());
        return ret2;
    }
}

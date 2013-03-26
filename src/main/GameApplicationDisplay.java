package main;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import stuff.NativeLoader;
import stuff.Preferences;
import texture.TextureManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/26/13
 * Time: 11:49 AM
 */
public class GameApplicationDisplay implements GameApplicationRunnable {
    protected static final String WINDOW_NAME = "CMSC425 - GameDev";
    protected static final String CMDLINE_WINDOW_NAME = "cmsc425.window.name";

    protected static final int WINDOW_WIDTH = 640;
    protected static final int WINDOW_HEIGHT = 480;
    protected static final String CMDLINE_WINDOW_WIDTH = "cmsc425.window.width";
    protected static final String CMDLINE_WINDOW_HEIGHT = "cmsc425.window.height";

    protected static final int FPS = 60;
    protected static final String CMDLINE_FPS = "cmsc425.window.fps";

    public static int windowWidth;
    public static int windowHeight;
    public static int fps;

    private boolean running = true;

//    static {
//        NativeLoader.load();
//    }

    public GameApplicationDisplay() {
        try {
            initBase();
            initGL();
            initData();
            mainLoop();
            cleanup();
            Display.destroy();
        }
        catch (Exception e) {
            e.printStackTrace();
//            NativeLoader.destroy();
            System.exit(1);
        }
//        NativeLoader.destroy();
        System.exit(0);
    }

    private void mainLoop() {
        long curTime = System.nanoTime();
        long tickCount = 0;
        while(running) {
            if(tickCount % 30 == 0) {
                TextureManager.doMaintenance();
            }
            if(Display.getWidth() != windowWidth ||
                    Display.getHeight() != windowHeight) {
                onResize(Display.getWidth(), Display.getHeight());
                windowWidth = Display.getWidth();
                windowHeight = Display.getHeight();
            }
            long nextTime = System.nanoTime();
            float delta = (nextTime - curTime) / 1000000000.0f;
            run(delta);
            Display.update(true);
            Display.sync(fps);
            tickCount++;
        }
    }

    protected void stop() {
        running = false;
    }

    public void initBase() throws LWJGLException {
        if(System.getProperties().containsKey(CMDLINE_FPS)) {
            fps = Integer.parseInt(System.getProperty(CMDLINE_FPS));
        }
        else {
            fps = FPS;
        }

        if(System.getProperties().containsKey(Preferences.CMDLINE_FILENAME)) {
            Preferences.restoreAll(System.getProperty(Preferences.CMDLINE_FILENAME));
        }
        else {
            Preferences.restoreAll();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (System.getProperties().containsKey(Preferences.CMDLINE_FILENAME)) {
                    Preferences.saveAll(System.getProperty(Preferences.CMDLINE_FILENAME));
                } else {
                    Preferences.saveAll();
                }
            }
        }));

        if(System.getProperties().containsKey(CMDLINE_WINDOW_NAME)) {
            Display.setTitle(System.getProperty(CMDLINE_WINDOW_NAME));
        }
        else {
            Display.setTitle(WINDOW_NAME);
        }

        int width = WINDOW_WIDTH;
        int height = WINDOW_HEIGHT;
        if(System.getProperties().containsKey(CMDLINE_WINDOW_WIDTH)) {
            width = Integer.parseInt(System.getProperty(CMDLINE_WINDOW_WIDTH));
        }
        if(System.getProperties().containsKey(CMDLINE_WINDOW_HEIGHT)) {
            height = Integer.parseInt(System.getProperty(CMDLINE_WINDOW_HEIGHT));
        }
        Display.setDisplayMode(new DisplayMode(width, height));

        PixelFormat pixelFormat = new PixelFormat();
        ContextAttribs contextAttribs = new ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true);
        Display.create(pixelFormat, contextAttribs);
        Display.processMessages();
        windowWidth = width;
        windowHeight = height;
        System.out.println("Running LWJGL with native Display");
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
    }

    protected void onResize(int width, int height) {
    }

    @Override
    public void initGL() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void run(float dt) {
        System.out.println("Running GameApplicationDisplay");
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void setResizable(boolean resizable) {
        Display.setResizable(resizable);
    }

    @Override
    public void clear(int flags) {
        while(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        }
        glClear(flags);
    }
}

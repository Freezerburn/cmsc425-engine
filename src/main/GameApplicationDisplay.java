package main;

import com.artemis.World;
import function.IntBinaryConsumer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.zeromq.ZMQ;
import structure.control.KeyboardManager;
import structure.control.MouseManager;
import structure.event.EventQueue;
import structure.mem.CleanupManager;
import structure.opengl.ShaderProgram;
import structure.tuple.Pair;
import stuff.NativeLoader;
import stuff.Preferences;
import texture.TextureManager;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntBinaryOperator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/26/13
 * Time: 11:49 AM
 */
public abstract class GameApplicationDisplay implements GameApplicationRunnable {
    public static final String RESIZE_STREAM = "windowResize";

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
    public static float tickRate;
    public static KeyboardManager keyboardManager;
    public static MouseManager mouseManager;
    public static final EventQueue Q = new EventQueue();
    public static final World artemisWorld = new World();
    public static final ZMQ.Context zmqContext = ZMQ.context(0);

    protected static final Lock shaderLock = new ReentrantLock();
    protected static ShaderProgram currentShader;

    private boolean running = true;

    public GameApplicationDisplay() {
        Q.createStream(GameApplicationDisplay.RESIZE_STREAM);
        tickRate = 1.0f / 60.0f;
        keyboardManager = new KeyboardManager();
        mouseManager = new MouseManager();
        boolean errorHappened = false;
        try {
            /*
            Private to this class, of no concern to anything implementing this class.
             */
            initBase();

            /*
            Order of operations that the implementing class needs to be concerned about.
            Namely:
                GL -> Data -> Resize -> Run... -> Stop -> Cleanup -> Program Exit
            THIS IS GUARANTEED TO HAPPEN IN THAT ORDER EVERY TIME

            UNLESS: An Exception is thrown, in which case it will skip any remaining
            steps and go directly to: Cleanup -> Program Exit
            So it can look like:
                GL -> Data -> Resize -> Run... -> Stop -> Cleanup -> Program Exit(0)
                \                                 /
                 ------------ Exception ----------
                                  |
                                  ---> Cleanup -> Program Exit(1)
             */
            initGL();
            initData();
            Q.pushImmediate(new Pair<>(windowWidth, windowHeight), RESIZE_STREAM);

            mainLoop();
        }
        catch (Exception e) {
            e.printStackTrace();
            errorHappened = true;
        }
        finally {
            if(currentShader != null) {
                currentShader.destroy();
            }

            currentShader = null;
            keyboardManager = null;
            mouseManager = null;
            System.gc();
            CleanupManager.update();

            cleanup();
            Display.destroy();
        }
        System.exit(errorHappened ? 1 : 0);
    }

    private void mainLoop() {
        long curTime = System.nanoTime();
        long tickCount = 0;
        float delta = 0.0f;
        while(running) {
            if(tickCount % 30 == 0) {
                TextureManager.doMaintenance();
                CleanupManager.update();
            }

            if(Display.getWidth() != windowWidth ||
                    Display.getHeight() != windowHeight) {
                windowWidth = Display.getWidth();
                windowHeight = Display.getHeight();
                Q.pushImmediate(new Pair<>(windowWidth, windowHeight), RESIZE_STREAM);
            }

            Q.update();

            /*
            The delta is always measures in seconds. This is to make calculations easy, so that
            you can just do something such as:
                x += velocity.x * dt
             */
            long nextTime = System.nanoTime();
            delta += (nextTime - curTime) / 1000000000.0f;
            curTime = nextTime;

            /*
            If for some reason a loop takes 2 * tickRate or greater time to get back to
            this point, this loop ensures that all ticks will be processed before drawing
            the next frame.

            This technique is used so that a consistent tick happens every frame, to prevent
            odd errors from happening if frames take too long to process on one machine
            versus another, or for any reason at all. It's similar to why any good physics
            library will always have you define a constant tick rate to use, rather than
            just using the main loop delta.
             */
            while(delta > GameApplicationDisplay.tickRate) {
                run(tickRate);
                delta -= tickRate;
            }

            /*
            Actually draw the frame to the screen, and use the high-accuracy timing function LWJGL
            exposes to keep the framerate consistent.
             */
            Display.update(true);
            Display.sync(fps);
            tickCount++;

            if(Display.isCloseRequested()) {
                stop();
            }
        }
    }

    protected void stop() {
        running = false;
    }

    private void initBase() throws LWJGLException {
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (System.getProperties().containsKey(Preferences.CMDLINE_FILENAME)) {
                Preferences.saveAll(System.getProperty(Preferences.CMDLINE_FILENAME));
            } else {
                Preferences.saveAll();
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

    @Override
    public void setResizable(boolean resizable) {
        Display.setResizable(resizable);
    }

    @Override
    public void setTickRate(float tickRate) {
        GameApplicationDisplay.tickRate = tickRate;
    }

    public static void changeShader(ShaderProgram prog) {
        if(prog == null) {
            throw new IllegalArgumentException("GameApplicationDisplay.currentShader cannot be null");
        }
        shaderLock.lock();
        currentShader = prog;
        prog.use();
        shaderLock.unlock();
    }

    public static ShaderProgram useShader() {
        shaderLock.lock();
        return currentShader;
    }

    public static void stopUsingShader() {
        shaderLock.unlock();
    }
}

package game;

import main.GameApplicationDisplay;
import org.lwjgl.input.Keyboard;
import org.zeromq.ZMQ;
import structure.control.KeyboardEvent;
import structure.control.KeyboardManager;
import structure.event.SubscriptionToken;
import structure.geometries.Cube;
import structure.mem.CleanupManager;
import structure.opengl.OrthoCamera;
import structure.opengl.ShaderProgram;
import structure.tuple.Pair;
import stuff.UIDGenerator;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburnv
 * Date: 6/20/13
 * Time: 6:12 PM
 */
public class OrthoTest1 extends GameApplicationDisplay {
    public static void main(String[] args) {
        new OrthoTest1();
    }

    public static OrthoTest1 instance;

    private SubscriptionToken stopToken, resizeToken;
    private OrthoCamera camera;

    private Cube test;

    @SuppressWarnings("unchecked")
    private boolean onResize(Object o) {
        Pair<Integer, Integer> newSize = (Pair<Integer, Integer>)o;
        System.out.println("Resized to: " + newSize.unus + ", " + newSize.duo);

        glViewport(0, 0, newSize.unus, newSize.duo);
        camera = new OrthoCamera((float)newSize.unus, (float)newSize.duo, "");

        return true;
    }

    @Override
    public void initGL() {
        glViewport(0, 0, windowWidth, windowHeight);
        glClearColor(0.7f, 0.7f, 0.7f, 1.0f);
    }

    @Override
    public void initData() {
        instance = this;

        ShaderProgram initialShader = new ShaderProgram();
        initialShader.addFragmentShader("fragcolor.glsl");
        initialShader.addVertexShader("vertexcolor.glsl");
        initialShader.link();
        GameApplicationDisplay.changeShader(initialShader);

        test = new Cube(initialShader, "model", "vertNormal");

        stopToken = keyboardManager.listenFor((e) -> stop(), true, Keyboard.KEY_Q, Keyboard.KEY_ESCAPE);
        resizeToken = Q.subscribe(
                UIDGenerator.getUid(),
                this::onResize,
                GameApplicationDisplay.RESIZE_STREAM
        );
    }

    @Override
    public void run(float dt) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        System.gc();

        test.draw();
    }

    @Override
    public void cleanup() {
    }
}

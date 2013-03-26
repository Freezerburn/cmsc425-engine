package game;

import main.GameApplicationDisplay;
import main.GameApplicationFrame;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import structure.opengl.Camera;
import structure.opengl.Geometry;
import structure.opengl.Matrix4;
import structure.opengl.ShaderProgram;
import stuff.Utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/19/13
 * Time: 8:41 PM
 */
public class Project1 extends GameApplicationDisplay {
    public static void main(String[] args) {
        new Project1();
    }

    ShaderProgram colorProg;
    Geometry geometry, cubeGeometry;
    Matrix4 projection;
    Matrix4 model;
    Matrix4 modelToCameraMatrix, cameraToClipMatrix;
    Camera camera;
    float frustumScale;

    boolean mouseRotationEnabled = false;

    @Override
    public void initGL() {
        glViewport(0, 0, windowWidth, windowHeight);
        glClearColor(0.7f, 0.7f, 0.7f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
    }

    @Override
    protected void onResize(int width, int height) {
        cameraToClipMatrix.m00 = frustumScale * (height / (float)width);
        cameraToClipMatrix.m11 = frustumScale;

        colorProg.use();
        colorProg.setUniform("cameraToClipMatrix", cameraToClipMatrix);
        colorProg.stopUsing();

        glViewport(0, 0, width, height);
    }

    @Override
    public void initData() {
        // TODO: Fix QuadTree stuff
//        QuadTree qt = new QuadTree(640, 480);
//        qt.add(new Test(100, 100, 100, 100));
//        qt.add(new Test(200, 200, 100, 100));
//        qt.add(new Test(300, 300, 50, 50));
//        qt.add(new Test(400, 400, 50, 50));
//        qt.add(new Test(500, 500, 50, 50));
//        System.out.println(qt.getIntersecting(new Test(150, 150, 100, 100)));

        setResizable(true);

        final float[] cubeVertices = new float[] {
                //  X     Y     Z       U     V
                // bottom
                -1.0f,-1.0f,-1.0f,   0.0f, 0.0f,
                1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
                -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,
                1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
                1.0f,-1.0f, 1.0f,   1.0f, 1.0f,
                -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,

                // top
                -1.0f, 1.0f,-1.0f,   0.0f, 0.0f,
                -1.0f, 1.0f, 1.0f,   0.0f, 1.0f,
                1.0f, 1.0f,-1.0f,   1.0f, 0.0f,
                1.0f, 1.0f,-1.0f,   1.0f, 0.0f,
                -1.0f, 1.0f, 1.0f,   0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,   1.0f, 1.0f,

                // front
                -1.0f,-1.0f, 1.0f,   1.0f, 0.0f,
                1.0f,-1.0f, 1.0f,   0.0f, 0.0f,
                -1.0f, 1.0f, 1.0f,   1.0f, 1.0f,
                1.0f,-1.0f, 1.0f,   0.0f, 0.0f,
                1.0f, 1.0f, 1.0f,   0.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,   1.0f, 1.0f,

                // back
                -1.0f,-1.0f,-1.0f,   0.0f, 0.0f,
                -1.0f, 1.0f,-1.0f,   0.0f, 1.0f,
                1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
                1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
                -1.0f, 1.0f,-1.0f,   0.0f, 1.0f,
                1.0f, 1.0f,-1.0f,   1.0f, 1.0f,

                // left
                -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,
                -1.0f, 1.0f,-1.0f,   1.0f, 0.0f,
                -1.0f,-1.0f,-1.0f,   0.0f, 0.0f,
                -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,   1.0f, 1.0f,
                -1.0f, 1.0f,-1.0f,   1.0f, 0.0f,

                // right
                1.0f,-1.0f, 1.0f,   1.0f, 1.0f,
                1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
                1.0f, 1.0f,-1.0f,   0.0f, 0.0f,
                1.0f,-1.0f, 1.0f,   1.0f, 1.0f,
                1.0f, 1.0f,-1.0f,   0.0f, 0.0f,
                1.0f, 1.0f, 1.0f,   0.0f, 1.0f
        };
        cubeGeometry = new Geometry();
        cubeGeometry.addBufferColumns(cubeVertices, GL_STATIC_DRAW, Geometry.VERTEX_3F, Geometry.VERTEX_2F);

        projection = new Matrix4();
        model = new Matrix4();

        float near = 1.0f, far = 600.0f;
        frustumScale = (float)(1.0 / Math.tan(Math.toRadians(20) / 2.0));
        cameraToClipMatrix = new Matrix4();
        cameraToClipMatrix.m00 = frustumScale;
        cameraToClipMatrix.m11 = frustumScale;
        cameraToClipMatrix.m22 = (far + near) / (near - far);
        cameraToClipMatrix.m23 = -1.0f;
        cameraToClipMatrix.m32 = (2 * near * far) / (near - far);

        camera = new Camera().lookAt(
//                20, 8, 5,
                0, 0, 5,
                0, 0, 0,
                0, 1, 0)
            .setMouseSensitivity(0.8f);
        colorProg = new ShaderProgram();
        colorProg.addFragmentShader("fragcolor.glsl");
        colorProg.addVertexShader("vertexcolor.glsl");
        colorProg.link();
        colorProg.use();
        colorProg.setUniform("cameraToClipMatrix", cameraToClipMatrix);
        colorProg.setUniform("loopDuration", 5.0f);
        colorProg.setUniform("fragLoopDuration", 5.0f);
        colorProg.setUniform("time", 0.0f);
        colorProg.setUniform("camera", camera.getMatrix());
        colorProg.setUniform("projection", Utils.perspective(projection, 50.0f, (float) windowWidth / (float) windowHeight, 0.1f, 100.0f));
        colorProg.setUniform("model", model);
        colorProg.stopUsing();
    }

    @Override
    public void run(float dt) {
        while(Mouse.next()) {
            if(mouseRotationEnabled) {
                int dx = Mouse.getDX();
                int dy = Mouse.getDY();
                camera.rotateWithMouse(dx, dy);
            }
        }
        while(Keyboard.next()) {
            if(Keyboard.getEventKey() == Keyboard.KEY_Q ||
                    Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                stop();
            }
            else if(Keyboard.getEventKey() == Keyboard.KEY_1 &&
                    Keyboard.getEventKeyState()) {
                if(mouseRotationEnabled) {
                    mouseRotationEnabled = false;
                    System.out.println("Disabled mouse rotation");
                }
                else {
                    mouseRotationEnabled = true;
                    System.out.println("Enabled mouse rotation");
                }
            }
        }
        if(Display.isCloseRequested()) {
            stop();
        }

//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        colorProg.use();
        colorProg.setUniform("time", dt);
        model.setIdentity();
//        camera.move(0.2f * dt, 0.0f * dt, 0.0f * dt);
        colorProg.setUniform("camera", camera.getMatrix());
//        colorProg.setUniform("model", model.rotate(1.0f * dt, new Vector3f(0, 1, 0)));
        cubeGeometry.draw();
        colorProg.stopUsing();
    }

    @Override
    public void cleanup() {
        colorProg.destroy();
        cubeGeometry.destroy();
    }
}

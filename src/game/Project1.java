package game;

import main.GameApplicationDisplay;
import main.GameApplicationFrame;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import structure.opengl.*;
import stuff.TempVars;
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
public class Project1 extends GameApplicationFrame {
    public static void main(String[] args) {
        new Project1();
    }

    ShaderProgram colorProg;
    Geometry geometry, cubeGeometry;
    Matrix4 projection;
    Matrix4 model;
    Matrix4 modelToCameraMatrix, cameraToClipMatrix;
    Camera camera;
    final static float CAM_SPEED = 5.75f;
    Vector3 camVel;
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
        model = new Matrix4().scaleLocal(1, 10, 1).translateLocal(0, 1, 0);

        float near = 1.0f, far = 600.0f;
        frustumScale = (float)(1.0 / Math.tan(Math.toRadians(70) / 2.0));
        cameraToClipMatrix = new Matrix4();
        cameraToClipMatrix.m00 = frustumScale;
        cameraToClipMatrix.m11 = frustumScale;
        cameraToClipMatrix.m22 = (far + near) / (near - far);
        cameraToClipMatrix.m23 = -1.0f;
        cameraToClipMatrix.m32 = (2 * near * far) / (near - far);

        camVel = new Vector3();
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
        colorProg.setUniform("projection", Utils.perspective(projection, 90.0f, (float) windowWidth / (float) windowHeight, 0.1f, 100.0f));
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
//                Mouse.setCursorPosition(windowHeight / 2, windowHeight / 2);
//                Mouse.next();
//                Mouse.getDX(); Mouse.getDY();
            }
        }
        while(Keyboard.next()) {
            if(Keyboard.getEventKey() == Keyboard.KEY_Q ||
                    Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                stop();
            }
            else if(Keyboard.getEventKeyState()) {
                if(Keyboard.getEventKey() == Keyboard.KEY_1) {
                    if(mouseRotationEnabled) {
                        mouseRotationEnabled = false;
                        Mouse.setGrabbed(false);
                        System.out.println("Disabled mouse rotation");
                    }
                    else {
                        mouseRotationEnabled = true;
                        Mouse.setGrabbed(true);
                        System.out.println("Enabled mouse rotation");
                    }
                }
            }

            if(Keyboard.getEventKey() == Keyboard.KEY_W) {
//                    camera.move(0, 0, -0.5f);
                camVel.z -= Keyboard.getEventKeyState() ? CAM_SPEED : -CAM_SPEED;
            }
            else if(Keyboard.getEventKey() == Keyboard.KEY_S) {
//                    camera.move(0, 0, 0.5f);
                camVel.z += Keyboard.getEventKeyState() ? CAM_SPEED : -CAM_SPEED;
            }
            else if(Keyboard.getEventKey() == Keyboard.KEY_A) {
//                camera.move(-0.5f, 0, 0);
                camVel.x -= Keyboard.getEventKeyState() ? CAM_SPEED : -CAM_SPEED;
            }
            else if(Keyboard.getEventKey() == Keyboard.KEY_D) {
//                camera.move(0.5f, 0, 0);
                camVel.x += Keyboard.getEventKeyState() ? CAM_SPEED : -CAM_SPEED;
            }
            else if(Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
                camVel.y += Keyboard.getEventKeyState() ? CAM_SPEED : -CAM_SPEED;
            }
            else if(Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
                camVel.y -= Keyboard.getEventKeyState() ? CAM_SPEED : -CAM_SPEED;
            }
            TempVars vars = TempVars.get();
            System.out.println(Vector3.mult(camVel, dt, vars.vect1));
            vars.release();
        }
        if(Display.isCloseRequested()) {
            stop();
        }

        TempVars vars = TempVars.get();
        camera.move(Vector3.mult(camVel, dt, vars.vect1));
        vars.release();

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

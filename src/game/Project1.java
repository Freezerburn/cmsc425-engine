package game;

import game.entimpl.Player;
import main.GameApplicationDisplay;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import structure.event.SubscriptionToken;
import structure.geometries.Cube;
import structure.geometries.SceneNode;
import structure.opengl.*;
import stuff.TempVars;
import stuff.Utils;
import texture.Texture;
import texture.TextureManager;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;


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

    public static Project1 instance;

    public static Camera camera;
    final static float CAM_SPEED = 5.75f;

    public static ArrayList<SceneNode> meshes = new ArrayList<SceneNode>(100);

    Player player;

    public static ShaderProgram colorProg;
    Mesh mesh, cubeMesh;
    Matrix4 projection;
    Matrix4 model;
    Matrix4 modelToCameraMatrix, cameraToClipMatrix;
    Vector3 camVel;
    float frustumScale;
    SceneNode test, test2;
    SceneNode floor, leftWall, rightWall, backWall, frontWall;
    SceneNode tree1, tree2, tree3, tree4, treeRot;
    SceneNode light;
    Texture floorTex;
    SubscriptionToken resizeToken;

    public static boolean mouseRotationEnabled = false;

    @Override
    public void initGL() {
        glViewport(0, 0, windowWidth, windowHeight);
        glClearColor(0.7f, 0.7f, 0.7f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
    }

    @Override
    public void initData() {
        instance = this;

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
        cubeMesh = new Mesh();
        cubeMesh.addBufferColumns(cubeVertices, GL_STATIC_DRAW, Mesh.VERTEX_3F, Mesh.VERTEX_2F);

        projection = new Matrix4();
        model = new Matrix4().translateLocal(0, 4, 0).scaleLocal(1, 5, 1);

//        float near = 0.1f, far = 600.0f;
        float fov = 75.0f;
        frustumScale = (float)(1.0 / Math.tan(Math.toRadians(fov) / 2.0));
        cameraToClipMatrix = Matrix4.perspective(new Matrix4(), fov, 0.1f, 600.0f);

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
        colorProg.setUniform("preModel", new Matrix4());
        colorProg.setUniform("model", model);
        Vector3 lightPos = new Vector3(6, 2, -15);
        colorProg.setUniform("lightPosition", lightPos);
        colorProg.setUniform("lightIntensities", new Vector3(1, 1, 1));
        colorProg.stopUsing();
        light = new Cube(colorProg, "model", "vertNormal").setPosition(lightPos);
        ((Cube)light).addTexture(TextureManager.loadTexture("res/gem.png", "gem", true, true));
        meshes.add(light);

        final float[] redCube = new float[] {
                //  R     G     B
                // bottom
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // top
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // front
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // back
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // left
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // right
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f
        };
        final float[] blueCube = new float[3 * 6 * 6];
        for(int i = 0; i < 3 * 6 * 6; i += 3) {
            blueCube[i] = 0;
            blueCube[i + 1] = 0;
            blueCube[i + 2] = 1;
        }
        final float[] greenCube = new float[3 * 6 * 6];
        for(int i = 0; i < 3 * 6 * 6; i += 3) {
            greenCube[i] = 0;
            greenCube[i + 1] = 1;
            greenCube[i + 2] = 0;
        }

        test = new Cube(colorProg, "model", "vertNormal").setPosition(-3, 0, 0);
        test2 = new Cube(colorProg, "model", "vertNormal").setPosition(-3, -0.001f, -0.001f).setScale(5, 1, 1);
        floor = new Cube(colorProg, "model", "vertNormal", redCube, Mesh.VERTEX_3F).setPosition(0, -2, 0).setScale(100, 1, 100);
        leftWall = new Cube(colorProg, "model", "vertNormal", blueCube, Mesh.VERTEX_3F).setPosition(-50, 50, 0).setScale(1, 100, 100);
        rightWall = new Cube(colorProg, "model", "vertNormal", greenCube, Mesh.VERTEX_3F).setPosition(50, 50, 0).setScale(1, 100, 100);
        meshes.add(floor);
        meshes.add(leftWall);
        meshes.add(rightWall);

        player = new Player();
        floorTex = TextureManager.loadTexture("res/floor.png", "floor", false, true);
        ((Cube)floor).addTexture(floorTex);
        ((Cube)leftWall).addTexture(TextureManager.loadTexture("res/wall.png", "wall", true, true));
        ((Cube)rightWall).addTexture(TextureManager.loadTexture("res/wall.png", "wall", true, true));
        tree1 = new Cube(colorProg, "model", "vertNormal").setPosition(10, 4, 10).setScale(2, 5, 2);
        tree2 = new Cube(colorProg, "model", "vertNormal").setPosition(-10, 4, 10).setScale(2, 5, 2);
        tree3 = new Cube(colorProg, "model", "vertNormal").setPosition(10, 4, -10).setScale(2, 5, 2);
        tree4 = new Cube(colorProg, "model", "vertNormal").setPosition(-10, 4, -10).setScale(2, 5, 2);
        System.out.println("======TREE ROT======");
        treeRot = new Cube(colorProg, "model", "vertNormal")
                .move(0, 4, -7)
                .rotate(70, 0, 0)
                .move(-2, -2, -2)
                .setScale(2, 2, 2);
        treeRot.rotate(0, 20, 0);
        System.out.println("======TREE ROT======");
//        System.out.println(treeRot.getMatrix());
        ((Cube)treeRot).addTexture(TextureManager.loadTexture("res/tree.png", "tree", true, true));
        ((Cube)tree1).addTexture(TextureManager.loadTexture("res/tree.png", "tree", true, true));
        ((Cube)tree2).addTexture(TextureManager.loadTexture("res/tree.png", "tree", true, true));
        ((Cube)tree3).addTexture(TextureManager.loadTexture("res/tree.png", "tree", true, true));
        ((Cube)tree4).addTexture(TextureManager.loadTexture("res/tree.png", "tree", true, true));
//        meshes.add(treeRot);
        meshes.add(tree1);
        meshes.add(tree2);
        meshes.add(tree3);
        meshes.add(tree4);

        System.out.println("ROUND TEST: " + Utils.roundTo(4.15968912342f, 3));
//        resizeToken = GameApplicationDisplay.Q.subscribe(
//                UIDGenerator.getUid(),
//                (e) -> {
//                    Pair<Integer, Integer> p = (Pair<Integer, Integer>)e;
//                    int height = p.unus;
//                    int width = p.duo;
//                    cameraToClipMatrix.m00 = frustumScale * (height / (float)width);
//                    cameraToClipMatrix.m11 = frustumScale;
//
//                    colorProg.use();
//                    colorProg.setUniform("cameraToClipMatrix", cameraToClipMatrix);
//                    colorProg.stopUsing();
//
//                    glViewport(0, 0, width, height);
//                    return true;
//                },
//                GameApplicationDisplay.RESIZE_STREAM
//        );
//        addWindowSizeListener((int width, int height) -> {
//            cameraToClipMatrix.m00 = frustumScale * (height / (float)width);
//            cameraToClipMatrix.m11 = frustumScale;
//
//            colorProg.use();
//            colorProg.setUniform("cameraToClipMatrix", cameraToClipMatrix);
//            colorProg.stopUsing();
//
//            glViewport(0, 0, width, height);
//        });
    }

    @Override
    public void run(float dt) {
//        while(Mouse.next()) {
//            if(mouseRotationEnabled) {
//                int dx = Mouse.getEventDX();
//                int dy = Mouse.getEventDY();
//                camera.rotateWithMouse(dx, dy);
//                player.onMouseMove(dx, dy);
//            }
//        }
        while(Keyboard.next()) {
            if(Keyboard.getEventKey() == Keyboard.KEY_Q ||
                    Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                stop();
            }
            else if(Keyboard.getEventKeyState()) {
                player.onKeyDown(Keyboard.getEventKey());
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
                else if(Keyboard.getEventKey() == Keyboard.KEY_Z) {
                    colorProg.use();
                    colorProg.setUniform("lightIntensities", new Vector3(1, 1, 1));
                    colorProg.stopUsing();
                }
                else if(Keyboard.getEventKey() == Keyboard.KEY_X) {
                    colorProg.use();
                    colorProg.setUniform("lightIntensities", new Vector3(0, 5, 1));
                    colorProg.stopUsing();
                }
                else if(Keyboard.getEventKey() == Keyboard.KEY_C) {
                    colorProg.use();
                    colorProg.setUniform("lightIntensities", new Vector3(5, 0, 1));
                    colorProg.stopUsing();
                }
                else if(Keyboard.getEventKey() == Keyboard.KEY_V) {
                    colorProg.use();
                    colorProg.setUniform("lightIntensities", new Vector3(5, 1, 0));
                    colorProg.stopUsing();
                }
                else if(Keyboard.getEventKey() == Keyboard.KEY_P) {
                    Vector3 lightPos = new Vector3(6, 2, -15);
                    colorProg.use();
                    colorProg.setUniform("lightPosition", lightPos);
                    colorProg.stopUsing();
                    light.setPosition(lightPos);
                }
                else if(Keyboard.getEventKey() == Keyboard.KEY_O) {
                    Vector3 lightPos = new Vector3(0, 20, 0);
                    colorProg.use();
                    colorProg.setUniform("lightPosition", lightPos);
                    colorProg.stopUsing();
                    light.setPosition(lightPos);
                }
                else if(Keyboard.getEventKey() == Keyboard.KEY_I) {
                    Vector3 lightPos = new Vector3(-6, 2, 15);
                    colorProg.use();
                    colorProg.setUniform("lightPosition", lightPos);
                    colorProg.stopUsing();
                    light.setPosition(lightPos);
                }
            }
            else {
                player.onKeyUp(Keyboard.getEventKey());
            }

        }
        if(Display.isCloseRequested()) {
            stop();
        }

        TempVars vars = TempVars.get();
        camera.moveRotated(Vector3.mult(camVel, dt, vars.vect1));
        vars.release();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        player.tick(dt);

        colorProg.use();
        colorProg.setUniform("time", dt);
        colorProg.setUniform("preModel", new Matrix4());
//        model.setIdentity();
//        camera.moveRotated(0.2f * dt, 0.0f * dt, 0.0f * dt);
        colorProg.setUniform("camera", camera.getMatrix());
//        colorProg.setUniform("model", model.rotate(1.0f * dt, new Vector3f(0, 1, 0)));
        colorProg.setUniform("model", model);
//        cubeMesh.draw();
//        test.draw();
//        test2.draw();
//        floor.draw();
//        leftWall.draw();
//        rightWall.draw();
//        tree1.draw();
//        tree2.draw();
//        tree3.draw();
//        tree4.draw();
        for(SceneNode geom : meshes) {
            geom.draw();
        }
        player.draw(dt);
        colorProg.stopUsing();

//        if(totalTicks > 10) {
//            stop();
//        }
    }

    @Override
    public void cleanup() {
        colorProg.destroy();
        cubeMesh.destroy();
    }
}

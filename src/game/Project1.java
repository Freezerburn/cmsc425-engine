package game;

import game.entimpl.Test;
import main.GameApplicationFrame;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import structure.opengl.Geometry;
import structure.opengl.ShaderProgram;
import structure.tree.QuadTree;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
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
    Geometry geometry;
    int positionBufferObject, vertexArray;

    @Override
    public void initGL() {
        glViewport(0, 0, windowWidth, windowHeight);
        glClearColor(0.7f, 0.7f, 0.7f, 1.0f);
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

        final float[] vertexPositions = new float[] {
                0.75f, 1.00f, 0.0f, 1.0f,
                0.75f, -0.75f, 0.0f, 1.0f,
                -0.75f, -0.75f, 0.0f, 1.0f
        };
        final float[] vertexPositions2 = new float[] {
                // LAYOUT
                // X Y Z W    R G B A
                0.0f, 0.5f, 0.0f, 0.9f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.366f, 0.0f, 0.9f, 0.0f, 1.0f, 0.0f, 1.0f,
                -0.5f, -0.366f, 0.0f, 0.9f, 0.0f, 0.0f, 1.0f, 1.0f
        };
        final float[] vertexPositions3 = new float[] {
                0.0f,    0.5f, 0.0f, 1.0f,
                0.5f, -0.366f, 0.0f, 1.0f,
                -0.5f, -0.366f, 0.0f, 1.0f,
                1.0f,    0.0f, 0.0f, 1.0f,
                0.0f,    1.0f, 0.0f, 1.0f,
                0.0f,    0.0f, 1.0f, 1.0f,
        };

        geometry = new Geometry();
//        geometry.addBufferOffset(vertexPositions3, GL_STATIC_DRAW, Geometry.VERTEX_4F, Geometry.VERTEX_4F);
//        geometry.addBufferColumns(vertexPositions2, GL_STATIC_DRAW, Geometry.VERTEX_4F, Geometry.VERTEX_4F);
        geometry.addBuffer(Arrays.copyOfRange(vertexPositions3, 0, 4 * 3), GL_STATIC_DRAW, Geometry.VERTEX_4F);
        geometry.addBuffer(Arrays.copyOfRange(vertexPositions3, 4 * 3, 4 * 3 * 2), GL_STATIC_DRAW, Geometry.VERTEX_4F);

        colorProg = new ShaderProgram();
        colorProg.addFragmentShader("fragcolor.glsl");
        colorProg.addVertexShader("vertexcolor.glsl");
        colorProg.link();
        colorProg.use();
        colorProg.setUniform("loopDuration", 5.0f);
        colorProg.setUniform("fragLoopDuration", 5.0f);
        colorProg.setUniform("time", 0.0f);
        colorProg.stopUsing();
    }

    @Override
    public void run(float dt) {
        while(Keyboard.next()) {
            if(Keyboard.getEventKey() == Keyboard.KEY_Q) {
                stop();
            }
        }
        if(Display.isCloseRequested()) {
            stop();
        }

        glClear(GL_COLOR_BUFFER_BIT);

        colorProg.use();
        colorProg.setUniform("time", dt);
        geometry.draw();
        colorProg.stopUsing();
    }

    @Override
    public void cleanup() {
        colorProg.destroy();
        geometry.destroy();
    }
}

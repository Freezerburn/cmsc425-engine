package structure.geometries;

import org.lwjgl.opengl.*;
import scene.Bounds;
import structure.opengl.Mesh;
import structure.opengl.ShaderProgram;
import structure.opengl.Vector3;
import stuff.TempVars;
import texture.Texture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 3/26/13
 * Time: 8:02 PM
 */
public class Cube extends SceneNode {
    protected static final float[] cubeVertices = new float[] {
            //  X     Y     Z
            // bottom
            -1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f, 1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f,-1.0f, 1.0f,
            -1.0f,-1.0f, 1.0f,

            // top
            -1.0f, 1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f,-1.0f,
            1.0f, 1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,

            // front
            -1.0f,-1.0f, 1.0f,
            1.0f,-1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f,-1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,

            // back
            -1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f,-1.0f,
            1.0f, 1.0f,-1.0f,

            // left
            -1.0f,-1.0f, 1.0f,
            -1.0f, 1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f,-1.0f,

            // right
            1.0f,-1.0f, 1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f, 1.0f,-1.0f,
            1.0f,-1.0f, 1.0f,
            1.0f, 1.0f,-1.0f,
            1.0f, 1.0f, 1.0f,
    };

    protected static final float[] cubeNormals = new float[] {
            // bottom
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,

            // top
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,

            // front
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,

            // back
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,

            // left
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,

            // right
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0
    };

    protected static final float[] cubeColors = new float[] {
            //  R     G     B
            // bottom
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            // top
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,

            // front
            1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,

            // back
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,

            // left
             0.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            // right
            1.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
    };
    protected static final Mesh CUBE_MESH = new Mesh();

    static {
        CUBE_MESH.addBuffer(cubeVertices, GL15.GL_STATIC_DRAW, Mesh.VERTEX_3F);
        CUBE_MESH.addBuffer(cubeColors, GL15.GL_STATIC_DRAW, Mesh.VERTEX_3F);
        CUBE_MESH.addBuffer(cubeNormals, GL15.GL_STATIC_DRAW, Mesh.VERTEX_3F);
    }

    protected Texture tex = null;

    public Cube(ShaderProgram prog, String uniformName, String normalsName) {
        super(prog, uniformName, CUBE_MESH);
        size.set(2, 2, 2);
    }

    public Cube(ShaderProgram prog, String uniformName, String normalsName, float[] colors, int type) {
        super(prog, uniformName, new Mesh());
        mesh.addBuffer(cubeVertices, GL15.GL_STATIC_DRAW, Mesh.VERTEX_3F);
        mesh.addBuffer(colors, GL15.GL_STATIC_DRAW, type);
        mesh.addBuffer(cubeNormals, GL15.GL_STATIC_DRAW, Mesh.VERTEX_3F);
        size.set(2, 2, 2);
    }

    public void addTexture(Texture tex) {
        float[] uv = new float[] {
                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexTopRightX(), tex.getTexTopRightY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),

                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexTopRightX(), tex.getTexTopRightY(),

                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexTopRightX(), tex.getTexTopRightY(),
                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexTopRightX(), tex.getTexTopRightY(),

                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexTopRightX(), tex.getTexTopRightY(),

                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
                tex.getTexTopRightX(), tex.getTexTopRightY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),

                tex.getTexTopRightX(), tex.getTexTopRightY(),
                tex.getTexBottomRightX(), tex.getTexBottomRightY(),
                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexTopRightX(), tex.getTexTopRightY(),
                tex.getTexBottomLeftX(), tex.getTexBottomLeftY(),
                tex.getTexTopLeftX(), tex.getTexTopLeftY(),
        };
        mesh.addBuffer(uv, GL15.GL_STATIC_DRAW, Mesh.VERTEX_2F);
        this.tex = tex;
    }

    public List<Vector3> getNormals() {
        List<Vector3> ret = new ArrayList<>(3);
        final Bounds bounds = getBounds();
        float right = bounds.getRight();
        float top = bounds.getTop();
        float back = bounds.getBack();
        float front = bounds.getFront();
        float bottom = bounds.getBottom();
        float left = bounds.getLeft();
        TempVars vars = TempVars.get();
        // Right normal (x-axis)
        //   Point A ==> Right-Top-Back
        //   Point B ==> Right-Top-Front
        //   Point C ==> Right-Bottom-Front
        Vector3 rightNormal = Vector3.normal(
                vars.vect1.set(right, top, back),
                vars.vect2.set(right, top, front),
                vars.vect3.set(right, bottom, front),
                new Vector3()
        ).normalizeLocal();
        // Up normal (y-axis)
        //   Point A ==> Right-Top-Back
        //   Point B ==> Left-Top-Back
        //   Point C ==> Left-Top-Front
        Vector3 upNormal = Vector3.normal(
                vars.vect1.set(right, top, back),
                vars.vect2.set(left, top, back),
                vars.vect3.set(left, top, front),
                new Vector3()
        ).normalizeLocal();
        // Back normal (z-axis)
        //   Point A ==> Left-Top-Back
        //   Point B ==> Right-Top-Back
        //   Point C ==> Right-Bottom-Back
        Vector3 backNormal = Vector3.normal(
                vars.vect1.set(left, top, back),
                vars.vect2.set(right, top, back),
                vars.vect3.set(right, bottom, back),
                new Vector3()
        ).normalizeLocal();
        vars.release();
        ret.add(rightNormal);
        ret.add(upNormal);
        ret.add(backNormal);
        return ret;
    }

    public List<Vector3> getVertices() {
        LinkedList<Vector3> ret = new LinkedList<>();
        final Bounds bounds = getBounds();
        float right = bounds.getRight();
        float top = bounds.getTop();
        float back = bounds.getBack();
        float front = bounds.getFront();
        float bottom = bounds.getBottom();
        float left = bounds.getLeft();
        ret.addLast(new Vector3(right, top, back));
        ret.addLast(new Vector3(left, top, back));
        ret.addLast(new Vector3(left, bottom, back));
        ret.addLast(new Vector3(right, bottom, back));
        ret.addLast(new Vector3(right, bottom, front));
        ret.addLast(new Vector3(left, bottom, front));
        ret.addLast(new Vector3(left, top, front));
        ret.addLast(new Vector3(right, top, front));
        return ret;
    }
}

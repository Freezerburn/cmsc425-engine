package structure.geometries;

import org.lwjgl.opengl.GL15;
import structure.opengl.Mesh;
import structure.opengl.ShaderProgram;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 8/6/13
 * Time: 8:56 AM
 * License: MIT
 */
public class Rect extends SceneNode {
    protected static final float[] VERTICES = new float[]{
            // NOTE: Currently runs counterclockwise. Does this
            // need to be changed?
            // X       Y
              -0.5f,  -0.5f,
               0.5f,  -0.5f,
               0.5f,   0.5f,

               0.5f,   0.5f,
              -0.5f,   0.5f,
              -0.5f,  -0.5f
    };

    protected static final Mesh RECT_MESH = new Mesh();
    static {
        RECT_MESH.addBuffer(VERTICES, GL15.GL_STATIC_DRAW, Mesh.VERTEX_2F);
    }


    protected ShaderProgram prog;
    protected String uniformName;

    public Rect(ShaderProgram prog, String uniformName) {
        super(prog, uniformName, RECT_MESH);
    }
}

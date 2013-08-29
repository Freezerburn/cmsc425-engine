package structure.opengl;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/28/13
 * Time: 10:22 AM
 */
public class Mesh {
    public static final int MAX_VBO = 16;
    public static final int VERTEX_1F = 0;
    public static final int VERTEX_2F = 1;
    public static final int VERTEX_3F = 2;
    public static final int VERTEX_4F = 3;

    public static final Object VAO_LOCK = new Object();

    protected static int currentVao = -1;

    protected int vao;
    protected int mode = GL_TRIANGLES;
    protected int[] vbos = new int[MAX_VBO];
    protected boolean usesIndices = false;
    protected int indicesRef = 0, numIndicies = 0, numVertices = 0, numRows = 0;
    protected short numBuffers = 0, numAttributes = 0;
    protected boolean destroyed = false;

    public Mesh() {
        this.vao = glGenVertexArrays();
    }

    protected int getDataTypeSize(int dataType) {
        if(dataType == VERTEX_1F) {
            return 1;
        }
        else if(dataType == VERTEX_2F) {
            return 2;
        }
        else if(dataType == VERTEX_3F) {
            return 3;
        }
        else {
            return 4;
        }
    }

    protected float[] convertColumnsToOffset(float[] data, float[] to, int rowSize, int... dataTypes) {
        int startPos = 0, toPos = 0;
        for(int i = 0; i < dataTypes.length; i++) {
            int size = getDataTypeSize(dataTypes[i]);
            for(int j = startPos; j < data.length; j += rowSize, toPos += size) {
//                System.out.println(Arrays.toString(Arrays.copyOfRange(data, j, j + size)));
                System.arraycopy(data, j, to, toPos, size);
            }
            startPos += size;
        }
//        System.out.println(Arrays.toString(to));
        return to;
    }

    public void addBuffer(float[] data, int usage, int dataType) {
        int dataTypeSize = getDataTypeSize(dataType);
        checkForErrors(data, dataTypeSize, dataType);

        uploadData(data, usage, numRows, dataType);
    }

    public void addBufferColumns(float[] data, int usage, int... dataTypes) {
        int rowSize = 0;
        for(int type : dataTypes) {
            rowSize += getDataTypeSize(type);
        }
        checkForErrors(data, rowSize, dataTypes);

        float[] converted = new float[data.length];
        uploadData(convertColumnsToOffset(data, converted, rowSize, dataTypes), usage, numRows, dataTypes);
    }

    public void addBufferOffset(float[] data, int usage, int... dataTypes) {
        int rowSize = 0;
        for(int type : dataTypes) {
            rowSize += getDataTypeSize(type);
        }
        checkForErrors(data, rowSize, dataTypes);

        uploadData(data, usage, numRows, dataTypes);
    }

    private void checkForErrors(float[] data, int rowSize, int... dataTypes) {
        // Make sure we never use more than 16 attributes.
        // I /believe/ that 16 is the max number of attributes OpenGL supports, according to a tutorial I read.
        if(numAttributes + dataTypes.length >= MAX_VBO) {
            throw new IllegalStateException("Can only have up to 16 attributes, requested: " + (numAttributes + dataTypes.length));
        }

        // Check that the number of "rows" matches anything previously added, or initializes.
        // The number of rows is basically equivalent to the number of vertices that the data defines. At least,
        // I think it is. This might make the next check redundant...
        if(numRows == 0) {
            numRows = data.length / rowSize;
        }
        else if(numRows != (data.length / rowSize)) {
            System.err.println(Arrays.toString(data));
            throw new IllegalStateException("Number of rows in buffers don't match: " + numRows + " vs " + (data.length / rowSize));
        }

        // Check that the number of vertices is equivalent to previous data, or initialize.
        if(numVertices == 0) {
            numVertices = data.length / rowSize;
        }
        else {
            int addingVerts = data.length / rowSize;
            if(addingVerts != numVertices) {
                System.err.println(Arrays.toString(data));
                throw new IllegalStateException("Vertex count for buffers don't match: " + numVertices + " vs " + addingVerts);
            }
        }
    }

    private void uploadData(float[] data, int usage, int numRows, int... dataTypes) {
        glBindVertexArray(vao);

        FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
        buf.put(data);
        buf.flip();
        int vbo = glGenBuffers();
        vbos[numBuffers++] = vbo;
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buf, usage);

        int startPos = 0;
        for(int i = 0; i < dataTypes.length; i++) {
            glEnableVertexAttribArray(numAttributes + i);
            glVertexAttribPointer(numAttributes + i, getDataTypeSize(dataTypes[i]), GL_FLOAT, false, 0, startPos * 4);
//            System.out.println((numAttributes + i) + ", " + getDataTypeSize(dataTypes[i]) + ", " + startPos);
            startPos += numRows * getDataTypeSize(dataTypes[i]);
        }
//        System.out.println("Num verts: " + numVertices);
        if(usesIndices) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesRef);
        }
        numAttributes += dataTypes.length;
        glBindVertexArray(0);
    }

    public void setIndices(int[] indices) {
        usesIndices = true;
        IntBuffer buf = BufferUtils.createIntBuffer(indices.length);
        buf.put(indices);
        buf.flip();

        glBindVertexArray(vao);
        indicesRef = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesRef);
        glBufferData(indicesRef, buf, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void draw() {
        synchronized (Mesh.VAO_LOCK) {
            if(currentVao != vao) {
                currentVao = vao;
                glBindVertexArray(vao);
            }
            if(usesIndices) {
                glDrawElements(0, (IntBuffer)null);
            }
            else {
                glDrawArrays(mode, 0, numVertices);
            }
            // For now, we immediately unbind the vao when done drawing. This might change later,
            // but is deliberately done here to prevent code outside of the Mesh class (or another Mesh?)
            // from modifying the vao's state.
            currentVao = 0;
            glBindVertexArray(0);
        }
    }

    public void destroy() {
        if(!destroyed) {
            destroyed = true;
            glDeleteVertexArrays(vao);
            for(int vao : vbos) {
                glDeleteBuffers(vao);
            }
        }
    }

    public boolean isValid() {
        return destroyed;
    }
}

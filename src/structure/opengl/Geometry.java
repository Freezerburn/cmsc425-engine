package structure.opengl;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

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
public class Geometry {
    public static int VERTEX_1F = 0;
    public static int VERTEX_2F = 1;
    public static int VERTEX_3F = 2;
    public static int VERTEX_4F = 3;

    protected static int currentVbo = -1;

    protected int vao;
    protected int type, bufferDataType;
    protected int[] vbos;
    protected boolean destroyed = false;
    protected int numVerts;

    public Geometry() {
        this.vao = glGenVertexArrays();
    }

//    public Geometry(int type, float[]... buffers) {
//        this.vao = glGenVertexArrays();
//        this.type = type;
//        this.vbos = new int[buffers.length];
//        this.bufferDataType = GL_STATIC_DRAW;
//        this.numVerts = buffers[0].length;
//        FloatBuffer[] floatBuffers = new FloatBuffer[buffers.length];
//        for(int i = 0; i < buffers.length; i++) {
//            FloatBuffer buf = BufferUtils.createFloatBuffer(buffers[i].length);
//            buf.put(buffers[i]);
//            buf.flip();
//            floatBuffers[i] = buf;
//        }
//        initBuffers(floatBuffers);
//    }
//
//    public Geometry(int type, FloatBuffer... buffers) {
//        this.vao = glGenVertexArrays();
//        this.type = type;
//        this.vbos = new int[buffers.length];
//        this.bufferDataType = GL_STATIC_DRAW;
//        this.numVerts = buffers[0].capacity();
//        this.initBuffers(buffers);
//    }
//
//    public Geometry(int type, int vertices, int... buffers) {
//        this.vao = glGenVertexArrays();
//        this.type = type;
//        this.vbos = buffers;
//        this.numVerts = vertices;
//        this.bufferDataType = GL_STATIC_DRAW;
//        for(int i = 0; i < buffers.length; i++) {
//            glBindBuffer(GL_ARRAY_BUFFER, buffers[i]);
//            glEnableVertexAttribArray(i);
//            glVertexAttribPointer(i, 4, GL_FLOAT, false, 0, 0);
//        }
//    }
//
//    public Geometry(int type, int divisions, float[]... buffers) {
//        this.vao = glGenVertexArrays();
//        this.type = type;
//        this.vbos = new int[buffers.length * divisions];
//        this.bufferDataType = GL_STATIC_DRAW;
//        this.numVerts = buffers[0].length / divisions;
//        FloatBuffer[] floatBuffers = new FloatBuffer[buffers.length * divisions];
//        for(int i = 0; i < buffers.length; i++) {
//            for(int j = 0; j < divisions; j++) {
//                int len = buffers[i].length / divisions;
//                FloatBuffer buf = BufferUtils.createFloatBuffer(len);
//                buf.put(buffers[i], len * j, len);
//                buf.flip();
//                floatBuffers[i * divisions + j] = buf;
//            }
//        }
//        initBuffers(floatBuffers);
//    }

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

    public void addBuffer(float[] data, int usage, int drawMode, int... dataTypes) {
        int rowSize = 0;
        for(int type : dataTypes) {
            rowSize += getDataTypeSize(type);
        }

        FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
        buf.put(data);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(vbo, buf, usage);

        glBindVertexArray(vao);
        int startPos = 0;
        for(int i = 0; i < dataTypes.length; i++) {
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, getDataTypeSize(dataTypes[i]), GL_FLOAT, false, rowSize, startPos);
        }
        glBindVertexArray(0);
    }

    protected void initBuffers(FloatBuffer... buffers) {
        glBindVertexArray(vao);
        for(int i = 0; i < buffers.length; i++) {
            FloatBuffer buf = buffers[i];
            int positionBufferObject = glGenBuffers();
            vbos[i] = positionBufferObject;
            glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
            glBufferData(GL_ARRAY_BUFFER, buf, bufferDataType);
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, 4, GL_FLOAT, false, 0, 0);
        }
    }

    public void draw() {
        if(currentVbo != vao) {
            currentVbo = vao;
            glBindVertexArray(vao);
        }
        glDrawArrays(type, 0, numVerts);
        // For now, we immediately unbind the vao when done drawing. This might change later,
        // but is deliberately done here to prevent code outside of the Geometry class from
        // modifying the vao's state.
        currentVbo = 0;
        glBindVertexArray(0);
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

    public boolean isDynamic() {
        return bufferDataType == GL_DYNAMIC_DRAW;
    }

    public boolean isStreaming() {
        return bufferDataType == GL_STREAM_DRAW;
    }
}

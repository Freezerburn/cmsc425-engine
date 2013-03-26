package structure.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix;
import stuff.Two;
import stuff.Utils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/28/13
 * Time: 10:27 AM
 */
public class ShaderProgram {
    public static final String SHADERS_DIR = "src/game/shaders";

    protected static int currentProgram = -1;

    protected int program = 0;
    protected boolean destroyed = false, linked = false;
    protected LinkedList<Integer> allShaders = new LinkedList<Integer>();
    protected FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    protected HashMap<String, Integer> uniformNameToLoc = new HashMap<String, Integer>();

    public void link() {
        program = createProgram();
        for(int shader : allShaders) {
            glDeleteShader(shader);
        }
        allShaders.clear();
        allShaders = null;
        linked = true;
    }

    public void addVertexShader(String file) {
        if(linked) {
            throw new IllegalStateException("Can't add a vertex shader after linking the program!");
        }
        else if(destroyed) {
            throw new IllegalStateException("Can't add a vertex shader to a destroyed program!");
        }
        int shader = createShader(GL_VERTEX_SHADER, file);
        allShaders.push(shader);
    }

    public void addFragmentShader(String file) {
        if(linked) {
            throw new IllegalStateException("Can't add a fragment shader after linking the program!");
        }
        else if(destroyed) {
            throw new IllegalStateException("Can't add a fragment shader to a destroyed program!");
        }
        int shader = createShader(GL_FRAGMENT_SHADER, file);
        allShaders.push(shader);
    }

    public void use() {
        if(currentProgram != program) {
            currentProgram = program;
            glUseProgram(program);
        }
    }

    public void stopUsing() {
        if(currentProgram == program) {
            currentProgram = 0;
            glUseProgram(0);
        }
    }

    protected int getUniformLoc(String name) {
        int ret = 0;
        if(uniformNameToLoc.containsKey(name)) {
            ret = uniformNameToLoc.get(name);
        }
        else {
            ret = glGetUniformLocation(program, name);
            uniformNameToLoc.put(name, ret);
        }
        return ret;
    }

    public void setUniform(String name, float val) {
        int loc = getUniformLoc(name);
        glUniform1f(loc, val);
    }

    public void setUniform(String name, Matrix4 m) {
        m.store(matrixBuffer);
        matrixBuffer.flip();
        int loc = getUniformLoc(name);
        glUniformMatrix4(loc, false, matrixBuffer);
    }

    public void destroy() {
        if(!destroyed) {
            glDeleteProgram(program);
            uniformNameToLoc.clear();
            uniformNameToLoc = null;
            program = 0;
            destroyed = true;
        }
    }

    public int getProgram() {
        return program;
    }

    public boolean isValid() {
        return destroyed;
    }

    public boolean isLinked() {
        return linked;
    }

    protected int createShader(int type, String file) {
        int ret = glCreateShader(type);
        if(file.charAt(0) == '/') {
            glShaderSource(ret, Utils.fileToString(SHADERS_DIR + file));
        }
        else {
            glShaderSource(ret, Utils.fileToString(SHADERS_DIR + "/" + file));
        }
        glCompileShader(ret);
        int status = glGetShaderi(ret, GL_COMPILE_STATUS);
        if(status == GL_FALSE) {
            int logLen = glGetShaderi(ret, GL_INFO_LOG_LENGTH);
            String errorMessage = glGetShaderInfoLog(ret, logLen + 1);
            System.out.println("Shader compilation problem for '" + file + "'.");
            System.out.println(" -- Err = " + errorMessage);
            System.exit(1);
        }
        return ret;
    }

    protected int createProgram() {
        int ret = glCreateProgram();
        for(int shader : allShaders) {
            glAttachShader(ret, shader);
        }
        glLinkProgram(ret);
        int status = glGetProgrami(ret, GL_LINK_STATUS);
        if(status == GL_FALSE) {
            int logLen = glGetProgrami(ret, GL_INFO_LOG_LENGTH);
            String errorMessage = glGetProgramInfoLog(ret, logLen + 1);
            System.out.println("Shader program linking problem.");
            System.out.println(" -- Err = " + errorMessage);
            System.exit(1);
        }
        for(int shader : allShaders) {
            glDetachShader(ret, shader);
        }
        return ret;
    }
}

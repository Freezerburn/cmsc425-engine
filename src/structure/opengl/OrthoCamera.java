package structure.opengl;

import main.GameApplicationDisplay;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 7/4/13
 * Time: 11:30 AM
 * License: MIT
 */
public class OrthoCamera {
    private float x, y, width, height;
    private String uniformName;
    private Matrix4 mat;

    public OrthoCamera(float width, float height, String uniformName) {
        this(0, 0, width, height, uniformName);
    }

    public OrthoCamera(float x, float y, float width, float height, String uniformName) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.uniformName = uniformName;
        this.mat = Matrix4.orthographic(
                new Matrix4(),
                x,
                x + width,
                y,
                y + height,
                0.00001f,
                600.0f
        );
//        update();
        GameApplicationDisplay.Q.subscribeToUpdates(this::update);
    }

    public void move(float x, float y) {
        Matrix4.translate(mat, x, y, 0, mat);
        this.x += x;
        this.y += y;
//        update();
    }

    protected void update() {
        ShaderProgram shader = GameApplicationDisplay.useShader();
        shader.use();
        shader.setUniform(uniformName, mat);
        GameApplicationDisplay.stopUsingShader();
    }
}

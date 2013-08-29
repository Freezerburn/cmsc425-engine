package structure.geometries;

import main.GameApplicationDisplay;
import scene.Bounds;
import structure.opengl.*;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 3/27/13
 * Time: 1:48 PM
 */
public class SceneNode {
    protected Mesh mesh;
    protected ArrayList<SceneNode> children;
    protected Vector3 pos = new Vector3(), size = new Vector3(1, 1, 1), scale = new Vector3(1, 1, 1), rotation = new Vector3(0, 0, 0);
    protected Matrix4 mat = new Matrix4();
    protected String uniformName;


    public SceneNode(ShaderProgram prog, String uniformName, Mesh mesh) {
        this.uniformName = uniformName;
        this.mesh = mesh;
        this.children = new ArrayList<>();
    }

    public void draw() {
        ShaderProgram prog = GameApplicationDisplay.useShader();
        prog.use();
        prog.setUniform(uniformName, mat);
        mesh.draw();
        GameApplicationDisplay.stopUsingShader();
    }

    public SceneNode setPosition(float x, float y, float z) {
        pos.set(x, y, z);
        refreshMatrix();
        return this;
    }

    public SceneNode setPosition(Vector3 pos) {
        return setPosition(pos.x, pos.y, pos.z);
    }

    public SceneNode move(float x, float y, float z) {
        pos.x += x;
        pos.y += y;
        pos.z += z;
        refreshMatrix();
        return this;
    }

    public SceneNode move(Vector3 amount) {
        return move(amount.x, amount.y, amount.z);
    }

    public SceneNode setSize(float x, float y, float z) {
        size.set(x, y, z);
        refreshMatrix();
        return this;
    }

    public SceneNode setSize(Vector3 size) {
        return setSize(size.x, size.y, size.z);
    }

    public SceneNode setScale(float x, float y, float z) {
        scale.set(x, y, z);
        refreshMatrix();
        return this;
    }

    public SceneNode setScale(Vector3 scale) {
        return setScale(scale.x, scale.y, scale.z);
    }

    public SceneNode rotate(float x, float y, float z) {
        rotation.addLocal(new Vector3(x, y, z));
        refreshMatrix();
        return this;
    }

    public Vector3 getSize() {
        return new Vector3(size).multLocal(scale);
    }

    public Vector3 getPos() {
        return new Vector3(pos);
    }

    public Vector3 getRotation() {
        return new Vector3(rotation);
    }

    public Bounds getBounds() {
        Vector3 actualSize = getSize();
        return new Bounds(pos, actualSize);
    }

    public Matrix4 getMatrix() {
        return new Matrix4(mat);
    }

    private void refreshMatrix() {
        Vector3 scaleHalfSize = size.mult(0.5f);
        Vector3 actualPos2 = pos.add(new Vector3(scaleHalfSize.x, scaleHalfSize.y, -scaleHalfSize.z));
        mat.setIdentity();
        mat.translateLocal(actualPos2.x, actualPos2.y, actualPos2.z);
        mat = Matrix4.rotate(mat, rotation.x, rotation.y, rotation.z, new Matrix4());
        mat.translateLocal(-scaleHalfSize.x, -scaleHalfSize.y, scaleHalfSize.z);
        mat.scaleLocal(scale.x, scale.y, scale.z);
    }

    public void addChild(SceneNode child) {
        children.add(child);
    }
}

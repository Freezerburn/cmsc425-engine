package scene;

import structure.opengl.Vector3;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/19/13
 * Time: 9:53 AM
 */
public class Node {
    protected Vector3 translation, scale, rotation;
    protected Vector3 center;
    protected String name;
    protected LinkedList<Node> children;

    public Node(String name) {
        this.name = name;
        this.center = Vector3.get();
        this.translation = Vector3.get();
        this.scale = Vector3.get();
        this.rotation = Vector3.get();
    }

    public void attachChild(Node child) {
        children.push(child);
    }

    public void moveCenter(float x, float y, float z) {
        center.x += x;
        center.y += y;
        center.z += z;
    }

    public void setCenter(float x, float y, float z) {
        center.x = x;
        center.y = y;
        center.z = z;
    }

    public void rotate(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void translate(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public void setTranslation(float x, float y, float z) {
        translation.x = x;
        translation.y = y;
        translation.z = z;
    }

    public void scale(float x, float y, float z) {
        scale.x += x;
        scale.y += y;
        scale.z += z;
    }
}

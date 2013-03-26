package structure.opengl;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import stuff.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: vince_000
 * Date: 3/8/13
 * Time: 7:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Camera {
    protected Matrix4 mat;
    protected Vector3 eye, view, up;

    protected float yaw = 0.0f, pitch = 0.0f;
    protected Quaternion orientation = new Quaternion();
    protected float currentRotationAroundX = 0.0f;
    protected float mouseSensitivity = 1.0f;
    protected float conversion = 1.0f;

    public Camera() {
        mat = new Matrix4();
        eye = new Vector3(0, 0, 1);
        view = new Vector3(0, 0, 0);
        up = new Vector3(0, 0, 1);
    }

    public Camera lookAt(float eyex, float eyey, float eyez,
                         float centerx, float centery, float centerz,
                         float upx, float upy, float upz) {
        eye.set(eyex, eyey, eyez);
        view.set(centerx, centery, centerz);
        up.set(upx, upy, upz);
//        Utils.lookAt(mat, eye, view, up);

//        Vector3 direction = Vector3.sub(eye, view);
        Vector3 direction = Vector3.sub(view, eye);
        System.out.println(direction);
        Quaternion.lookAt(orientation, direction, up);
        Matrix4.lookAt(mat, eye, new Vector3(orientation.x, orientation.y, orientation.z), up);
//        System.out.println(eye + " --- " + view + " --- " + up);
        System.out.println(eye + " --- " + new Vector3(orientation.x, orientation.y, orientation.z) + " --- " + up);

        return this;
    }

    public Camera setMouseSensitivity(float sensitivity) {
        mouseSensitivity = sensitivity;
        return this;
    }

    public Camera rotateWithMouse(int dx, int dy) {
        if(dx == 0 && dy == 0) {
            return this;
        }

        yaw += Math.toRadians(dy / (conversion * mouseSensitivity));
        pitch += Math.toRadians(dx / (conversion * mouseSensitivity));

        orientation = Quaternion.fromEulerAngles(orientation, yaw, pitch, 0);
//        up = Quaternion.transform(new Vector3f(0, 1, 0), orientation, new Vector3f());
//        up = up.normalise(up);
//        view = Vector3f.add(Quaternion.transform(new Vector3(0, 0, 1), orientation, new Vector3()), eye, new Vector3());
        view = Quaternion.transform(Vector3.forward(), orientation).add(eye);
        view.normalizeLocal();
        System.out.println(eye + " --- " + view + " --- " + up);

        Matrix4.lookAt(mat, eye, view, up);

        return this;
    }

    protected void rotateCamera(float angle, float x, float y, float z) {
        Quaternion temp, quatView, result;

        temp = Quaternion.fromAxisAngle(new Quaternion(), angle, new Vector3(x, y, z));
        quatView = new Quaternion(0, view);
        result = Quaternion.mult(Quaternion.mult(temp, quatView, new Quaternion()), temp.conjugate(), new Quaternion());
//        result = Quaternion.mult(quatView, temp, new Quaternion());

        view.x = result.x;
        view.y = result.y;
        view.z = result.z;
    }

    public Camera move(float x, float y, float z) {
        eye.x += x;
        eye.y += y;
        eye.z += z;
        view.x += x;
        view.y += y;
        view.z += z;
        Utils.lookAt(mat, eye, view, up);
        return this;
    }

    public Matrix4 getMatrix() {
//        Matrix4f out = new Matrix4f();
//        Matrix4f.translate(eye, out, out);
//        Matrix4f.mul(out, orientation.toMatrix(), out);
//        return Utils.lookAt(out, eye, new Vector3f(orientation.x, orientation.y, orientation.z), up);
        return mat;
    }
}

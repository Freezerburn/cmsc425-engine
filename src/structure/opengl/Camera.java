package structure.opengl;

import game.Project1;
import main.GameApplicationDisplay;
import structure.control.MouseEvent;
import stuff.TempVars;
import stuff.Utils;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 3/8/13
 * Time: 7:39 PM
 * License: MIT
 */
public class Camera {
    protected Matrix4 mat;
    protected Vector3 eye, view, up;

    protected static final float MAX_PITCH = 65.0f;
    protected float yaw = 0.0f, pitch = 0.0f;
    protected Quaternion orientation = new Quaternion();
    protected Quaternion yawq = new Quaternion(), pitchq = new Quaternion();
    protected float currentRotationAroundX = 0.0f;
    protected float mouseSensitivity = 1.0f;
    protected float conversion = 5.0f;
    protected float mults = 0;

    public Camera() {
        mat = new Matrix4();
        eye = new Vector3(0, 0, 1);
        view = new Vector3(0, 0, 0);
        up = new Vector3(0, 0, 1);
        GameApplicationDisplay.mouseManager.listenForMovement(this::rotateWithMouse);
//        Project1.instance.addMouseMovementListener(this::rotateWithMouse);
    }

    public Camera lookAt(float eyex, float eyey, float eyez,
                         float centerx, float centery, float centerz,
                         float upx, float upy, float upz) {
        pitch = 0;
        yaw = 0;
        eye.set(eyex, eyey, eyez);
        view.set(centerx, centery, centerz);
        up.set(upx, upy, upz);

        Vector3 direction = Vector3.sub(view, eye);
        direction.z = -direction.z;
        System.out.println(direction);
        Quaternion.lookAt(orientation, direction, up);
        Matrix4.lookAt(mat, eye, new Vector3(orientation.x, orientation.y, orientation.z), up);

        return this;
    }

    public Camera setMouseSensitivity(float sensitivity) {
        mouseSensitivity = sensitivity;
        return this;
    }

    public void rotateWithMouse(MouseEvent me) {
        if(!Project1.mouseRotationEnabled) {
            return;
        }
        float dx = me.dx;
        float dy = me.dy;
        if(dx == 0 && dy == 0) {
            return;
        }

        yaw -= dx / (conversion * mouseSensitivity);
        pitch += dy / (conversion * mouseSensitivity);
        float dxf = -dx / (conversion * mouseSensitivity);
        float dyf = dy / (conversion * mouseSensitivity);
        if(pitch > MAX_PITCH) {
            pitch = MAX_PITCH;
            dyf = 0;
        }
        else if(pitch < -MAX_PITCH) {
            pitch = -MAX_PITCH;
            dyf = 0;
        }

        // Allocation free code! :D
        TempVars vars = TempVars.get();
        orientation = Quaternion.mult(Quaternion.fromAxisAngle(vars.quat1, dxf, Vector3.UP), orientation, orientation);
        orientation = Quaternion.mult(orientation, Quaternion.fromAxisAngle(vars.quat1, dyf, Vector3.RIGHT), orientation);
        // We just normalize every frame for now. Theoretically, should only need to normalize every
        // n frames/multiplications. (should only optimize this if it turns into a critical section, or
        // just an easy-ish way to gain a little performance)
        orientation.normalizeLocal();

        view.set(Vector3.transform(vars.vect1.set(Vector3.FORWARD), orientation).addLocal(eye));
        vars.release();

        Matrix4.lookAt(mat, eye, view, up);
    }

    public Camera rotateEyeWithMouse(int dx, int dy) {
        return this;
    }

    public Camera moveRotated(float x, float y, float z) {
        return moveRotated(new Vector3(x, y, z));
    }

    public Camera moveRotated(Vector3 v) {
        TempVars vars = TempVars.get();
        float y = v.y;
        v = Vector3.transform(vars.vect1.set(v.x, 0, v.z), Quaternion.fromAxisAngle(vars.quat1, yaw, Vector3.UP), v);
        eye.addLocal(v.set(v.x, y, v.z));
        view = Vector3.transform(Vector3.forward(), orientation).add(eye);
        vars.release();
        Matrix4.lookAt(mat, eye, view, up);
        return this;
    }

    public Camera move(float x, float y, float z) {
        return move(new Vector3(x, y, z));
    }

    public Camera move(Vector3 v) {
        eye.addLocal(v);
        view.addLocal(v);
        Matrix4.lookAt(mat, eye, view, up);
        return this;
    }

    public Matrix4 getMatrix() {
        return mat;
    }

    public Vector3 getPosition() {
        return new Vector3(eye);
    }

    public Quaternion getOrientation() {
        return new Quaternion(orientation);
    }
}

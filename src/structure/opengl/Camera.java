package structure.opengl;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import stuff.TempVars;
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

//        orientation = Quaternion.fromEulerAngles(orientation, yaw, pitch, 0);
        // Allocation free code! :D (almost, at least)
        TempVars vars = TempVars.get();
        if(dx > 0) {
        }
        orientation = Quaternion.fromAxisAngle(vars.quat1, dxf, Vector3.UP).mult(orientation);
        orientation = orientation.mult(Quaternion.fromAxisAngle(vars.quat1, dyf, Vector3.RIGHT));
//        orientation = Quaternion.fromAxisAngle(vars.quat1, dxf, Vector3.UP)
//            .mult(Quaternion.fromAxisAngle(vars.quat2, dyf, Vector3.RIGHT)).mult(orientation);
//        orientation = orientation.mult(Quaternion.fromAxisAngle(vars.quat1, dyf, Vector3.right())
//                .mult(Quaternion.fromAxisAngle(vars.quat2, dxf, Vector3.up())));
//        orientation = Quaternion.fromAxisAngle(vars.quat1, yaw, Vector3.up())
//                .mult(Quaternion.fromAxisAngle(vars.quat2, pitch, Vector3.right()));
//        up = Quaternion.transform(vars.vect1.set(Vector3.UP), Quaternion.fromAxisAngle(vars.quat1, pitch, Vector3.RIGHT));
//        up = up.normalizeLocal();
        view.set(Vector3.transform(vars.vect1.set(Vector3.FORWARD), orientation).addLocal(eye));
        vars.release();
//        view.normalizeLocal();
        System.out.println(eye + " --- " + view + " --- " + up);

        Matrix4.lookAt(mat, eye, view, up);

        return this;
    }

    public Camera move(float x, float y, float z) {
        return move(new Vector3(x, y, z));
    }

    public Camera move(Vector3 v) {
//        if (keyboard.IsKeyDown(Keys.Up))
//            Direction *= Quaternion.CreateFromAxisAngle(new Vector3(1, 0, 0), TurnSpeed);
//        if (keyboard.IsKeyDown(Keys.Down))
//            Direction *= Quaternion.CreateFromAxisAngle(new Vector3(-1, 0, 0), TurnSpeed);
//        if (keyboard.IsKeyDown(Keys.Left))
//            Direction = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, 1), TurnSpeed) * Direction;
//        if (keyboard.IsKeyDown(Keys.Right))
//            Direction = Quaternion.CreateFromAxisAngle(new Vector3(0, 0, -1), TurnSpeed) * Direction;
        TempVars vars = TempVars.get();
        float y = v.y;
        v = Vector3.transform(vars.vect1.set(v.x, 0, v.z), Quaternion.fromAxisAngle(vars.quat1, yaw, Vector3.UP));
        eye.addLocal(v.set(v.x, y, v.z));
        view = Vector3.transform(Vector3.forward(), orientation).add(eye);
        vars.release();
        Matrix4.lookAt(mat, eye, view, up);
        return this;
    }

    public Matrix4 getMatrix() {
        return mat;
    }
}

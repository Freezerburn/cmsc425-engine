package structure.opengl;

/**
 * Created with IntelliJ IDEA.
 * User: vince_000
 * Date: 6/17/13
 * Time: 12:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CameraEuler {
    protected Matrix4 mat;
    protected float anglex, angley;
    protected float mouseSensitivity;

    public CameraEuler() {
        anglex = 0;
        angley = 0;
        mat = new Matrix4();
        mouseSensitivity = 1;
    }

    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;
    }
}

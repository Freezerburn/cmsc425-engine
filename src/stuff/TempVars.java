package stuff;

import structure.opengl.Quaternion;
import structure.opengl.Vector3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: vince_000
 * Date: 3/24/13
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class TempVars {
    private static TempVars ourInstance = new TempVars();
    private static final Lock theLock = new ReentrantLock();

    public Vector3 vect1 = new Vector3(),
            vect2 = new Vector3(),
            vect3 = new Vector3();
    public Quaternion quat1 = new Quaternion(),
            quat2 = new Quaternion(),
            quat3 = new Quaternion();

    public void lock() {
        theLock.lock();
    }

    public void release() {
        theLock.unlock();
    }

    public static TempVars get() {
        return ourInstance;
    }

    private TempVars() {
    }
}

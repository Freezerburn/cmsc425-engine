package scene;

import stuff.Utils;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/19/13
 * Time: 3:07 PM
 */
public class Vector3 {
    public static final Vector3 ZERO = new Vector3(0.0f, 0.0f, 0.0f);

    protected static final int INITIAL_VECTORS = 1000;
    protected static final LinkedList<Vector3> recycled = new LinkedList<Vector3>();

    static {
        for(int i = 0; i < INITIAL_VECTORS; i++) {
            recycled.push(new Vector3());
        }
    }

    public static Vector3 get() {
        return get(0.0f, 0.0f, 0.0f);
    }

    public static Vector3 get(Vector3 vec) {
        return get(vec.x, vec.y, vec.z);
    }

    public static Vector3 get(float x, float y) {
        return get(x, y, 0.0f);
    }

    public static Vector3 get(float x, float y, float z) {
        if(recycled.size() > 0) {
            Vector3 ret = recycled.pop();
            ret.x = x;
            ret.y = y;
            ret.z = z;
            return ret;
        }
        return new Vector3(x, y, z);
    }

    public static void recycle(Vector3 vec) {
        if(recycled.size() > INITIAL_VECTORS) {
            double result = Math.abs(Utils.random.nextGaussian());
            if(result < 0.1) {
                recycled.push(vec);
            }
        }
        else {
            recycled.push(vec);
        }
    }

    /* ====== END STATIC METHODS ====== */

    public float x, y, z;

    protected Vector3() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    protected Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 other) {
        return Vector3.get(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 iadd(Vector3 other) {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    public Vector3 sadd(float scalar) {
        return Vector3.get(x + scalar, y + scalar, z + scalar);
    }

    public Vector3 isadd(float scalar) {
        x += scalar;
        y += scalar;
        z += scalar;
        return this;
    }

    public Vector3 smult(float scalar) {
        return Vector3.get(x * scalar, y * scalar, z * scalar);
    }

    public Vector3 ismult(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    public float length() {
        // Casting = ick
        return (float)Math.sqrt((double)(x * x + y * y + z * z));
    }

    public Vector3 normalized() {
        return smult(length());
    }

    public void normalize() {
        ismult(length());
    }

    @Override
    public String toString() {
        return String.format("Vec3{x: %f, y: %f, z: %f}", x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        else if(o instanceof Vector3) {
            Vector3 v = (Vector3)o;
            return Float.compare(v.x, x) == 0 &&
                    Float.compare(v.y, y) == 0 &&
                    Float.compare(v.z, z) == 0;
        }
        return false;
    }
}

package structure.opengl;

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

//    static {
//        for(int i = 0; i < INITIAL_VECTORS; i++) {
//            recycled.push(new Vector3());
//        }
//    }

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

    public Vector3() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public Vector3(float x, float y) {
        this(x, y, 0.0f);
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 v) {
        this(v.x, v.y, v.z);
    }

    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 set(Vector3 other) {
        return set(other.x, other.y, other.z);
    }

    public Vector3 add(Vector3 other) {
        return Vector3.add(this, other, new Vector3());
    }

    public Vector3 addLocal(Vector3 other) {
        return Vector3.add(this, other, this);
    }

    public Vector3 add(float scalar) {
        return Vector3.add(this, scalar, new Vector3());
    }

    public Vector3 addLocal(float scalar) {
        return Vector3.add(this, scalar, this);
    }

    public Vector3 sub(Vector3 other) {
        return Vector3.sub(this, other);
    }

    public Vector3 subLocal(Vector3 other) {
        return Vector3.sub(this, other, this);
    }

    public Vector3 sub(float scalar) {
        return Vector3.sub(this, scalar);
    }

    public Vector3 subLocal(float scalar) {
        return Vector3.sub(this, scalar, this);
    }

    public Vector3 mult(Vector3 other) {
        return Vector3.mult(this, other, new Vector3());
    }

    public Vector3 multLocal(Vector3 other) {
        return Vector3.mult(this, other, this);
    }

    public Vector3 mult(float scalar) {
        return Vector3.mult(this, scalar, new Vector3());
    }

    public Vector3 multLocal(float scalar) {
        return Vector3.mult(this, scalar, this);
    }

    public float length() {
        // Casting = ick
        return (float)Math.sqrt((double)(x * x + y * y + z * z));
    }

    public Vector3 normalize() {
        multLocal(length());
        return this;
    }

    public Vector3 normalizeLocal() {
        return mult(length());
    }

    public Vector3 negate() {
        return Vector3.negate(this, new Vector3());
    }

    public Vector3 negateLocal() {
        return Vector3.negate(this, this);
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

    /*
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
     */

    public static Vector3 up() {
        return new Vector3(0, 1, 0);
    }

    public static Vector3 down() {
        return Vector3.up().negateLocal();
    }

    public static Vector3 right() {
        return new Vector3(1, 0, 0);
    }

    public static Vector3 left() {
        return Vector3.right().negateLocal();
    }

    public static Vector3 forward() {
        return new Vector3(0, 0, -1);
    }

    public static Vector3 backward() {
        return Vector3.forward().negateLocal();
    }

    public static Vector3 mult(Vector3 v1, Vector3 v2, Vector3 result) {
        result.x = v1.x * v2.x;
        result.y = v1.y * v2.y;
        result.z = v1.z * v2.z;
        return result;
    }

    public static Vector3 mult(Vector3 v, float scalar, Vector3 result) {
        result.x = v.x * scalar;
        result.y = v.y * scalar;
        result.z = v.z * scalar;
        return result;
    }

    public static Vector3 add(Vector3 v1, Vector3 v2, Vector3 result) {
        result.x = v1.x + v2.x;
        result.y = v1.y + v2.y;
        result.z = v1.z + v2.z;
        return result;
    }

    public static Vector3 add(Vector3 v, float scalar, Vector3 result) {
        result.x = v.x + scalar;
        result.y = v.y + scalar;
        result.z = v.z + scalar;
        return result;
    }

    public static Vector3 sub(Vector3 v1, Vector3 v2) {
        return Vector3.sub(v1, v2, new Vector3());
    }

    public static Vector3 sub(Vector3 v, float scalar) {
        return Vector3.sub(v, scalar, new Vector3());
    }

    public static Vector3 sub(Vector3 v1, Vector3 v2, Vector3 result) {
        result.x = v1.x - v2.x;
        result.y = v1.y - v2.y;
        result.z = v1.z - v2.z;
        return result;
    }

    public static Vector3 sub(Vector3 v, float scalar, Vector3 result) {
        result.x = v.x - scalar;
        result.y = v.y - scalar;
        result.z = v.z - scalar;
        return result;
    }

    public static Vector3 transform(Vector3 v, Quaternion q) {
        return transform(v, q, new Vector3());
    }

    public static Vector3 transform(Vector3 v, Quaternion q, Vector3 result) {
        return result;
    }

    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return Vector3.cross(v1, v2, new Vector3());
    }

    public static Vector3 cross(Vector3 v1, Vector3 v2, Vector3 result) {
        result.x = v1.y * v2.z - v1.z * v2.y;
        result.y = v1.z * v2.x - v1.x * v2.z;
        result.z = v1.x * v2.y - v1.y * v2.x;
        return result;
    }

    public static Vector3 negate(Vector3 v) {
        return Vector3.negate(v, new Vector3());
    }

    public static Vector3 negate(Vector3 v, Vector3 result) {
        return Vector3.mult(v, -1, result);
    }
}

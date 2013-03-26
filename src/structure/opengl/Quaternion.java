package structure.opengl;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 3/9/13
 * Time: 6:43 PM
 */
public class Quaternion {
    public float w, x, y, z;

    public Quaternion() {
        this.w = 1.0f;
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public Quaternion(float w, Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
    }

    public Quaternion(float w, float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Quaternion other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public float length() {
        return (float)Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Quaternion normalize() {
        return Quaternion.normalize(new Quaternion(this));
    }

    public Quaternion normalizeLocal() {
        return Quaternion.normalize(this);
    }

    public Quaternion conjugate() {
        return Quaternion.conjugate(new Quaternion(this));
    }

    public Quaternion mult(Quaternion other) {
        return Quaternion.mult(this, other, new Quaternion());
    }

    public Quaternion fromAxisAngle(float angle, Vector3 axis) {
        return Quaternion.fromAxisAngle(new Quaternion(this), angle, axis);
    }

    public Matrix4 toMatrixUnit() {
        return Quaternion.toMatrixUnit(new Matrix4(), this);
    }

    public Matrix4 toMatrix() {
        return Quaternion.toMatrix(new Matrix4(), this);
    }

    public Quaternion lookAt(Vector3 direction, Vector3 up) {
        return Quaternion.lookAt(new Quaternion(this), direction, up);
    }

    public Quaternion lookAtLocal(Vector3 direction, Vector3 up) {
        return Quaternion.lookAt(this, direction, up);
    }

    /*
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
     */

    public static Quaternion fromAxisAngle(Quaternion q, float angle, Vector3 axis) {
        angle = (float)Math.toRadians(angle);
        float sinAngle2 = (float)Math.sin(angle / 2.0);
        q.x = axis.x * sinAngle2;
        q.y = axis.y * sinAngle2;
        q.y = axis.y * sinAngle2;
        q.w = (float)Math.cos(angle / 2.0);
//        q.w = (float)Math.cos(angle);
        return q;
    }

    public static Quaternion fromEulerAngles(Quaternion q, float yaw, float pitch, float roll) {
        yaw = (float)Math.toRadians(yaw);
        pitch = (float)Math.toRadians(pitch);
        roll = (float)Math.toRadians(roll);

        float rollOver2 = roll * 0.5f;
        float sinRollOver2 = (float)Math.sin((double)rollOver2);
        float cosRollOver2 = (float)Math.cos((double)rollOver2);
        float pitchOver2 = pitch * 0.5f;
        float sinPitchOver2 = (float)Math.sin((double)pitchOver2);
        float cosPitchOver2 = (float)Math.cos((double)pitchOver2);
        float yawOver2 = yaw * 0.5f;
        float sinYawOver2 = (float)Math.sin((double)yawOver2);
        float cosYawOver2 = (float)Math.cos((double)yawOver2);
        Quaternion result;
        q.w = cosYawOver2 * cosPitchOver2 * cosRollOver2 + sinYawOver2 * sinPitchOver2 * sinRollOver2;
        q.x = cosYawOver2 * sinPitchOver2 * cosRollOver2 + sinYawOver2 * cosPitchOver2 * sinRollOver2;
        q.y = sinYawOver2 * cosPitchOver2 * cosRollOver2 - cosYawOver2 * sinPitchOver2 * sinRollOver2;
        q.z = cosYawOver2 * cosPitchOver2 * sinRollOver2 - sinYawOver2 * sinPitchOver2 * cosRollOver2;

        q.normalizeLocal();
        return q;
    }

    public static Matrix4 toMatrixUnit(Matrix4 out, Quaternion q) {
        out.m00 = 1 - 2 * q.y * q.y - 2 * q.z * q.z;
        out.m01 = 2 * q.x * q.y - 2 * q.w * q.z;
        out.m02 = 2 * q.x * q.z + 2 * q.w + q.y;
        out.m03 = 0;

        out.m10 = 2 * q.x * q.y + 2 * q.w * q.z;
        out.m11 = 1 - 2 * q.x * q.x - 2 * q.z * q.z;
        out.m12 = 2 * q.y * q.z + 2 * q.w * q.x;
        out.m13 = 0;

        out.m20 = 2 * q.x * q.z - 2 * q.w * q.z;
        out.m21 = 2 * q.y * q.z - 2 * q.w * q.x;
        out.m22 = 1 - 2 * q.x * q.x - 2 * q.y * q.y;
        out.m23 = 0;

        out.m30 = 0;
        out.m31 = 0;
        out.m32 = 0;
        out.m33 = 1;

        return out;
    }

    public static Matrix4 toMatrix(Matrix4 out, Quaternion q) {
        out.m00 = 1 - 2 * q.y * q.y - 2 * q.z * q.z;
        out.m01 = 2 * q.x * q.y - 2 * q.w * q.z;
        out.m02 = 2 * q.x * q.z + 2 * q.w * q.y;
        out.m03 = 0;

        out.m10 = 2 * q.x * q.y + 2 * q.w * q.z;
        out.m11 = 1 - 2 * q.x * q.x - 2 * q.z * q.x;
        out.m12 = 2 * q.y * q.z - 2 * q.w * q.x;
        out.m13 = 0;

        out.m20 = 2 * q.x * q.z - 2 * q.w * q.y;
        out.m21 = 2 * q.y * q.z + 2 * q.w + q.x;
        out.m22 = 1 - 2 * q.x * q.x - 2 * q.y * q.y;
        out.m23 = 0;

        out.m30 = 0;
        out.m31 = 0;
        out.m32 = 0;
        out.m33 = 1;

        return out;
    }

    public static Quaternion mult(Quaternion A, Quaternion B, Quaternion C) {
        C.x = A.w*B.x + A.x*B.w + A.y*B.z - A.z*B.y;
        C.y = A.w*B.y + A.y*B.w + A.z*B.x - A.x*B.z;
        C.z = A.w*B.z + A.z*B.w + A.x*B.y - A.y*B.x;
        C.w = A.w*B.w - A.x*B.x - A.y*B.y - A.z*B.z;
        return C;
    }

    public static Vector3 mult(Quaternion A, Vector3 V, Vector3 B) {
        Vector3 Vn = V.normalizeLocal();
        Quaternion vecQuat = new Quaternion(), resQuat = new Quaternion();
        vecQuat.x = Vn.x;
        vecQuat.y = Vn.y;
        vecQuat.z = Vn.z;
        vecQuat.w = 0;

        resQuat = vecQuat.mult(A.conjugate());
        resQuat = A.mult(resQuat);

        B.x = resQuat.x;
        B.y = resQuat.y;
        B.z = resQuat.z;
        return B;
    }

    public static Vector3 transform(Vector3 v, Quaternion q) {
        return Quaternion.transform(v, q, new Vector3());
    }

    public static Vector3 transform(Vector3 v, Quaternion q, Vector3 result) {
        result.x = v.x * (q.x * q.x + q.w * q.w - q.y * q.y - q.z * q.z) +
                v.y * (2 * q.x * q.y - 2 * q.w * q.z) +
                v.z * (2 * q.x * q.z + 2 * q.w * q.y);
        result.y = v.x * (2 * q.w * q.z + 2 * q.x * q.y) +
                v.y * (q.w * q.w - q.x * q.x + q.y * q.y - q.z * q.z) +
                q.z * (-2 * q.w * q.x + 2 * q.y * q.z);
        result.z = v.x * (-2 * q.w * q.y + 2 * q.x * q.z) +
                v.y * (2 * q.w * q.x + 2 * q.y * q.z) +
                v.z * (q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z);
//        Matrix4 mat = q.toMatrixUnit();
        return result;
    }

    public static Quaternion normalize(Quaternion q) {
        float len = q.length();
        q.x = q.x / len;
        q.y = q.y / len;
        q.z = q.y / len;
        q.w = q.w / len;
        return q;
    }

    public static Quaternion conjugate(Quaternion q) {
        q.x = -q.x;
        q.y = -q.y;
        q.z = -q.z;
        return q;
    }

    public static Quaternion fromAxes(Quaternion q, Vector3 x, Vector3 y, Vector3 z) {
        // Uses code from the method of the same name from:
        // https://code.google.com/p/jmonkeyengine/source/browse/branches/jme3/src/core/com/jme3/math/Quaternion.java?r=5231
        return Quaternion.fromRotationMatrix(q,
                x.x, y.x, z.x,
                x.y, y.y, z.y,
                x.z, y.z, z.z);
    }

    public static Quaternion fromRotationMatrix(Quaternion q,
                                                float m00,
                                                float m01,
                                                float m02,
                                                float m10,
                                                float m11,
                                                float m12,
                                                float m20,
                                                float m21,
                                                float m22) {
        // Uses code from the method of the same name from:
        // https://code.google.com/p/jmonkeyengine/source/browse/branches/jme3/src/core/com/jme3/math/Quaternion.java?r=5231
        float t = m00 + m11 + m22;

        // we protect the division by s by ensuring that s>=1
        if (t >= 0) { // |w| >= .5
            float s = (float)Math.sqrt(t+1); // |s|>=1 ...
            q.w = 0.5f * s;
            s = 0.5f / s;                 // so this division isn't bad
            q.x = (m21 - m12) * s;
            q.y = (m02 - m20) * s;
            q.z = (m10 - m01) * s;
        } else if ((m00 > m11) && (m00 > m22)) {
            float s = (float)Math.sqrt(1.0f + m00 - m11 - m22); // |s|>=1
            q.x = s * 0.5f; // |x| >= .5
            s = 0.5f / s;
            q.y = (m10 + m01) * s;
            q.z = (m02 + m20) * s;
            q.w = (m21 - m12) * s;
        } else if (m11 > m22) {
            float s = (float)Math.sqrt(1.0f + m11 - m00 - m22); // |s|>=1
            q.y = s * 0.5f; // |y| >= .5
            s = 0.5f / s;
            q.x = (m10 + m01) * s;
            q.z = (m21 + m12) * s;
            q.w = (m02 - m20) * s;
        } else {
            float s = (float)Math.sqrt(1.0f + m22 - m00 - m11); // |s|>=1
            q.z = s * 0.5f; // |z| >= .5
            s = 0.5f / s;
            q.x = (m02 + m20) * s;
            q.y = (m21 + m12) * s;
            q.w = (m10 - m01) * s;
        }

        return q;
    }

    public static Quaternion lookAt(Quaternion q, Vector3 direction, Vector3 up) {
        Vector3 vect3 = direction.normalize();
        Vector3 vect1 = Vector3.cross(up, direction).normalizeLocal();
        Vector3 vect2 = Vector3.cross(direction, vect1).normalizeLocal();
        Quaternion.fromAxes(q, vect1, vect2, vect3);
        Quaternion.normalize(q);
        return q;
    }

//    inline quaternion quaternion::Slerp(quaternion q1, quaternion q2, f32 time)
//    {
//        f32 angle = q1.getDotProduct(q2);
//
//        if (angle < 0.0f)
//        {
//            q1 *= -1.0f;
//            angle *= -1.0f;
//        }
//
//        f32 scale;
//        f32 invscale;
//
//        if ((angle + 1.0f) > 0.05f)
//        {
//            if ((1.0f - angle) < 0.05f)  // linear interploation
//            {
//                scale = 1.0f - time;
//                invscale = time;
//            }
//            else // spherical interpolation
//            {
//                f32 theta = (f32)acos(angle);
//                f32 invsintheta = 1.0f / (f32)sin(theta);
//                scale = (f32)sin(theta * (1.0f-time)) * invsintheta;
//                invscale = (f32)sin(theta * time) * invsintheta;
//            }
//        }
//        else
//        {
//            q2 = quaternion(-q1.Y, q1.X, -q1.W, q1.Z);
//            scale = (f32)sin(PI * (0.5f - time));
//            invscale = (f32)sin(PI * time);
//        }
//
//        *this = (q1*scale) + (q2*invscale);
//        return *this;
//    }
}

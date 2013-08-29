package structure.opengl;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 3/25/13
 * Time: 9:03 PM
 */
public class Matrix4 {
    public static final float GLMAT_EPSILON = 0.000001f;
    private static final FloatBuffer matrixOpBuff = BufferUtils.createFloatBuffer(16);
    private static final float[] matrixOpArr = new float[16];
    private static final Matrix4 tempMat = new Matrix4();
    private static final Vector3 tempVec = new Vector3();

    public float m00, m01, m02, m03,
        m10, m11, m12, m13,
        m20, m21, m22, m23,
        m30, m31, m32, m33;

    public Matrix4() {
        this(1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    public Matrix4(Matrix4 other) {
        this(other.m00, other.m01, other.m02, other.m03,
                other.m10, other.m11, other.m12, other.m13,
                other.m20, other.m21, other.m22, other.m23,
                other.m30, other.m31, other.m32, other.m33);
    }

    public Matrix4(float m00, float m01, float m02, float m03,
                   float m10, float m11, float m12, float m13,
                   float m20, float m21, float m22, float m23,
                   float m30, float m31, float m32, float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;

        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;

        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;

        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public Matrix4 scale(float x, float y, float z) {
        return Matrix4.scale(this, x, y, z, new Matrix4());
    }

    public Matrix4 scaleLocal(float x, float y, float z) {
        return Matrix4.scale(this, x, y, z, this);
    }

    public Matrix4 translate(float x, float y, float z) {
        return Matrix4.translate(this, x, y, z, new Matrix4());
    }

    public Matrix4 translateLocal(float x, float y, float z) {
        return Matrix4.translate(this, x, y, z, this);
    }

    public Matrix4 setIdentity() {
        this.m00 = 1;
        this.m01 = 0;
        this.m02 = 0;
        this.m03 = 0;

        this.m10 = 0;
        this.m11 = 1;
        this.m12 = 0;
        this.m13 = 0;

        this.m20 = 0;
        this.m21 = 0;
        this.m22 = 1;
        this.m23 = 0;

        this.m30 = 0;
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = 1;

        return this;
    }

    public void load(FloatBuffer buf) {
        // Column-major order for OpenGL.
        m00 = buf.get();
        m10 = buf.get();
        m20 = buf.get();
        m30 = buf.get();

        m01 = buf.get();
        m11 = buf.get();
        m21 = buf.get();
        m31 = buf.get();

        m02 = buf.get();
        m12 = buf.get();
        m22 = buf.get();
        m32 = buf.get();

        m03 = buf.get();
        m13 = buf.get();
        m23 = buf.get();
        m33 = buf.get();
    }

    public void store(FloatBuffer buf) {
        // Column-major order for OpenGL.
        buf.put(m00);
        buf.put(m10);
        buf.put(m20);
        buf.put(m30);

        buf.put(m01);
        buf.put(m11);
        buf.put(m21);
        buf.put(m31);

        buf.put(m02);
        buf.put(m12);
        buf.put(m22);
        buf.put(m32);

        buf.put(m03);
        buf.put(m13);
        buf.put(m23);
        buf.put(m33);
    }

    @Override
    public String toString() {
        return String.format("[\n%-8.2f %-8.2f %-8.2f %-8.2f\n" +
                "%-8.2f %-8.2f %-8.2f %-8.2f\n" +
                "%-8.2f %-8.2f %-8.2f %-8.2f\n" +
                "%-8.2f %-8.2f %-8.2f %-8.2f\n]",
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33);
    }

    /*
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
     */

    public static Matrix4 mult(Matrix4 A, Matrix4 B) {
        return Matrix4.mult(A, B, tempMat.setIdentity());
    }

    public static Matrix4 mult(Matrix4 A, Matrix4 B, Matrix4 result) {
        result.m00 = A.m00 * B.m00 + A.m01 * B.m10 + A.m02 * B.m20 + A.m03 * B.m30;
        result.m01 = A.m00 * B.m01 + A.m01 * B.m11 + A.m02 * B.m21 + A.m03 * B.m31;
        result.m02 = A.m00 * B.m02 + A.m01 * B.m12 + A.m02 * B.m22 + A.m03 * B.m32;
        result.m03 = A.m00 * B.m03 + A.m01 * B.m13 + A.m02 * B.m23 + A.m03 * B.m33;

        result.m10 = A.m10 * B.m00 + A.m11 * B.m10 + A.m12 * B.m20 + A.m13 * B.m30;
        result.m11 = A.m10 * B.m01 + A.m11 * B.m11 + A.m12 * B.m21 + A.m13 * B.m31;
        result.m12 = A.m10 * B.m02 + A.m11 * B.m12 + A.m12 * B.m22 + A.m13 * B.m32;
        result.m13 = A.m10 * B.m03 + A.m11 * B.m13 + A.m12 * B.m23 + A.m13 * B.m33;

        result.m20 = A.m20 * B.m00 + A.m21 * B.m10 + A.m22 * B.m20 + A.m23 * B.m30;
        result.m21 = A.m20 * B.m01 + A.m21 * B.m11 + A.m22 * B.m21 + A.m23 * B.m31;
        result.m22 = A.m20 * B.m02 + A.m21 * B.m12 + A.m22 * B.m22 + A.m23 * B.m32;
        result.m23 = A.m20 * B.m03 + A.m21 * B.m13 + A.m22 * B.m23 + A.m23 * B.m33;

        result.m30 = A.m30 * B.m00 + A.m31 * B.m10 + A.m32 * B.m20 + A.m33 * B.m30;
        result.m31 = A.m30 * B.m01 + A.m31 * B.m11 + A.m32 * B.m21 + A.m33 * B.m31;
        result.m32 = A.m30 * B.m02 + A.m31 * B.m12 + A.m32 * B.m22 + A.m33 * B.m32;
        result.m33 = A.m30 * B.m03 + A.m31 * B.m13 + A.m32 * B.m23 + A.m33 * B.m33;

        return result;
    }

    public static Vector3 mult(Matrix4 mat, Vector3 v) {
        return Matrix4.mult(mat, v, tempVec.set(0, 0, 0));
    }

    public static Vector3 mult(Matrix4 mat, Vector3 v, Vector3 result) {
        result.x = mat.m00 * v.x + mat.m01 * v.y + mat.m02 * v.z + mat.m03;
        result.y = mat.m10 * v.x + mat.m11 * v.y + mat.m12 * v.z + mat.m13;
        result.z = mat.m20 * v.x + mat.m21 * v.y + mat.m22 * v.z + mat.m23;

        return result;
    }

    public static Matrix4 scale(Matrix4 mat, float x, float y, float z, Matrix4 result) {
        tempMat.setIdentity();
        tempMat.m00 = x;
        tempMat.m11 = y;
        tempMat.m22 = z;
        Matrix4.mult(tempMat, mat, result);
        return result;
    }

    public static Matrix4 translate(Matrix4 mat, float x, float y, float z, Matrix4 result) {
        tempMat.setIdentity();
        tempMat.m30 = x;
        tempMat.m31 = y;
        tempMat.m32 = z;
        Matrix4.mult(tempMat, mat, result);
        return result;
    }

    public static Matrix4 rotate(Matrix4 mat, float x, float y, float z, Matrix4 result) {
        x = (float)Math.toRadians(x);
        y = (float)Math.toRadians(y);
        z = (float)Math.toRadians(z);
        float A = (float)Math.cos(x);
        float B = (float)Math.sin(x);
        float C = (float)Math.cos(y);
        float D = (float)Math.sin(y);
        float E = (float)Math.cos(z);
        float F = (float)Math.sin(z);

        float AD = A * D;
        float BD = B * D;

        tempMat.m00 = C * E;
        tempMat.m01 = -C * F;
        tempMat.m02 = D;
        tempMat.m03 = 0;

        tempMat.m10 = BD * E + A * F;
        tempMat.m11 = -BD * F + A * E;
        tempMat.m12 = -B * C;
        tempMat.m13 = 0;

        tempMat.m20 = -AD * E + B * F;
        tempMat.m21 = AD * F + B * E;
        tempMat.m22 = A * C;
        tempMat.m23 = 0;

        tempMat.m30 = 0;
        tempMat.m31 = 0;
        tempMat.m32 = 0;
        tempMat.m33 = 0;

        Matrix4.mult(tempMat, mat, result);

        return result;
    }

    public static Matrix4 transpose(Matrix4 mat, Matrix4 result) {
        result.m00 = mat.m00;
        result.m01 = mat.m10;
        result.m02 = mat.m20;
        result.m03 = mat.m30;

        result.m10 = mat.m01;
        result.m11 = mat.m11;
        result.m12 = mat.m21;
        result.m13 = mat.m31;

        result.m20 = mat.m02;
        result.m21 = mat.m12;
        result.m22 = mat.m22;
        result.m23 = mat.m32;

        result.m30 = mat.m03;
        result.m31 = mat.m13;
        result.m32 = mat.m23;
        result.m33 = mat.m33;

        return result;
    }

    public static Matrix4 lookAt(Matrix4 mat, Vector3 eye, Vector3 center, Vector3 up) {
        double x0, x1, x2, y0, y1, y2, z0, z1, z2, len,
                eyex = eye.x, eyey = eye.y, eyez = eye.z,
                upx = up.x, upy = up.y, upz = up.z,
                centerx = center.x, centery = center.y, centerz = center.z;

        if (Math.abs(eyex - centerx) < GLMAT_EPSILON &&
                Math.abs(eyey - centery) < GLMAT_EPSILON &&
                Math.abs(eyez - centerz) < GLMAT_EPSILON) {
            return mat.setIdentity();
        }

        z0 = eyex - centerx;
        z1 = eyey - centery;
        z2 = eyez - centerz;

        len = 1 / Math.sqrt(z0 * z0 + z1 * z1 + z2 * z2);
        z0 *= len;
        z1 *= len;
        z2 *= len;

        x0 = upy * z2 - upz * z1;
        x1 = upz * z0 - upx * z2;
        x2 = upx * z1 - upy * z0;
        len = Math.sqrt(x0 * x0 + x1 * x1 + x2 * x2);
        if (Double.isNaN(len)) {
            x0 = 0;
            x1 = 0;
            x2 = 0;
        } else {
            len = 1 / len;
            x0 *= len;
            x1 *= len;
            x2 *= len;
        }

        y0 = z1 * x2 - z2 * x1;
        y1 = z2 * x0 - z0 * x2;
        y2 = z0 * x1 - z1 * x0;

        len = Math.sqrt(y0 * y0 + y1 * y1 + y2 * y2);
        if (Double.isNaN(len)) {
            y0 = 0;
            y1 = 0;
            y2 = 0;
        } else {
            len = 1 / len;
            y0 *= len;
            y1 *= len;
            y2 *= len;
        }

        /*
        TODO: Refactor to just use mat directly.
         */
        matrixOpArr[0] = (float)x0;
        matrixOpArr[1] = (float)y0;
        matrixOpArr[2] = (float)z0;
        matrixOpArr[3] = 0;
        matrixOpArr[4] = (float)x1;
        matrixOpArr[5] = (float)y1;
        matrixOpArr[6] = (float)z1;
        matrixOpArr[7] = 0;
        matrixOpArr[8] = (float)x2;
        matrixOpArr[9] = (float)y2;
        matrixOpArr[10] = (float)z2;
        matrixOpArr[11] = 0;
        matrixOpArr[12] = (float)-(x0 * eyex + x1 * eyey + x2 * eyez);
        matrixOpArr[13] = (float)-(y0 * eyex + y1 * eyey + y2 * eyez);
        matrixOpArr[14] = (float)-(z0 * eyex + z1 * eyey + z2 * eyez);
        matrixOpArr[15] = 1;
        matrixOpBuff.put(matrixOpArr);
        matrixOpBuff.flip();
        mat.load(matrixOpBuff);
        matrixOpBuff.flip();

        return mat;
    }

    public static Matrix4 perspective(Matrix4 outm, float fov, float near, float far) {
        float frustumScale = (float)(1.0 / Math.tan(Math.toRadians(fov) / 2.0));

        outm.m00 = frustumScale;
        outm.m01 = 0;
        outm.m02 = 0;
        outm.m03 = 0;

        outm.m10 = 0;
        outm.m11 = frustumScale;
        outm.m12 = 0;
        outm.m13 = 0;

        outm.m20 = 0;
        outm.m21 = 0;
        outm.m22 = (far + near) / (near - far);
        outm.m23 = -1.0f;

        outm.m30 = 0;
        outm.m31 = 0;
        outm.m32 = (2 * near * far) / (near - far);
        outm.m33 = 0;

        return outm;
    }

    public static Matrix4 orthographic(Matrix4 out, float left, float right, float bottom, float top, float near, float far) {
        out.m00 = 2.0f / (right - left);
        out.m10 = 0.0f;
        out.m20 = 0.0f;
        out.m30 = -(right - left) / (right - left);

        out.m01 = 0.0f;
        out.m11 = 2.0f / (top - bottom);
        out.m21 = 0.0f;
        out.m31 = -(top - bottom) / (top - bottom);

        out.m02 = 0.0f;
        out.m12 = 0.0f;
        out.m22 = -2.0f / (far - near);
        out.m32 = -(far + near) / (far - near);

        out.m03 = 0.0f;
        out.m13 = 0.0f;
        out.m23 = 0.0f;
        out.m33 = 1.0f;

        return out;
    }
}

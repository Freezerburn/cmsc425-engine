package stuff;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import structure.opengl.Matrix4;
import structure.opengl.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 12/29/12
 * Time: 10:51 PM
 */
public class Utils {
    protected static final float fastNorm_a = (float)((1 + Math.sqrt(4 - 2 * Math.sqrt(2))) / 2.0);
    protected static final float fastNorm_b = (float)Math.sqrt(0.5);
    private static final float[] matrixOpArr = new float[16];
    private static final FloatBuffer matrixOpBuff = BufferUtils.createFloatBuffer(16);
    public static final Random random = new Random(System.nanoTime());
    public static final float GLMAT_EPSILON = 0.000001f;

    // Warning: 0.5 - 1.5% inaccuracy.
    public static float fastSqrt(float x) {
        return Float.intBitsToFloat(532483686 + (Float.floatToRawIntBits(x) >> 1));
    }

    //Approximates sqrt(x^2 + y^2) with a maximum error of 4%.
    public static float fastNorm(float x, float y) {
        x = Math.abs(x);
        y = Math.abs(y);
        float metricOne = Math.max(x, y);
        float metricTwo = fastNorm_b * (x + y);
        return fastNorm_a * Math.max(metricOne, metricTwo);
    }

    public static String convertToActualDir(String dir) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            String fileName = System.getProperty("user.dir");
            fileName = fileName.substring(2);
            return fileName + "\\" + dir;
        } else {
            return System.getProperty("user.dir") + "/" + dir;
        }
    }

    public static BufferedImage loadImage(String file) {
        BufferedImage image = null;
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                String fileName = System.getProperty("user.dir");
                fileName = fileName.substring(2);
                fileName = "file:" + fileName + "\\" + file;
                image = ImageIO.read(new URL(fileName));
            } else {
                String fileName = "file:" + System.getProperty("user.dir") + "/" + file;
                image = ImageIO.read(new URL(fileName));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return image;
    }

    public static String fileToString(String dir) {
        dir = convertToActualDir(dir);
        BufferedReader reader = null;
        StringBuilder ret = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(dir));
            char[] buf = new char[1024];
            int numRead;
            while((numRead = reader.read(buf)) != -1) {
                ret.append(buf);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret.toString();
    }

    public static Matrix4 lookAt(Matrix4 outm, Vector3 eye, Vector3 center, Vector3 up) {
        double x0, x1, x2, y0, y1, y2, z0, z1, z2, len,
            eyex = eye.x, eyey = eye.y, eyez = eye.z,
            upx = up.x, upy = up.y, upz = up.z,
            centerx = center.x, centery = center.y, centerz = center.z;

        if (Math.abs(eyex - centerx) < GLMAT_EPSILON &&
                Math.abs(eyey - centery) < GLMAT_EPSILON &&
                Math.abs(eyez - centerz) < GLMAT_EPSILON) {
            return outm.setIdentity();
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
        outm.load(matrixOpBuff);
        matrixOpBuff.flip();

        return outm;
    }

    public static Matrix4 perspective(Matrix4 outm, float fovy, float aspect, float near, float far) {
        double f = 1.0 / Math.tan(fovy / 2),
                nf = 1 / (near - far);
        matrixOpArr[0] = (float)f / aspect;
        matrixOpArr[1] = 0;
        matrixOpArr[2] = 0;
        matrixOpArr[3] = 0;
        matrixOpArr[4] = 0;
        matrixOpArr[5] = (float)f;
        matrixOpArr[6] = 0;
        matrixOpArr[7] = 0;
        matrixOpArr[8] = 0;
        matrixOpArr[9] = 0;
        matrixOpArr[10] = (float)((far + near) * nf);
        matrixOpArr[11] = -1;
        matrixOpArr[12] = 0;
        matrixOpArr[13] = 0;
        matrixOpArr[14] = (float)((2 * far * near) * nf);
        matrixOpArr[15] = 0;
        matrixOpBuff.put(matrixOpArr);
        matrixOpBuff.flip();
        outm.load(matrixOpBuff);
        matrixOpBuff.flip();

        return outm;
    }
}

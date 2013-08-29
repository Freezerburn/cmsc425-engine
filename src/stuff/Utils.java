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

    public static float roundTo(float num, int decimals) {
        float power = (float)Math.pow(10, decimals);
        num *= power;
        num = (float)Math.floor(num);
        num /= power;
        return num;
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
                ret.append(buf, 0, numRead);
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

}

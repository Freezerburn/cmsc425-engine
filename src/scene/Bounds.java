package scene;

import structure.opengl.Vector3;
import stuff.Utils;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/19/13
 * Time: 7:45 PM
 */
public class Bounds {
    private static final int INITIAL_CACHED = 1000;
    private static final LinkedList<Bounds> recycled = new LinkedList<Bounds>();

    static {
        for(int i = 0; i < INITIAL_CACHED; i++) {
            recycled.push(new Bounds(Vector3.ZERO, 1.0f, 1.0f, 0.0f));
        }
    }

    public static Bounds get() {
        return get(Vector3.ZERO, 0.0f, 0.0f, 0.0f);
    }

    public static Bounds get(float width, float height) {
        return get(Vector3.ZERO, width, 0.0f, height);
    }

    public static Bounds get(Vector3 center, float width, float height) {
        return get(center, width, 0.0f, height);
    }

    public static Bounds get(Bounds bounds) {
        return get(bounds.getCenter(), bounds.getWidth(), bounds.getHeight(), bounds.getDepth());
    }

    public static Bounds get(Vector3 center, float width, float height, float depth) {
        if(!recycled.isEmpty()) {
            final Bounds ret = recycled.pop();
            ret.reset(center, width, height, depth);
            return ret;
        }
        return new Bounds(center, width, height, depth);
    }

    public static void recycle(Bounds b) {
        if(recycled.size() > INITIAL_CACHED) {
            double result = Math.abs(Utils.random.nextGaussian());
            if(result < 0.1) {
                recycled.push(b);
            }
        }
        else {
            recycled.push(b);
        }
    }

    /* ====== STATIC METHODS END ====== */

    protected Vector3 leftTopBackCorner, rightBottomFrontCorner, center;
    protected Vector3 size;

    protected Bounds(Vector3 center, float width, float height, float depth) {
        reset(center, width, height, depth);
    }

    protected void reset(Vector3 center, float width, float height, float depth) {
//        System.out.println("Center: " + view);
//        System.out.println(String.format("w: %f, h: %f, d: %f", width, height, depth));
        float left = width == 0.0f ? center.x : center.x - width / 2.0f;
        float right = width == 0.0f ? center.x : center.x + width / 2.0f;
        float front = depth == 0.0f ? center.y : center.y - depth / 2.0f;
        float back = depth == 0.0f ? center.y : center.y + depth / 2.0f;
        float top = height == 0.0f ? center.z : center.z + height / 2.0f;
        float bottom = height == 0.0f ? center.z : center.z - height / 2.0f;
        this.leftTopBackCorner = Vector3.get(left, top, back);
        this.rightBottomFrontCorner = Vector3.get(right, bottom, front);
//        System.out.println("LTB: " + leftTopBackCorner + ", RBF: " + rightBottomFrontCorner);
        this.center = center;
        this.size = Vector3.get(width, depth, height);
    }

    public void move(float x, float y, float z) {
        leftTopBackCorner.x += x;
        leftTopBackCorner.y += y;
        leftTopBackCorner.x += z;
        rightBottomFrontCorner.x += x;
        rightBottomFrontCorner.y += y;
        rightBottomFrontCorner.z += z;
    }

    public void moveTo(float x, float y, float z) {
        float left = x - size.x / 2.0f;
        float right = x + size.x / 2.0f;
        float top = y + size.y / 2.0f;
        float bottom = y - size.y / 2.0f;
        float front = size.z == 0.0f ? z - size.z / 2.0f : z;
        float back = size.z == 0.0f ? z + size.z / 2.0f : z;
    }

    public void setCenter(Vector3 v) {
        moveTo(v.x, v.y, v.z);
        center.x = v.x;
        center.y = v.y;
        center.z = v.z;
    }

    public Vector3 getCenter() {
        return Vector3.get(center);
    }

    public float getWidth() {
        return Math.abs(rightBottomFrontCorner.x - leftTopBackCorner.x);
    }

    public float getHeight() {
        return Math.abs(rightBottomFrontCorner.y - leftTopBackCorner.y);
    }

    public float getDepth() {
        return Math.abs(rightBottomFrontCorner.z - leftTopBackCorner.y);
    }

    public float getLeft() {
        return leftTopBackCorner.x;
    }

    public float getTop() {
        return leftTopBackCorner.y;
    }

    public float getBack() {
        return leftTopBackCorner.z;
    }

    public float getRight() {
        return rightBottomFrontCorner.x;
    }

    public float getBottom() {
        return rightBottomFrontCorner.y;
    }

    public float getFront() {
        return rightBottomFrontCorner.z;
    }

    public boolean intersects(Bounds other) {
        System.out.println(String.format("Checking bounds:\n" +
                "\tLvR - %f > %f == RvL - %f < %f\n" +
                "\tTvB - %f < %f == BvT - %f < %f\n" +
                "\tFvB - %f > %f == BvF - %f < %f",
                other.getLeft(), getRight(), other.getRight(), getLeft(),
                other.getTop(), getBottom(), other.getBottom(), getBottom(),
                other.getFront(), getBack(), other.getBack(), getFront()));
        if(other.getLeft() > getRight()) return false;
        if(other.getRight() < getLeft()) return false;
        if(other.getTop() < getBottom()) return false;
        if(other.getBottom() > getTop()) return false;
        if(other.getFront() > getBack()) return false;
        if(other.getBack() < getFront()) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Bounds{LTB: " + leftTopBackCorner + "RBF: " + rightBottomFrontCorner + "}";
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        else if(o instanceof Bounds) {
            Bounds b = (Bounds)o;
            return b.leftTopBackCorner.equals(leftTopBackCorner) &&
                    b.rightBottomFrontCorner.equals(rightBottomFrontCorner);
        }
        return false;
    }
}

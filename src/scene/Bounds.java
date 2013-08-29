package scene;

import structure.geometries.Cube;
import structure.opengl.Vector3;
import stuff.TempVars;
import stuff.Utils;

import java.util.LinkedList;
import java.util.List;

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
            ret.set(center, width, height, depth);
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
    protected Vector3 size, vel;

    public Bounds(Vector3 center, float width, float height, float depth) {
        set(center, width, height, depth);
    }

    public Bounds(Vector3 center, Vector3 size) {
        set(center, size);
    }

    public Bounds set(Vector3 center, float width, float height, float depth) {
//        System.out.println("Center: " + view);
//        System.out.println(String.format("w: %f, h: %f, d: %f", width, height, depth));
        float left = width == 0.0f ? center.x : center.x - width / 2.0f;
        float right = width == 0.0f ? center.x : center.x + width / 2.0f;
        float front = depth == 0.0f ? center.z : center.z - depth / 2.0f;
        float back = depth == 0.0f ? center.z : center.z + depth / 2.0f;
        float top = height == 0.0f ? center.y : center.y + height / 2.0f;
        float bottom = height == 0.0f ? center.y : center.y - height / 2.0f;
        this.leftTopBackCorner = Vector3.get(left, top, back);
        this.rightBottomFrontCorner = Vector3.get(right, bottom, front);
//        System.out.println("LTB: " + leftTopBackCorner + ", RBF: " + rightBottomFrontCorner);
        this.center = center;
        this.size = Vector3.get(width, depth, height);
        this.vel = new Vector3();
        return this;
    }

    public Bounds set(Vector3 center, Vector3 size) {
        return set(center, size.x, size.y, size.z);
    }

    public Bounds moveLocal(float x, float y, float z) {
        leftTopBackCorner.x += x;
        leftTopBackCorner.y += y;
        leftTopBackCorner.z += z;
        rightBottomFrontCorner.x += x;
        rightBottomFrontCorner.y += y;
        rightBottomFrontCorner.z += z;
        vel.set(x, y, z);
        return this;
    }

    public void moveToLocal(float x, float y, float z) {
        float left = x - size.x / 2.0f;
        float right = x + size.x / 2.0f;
        float top = y + size.y / 2.0f;
        float bottom = y - size.y / 2.0f;
        float front = size.z == 0.0f ? z - size.z / 2.0f : z;
        float back = size.z == 0.0f ? z + size.z / 2.0f : z;
    }

    public void setCenterLocal(Vector3 v) {
        moveToLocal(v.x, v.y, v.z);
        center.x = v.x;
        center.y = v.y;
        center.z = v.z;
    }

    public Vector3 getVelocity() {
        return new Vector3(vel);
    }

    public Vector3 getVelocity(Vector3 dest) {
        return dest.set(vel);
    }

    public Vector3 getCenter() {
        return getCenter(new Vector3());
    }

    public Vector3 getCenter(Vector3 dest) {
        getSize(dest);
        return dest.set(rightBottomFrontCorner.x - dest.x * 0.5f,
                rightBottomFrontCorner.y + dest.y * 0.5f,
                rightBottomFrontCorner.z + dest.z * 0.5f);
    }

    public float getWidth() {
        return Math.abs(rightBottomFrontCorner.x - leftTopBackCorner.x);
    }

    public float getHeight() {
        return Math.abs(rightBottomFrontCorner.y - leftTopBackCorner.y);
    }

    public float getDepth() {
        return Math.abs(rightBottomFrontCorner.z - leftTopBackCorner.z);
    }

    public Vector3 getSize() {
        return new Vector3(getWidth(), getHeight(), getDepth());
    }

    public Vector3 getSize(Vector3 dest) {
        return dest.set(getWidth(), getHeight(), getDepth());
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
                "\tFvB - %f < %f == BvF - %f > %f",
                other.getLeft(), getRight(), other.getRight(), getLeft(),
                other.getTop(), getBottom(), other.getBottom(), getBottom(),
                other.getFront(), getBack(), other.getBack(), getFront()));
        if(other.getLeft() > getRight()) return false;
        if(other.getRight() < getLeft()) return false;
        if(other.getTop() < getBottom()) return false;
        if(other.getBottom() > getTop()) return false;
        if(other.getFront() < getBack()) return false;
        if(other.getBack() > getFront()) return false;
        return true;
    }

//    protected static boolean AABBCollision( Entity e1, Entity e2, Vector2 normal ) {
//        Vector2 absDistance = new Vector2();
//        double xMag = 0.0;
//        double yMag = 0.0;
//        Vector2 distance = e2.middlePosition().subtract( e1.middlePosition() );
//        double e1Width = e1.right() - e1.left();
//        double e1Height = e1.top() - e1.bottom();
//        double e2Width = e2.right() - e2.right();
//        double e2Height = e2.top() - e2.bottom();
//        double xAdd = ( e1Width + e2Width ) / 2.0;
//        double yAdd = ( e1Height + e2Height ) / 2.0;
//        absDistance.x = Math.abs( distance.x );
//        absDistance.y = Math.abs( distance.y );
//        if( !( absDistance.x < xAdd && absDistance.y < yAdd ) ) {
//            return false;
//        }
//        xMag = xAdd - absDistance.x;
//        yMag = yAdd - absDistance.y;
//        if( xMag < yMag ) {
//            normal.x = distance.x > 0 ? -xMag : xMag;
//        }
//        else {
//            normal.y = distance.y > 0 ? -yMag : yMag;
//        }
//        return true;
//    }

    public boolean getIntersectionDelta(Bounds other, Vector3 dest) {
        dest.set(0, 0, 0);
        TempVars vars = TempVars.get();
        Vector3 absDistance;
        if(dest == vars.vect1) {
            absDistance = vars.vect2.set(0, 0, 0);
        }
        else {
            absDistance = vars.vect1.set(0, 0, 0);
        }
        float xMag = 0.0f;
        float yMag = 0.0f;
        float zMag = 0.0f;
        Vector3 distance = other.getCenter().sub(getCenter());
        float xAdd = (getWidth() + other.getWidth()) / 2.0f;
        float yAdd = (getHeight() + other.getHeight()) / 2.0f;
        float zAdd = (getDepth() + other.getDepth()) / 2.0f;
        absDistance.x = Math.abs(distance.x);
        absDistance.y = Math.abs(distance.y);
        absDistance.z = Math.abs(distance.z);
        System.out.println("CENTERS: " + getCenter() + " == " + other.getCenter());
        System.out.println("ABS: " + absDistance + ", " + xAdd + ", " + yAdd + ", " + zAdd);
        if(!(absDistance.x < xAdd && absDistance.y < yAdd && absDistance.z < zAdd)) {
            return false;
        }

        xMag = xAdd - absDistance.x;
        yMag = yAdd - absDistance.y;
        zMag = zAdd - absDistance.z;
        vars.release();
        if(xMag < yMag && xMag < zMag) {
            dest.x = distance.x > 0 ? -xMag : xMag;
        }
        else if(yMag < xMag && yMag < zMag) {
            dest.y = distance.y > 0 ? -yMag : yMag;
        }
        else {
            dest.z = distance.z > 0 ? -zMag : zMag;
        }
        return true;
    }

    public boolean getSweepingAABB(Bounds other, Vector3 result) {
//        result.set(0, 0, 0);
        TempVars vars = TempVars.get();
        Vector3 VA = getVelocity();
        Vector3 VB = other.getVelocity();
        Vector3 V = Vector3.sub(VA, VB);
        Vector3 B = other.getCenter();
        Vector3 A = getCenter();
        Vector3 HSB = new Vector3(other.getWidth(), other.getHeight(), other.getDepth()).multLocal(0.5f);
        Vector3 HSA = new Vector3(getWidth(), getHeight(), getDepth()).multLocal(0.5f);

        float DX = (B.x - HSB.x) - (A.x + HSA.x);
        float DY = (B.y - HSB.y) - (A.y + HSA.y);
        float DZ = (B.z - HSB.z) - (A.z + HSA.z);
        float U0x = V.x != 0 ? DX / V.x : 0;
        float U0y = V.y != 0 ? DY / V.y : 0;
        float U0z = V.z != 0 ? DZ / V.z : 0;
        Vector3 U0 = new Vector3(U0x, U0y, U0z);
        float T0 = Vector3.max(U0);

        float DX2 = (B.x + HSB.x) - (A.x - HSA.x);
        float DY2 = (B.y + HSB.y) - (A.y - HSA.y);
        float DZ2 = (B.z + HSB.z) - (A.z - HSA.z);
        float U1x = V.x != 0 ? DX2 / V.x : 1;
        float U1y = V.y != 0 ? DY2 / V.y : 1;
        float U1z = V.z != 0 ? DZ2 / V.z : 1;
        Vector3 U1 = new Vector3(U1x, U1y, U1z);
        float T1 = Vector3.min(U1);

        System.out.println("VEL: " + V);
        System.out.println("DX1/2: " + DX + ", " + DX2);
        System.out.println("DY1/2: " + DY + ", " + DY2);
        System.out.println("VA/B: " + VA + ", " + VB);
        System.out.println("U0/1: "+ U0 + " |||| " + U1);
        System.out.println("T0/1: " + T0 + ", " + T1);
        boolean C1 = T0 < T1;
        boolean C2 = T0 >= 0 && T0 <= 1 && T1 >= 0 && T1 <= 1;
        if(!(C1 && C2)) {
            vars.release();
            result.set(0, 0, 0);
            return false;
        }

        Vector3 CPA = A.add(Vector3.mult(getVelocity(vars.vect1), T0, vars.vect1));
        Vector3 CPB = B.add(Vector3.mult(other.getVelocity(vars.vect2), T0, vars.vect2));

        vars.release();
        result.set(CPA);
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

    /*
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
    ===================================================================================================================
     */

    public static boolean getOBBIntersection(Cube g1, Cube g2) {
        List<Vector3> normals1 = g1.getNormals();
        List<Vector3> normals2 = g2.getNormals();
        System.out.println(normals1);
        System.out.println(normals2);
        List<Vector3> vertices1 = g1.getVertices();
        List<Vector3> vertices2 = g2.getVertices();
        System.out.println(vertices1);
        System.out.println(vertices2);

        float min1 = 0, min2 = 0, max1 = 0, max2 = 0;
        for(Vector3 normal : normals1) {
            min1 = Vector3.dot(normal, vertices1.get(0));
            max1 = min1;
            min2 = Vector3.dot(normal, vertices2.get(0));
            max2 = min2;
            for(int i = 1; i< vertices1.size(); i++) {
                float p = Vector3.dot(normal, vertices1.get(i));
                if(p < min1) {
                    p = min1;
                }
                else if(p > max1) {
                    p = max1;
                }
            }
            for(int i = 1; i< vertices2.size(); i++) {
                float p = Vector3.dot(normal, vertices2.get(i));
                if(p < min2) {
                    p = min2;
                }
                else if(p > max2) {
                    p = max2;
                }
            }
        }
        System.out.println("Min/Max1: " + min1 + ", " + max1);
        System.out.println("Min/Max2: " + min2 + ", " + max2);
        if(max2 < min1 || max1 < min2) {
            return false;
        }
        for(Vector3 normal : normals2) {
            min1 = Vector3.dot(normal, vertices1.get(0));
            max1 = min1;
            min2 = Vector3.dot(normal, vertices2.get(0));
            max2 = min2;
            for(int i = 1; i< vertices1.size(); i++) {
                float p = Vector3.dot(normal, vertices1.get(i));
                if(p < min1) {
                    p = min1;
                }
                else if(p > max1) {
                    p = max1;
                }
            }
            for(int i = 1; i< vertices2.size(); i++) {
                float p = Vector3.dot(normal, vertices2.get(i));
                if(p < min2) {
                    p = min2;
                }
                else if(p > max2) {
                    p = max2;
                }
            }
        }
        System.out.println("Min/Max1: " + min1 + ", " + max1);
        System.out.println("Min/Max2: " + min2 + ", " + max2);
        if(max2 < min1 || max1 < min2) {
            return false;
        }
        return true;
    }

    public static boolean getAABBIntersection(Bounds b1, Bounds b2, Vector3 delta) {
        Vector3 center1 = b1.getCenter();
        Vector3 center2 = b2.getCenter();
        Vector3 size1 = b1.getSize();
        Vector3 size2 = b2.getSize();
        Vector3 halfSize1 = size1.mult(0.5f);
        Vector3 halfSize2 = size2.mult(0.5f);

        Vector3 directions = center2.sub(center1).normalizeLocal();
        Vector3 overlap1 = new Vector3(b2.getLeft() - b1.getRight(),
                b2.getBottom() - b1.getTop(),
                b2.getFront() - b1.getBack());
//        Vector3 overlap2 = new Vector3(b1.getRight() - b2.getLeft(),
        Vector3 overlap2 = new Vector3(b1.getLeft() - b2.getRight(),
                b1.getBottom() - b2.getTop(),
                b1.getFront() - b2.getBack());
//        System.out.println("DIRECTIONS: " + directions);
//        System.out.println("OVERLAP1: " + overlap1);
//        System.out.println("OVERLAP2: " + overlap2);

        Vector3 actualOverlap = new Vector3();
        if(directions.x > 0) {
            actualOverlap.x = overlap1.x;
        }
        else {
            actualOverlap.x = overlap2.x;
        }
        if(directions.y > 0) {
            actualOverlap.y = overlap1.y;
        }
        else {
            actualOverlap.y = overlap2.y;
        }
        if(directions.z > 0) {
            actualOverlap.z = overlap1.z;
        }
        else {
            actualOverlap.z = overlap2.z;
        }
//        System.out.println("ACTUAL: " + actualOverlap);
        if(actualOverlap.x > actualOverlap.y && actualOverlap.x > actualOverlap.z) {
            if(actualOverlap.x > 0) {
                delta.set(0, 0, 0);
                return false;
            }
            else {
                delta.set(Math.signum(directions.x) * actualOverlap.x, 0, 0);
            }
        }
        else if(actualOverlap.y > actualOverlap.z) {
            if(actualOverlap.y > 0) {
                delta.set(0, 0, 0);
                return false;
            }
            else {
                delta.set(0, Math.signum(directions.y) * actualOverlap.y, 0);
            }
        }
        else {
            if(actualOverlap.z > 0) {
                delta.set(0, 0, 0);
                return false;
            }
            else {
                delta.set(0, 0, Math.signum(directions.z) * actualOverlap.z);
            }
        }
//        System.out.println("DELTA: " + delta);
//        System.out.println();

        return true;
//        Vector3 center1 = b1.getCenter();
//        Vector3 center2 = b2.getCenter();
//        Vector3 halfSize1 = b1.getSize().mult(0.5f);
//        Vector3 halfSize2 = b2.getSize().mult(0.5f);
//
//        Vector3 length = center2.sub(center1);
//        Vector3 gapBetweenBoxes = length.sub(halfSize1).sub(halfSize2);
//        float leastGap = Vector3.min(gapBetweenBoxes);
//        if(leastGap < 0) {
//            Vector3 absGap = new Vector3(Math.abs(gapBetweenBoxes.x),
//                    Math.abs(gapBetweenBoxes.y),
//                    Math.abs(gapBetweenBoxes.z));
//            System.out.println("GAP: " + gapBetweenBoxes);
//
//            delta.x = gapBetweenBoxes.x;
//            return true;
//        }
//
//        return false;
    }
}

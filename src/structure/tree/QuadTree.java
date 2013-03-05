package structure.tree;

import scene.Bounds;
import scene.Vector3;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/21/13
 * Time: 10:01 AM
 */
public class QuadTree implements  NTreeBasic<Collidable<Bounds>> {
    protected NTree<Bounds> tree;

    @Override
    public void add(Collidable<Bounds> bounds) {
        tree.add(new QuadTreeCollidable(bounds.get()));
    }

    @Override
    public void remove(Collidable<Bounds> bounds) {
        tree.remove(new QuadTreeCollidable(bounds.get()));
    }

    @Override
    public List<Collidable<Bounds>> getIntersecting(Collidable<Bounds> bounds) {
        return tree.getIntersecting(new QuadTreeCollidable(bounds.get()));
    }

    protected class QuadTreeCollidable implements Collidable<Bounds> {
        protected Bounds bounds;

        public QuadTreeCollidable(float width, float height) {
            bounds = Bounds.get(Vector3.get(width / 2.0f, height / 2.0f, 0.0f), width, height);
        }

        public QuadTreeCollidable(Bounds bounds) {
            this.bounds = bounds;
        }

        @Override
        public boolean collidesWith(Collidable<Bounds> c) {
            return bounds.intersects(c.get());
        }

        @Override
        public Bounds get() {
            return bounds;
        }

        @Override
        public boolean equals(Object o) {
            if(o == this) {
                return true;
            }
            else if(o instanceof QuadTreeCollidable) {
                QuadTreeCollidable qtc = (QuadTreeCollidable)o;
                return qtc.bounds.equals(bounds);
            }
            return false;
        }

        @Override
        public String toString() {
            return get().toString();
        }
    }

    public QuadTree(final float width, final float height) {
        tree = new NTree<Bounds>(4, 4, new QuadTreeCollidable(width, height),
                new CollidableDivisor<Bounds>() {
                    @Override
                    public void divide(Collidable<Bounds> start, List<Collidable<Bounds>> l) {
                        int diff = 4 - l.size();
                        for(int i = 0; i < diff; i++) {
                            l.add(new QuadTreeCollidable(0.0f, 0.0f));
                        }
                        Bounds boundsTL = l.get(0).get();
                        Bounds boundsTR = l.get(1).get();
                        Bounds boundsBL = l.get(2).get();
                        Bounds boundsBR = l.get(3).get();
                        Bounds startBounds = start.get();
                        Vector3 startCenter = startBounds.getCenter();
                        float halfWidth = startBounds.getWidth() / 2.0f;
                        float halfHeight = startBounds.getHeight() / 2.0f;
                        Vector3 temp = Vector3.get(startCenter.x - halfWidth / 2.0f, startCenter.y + halfHeight / 2.0f);
                        boundsTL.setCenter(temp);
                        temp.x = startCenter.x + halfWidth;
                        boundsTR.setCenter(temp);
                        temp.y -= halfHeight;
                        boundsBR.setCenter(temp);
                        temp.x -= halfWidth;
                        boundsBL.setCenter(temp);
                        Vector3.recycle(startCenter);
                        Vector3.recycle(temp);
                    }
                });
    }
}

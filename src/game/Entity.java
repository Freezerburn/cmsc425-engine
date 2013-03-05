package game;

import scene.Bounds;
import structure.tree.Collidable;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/21/13
 * Time: 5:41 PM
 */
public abstract class Entity implements Collidable<Bounds> {
    protected Bounds bounds;

    public final void destroy() {
        // TODO
    }
    protected abstract void onDestroy();

    @Override
    public boolean collidesWith(Collidable<Bounds> c) {
        return c.get().intersects(bounds);
    }

    @Override
    public Bounds get() {
        return Bounds.get(bounds);
    }
}

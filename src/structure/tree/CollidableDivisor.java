package structure.tree;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/21/13
 * Time: 2:58 PM
 */
public interface CollidableDivisor<T> {
    public void divide(Collidable<T> t, List<Collidable<T>> ts);
}

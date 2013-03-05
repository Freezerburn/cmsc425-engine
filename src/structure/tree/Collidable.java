package structure.tree;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/21/13
 * Time: 10:04 AM
 */
public interface Collidable<T> {
    public boolean collidesWith(Collidable<T> c);
    public T get();
}

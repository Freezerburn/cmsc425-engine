package structure.tree;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/21/13
 * Time: 2:33 PM
 */
public interface NTreeBasic<T> {
    public void add(T t);
    public void remove(T t);
    public List<T> getIntersecting(T t);
}

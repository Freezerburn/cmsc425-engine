package stuff;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/9/13
 * Time: 12:11 PM
 */
public class ThreeTuple<T, K, J> {
    public T one;
    public K two;
    public J three;

    public ThreeTuple(T t, K k, J j) {
        this.one = t;
        this.two = k;
        this.three = j;
    }
}

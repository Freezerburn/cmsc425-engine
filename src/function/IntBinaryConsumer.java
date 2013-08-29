package function;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/20/13
 * Time: 6:21 PM
 * License: MIT
 */
@FunctionalInterface
public interface IntBinaryConsumer {
    public void applyAsInt(int left, int right);
}

package stuff;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/29/13
 * Time: 4:20 PM
 * License: MIT
 */
public class UIDGenerator {
    public static final long NULL_ID = Long.MIN_VALUE;
    private static long nextUid = Long.MIN_VALUE + 1;

    public static long getUid() {
        return nextUid++;
    }
}

package structure.tuple;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/29/13
 * Time: 4:00 PM
 * License: MIT
 */
public class Triple<Unus, Duo, Tres> {
    public Unus unus;
    public Duo duo;
    public Tres tres;

    public Triple(Unus first, Duo second, Tres third) {
        unus = first;
        duo = second;
        tres = third;
    }
}

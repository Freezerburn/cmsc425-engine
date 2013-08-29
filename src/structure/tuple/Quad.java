package structure.tuple;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/29/13
 * Time: 4:01 PM
 * License: MIT
 */
public class Quad<Unus, Duo, Tres, Quattuor> {
    public Unus unus;
    public Duo duo;
    public Tres tres;
    public Quattuor quattuor;

    public Quad(Unus first, Duo second, Tres third, Quattuor fourth) {
        unus = first;
        duo = second;
        tres = third;
        quattuor = fourth;
    }
}

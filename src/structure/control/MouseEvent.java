package structure.control;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/23/13
 * Time: 10:10 PM
 * License: MIT
 */
public class MouseEvent {
    public int x, y;
    public int dx, dy;
    public int button;
    public boolean leftPressed, rightPressed, middlePressed;
    protected boolean consumed;

    public MouseEvent() {
        consumed = false;
    }

    public void reset(int x,
                      int y,
                      int dx,
                      int dy,
                      int button,
                      boolean leftPressed,
                      boolean rightPressed,
                      boolean middlePressed) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.button = button;
        this.leftPressed = leftPressed;
        this.rightPressed = rightPressed;
        this.middlePressed = middlePressed;
        this.consumed = false;
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }
}

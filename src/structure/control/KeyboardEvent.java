package structure.control;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/20/13
 * Time: 6:37 PM
 * License: MIT
 */
public class KeyboardEvent {
    public int key;
    public boolean pushed;
    public boolean shiftDown;
    public boolean ctrlDown;
    public boolean altDown;
    public boolean superDown;

    protected boolean consumed;

    public KeyboardEvent(int key, boolean pushed, boolean shiftDown, boolean ctrlDown, boolean altDown, boolean superDown) {
        this.key = key;
        this.pushed = pushed;
        this.shiftDown = shiftDown;
        this.ctrlDown = ctrlDown;
        this.altDown = altDown;
        this.superDown = superDown;
        this.consumed = false;
    }

    public void reset(int key, boolean pushed, boolean shiftDown, boolean ctrlDown, boolean altDown, boolean superDown) {
        this.key = key;
        this.pushed = pushed;
        this.shiftDown = shiftDown;
        this.ctrlDown = ctrlDown;
        this.altDown = altDown;
        this.superDown = superDown;
        this.consumed = false;
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }
}

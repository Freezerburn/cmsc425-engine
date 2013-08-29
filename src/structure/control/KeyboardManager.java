package structure.control;

import main.GameApplicationDisplay;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import structure.event.SubscriptionToken;
import stuff.UIDGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/20/13
 * Time: 6:34 PM
 * License: MIT
 */
public class KeyboardManager {
    private class ListenForKey {
        public Consumer<KeyboardEvent> listener;
        public int key;

        public ListenForKey(Consumer<KeyboardEvent> listener, int key) {
            this.listener = listener;
            this.key = key;
        }
    }
    private class ListenForKeyAndPress {
        public Consumer<KeyboardEvent> listener;
        public int key;
        public boolean pressed;

        public ListenForKeyAndPress(Consumer<KeyboardEvent> listener, int key, boolean pressed) {
            this.listener = listener;
            this.key = key;
            this.pressed = pressed;
        }
    }

    public static final String PRESS_STREAM = "keyPress";
    public static final String RELEASE_STREAM = "keyRelease";

    protected KeyboardEvent tmpEvent = new KeyboardEvent(0, false, false, false, false, false);
    protected boolean leftShiftPressed = false;
    protected boolean rightShiftPressed = false;
    protected boolean leftAltPressed = false;
    protected boolean rightAltPressed = false;
    protected boolean leftCtrlPressed = false;
    protected boolean rightCtrlPressed = false;
    protected boolean leftMetaPressed = false;
    protected boolean rightMetaPressed = false;

    public KeyboardManager() {
        GameApplicationDisplay.Q.createStream(PRESS_STREAM);
        GameApplicationDisplay.Q.createStream(RELEASE_STREAM);
        GameApplicationDisplay.Q.subscribeToUpdates(this::update);
    }

    public boolean shiftPressed() {
        return leftShiftPressed || rightShiftPressed;
    }

    public boolean altPressed() {
        return leftAltPressed || rightAltPressed;
    }

    public boolean ctrlPressed() {
        return leftCtrlPressed || rightCtrlPressed;
    }

    public boolean metaPressed() {
        return leftMetaPressed || rightMetaPressed;
    }

    protected void update() {
        while(Keyboard.next()) {
            tmpEvent.reset(
                    Keyboard.getEventKey(),
                    Keyboard.getEventKeyState(),
                    leftShiftPressed || rightShiftPressed,
                    leftCtrlPressed || rightCtrlPressed,
                    leftAltPressed || rightAltPressed,
                    leftMetaPressed || rightMetaPressed
            );
            checkPressedModifiers(tmpEvent.key, tmpEvent.pushed);
            pushEvent(tmpEvent);
        }
    }

    public void checkPressedModifiers(int key, boolean pushed) {
        switch (key) {
            case Keyboard.KEY_LSHIFT:
                leftShiftPressed = pushed;
                break;
            case Keyboard.KEY_RSHIFT:
                rightShiftPressed = pushed;
                break;
            case Keyboard.KEY_LCONTROL:
                leftCtrlPressed = pushed;
                break;
            case Keyboard.KEY_RCONTROL:
                rightCtrlPressed = pushed;
                break;
            case Keyboard.KEY_LMETA:
                leftMetaPressed = pushed;
                break;
            case Keyboard.KEY_RMETA:
                rightMetaPressed = pushed;
                break;
            case Keyboard.KEY_LMENU:
                leftAltPressed = pushed;
                break;
            case Keyboard.KEY_RMENU:
                rightAltPressed = pushed;
                break;
        }
    }

    public void pushEvent(int key, boolean pushed, boolean shiftDown, boolean ctrlDown, boolean altDown, boolean superDown) {
        tmpEvent.reset(
                key,
                pushed,
                shiftDown,
                ctrlDown,
                altDown,
                superDown
        );
        pushEvent(tmpEvent);
    }

    public void pushEvent(KeyboardEvent event) {
        checkPressedModifiers(event.key, event.pushed);
        if(event.pushed) {
            GameApplicationDisplay.Q.pushImmediate(event, PRESS_STREAM);
        }
        else {
            GameApplicationDisplay.Q.pushImmediate(event, RELEASE_STREAM);
        }
    }

    public SubscriptionToken listen(Consumer<KeyboardEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                UIDGenerator.getUid(),
                (e) -> {
                    listener.accept((KeyboardEvent)e);
                    return ((KeyboardEvent)e).isConsumed();
                },
                KeyboardManager.PRESS_STREAM, KeyboardManager.RELEASE_STREAM
        );
    }

    public SubscriptionToken listen(long uid, Consumer<KeyboardEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                uid,
                (e) -> {
                    listener.accept((KeyboardEvent)e);
                    return ((KeyboardEvent)e).isConsumed();
                },
                KeyboardManager.PRESS_STREAM, KeyboardManager.RELEASE_STREAM
        );
    }

    public SubscriptionToken listenFor(Consumer<KeyboardEvent> listener, int key) {
        return GameApplicationDisplay.Q.subscribe(
                UIDGenerator.getUid(),
                (e) -> ((KeyboardEvent)e).key == key,
                (e) -> {
                    listener.accept((KeyboardEvent)e);
                    return ((KeyboardEvent)e).isConsumed();
                },
                KeyboardManager.PRESS_STREAM, KeyboardManager.RELEASE_STREAM
        );
    }

    public SubscriptionToken listenFor(long uid, Consumer<KeyboardEvent> listener, int key) {
        return GameApplicationDisplay.Q.subscribe(
                uid,
                (e) -> ((KeyboardEvent)e).key == key,
                (e) -> {
                    listener.accept((KeyboardEvent)e);
                    return ((KeyboardEvent)e).isConsumed();
                },
                KeyboardManager.PRESS_STREAM, KeyboardManager.RELEASE_STREAM
        );
    }

    public SubscriptionToken listenFor(Consumer<KeyboardEvent> listener, boolean pressed, int... keys) {
        return GameApplicationDisplay.Q.subscribe(
                UIDGenerator.getUid(),
                /*
                TODO: Refactor later to not do all this crazy stream stuff?
                 */
                (e) -> Arrays.stream(keys).anyMatch((key) -> ((KeyboardEvent)e).key == key),
                (e) -> {
                    listener.accept((KeyboardEvent)e);
                    return ((KeyboardEvent)e).isConsumed();
                },
                pressed ? KeyboardManager.PRESS_STREAM : KeyboardManager.RELEASE_STREAM
        );
    }

    public SubscriptionToken listenFor(long uid, Consumer<KeyboardEvent> listener, int key, boolean pressed) {
        return GameApplicationDisplay.Q.subscribe(
                uid,
                (e) -> {
                    KeyboardEvent ke = (KeyboardEvent)e;
                    return ke.key == key;
                },
                (e) -> {
                    listener.accept((KeyboardEvent)e);
                    return ((KeyboardEvent)e).isConsumed();
                },
                pressed ? KeyboardManager.PRESS_STREAM : KeyboardManager.RELEASE_STREAM
        );
    }
}

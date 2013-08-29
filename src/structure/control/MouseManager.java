package structure.control;

import main.GameApplicationDisplay;
import org.lwjgl.input.Mouse;
import structure.event.SubscriptionToken;
import stuff.UIDGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/23/13
 * Time: 10:10 PM
 * License: MIT
 */
public class MouseManager {
    public static final String PRESS_STREAM = "mousePress";
    public static final String RELEASE_STREAM = "mouseRelease";
    public static final String MOVE_STREAM = "mouseMove";

    private MouseEvent tmpEvent;

    public MouseManager() {
        GameApplicationDisplay.Q.subscribeToUpdates(this::update);
        GameApplicationDisplay.Q.createStream(MouseManager.PRESS_STREAM);
        GameApplicationDisplay.Q.createStream(MouseManager.RELEASE_STREAM);
        GameApplicationDisplay.Q.createStream(MouseManager.MOVE_STREAM);

        tmpEvent = new MouseEvent();
    }

    public void update() {
        while(Mouse.next()) {
            tmpEvent.reset(
                    Mouse.getEventX(),
                    Mouse.getEventY(),
                    Mouse.getEventDX(),
                    Mouse.getEventDY(),
                    Mouse.getEventButton(),
                    Mouse.getEventButton() == 0 && Mouse.getEventButtonState(),
                    Mouse.getEventButton() == 1 && Mouse.getEventButtonState(),
                    Mouse.getEventButton() == 2 && Mouse.getEventButtonState()
            );
            pushEvent(tmpEvent);
        }
    }

    public void pushEvent(MouseEvent event) {
        if(event.dx != 0 || event.dy != 0) {
            GameApplicationDisplay.Q.pushImmediate(event, MouseManager.MOVE_STREAM);
        }
        else if(event.rightPressed || event.leftPressed || event.middlePressed) {
            GameApplicationDisplay.Q.pushImmediate(event, MouseManager.PRESS_STREAM);
        }
        else {
            GameApplicationDisplay.Q.pushImmediate(event, MouseManager.RELEASE_STREAM);
        }
    }

    public SubscriptionToken listen(Consumer<MouseEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                UIDGenerator.getUid(),
                (e) -> {
                    MouseEvent me = (MouseEvent)e;
                    listener.accept(me);
                    return me.isConsumed();
                },
                MouseManager.PRESS_STREAM, MouseManager.RELEASE_STREAM, MouseManager.MOVE_STREAM
        );
    }

    public SubscriptionToken listen(long uid, Consumer<MouseEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                uid,
                (e) -> {
                    MouseEvent me = (MouseEvent)e;
                    listener.accept(me);
                    return me.isConsumed();
                },
                MouseManager.PRESS_STREAM, MouseManager.RELEASE_STREAM, MouseManager.MOVE_STREAM
        );
    }

    public SubscriptionToken listenForMovement(Consumer<MouseEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                UIDGenerator.getUid(),
                (e) -> {
                    MouseEvent me = (MouseEvent)e;
                    listener.accept(me);
                    return me.isConsumed();
                },
                MouseManager.MOVE_STREAM
        );
    }

    public SubscriptionToken listenForMovement(long uid, Consumer<MouseEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                uid,
                (e) -> {
                    MouseEvent me = (MouseEvent)e;
                    listener.accept(me);
                    return me.isConsumed();
                },
                MouseManager.MOVE_STREAM
        );
    }

    public SubscriptionToken listenForButton(Consumer<MouseEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                UIDGenerator.getUid(),
                (e) -> {
                    MouseEvent me = (MouseEvent)e;
                    listener.accept(me);
                    return me.isConsumed();
                },
                MouseManager.PRESS_STREAM, MouseManager.RELEASE_STREAM
        );
    }

    public SubscriptionToken listenForButton(long uid, Consumer<MouseEvent> listener) {
        return GameApplicationDisplay.Q.subscribe(
                uid,
                (e) -> {
                    MouseEvent me = (MouseEvent)e;
                    listener.accept(me);
                    return me.isConsumed();
                },
                MouseManager.PRESS_STREAM, MouseManager.RELEASE_STREAM
        );
    }
}

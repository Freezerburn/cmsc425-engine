package structure.event;

import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/29/13
 * Time: 6:53 PM
 * License: MIT
 */
public class SubscriptionToken {
    private Object[] streams;
    private Consumer<Object> unsubscriber;
    private EventQueue.Subscriber ref;

    public SubscriptionToken(Object[] streams, EventQueue.Subscriber ref, Consumer<Object> unsubscriber) {
        this.streams = streams;
        this.ref = ref;
        this.unsubscriber = unsubscriber;
    }

    public void unsubscribe() {
        for(Object stream : streams) {
            unsubscriber.accept(stream);
        }
    }
}

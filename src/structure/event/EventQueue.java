package structure.event;

import structure.tuple.Quad;
import structure.tuple.Triple;
import stuff.UIDGenerator;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created with IntelliJ IDEA.
 * Author: Vincent "FreezerburnV" K.
 * Date: 6/25/13
 * Time: 7:31 PM
 * License: MIT
 */
public class EventQueue {
    @SuppressWarnings("unchecked")
    protected static class Subscriber {
        long uid;
        Object[] descriptors;
        Predicate<Object> filter;
        Predicate<Object> subscriber;

        public Subscriber(long uid, Object[] descriptors, Predicate<Object> subscriber) {
            this.uid = uid;
            this.descriptors = descriptors;
            this.subscriber = subscriber;
        }

        public Subscriber(long uid, Object[] descriptors, Predicate<Object> subscriber, Predicate<Object> filter) {
            this.uid = uid;
            this.descriptors = descriptors;
            this.subscriber = subscriber;
            this.filter = filter;
        }
    }

    private final HashMap<Object, LinkedList<WeakReference<Subscriber>>> subscriptions = new HashMap<>();
    private final Queue<Quad<Integer, Object[], Object, Long>> queuedItems = new LinkedList<>();
    private final LinkedList<Runnable> updaters = new LinkedList<>();

    public EventQueue() { }

    public void createStream(Object descriptor) {
        synchronized (subscriptions) {
            if(!subscriptions.containsKey(descriptor)) {
                subscriptions.put(descriptor, new LinkedList<>());
            }
            else {
            /*
            Theoretically shouldn't happen, but I figure it's better to have this here just in
            case. Better than having weird issues to debug later on, eurgh.
             */
                throw new IllegalArgumentException("Cannot create two streams of the same type");
            }
        }
    }

    public void removeStream(Object descriptor) {
        synchronized (subscriptions) {
            if(subscriptions.containsKey(descriptor)) {
                subscriptions.remove(descriptor);
            }
        }
    }

    private void addSubscriber(Subscriber s, Object[] descriptors) {
        for(Object descriptor : descriptors) {
            synchronized (subscriptions) {
                if(subscriptions.containsKey(descriptor)) {
                    subscriptions.get(descriptor).add(new WeakReference<>(s));
                }
            }
        }
    }

    private SubscriptionToken getToken(Subscriber s, Object[] streams) {
        /*
        Gosh dang I love lambda functions in Java 8. So nice to be able to just do things like this
        so easily. ;u;
         */
        SubscriptionToken ret = new SubscriptionToken(
                streams,
                s,
                (stream) -> {
                    synchronized (subscriptions) {
                        if(subscriptions.containsKey(stream)) {
                            for(Iterator<WeakReference<Subscriber>> it = subscriptions.get(stream).iterator(); it.hasNext();) {
                                WeakReference<Subscriber> wref = it.next();
                                Subscriber sref = wref.get();
                                if(sref != null) {
                                    if(sref.equals(s)) {
                                        it.remove();
                                        break;
                                    }
                                }
                                else {
                                    it.remove();
                                }
                            }
                        }
                    }
                }
        );
        return ret;
    }

    public SubscriptionToken subscribe(long uid, Predicate<Object> subscription, Object... descriptors) {
        Subscriber s = new Subscriber(uid, descriptors, subscription);
        addSubscriber(s, descriptors);
        return getToken(s, descriptors);
    }

    public SubscriptionToken subscribe(long uid, Predicate<Object> subscription, Predicate<Object> filter, Object... descriptors) {
        Subscriber s = new Subscriber(uid, descriptors, subscription, filter);
        addSubscriber(s, descriptors);
        return getToken(s, descriptors);
    }

    public void update() {
        /*
        This needs to happen before we go through all of our queued items, as these might add
        items to the queue.
        For example: the KeyboardManager might have its update function called here, which can
        very easily add KeyboardEvents to the queue.
         */
        synchronized (updaters) {
            for(Runnable r : updaters) {
                r.run();
            }
        }
        synchronized (queuedItems) {
            for(Iterator<Quad<Integer, Object[], Object, Long>> it = queuedItems.iterator(); it.hasNext();) {
                Quad<Integer, Object[], Object, Long> queuedItem = it.next();
                queuedItem.unus--;
                if(queuedItem.unus == 0) {
                    pushImmediate(queuedItem.quattuor, queuedItem.tres, queuedItem.duo);
                    it.remove();
                }
            }
        }
    }

    public void subscribeToUpdates(Runnable r) {
        synchronized (updaters) {
            updaters.push(r);
        }
    }

    public void unsubscribeToUpdates(Runnable r) {
        synchronized (updaters) {
            updaters.remove(r);
        }
    }

    public void pushImmediate(Object item, Object... descriptors) {
        pushImmediateTo(UIDGenerator.NULL_ID, item, descriptors);
    }

    public void pushImmediateTo(long uid, Object item, Object... descriptors) {
        for(Object descriptor : descriptors) {
            synchronized (subscriptions) {
                if(subscriptions.containsKey(descriptor)) {
                    for(Iterator<WeakReference<Subscriber>> it = subscriptions.get(descriptor).iterator(); it.hasNext();) {
                        WeakReference<Subscriber> wref = it.next();
                        /*
                        Note that the sref (if it exists) CANNOT be garbage collected while
                        the (hard) reference is held here. It's best to do it this way, so that
                        we don't check for wref.get() != null, then it gets gced before it gets
                        used.
                         */
                        Subscriber sref = wref.get();
                        if(sref != null) {
                            if(sref.filter != null) {
                                if(item == null) {
                                    if(uid == UIDGenerator.NULL_ID || uid == sref.uid) {
                                        if(sref.subscriber.test(item)) {
                                            break;
                                        }
                                    }
                                }
                                else {
                                    if(uid == UIDGenerator.NULL_ID || uid == sref.uid) {
                                        if(sref.filter.test(item)) {
                                            if(sref.subscriber.test(item)) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                if(uid == UIDGenerator.NULL_ID || uid == sref.uid) {
                                    if(sref.subscriber.test(item)) {
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    public void pushLater(int updates, Object... descriptors) {
        synchronized (queuedItems) {
            queuedItems.add(new Quad<>(updates, descriptors, null, UIDGenerator.NULL_ID));
        }
    }

    public void pushLater(Object item, int updates, Object... descriptors) {
        synchronized (queuedItems) {
            queuedItems.add(new Quad<>(updates, descriptors, item, UIDGenerator.NULL_ID));
        }
    }

    public void pushLaterTo(int updates, long uid, Object... descriptors) {
        synchronized (queuedItems) {
            queuedItems.add(new Quad<>(updates, descriptors, null, uid));
        }
    }

    public void pushLaterTo(Object item, int updates, long uid, Object... descriptors) {
        synchronized (queuedItems) {
            queuedItems.add(new Quad<>(updates, descriptors, item, uid));
        }
    }
}

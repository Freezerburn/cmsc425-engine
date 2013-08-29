/**
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.subjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.operators.SafeObservableSubscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import rx.util.functions.Func0;
import rx.util.functions.Func1;

/**
 * Subject that, once and {@link Observer} has subscribed, publishes all subsequent events to the subscriber.
 *
 * <p>
 * Example usage:
 * <p>
 * <pre> {@code

  PublishSubject<Object> subject = PublishSubject.create();
  // observer1 will receive all onNext and onCompleted events
  subject.subscribe(observer1);
  subject.onNext("one");
  subject.onNext("two");
  // observer2 will only receive "three" and onCompleted
  subject.subscribe(observer2);
  subject.onNext("three");
  subject.onCompleted();

  } </pre>
 *
 * @param <T>
 */
public class PublishSubject<T> extends Subject<T, T> {
    public static <T> PublishSubject<T> create() {
        final ConcurrentHashMap<Subscription, Observer<T>> observers = new ConcurrentHashMap<Subscription, Observer<T>>();
        final AtomicReference<Notification<T>> terminalState = new AtomicReference<Notification<T>>();

        Func1<Observer<T>, Subscription> onSubscribe = new Func1<Observer<T>, Subscription>() {
            @Override
            public Subscription call(Observer<T> observer) {
                // shortcut check if terminal state exists already
                Subscription s = checkTerminalState(observer);
                if(s != null) return s;

                final SafeObservableSubscription subscription = new SafeObservableSubscription();

                subscription.wrap(new Subscription() {
                    @Override
                    public void unsubscribe() {
                        // on unsubscribe remove it from the map of outbound observers to notify
                        observers.remove(subscription);
                    }
                });

                /**
                 * NOTE: We are synchronizing to avoid a race condition between terminalState being set and
                 * a new observer being added to observers.
                 *
                 * The synchronization only occurs on subscription and terminal states, it does not affect onNext calls
                 * so a high-volume hot-observable will not pay this cost for emitting data.
                 *
                 * Due to the restricted impact of blocking synchronization here I have not pursued more complicated
                 * approaches to try and stay completely non-blocking.
                 */
                synchronized (terminalState) {
                    // check terminal state again
                    s = checkTerminalState(observer);
                    if (s != null)
                        return s;

                    // on subscribe add it to the map of outbound observers to notify
                    observers.put(subscription, observer);

                    return subscription;
                }
            }

            private Subscription checkTerminalState(Observer<T> observer) {
                Notification<T> n = terminalState.get();
                if (n != null) {
                    // we are terminated to immediately emit and don't continue with subscription
                    if (n.isOnCompleted()) {
                        observer.onCompleted();
                    } else {
                        observer.onError(n.getThrowable());
                    }
                    return Subscriptions.empty();
                } else {
                    return null;
                }
            }
        };

        return new PublishSubject<T>(onSubscribe, observers, terminalState);
    }

    private final ConcurrentHashMap<Subscription, Observer<T>> observers;
    private final AtomicReference<Notification<T>> terminalState;

    protected PublishSubject(Func1<Observer<T>, Subscription> onSubscribe, ConcurrentHashMap<Subscription, Observer<T>> observers, AtomicReference<Notification<T>> terminalState) {
        super(onSubscribe);
        this.observers = observers;
        this.terminalState = terminalState;
    }

    @Override
    public void onCompleted() {
        /**
         * Synchronizing despite terminalState being an AtomicReference because of multi-step logic in subscription.
         * Why use AtomicReference then? Convenient for passing around a mutable reference holder between the
         * onSubscribe function and PublishSubject instance... and it's a "better volatile" for the shortcut codepath.
         */
        synchronized (terminalState) {
            terminalState.set(new Notification<T>());
        }
        for (Observer<T> observer : snapshotOfValues()) {
            observer.onCompleted();
        }
        observers.clear();
    }

    @Override
    public void onError(Throwable e) {
        /**
         * Synchronizing despite terminalState being an AtomicReference because of multi-step logic in subscription.
         * Why use AtomicReference then? Convenient for passing around a mutable reference holder between the
         * onSubscribe function and PublishSubject instance... and it's a "better volatile" for the shortcut codepath.
         */
        synchronized (terminalState) {
            terminalState.set(new Notification<T>(e));
        }
        for (Observer<T> observer : snapshotOfValues()) {
            observer.onError(e);
        }
        observers.clear();
    }

    @Override
    public void onNext(T args) {
        for (Observer<T> observer : snapshotOfValues()) {
            observer.onNext(args);
        }
    }

    /**
     * Current snapshot of 'values()' so that concurrent modifications aren't included.
     *
     * This makes it behave deterministically in a single-threaded execution when nesting subscribes.
     *
     * In multi-threaded execution it will cause new subscriptions to wait until the following onNext instead
     * of possibly being included in the current onNext iteration.
     *
     * @return List<Observer<T>>
     */
    private Collection<Observer<T>> snapshotOfValues() {
        return new ArrayList<Observer<T>>(observers.values());
    }
}

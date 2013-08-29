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
package rx.operators;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observables.GroupedObservable;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import rx.util.functions.Func1;
import rx.util.functions.Functions;

/**
 * Groups the items emitted by an Observable according to a specified criterion, and emits these
 * grouped items as Observables, one Observable per group.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/groupBy.png">
 */
public final class OperationGroupBy {

    public static <K, T, R> Func1<Observer<GroupedObservable<K, R>>, Subscription> groupBy(Observable<T> source, final Func1<T, K> keySelector, final Func1<T, R> elementSelector) {

        final Observable<KeyValue<K, R>> keyval = source.map(new Func1<T, KeyValue<K, R>>() {
            @Override
            public KeyValue<K, R> call(T t) {
                K key = keySelector.call(t);
                R value = elementSelector.call(t);

                return new KeyValue<K, R>(key, value);
            }
        });

        return new GroupBy<K, R>(keyval);
    }

    public static <K, T> Func1<Observer<GroupedObservable<K, T>>, Subscription> groupBy(Observable<T> source, final Func1<T, K> keySelector) {
        return groupBy(source, keySelector, Functions.<T> identity());
    }

    private static class GroupBy<K, V> implements Func1<Observer<GroupedObservable<K, V>>, Subscription> {

        private final Observable<KeyValue<K, V>> source;
        private final ConcurrentHashMap<K, GroupedSubject<K, V>> groupedObservables = new ConcurrentHashMap<K, GroupedSubject<K, V>>();
        private final SafeObservableSubscription actualParentSubscription = new SafeObservableSubscription();
        private final AtomicInteger numGroupSubscriptions = new AtomicInteger();
        private final AtomicBoolean unsubscribeRequested = new AtomicBoolean(false);

        private GroupBy(Observable<KeyValue<K, V>> source) {
            this.source = source;
        }

        @Override
        public Subscription call(final Observer<GroupedObservable<K, V>> observer) {
            final GroupBy<K, V> _this = this;
            actualParentSubscription.wrap(source.subscribe(new Observer<KeyValue<K, V>>() {

                @Override
                public void onCompleted() {
                    // we need to propagate to all children I imagine ... we can't just leave all of those Observable/Observers hanging
                    for (GroupedSubject<K, V> o : groupedObservables.values()) {
                        o.onCompleted();
                    }
                    // now the parent
                    observer.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    // we need to propagate to all children I imagine ... we can't just leave all of those Observable/Observers hanging 
                    for (GroupedSubject<K, V> o : groupedObservables.values()) {
                        o.onError(e);
                    }
                    // now the parent
                    observer.onError(e);
                }

                @Override
                public void onNext(KeyValue<K, V> value) {
                    GroupedSubject<K, V> gs = groupedObservables.get(value.key);
                    if (gs == null) {
                        if (unsubscribeRequested.get()) {
                            // unsubscribe has been requested so don't create new groups
                            // only send data to groups already created
                            return;
                        }
                        /*
                         * Technically the source should be single-threaded so we shouldn't need to do this but I am
                         * programming defensively as most operators are so this can work with a concurrent sequence
                         * if it ends up receiving one.
                         */
                        GroupedSubject<K, V> newGs = GroupedSubject.<K, V> create(value.key, _this);
                        GroupedSubject<K, V> existing = groupedObservables.putIfAbsent(value.key, newGs);
                        if (existing == null) {
                            // we won so use the one we created
                            gs = newGs;
                            // since we won the creation we emit this new GroupedObservable
                            observer.onNext(gs);
                        } else {
                            // another thread beat us so use the existing one
                            gs = existing;
                        }
                    }
                    gs.onNext(value.value);
                }
            }));

            return new Subscription() {

                @Override
                public void unsubscribe() {
                    if (numGroupSubscriptions.get() == 0) {
                        // if we have no group subscriptions we will unsubscribe
                        actualParentSubscription.unsubscribe();
                        // otherwise we mark to not send any more groups (waiting on existing groups to finish)
                        unsubscribeRequested.set(true);
                    }
                }
            };
        }

        /**
         * Children notify of being subscribed to.
         * 
         * @param key
         */
        private void subscribeKey(K key) {
            numGroupSubscriptions.incrementAndGet();
        }

        /**
         * Children notify of being unsubscribed from.
         * 
         * @param key
         */
        private void unsubscribeKey(K key) {
            int c = numGroupSubscriptions.decrementAndGet();
            if (c == 0) {
                actualParentSubscription.unsubscribe();
            }
        }
    }

    private static class GroupedSubject<K, T> extends GroupedObservable<K, T> implements Observer<T> {

        static <K, T> GroupedSubject<K, T> create(final K key, final GroupBy<K, T> parent) {
            @SuppressWarnings("unchecked")
            final AtomicReference<Observer<T>> subscribedObserver = new AtomicReference<Observer<T>>(EMPTY_OBSERVER);

            return new GroupedSubject<K, T>(key, new Func1<Observer<T>, Subscription>() {

                private final SafeObservableSubscription subscription = new SafeObservableSubscription();

                @Override
                public Subscription call(Observer<T> observer) {
                    // register Observer
                    subscribedObserver.set(observer);

                    parent.subscribeKey(key);

                    return subscription.wrap(new Subscription() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public void unsubscribe() {
                            // we remove the Observer so we stop emitting further events (they will be ignored if parent continues to send)
                            subscribedObserver.set(EMPTY_OBSERVER);
                            // now we need to notify the parent that we're unsubscribed
                            parent.unsubscribeKey(key);
                        }
                    });
                }
            }, subscribedObserver);
        }

        private final AtomicReference<Observer<T>> subscribedObserver;

        public GroupedSubject(K key, Func1<Observer<T>, Subscription> onSubscribe, AtomicReference<Observer<T>> subscribedObserver) {
            super(key, onSubscribe);
            this.subscribedObserver = subscribedObserver;
        }

        @Override
        public void onCompleted() {
            subscribedObserver.get().onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            subscribedObserver.get().onError(e);
        }

        @Override
        public void onNext(T v) {
            subscribedObserver.get().onNext(v);
        }

    }

    @SuppressWarnings("rawtypes")
    private static Observer EMPTY_OBSERVER = new Observer() {

        @Override
        public void onCompleted() {
            // do nothing            
        }

        @Override
        public void onError(Throwable e) {
            // do nothing            
        }

        @Override
        public void onNext(Object args) {
            // do nothing
        }

    };

    private static class KeyValue<K, V> {
        private final K key;
        private final V value;

        private KeyValue(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}

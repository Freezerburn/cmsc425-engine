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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.TestScheduler;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action0;
import rx.util.functions.Func1;

/**
 * Transforms an Observable that emits Observables into a single Observable that emits the items
 * emitted by the most recently published of those Observables.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/switchDo.png">
 */
public final class OperationSwitch {

    /**
     * This function transforms an {@link Observable} sequence of {@link Observable} sequences into a single {@link Observable} sequence which produces values from the most recently published
     * {@link Observable}.
     * 
     * @param sequences
     *            The {@link Observable} sequence consisting of {@link Observable} sequences.
     * @return A {@link Func1} which does this transformation.
     */
    public static <T> Func1<Observer<T>, Subscription> switchDo(final Observable<Observable<T>> sequences) {
        return new Func1<Observer<T>, Subscription>() {
            @Override
            public Subscription call(Observer<T> observer) {
                return new Switch<T>(sequences).call(observer);
            }
        };
    }

    private static class Switch<T> implements Func1<Observer<T>, Subscription> {

        private final Observable<Observable<T>> sequences;

        public Switch(Observable<Observable<T>> sequences) {
            this.sequences = sequences;
        }

        @Override
        public Subscription call(Observer<T> observer) {
            SafeObservableSubscription subscription = new SafeObservableSubscription();
            subscription.wrap(sequences.subscribe(new SwitchObserver<T>(observer, subscription)));
            return subscription;
        }
    }

    private static class SwitchObserver<T> implements Observer<Observable<T>> {

        private final Observer<T> observer;
        private final SafeObservableSubscription parent;
        private final AtomicReference<Subscription> subsequence = new AtomicReference<Subscription>();

        public SwitchObserver(Observer<T> observer, SafeObservableSubscription parent) {
            this.observer = observer;
            this.parent = parent;
        }

        @Override
        public void onCompleted() {
            unsubscribeFromSubSequence();
            observer.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            unsubscribeFromSubSequence();
            observer.onError(e);
        }

        @Override
        public void onNext(Observable<T> args) {
            unsubscribeFromSubSequence();

            subsequence.set(args.subscribe(new Observer<T>() {
                @Override
                public void onCompleted() {
                    // Do nothing.
                }

                @Override
                public void onError(Throwable e) {
                    parent.unsubscribe();
                    observer.onError(e);
                }

                @Override
                public void onNext(T args) {
                    observer.onNext(args);
                }
            }));
        }

        private void unsubscribeFromSubSequence() {
            Subscription previousSubscription = subsequence.get();
            if (previousSubscription != null) {
                previousSubscription.unsubscribe();
            }
        }
    }
}

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

import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;

/**
 * Instruct an Observable to pass control to another Observable rather than invoking
 * <code>onError</code> if it encounters an error.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/onErrorResumeNext.png">
 * <p>
 * By default, when an Observable encounters an error that prevents it from emitting the expected
 * item to its Observer, the Observable invokes its Observer's <code>onError</code> method, and
 * then quits without invoking any more of its Observer's methods. The onErrorResumeNext operation
 * changes this behavior. If you pass an Observable (resumeSequence) to onErrorResumeNext, if the
 * source Observable encounters an error, instead of invoking its Observer's <code>onError</code>
 * method, it will instead relinquish control to this new Observable, which will invoke the
 * Observer's <code>onNext</code> method if it is able to do so. In such a case, because no
 * Observable necessarily invokes <code>onError</code>, the Observer may never know that an error
 * happened.
 * <p>
 * You can use this to prevent errors from propagating or to supply fallback data should errors be
 * encountered.
 */
public final class OperationOnErrorResumeNextViaObservable<T> {

    public static <T> Func1<Observer<T>, Subscription> onErrorResumeNextViaObservable(Observable<T> originalSequence, Observable<T> resumeSequence) {
        return new OnErrorResumeNextViaObservable<T>(originalSequence, resumeSequence);
    }

    private static class OnErrorResumeNextViaObservable<T> implements Func1<Observer<T>, Subscription> {

        private final Observable<T> resumeSequence;
        private final Observable<T> originalSequence;

        public OnErrorResumeNextViaObservable(Observable<T> originalSequence, Observable<T> resumeSequence) {
            this.resumeSequence = resumeSequence;
            this.originalSequence = originalSequence;
        }

        public Subscription call(final Observer<T> observer) {
            final SafeObservableSubscription subscription = new SafeObservableSubscription();

            // AtomicReference since we'll be accessing/modifying this across threads so we can switch it if needed
            final AtomicReference<SafeObservableSubscription> subscriptionRef = new AtomicReference<SafeObservableSubscription>(subscription);

            // subscribe to the original Observable and remember the subscription
            subscription.wrap(originalSequence.subscribe(new Observer<T>() {
                public void onNext(T value) {
                    // forward the successful calls unless resumed
                    if (subscriptionRef.get()==subscription)
                        observer.onNext(value);
                }

                /**
                 * Instead of passing the onError forward, we intercept and "resume" with the resumeSequence.
                 */
                public void onError(Throwable ex) {
                    /* remember what the current subscription is so we can determine if someone unsubscribes concurrently */
                    SafeObservableSubscription currentSubscription = subscriptionRef.get();
                    // check that we have not been unsubscribed and not already resumed before we can process the error
                    if (currentSubscription == subscription) {
                        /* error occurred, so switch subscription to the 'resumeSequence' */
                        SafeObservableSubscription innerSubscription = new SafeObservableSubscription(resumeSequence.subscribe(observer));
                        /* we changed the sequence, so also change the subscription to the one of the 'resumeSequence' instead */
                        if (!subscriptionRef.compareAndSet(currentSubscription, innerSubscription)) {
                            // we failed to set which means 'subscriptionRef' was set to NULL via the unsubscribe below
                            // so we want to immediately unsubscribe from the resumeSequence we just subscribed to
                            innerSubscription.unsubscribe();
                        }
                    }
                }

                public void onCompleted() {
                    // forward the successful calls unless resumed
                    if (subscriptionRef.get()==subscription)
                        observer.onCompleted();
                }
            }));

            return new Subscription() {
                public void unsubscribe() {
                    // this will get either the original, or the resumeSequence one and unsubscribe on it
                    Subscription s = subscriptionRef.getAndSet(null);
                    if (s != null) {
                        s.unsubscribe();
                    }
                }
            };
        }
    }
}

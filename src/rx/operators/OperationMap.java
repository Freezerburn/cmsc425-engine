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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;

/**
 * Applies a function of your choosing to every item emitted by an Observable, and returns this
 * transformation as a new Observable.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/map.png">
 */
public final class OperationMap {

    /**
     * Accepts a sequence and a transformation function. Returns a sequence that is the result of
     * applying the transformation function to each item in the sequence.
     * 
     * @param sequence
     *            the input sequence.
     * @param func
     *            a function to apply to each item in the sequence.
     * @param <T>
     *            the type of the input sequence.
     * @param <R>
     *            the type of the output sequence.
     * @return a sequence that is the result of applying the transformation function to each item in the input sequence.
     */
    public static <T, R> Func1<Observer<R>, Subscription> map(Observable<T> sequence, Func1<T, R> func) {
        return new MapObservable<T, R>(sequence, func);
    }

    /**
     * Accepts a sequence of observable sequences and a transformation function. Returns a flattened sequence that is the result of
     * applying the transformation function to each item in the sequence of each observable sequence.
     * <p>
     * The closure should return an Observable which will then be merged.
     * 
     * @param sequence
     *            the input sequence.
     * @param func
     *            a function to apply to each item in the sequence.
     * @param <T>
     *            the type of the input sequence.
     * @param <R>
     *            the type of the output sequence.
     * @return a sequence that is the result of applying the transformation function to each item in the input sequence.
     */
    public static <T, R> Func1<Observer<R>, Subscription> mapMany(Observable<T> sequence, Func1<T, Observable<R>> func) {
        return OperationMerge.merge(Observable.create(map(sequence, func)));
    }

    /**
     * An observable sequence that is the result of applying a transformation to each item in an input sequence.
     * 
     * @param <T>
     *            the type of the input sequence.
     * @param <R>
     *            the type of the output sequence.
     */
    private static class MapObservable<T, R> implements Func1<Observer<R>, Subscription> {
        public MapObservable(Observable<T> sequence, Func1<T, R> func) {
            this.sequence = sequence;
            this.func = func;
        }

        private Observable<T> sequence;

        private Func1<T, R> func;

        public Subscription call(Observer<R> observer) {
            return sequence.subscribe(new MapObserver<T, R>(observer, func));
        }
    }

    /**
     * An observer that applies a transformation function to each item and forwards the result to an inner observer.
     * 
     * @param <T>
     *            the type of the observer items.
     * @param <R>
     *            the type of the inner observer items.
     */
    private static class MapObserver<T, R> implements Observer<T> {
        public MapObserver(Observer<R> observer, Func1<T, R> func) {
            this.observer = observer;
            this.func = func;
        }

        Observer<R> observer;

        Func1<T, R> func;

        public void onNext(T value) {
            // let the exception be thrown if func fails as a SafeObserver wrapping this will handle it
            observer.onNext(func.call(value));
        }

        public void onError(Throwable ex) {
            observer.onError(ex);
        }

        public void onCompleted() {
            observer.onCompleted();
        }
    }
}

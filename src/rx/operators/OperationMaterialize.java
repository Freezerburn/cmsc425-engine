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

import java.util.List;
import java.util.Vector;

import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;

/**
 * Turns all of the notifications from an Observable into <code>onNext</code> emissions, and marks
 * them with their original notification types within {@link Notification} objects.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/materialize.png">
 * <p>
 * See <a href="http://msdn.microsoft.com/en-us/library/hh229453(v=VS.103).aspx">here</a> for the
 * Microsoft Rx equivalent.
 */
public final class OperationMaterialize {

    /**
     * Materializes the implicit notifications of an observable sequence as explicit notification values.
     * 
     * @param sequence
     *            An observable sequence of elements to project.
     * @return An observable sequence whose elements are the result of materializing the notifications of the given sequence.
     * @see <a href="http://msdn.microsoft.com/en-us/library/hh229453(v=VS.103).aspx">Observable.Materialize(TSource) Method </a>
     */
    public static <T> Func1<Observer<Notification<T>>, Subscription> materialize(final Observable<T> sequence) {
        return new MaterializeObservable<T>(sequence);
    }

    private static class MaterializeObservable<T> implements Func1<Observer<Notification<T>>, Subscription> {

        private final Observable<T> sequence;

        public MaterializeObservable(Observable<T> sequence) {
            this.sequence = sequence;
        }

        @Override
        public Subscription call(final Observer<Notification<T>> observer) {
            return sequence.subscribe(new Observer<T>() {

                @Override
                public void onCompleted() {
                    observer.onNext(new Notification<T>());
                    observer.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    observer.onNext(new Notification<T>(e));
                    observer.onCompleted();
                }

                @Override
                public void onNext(T value) {
                    observer.onNext(new Notification<T>(value));
                }

            });
        }

    }
}

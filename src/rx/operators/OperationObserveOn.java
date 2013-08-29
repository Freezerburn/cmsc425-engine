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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.concurrency.ImmediateScheduler;
import rx.concurrency.Schedulers;
import rx.util.functions.Func1;

/**
 * Asynchronously notify Observers on the specified Scheduler.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/observeOn.png">
 */
public class OperationObserveOn {

    public static <T> Func1<Observer<T>, Subscription> observeOn(Observable<T> source, Scheduler scheduler) {
        return new ObserveOn<T>(source, scheduler);
    }

    private static class ObserveOn<T> implements Func1<Observer<T>, Subscription> {
        private final Observable<T> source;
        private final Scheduler scheduler;

        public ObserveOn(Observable<T> source, Scheduler scheduler) {
            this.source = source;
            this.scheduler = scheduler;
        }

        @Override
        public Subscription call(final Observer<T> observer) {
            if (scheduler instanceof ImmediateScheduler) {
                // do nothing if we request ImmediateScheduler so we don't invoke overhead
                return source.subscribe(observer);
            } else {
                return source.subscribe(new ScheduledObserver<T>(observer, scheduler));
            }
        }
    }
}

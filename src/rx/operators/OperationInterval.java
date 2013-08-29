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

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.concurrency.TestScheduler;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action0;
import rx.util.functions.Func1;

/**
 * Returns an observable sequence that produces a value after each period.
 * The value starts at 0 and counts up each period.
 */
public final class OperationInterval {

    /**
     * Creates an event each time interval.
     */
    public static Func1<Observer<Long>, Subscription> interval(long interval, TimeUnit unit) {
        return new Interval(interval, unit, Schedulers.executor(Executors.newSingleThreadScheduledExecutor()));
    }

    /**
     * Creates an event each time interval.
     */
    public static Func1<Observer<Long>, Subscription> interval(long interval, TimeUnit unit, Scheduler scheduler) {
        return new Interval(interval, unit, scheduler);
    }

    private static class Interval implements Func1<Observer<Long>, Subscription> {
        private final long period;
        private final TimeUnit unit;
        private final Scheduler scheduler;
        
        private long currentValue;

        private Interval(long period, TimeUnit unit, Scheduler scheduler) {
            this.period = period;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        @Override
        public Subscription call(final Observer<Long> observer) {
            final Subscription wrapped = scheduler.schedulePeriodically(new Action0() {
                @Override
                public void call() {
                    observer.onNext(currentValue);
                    currentValue++;
                }
            }, period, period, unit);
            
            return Subscriptions.create(new Action0() {
                @Override
                public void call() {
                    wrapped.unsubscribe();
                    observer.onCompleted();
                }
            });
        }
    }
}

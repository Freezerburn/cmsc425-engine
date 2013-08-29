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

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import rx.util.Exceptions;
import rx.util.functions.Func1;

/**
 * Returns an Iterable that blocks until the Observable emits another item, then returns that item.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.next.png">
 */
public final class OperationNext {

    public static <T> Iterable<T> next(final Observable<T> items) {

        NextObserver<T> nextObserver = new NextObserver<T>();
        final NextIterator<T> nextIterator = new NextIterator<T>(nextObserver);

        items.materialize().subscribe(nextObserver);

        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return nextIterator;
            }
        };

    }

    private static class NextIterator<T> implements Iterator<T> {

        private final NextObserver<T> observer;

        private NextIterator(NextObserver<T> observer) {
            this.observer = observer;
        }

        @Override
        public boolean hasNext() {
            return !observer.isCompleted(false);
        }

        @Override
        public T next() {
            if (observer.isCompleted(true)) {
                throw new IllegalStateException("Observable is completed");
            }

            observer.await();

            try {
                return observer.takeNext();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw Exceptions.propagate(e);
            }

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read only iterator");
        }
    }

    private static class NextObserver<T> implements Observer<Notification<T>> {
        private final BlockingQueue<Notification<T>> buf = new ArrayBlockingQueue<Notification<T>>(1);
        private final AtomicBoolean waiting = new AtomicBoolean(false);

        @Override
        public void onCompleted() {
            // ignore
        }

        @Override
        public void onError(Throwable e) {
            // ignore
        }

        @Override
        public void onNext(Notification<T> args) {

            if (waiting.getAndSet(false) || !args.isOnNext()) {
                Notification<T> toOffer = args;
                while (!buf.offer(toOffer)) {
                    Notification<T> concurrentItem = buf.poll();

                    // in case if we won race condition with onComplete/onError method
                    if (!concurrentItem.isOnNext()) {
                        toOffer = concurrentItem;
                    }
                }
            }

        }

        public void await() {
            waiting.set(true);
        }

        public boolean isCompleted(boolean rethrowExceptionIfExists) {
            Notification<T> lastItem = buf.peek();
            if (lastItem == null) {
                return false;
            }

            if (lastItem.isOnError()) {
                if (rethrowExceptionIfExists) {
                    throw Exceptions.propagate(lastItem.getThrowable());
                } else {
                    return true;
                }
            }

            return lastItem.isOnCompleted();
        }

        public T takeNext() throws InterruptedException {
            Notification<T> next = buf.take();

            if (next.isOnError()) {
                throw Exceptions.propagate(next.getThrowable());
            }

            if (next.isOnCompleted()) {
                throw new IllegalStateException("Observable is completed");
            }

            return next.getValue();

        }

    }
}

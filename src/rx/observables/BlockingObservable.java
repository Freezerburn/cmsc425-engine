package rx.observables;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.operators.SafeObservableSubscription;
import rx.operators.SafeObserver;
import rx.operators.OperationMostRecent;
import rx.operators.OperationNext;
import rx.operators.OperationToFuture;
import rx.operators.OperationToIterator;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import rx.util.functions.Func1;
import rx.util.functions.FuncN;
import rx.util.functions.Functions;

/**
 * An extension of {@link Observable} that provides blocking operators.
 * <p>
 * You construct a BlockingObservable from an Observable with {@link #from(Observable)} or
 * {@link Observable#toBlockingObservable()}
 * <p>
 * The documentation for this interface makes use of a form of marble diagram that has been
 * modified to illustrate blocking operators. The following legend explains these marble diagrams:
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.legend.png">
 * <p>
 * For more information see the
 * <a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators">Blocking
 * Observable Operators</a> page at the RxJava Wiki.
 * 
 * @param <T>
 */
public class BlockingObservable<T> extends Observable<T> {

    protected BlockingObservable(Func1<Observer<T>, Subscription> onSubscribe) {
        super(onSubscribe);
    }
    
    /**
     * Used to prevent public instantiation
     */
    @SuppressWarnings("unused")
    private BlockingObservable() {
        // prevent public instantiation
    }

    /**
     * Convert an Observable into a BlockingObservable.
     */
    public static <T> BlockingObservable<T> from(final Observable<T> o) {
        return new BlockingObservable<T>(new Func1<Observer<T>, Subscription>() {

            @Override
            public Subscription call(Observer<T> observer) {
                return o.subscribe(observer);
            }
        });
    }

    /**
     * Returns an {@link Iterator} that iterates over all items emitted by a specified
     * {@link Observable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.toIterator.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param <T>
     *            the type of items emitted by the source {@link Observable}
     * @return an {@link Iterator} that can iterate over the items emitted by the {@link Observable}
     */
    public static <T> Iterator<T> toIterator(Observable<T> source) {
        return OperationToIterator.toIterator(source);
    }

    /**
     * Returns the last item emitted by a specified {@link Observable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.last.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @return the last item emitted by the source {@link Observable}
     */
    public static <T> T last(final Observable<T> source) {
        return from(source).last();
    }

    /**
     * Returns the last item emitted by an {@link Observable} that matches a given predicate.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.last.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the last item emitted by the {@link Observable} for which the predicate function
     *         returns <code>true</code>
     */
    public static <T> T last(final Observable<T> source, final Func1<T, Boolean> predicate) {
        return last(source.filter(predicate));
    }

    /**
     * Returns the last item emitted by an {@link Observable} that matches a given predicate.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.last.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the last item emitted by the {@link Observable} for which the predicate function
     *         returns <code>true</code>
     */
    public static <T> T last(final Observable<T> source, final Object predicate) {
        return last(source.filter(predicate));
    }

    /**
     * Returns the last item emitted by an {@link Observable}, or a default value if no item is
     * emitted.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.lastOrDefault.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no items
     * @param <T>
     *            the type of items emitted by the {@link Observable}
     * @return the last item emitted by an {@link Observable}, or the default value if no item is
     *         emitted
     */
    public static <T> T lastOrDefault(Observable<T> source, T defaultValue) {
        return from(source).lastOrDefault(defaultValue);
    }

    /**
     * Returns the last item emitted by an {@link Observable} that matches a given predicate, or a
     * default value if no such item is emitted.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.lastOrDefault.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @param <T>
     *            the type of items emitted by the {@link Observable}
     * @return the last item emitted by an {@link Observable} that matches the predicate, or the
     *         default value if no matching item is emitted
     */
    public static <T> T lastOrDefault(Observable<T> source, T defaultValue, Func1<T, Boolean> predicate) {
        return lastOrDefault(source.filter(predicate), defaultValue);
    }

    /**
     * Returns the last item emitted by an {@link Observable} that matches a given predicate, or a
     * default value if no such item is emitted.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.lastOrDefault.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @param <T>
     *            the type of items emitted by the {@link Observable}
     * @return the last item emitted by an {@link Observable} that matches the predicate, or the
     *         default value if no matching item is emitted
     */
    public static <T> T lastOrDefault(Observable<T> source, T defaultValue, Object predicate) {
        @SuppressWarnings("rawtypes")
        final FuncN _f = Functions.from(predicate);

        return lastOrDefault(source, defaultValue, new Func1<T, Boolean>() {
            @Override
            public Boolean call(T args) {
                return (Boolean) _f.call(args);
            }
        });
    }

    /**
     * Returns an {@link Iterable} that always returns the item most recently emitted by an
     * {@link Observable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.mostRecent.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param <T>
     *            the type of items emitted by the {@link Observable}
     * @param initialValue
     *            the initial value that will be yielded by the {@link Iterable} sequence if the
     *            {@link Observable} has not yet emitted an item
     * @return an {@link Iterable} that on each iteration returns the item that the
     *         {@link Observable} has most recently emitted
     */
    public static <T> Iterable<T> mostRecent(Observable<T> source, T initialValue) {
        return OperationMostRecent.mostRecent(source, initialValue);
    }

    /**
     * Returns an {@link Iterable} that blocks until the {@link Observable} emits another item,
     * then returns that item.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.next.png">
     * 
     * @param items
     *            the source {@link Observable}
     * @param <T>
     *            the type of items emitted by the {@link Observable}
     * @return an {@link Iterable} that blocks upon each iteration until the {@link Observable}
     *         emits a new item, whereupon the Iterable returns that item
     */
    public static <T> Iterable<T> next(Observable<T> items) {
        return OperationNext.next(items);
    }

    private static <T> T _singleOrDefault(BlockingObservable<T> source, boolean hasDefault, T defaultValue) {
        Iterator<T> it = source.toIterable().iterator();

        if (!it.hasNext()) {
            if (hasDefault) {
                return defaultValue;
            }
            throw new IllegalStateException("Expected single entry. Actually empty stream.");
        }

        T result = it.next();

        if (it.hasNext()) {
            throw new IllegalStateException("Expected single entry. Actually more than one entry.");
        }

        return result;
    }

    /**
     * If the {@link Observable} completes after emitting a single item, return that item,
     * otherwise throw an exception.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.single.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @return the single item emitted by the {@link Observable}
     * @throws IllegalStateException
     *             if the {@link Observable} does not emit exactly one item
     */
    public static <T> T single(Observable<T> source) {
        return from(source).single();
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a given
     * predicate, return that item, otherwise throw an exception.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.single.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the source {@link Observable} that matches the predicate
     * @throws IllegalStateException
     *             if the {@link Observable} does not emit exactly one item that matches the
     *             predicate
     */
    public static <T> T single(Observable<T> source, Func1<T, Boolean> predicate) {
        return from(source).single(predicate);
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a given
     * predicate, return that item, otherwise throw an exception.
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.single.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the source {@link Observable} that matches the predicate
     * @throws IllegalStateException
     *             if the {@link Observable} does not emit exactly one item that matches the
     *             predicate
     */
    public static <T> T single(Observable<T> source, Object predicate) {
        return from(source).single(predicate);
    }

    /**
     * If the {@link Observable} completes after emitting a single item, return that item, otherwise
     * return a default value.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.singleOrDefault.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no items
     * @return the single item emitted by the source {@link Observable}, or a default value if no
     *         value is emitted
     */
    public static <T> T singleOrDefault(Observable<T> source, T defaultValue) {
        return from(source).singleOrDefault(defaultValue);
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a given
     * predicate, return that item, otherwise return a default value.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.singleOrDefault.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the source {@link Observable} that matches the predicate,
     *         or a default value if no such value is emitted
     */
    public static <T> T singleOrDefault(Observable<T> source, T defaultValue, Func1<T, Boolean> predicate) {
        return from(source).singleOrDefault(defaultValue, predicate);
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a given
     * predicate, return that item, otherwise return a default value.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.singleOrDefault.p.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the source {@link Observable} that matches the predicate,
     *         or a default value if no such value is emitted
     */
    public static <T> T singleOrDefault(Observable<T> source, T defaultValue, Object predicate) {
        return from(source).singleOrDefault(defaultValue, predicate);
    }

    /**
     * Returns a {@link Future} representing the single value emitted by an {@link Observable}.
     * <p>
     * <code>toFuture()</code> throws an exception if the {@link Observable} emits more than one
     * item. If the Observable may emit more than item, use
     * {@link Observable#toList toList()}.toFuture()</code>.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.toFuture.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @return a Future that expects a single item to be emitted by the source {@link Observable}
     */
    public static <T> Future<T> toFuture(final Observable<T> source) {
        return OperationToFuture.toFuture(source);
    }

    /**
     * Converts an {@link Observable} into an {@link Iterable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.toIterable.png">
     * 
     * @param source
     *            the source {@link Observable}
     * @return an {@link Iterable} version of the underlying {@link Observable}
     */
    public static <T> Iterable<T> toIterable(final Observable<T> source) {
        return from(source).toIterable();
    }

    /**
     * Used for protecting against errors being thrown from {@link Observer} implementations and
     * ensuring onNext/onError/onCompleted contract compliance.
     * <p>
     * See https://github.com/Netflix/RxJava/issues/216 for discussion on "Guideline 6.4: Protect
     * calls to user code from within an operator"
     */
    private Subscription protectivelyWrapAndSubscribe(Observer<T> o) {
        SafeObservableSubscription subscription = new SafeObservableSubscription();
        return subscription.wrap(subscribe(new SafeObserver<T>(subscription, o)));
    }
    
    /**
     * Invoke a method on each item emitted by the {@link Observable}; block until the Observable
     * completes.
     * <p>
     * NOTE: This will block even if the Observable is asynchronous.
     * <p>
     * This is similar to {@link #subscribe(Observer)}, but it blocks. Because it blocks it does
     * not need the {@link Observer#onCompleted()} or {@link Observer#onError(Throwable)} methods.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.forEach.png">
     * 
     * @param onNext
     *            the {@link Action1} to invoke for every item emitted by the {@link Observable}
     * @throws RuntimeException
     *             if an error occurs
     */
    public void forEach(final Action1<T> onNext) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> exceptionFromOnError = new AtomicReference<Throwable>();

        /**
         * Wrapping since raw functions provided by the user are being invoked.
         * 
         * See https://github.com/Netflix/RxJava/issues/216 for discussion on "Guideline 6.4: Protect calls to user code from within an operator"
         */
        protectivelyWrapAndSubscribe(new Observer<T>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                /*
                 * If we receive an onError event we set the reference on the outer thread
                 * so we can git it and throw after the latch.await().
                 * 
                 * We do this instead of throwing directly since this may be on a different thread and the latch is still waiting.
                 */
                exceptionFromOnError.set(e);
                latch.countDown();
            }

            @Override
            public void onNext(T args) {
                onNext.call(args);
            }
        });
        // block until the subscription completes and then return
        try {
            latch.await();
        } catch (InterruptedException e) {
            // set the interrupted flag again so callers can still get it
            // for more information see https://github.com/Netflix/RxJava/pull/147#issuecomment-13624780
            Thread.currentThread().interrupt();
            // using Runtime so it is not checked
            throw new RuntimeException("Interrupted while waiting for subscription to complete.", e);
        }

        if (exceptionFromOnError.get() != null) {
            if (exceptionFromOnError.get() instanceof RuntimeException) {
                throw (RuntimeException) exceptionFromOnError.get();
            } else {
                throw new RuntimeException(exceptionFromOnError.get());
            }
        }
    }

    /**
     * Invoke a method on each item emitted by the {@link Observable}; block until the Observable
     * completes.
     * <p>
     * NOTE: This will block even if the Observable is asynchronous.
     * <p>
     * This is similar to {@link #subscribe(Observer)}, but it blocks. Because it blocks it does
     * not need the {@link Observer#onCompleted()} or {@link Observer#onError(Throwable)} methods.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.forEach.png">
     * 
     * @param o
     *            the {@link Action1} to invoke for every item emitted by the {@link Observable}
     * @throws RuntimeException
     *             if an error occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void forEach(final Object o) {
        if (o instanceof Action1) {
            // in case a dynamic language is not correctly handling the overloaded methods and we receive an Action1 just forward to the correct method.
            forEach((Action1) o);
        }

        // lookup and memoize onNext
        if (o == null) {
            throw new RuntimeException("onNext must be implemented");
        }
        final FuncN onNext = Functions.from(o);

        forEach(new Action1() {

            @Override
            public void call(Object args) {
                onNext.call(args);
            }

        });
    }
    
    /**
     * Returns an {@link Iterator} that iterates over all items emitted by a specified
     * {@link Observable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.getIterator.png">
     * 
     * @return an {@link Iterator} that can iterate over the items emitted by the {@link Observable}
     */
    public Iterator<T> getIterator() {
        return OperationToIterator.toIterator(this);
    }

    /**
     * Returns the last item emitted by a specified {@link Observable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.last.png">
     * 
     * @return the last item emitted by the source {@link Observable}
     */
    public T last() {
        T result = null;
        for (T value : toIterable()) {
            result = value;
        }
        return result;
    }

    /**
     * Returns the last item emitted by a specified {@link Observable} that matches a predicate.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.last.p.png">
     * 
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the last item emitted by the {@link Observable} that matches the predicate
     */
    public T last(final Func1<T, Boolean> predicate) {
        return last(this, predicate);
    }

    /**
     * Returns the last item emitted by a specified {@link Observable} that matches a predicate.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.last.p.png">
     * 
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the last item emitted by the {@link Observable} that matches the predicate
     */
    public T last(final Object predicate) {
        @SuppressWarnings("rawtypes")
        final FuncN _f = Functions.from(predicate);

        return last(this, new Func1<T, Boolean>() {
            @Override
            public Boolean call(T args) {
                return (Boolean) _f.call(args);
            }
        });
    }

    /**
     * Returns the last item emitted by a specified {@link Observable}, or a default value if no
     * items are emitted.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.lastOrDefault.png">
     * 
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no items
     * @return the last item emitted by the {@link Observable}, or the default value if no items
     *         are emitted
     */
    public T lastOrDefault(T defaultValue) {
        boolean found = false;
        T result = null;

        for (T value : toIterable()) {
            found = true;
            result = value;
        }

        if (!found) {
            return defaultValue;
        }

        return result;
    }

    /**
     * Returns the last item emitted by a specified {@link Observable} that matches a predicate, or
     * a default value if no such items are emitted.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.lastOrDefault.p.png">
     * 
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the last item emitted by the {@link Observable} that matches the predicate, or the
     *         default value if no matching items are emitted
     */
    public T lastOrDefault(T defaultValue, Func1<T, Boolean> predicate) {
        return lastOrDefault(this, defaultValue, predicate);
    }

    /**
     * Returns the last item emitted by a specified {@link Observable} that matches a predicate, or
     * a default value if no such items are emitted.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.lastOrDefault.p.png">
     * 
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the last item emitted by the {@link Observable} that matches the predicate, or the
     *         default value if no matching items are emitted
     */
    public T lastOrDefault(T defaultValue, Object predicate) {
        return lastOrDefault(this, defaultValue, predicate);
    }

    /**
     * Returns an {@link Iterable} that always returns the item most recently emitted by an
     * {@link Observable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.mostRecent.png">
     * 
     * @param initialValue
     *            the initial value that will be yielded by the {@link Iterable} sequence if the
     *            {@link Observable} has not yet emitted an item
     * @return an {@link Iterable} that on each iteration returns the item that the
     *         {@link Observable} has most recently emitted
     */
    public Iterable<T> mostRecent(T initialValue) {
        return mostRecent(this, initialValue);
    }

    /**
     * Returns an {@link Iterable} that blocks until the {@link Observable} emits another item,
     * then returns that item.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.next.png">
     * 
     * @return an {@link Iterable} that blocks upon each iteration until the {@link Observable}
     *         emits a new item, whereupon the Iterable returns that item
     */
    public Iterable<T> next() {
        return next(this);
    }

    /**
     * If the {@link Observable} completes after emitting a single item, return that item,
     * otherwise throw an exception.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.single.png">
     * 
     * @return the single item emitted by the {@link Observable}
     */
    public T single() {
        return _singleOrDefault(this, false, null);
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a given
     * predicate, return that item, otherwise throw an exception.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.single.p.png">
     * 
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the source {@link Observable} that matches the predicate
     */
    public T single(Func1<T, Boolean> predicate) {
        return _singleOrDefault(from(this.filter(predicate)), false, null);
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a given
     * predicate, return that item, otherwise throw an exception.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.single.p.png">
     * 
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the source {@link Observable} that matches the predicate
     */
    public T single(Object predicate) {
        @SuppressWarnings("rawtypes")
        final FuncN _f = Functions.from(predicate);

        return single(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return (Boolean) _f.call(t);
            }
        });
    }

    /**
     * If the {@link Observable} completes after emitting a single item, return that item; if it
     * emits more than one item, throw an exception; if it emits no items, return a default value.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.singleOrDefault.png">
     * 
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no items
     * @return the single item emitted by the {@link Observable}, or the default value if no items
     *         are emitted
     */
    public T singleOrDefault(T defaultValue) {
        return _singleOrDefault(this, true, defaultValue);
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a predicate,
     * return that item; if it emits more than one such item, throw an exception; if it emits no
     * items, return a default value.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.singleOrDefault.p.png">
     * 
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the {@link Observable} that matches the predicate, or the
     *         default value if no such items are emitted
     */
    public T singleOrDefault(T defaultValue, Func1<T, Boolean> predicate) {
        return _singleOrDefault(from(this.filter(predicate)), true, defaultValue);
    }

    /**
     * If the {@link Observable} completes after emitting a single item that matches a predicate,
     * return that item; if it emits more than one such item, throw an exception; if it emits no
     * items, return a default value.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.singleOrDefault.p.png">
     * 
     * @param defaultValue
     *            a default value to return if the {@link Observable} emits no matching items
     * @param predicate
     *            a predicate function to evaluate items emitted by the {@link Observable}
     * @return the single item emitted by the {@link Observable} that matches the predicate, or the
     *         default value if no such items are emitted
     */
    public T singleOrDefault(T defaultValue, final Object predicate) {
        @SuppressWarnings("rawtypes")
        final FuncN _f = Functions.from(predicate);

        return singleOrDefault(defaultValue, new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return (Boolean) _f.call(t);
            }
        });
    }

    /**
     * Returns a {@link Future} representing the single value emitted by an {@link Observable}.
     * <p>
     * <code>toFuture()</code> throws an exception if the Observable emits more than one item. If
     * the Observable may emit more than item, use
     * {@link Observable#toList toList()}.toFuture()</code>.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.toFuture.png">
     * 
     * @return a {@link Future} that expects a single item to be emitted by the source
     *         {@link Observable}
     */
    public Future<T> toFuture() {
        return toFuture(this);
    }

    /**
     * Converts an {@link Observable} into an {@link Iterable}.
     * <p>
     * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/B.toIterable.png">
     * 
     * @return an {@link Iterable} version of the underlying {@link Observable}
     */
    public Iterable<T> toIterable() {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return getIterator();
            }
        };
    }
}

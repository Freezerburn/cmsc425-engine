package rx.operators;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Func1;

/**
 * Converts a Future into an Observable.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/from.Future.png">
 * <p>
 * You can convert any object that supports the Future interface into an Observable that emits the
 * return value of the get() method of that object, by using the from operation.
 * <p>
 * This is blocking so the Subscription returned when calling
 * <code>Observable.subscribe(Observer)</code> does nothing.
 */
public class OperationToObservableFuture {
    private static class ToObservableFuture<T> implements Func1<Observer<T>, Subscription> {
        private final Future<T> that;
        private final Long time;
        private final TimeUnit unit;

        public ToObservableFuture(Future<T> that) {
            this.that = that;
            this.time = null;
            this.unit = null;
        }

        public ToObservableFuture(Future<T> that, long time, TimeUnit unit) {
            this.that = that;
            this.time = time;
            this.unit = unit;
        }

        @Override
        public Subscription call(Observer<T> observer) {
            try {
                T value = (time == null) ? that.get() : that.get(time, unit);

                if (!that.isCancelled()) {
                    observer.onNext(value);
                }
                observer.onCompleted();
            } catch (Throwable e) {
                observer.onError(e);
            }

            // the get() has already completed so there is no point in
            // giving the user a way to cancel.
            return Subscriptions.empty();
        }
    }

    public static <T> Func1<Observer<T>, Subscription> toObservableFuture(final Future<T> that) {
        return new ToObservableFuture<T>(that);
    }

    public static <T> Func1<Observer<T>, Subscription> toObservableFuture(final Future<T> that, long time, TimeUnit unit) {
        return new ToObservableFuture<T>(that, time, unit);
    }
}

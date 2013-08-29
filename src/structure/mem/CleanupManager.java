package structure.mem;

import main.GameApplicationDisplay;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: vince_000
 * Date: 7/7/13
 * Time: 10:30 PM
 */
public class CleanupManager {
    private static final ReferenceQueue<Object> Q = new ReferenceQueue<>();
    private static final HashMap<Reference<? extends Object>, Runnable> toOnCleanup = new HashMap<>();

    static {
        GameApplicationDisplay.Q.subscribeToUpdates(CleanupManager::update);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            update();
            Collection<Runnable> toRun = toOnCleanup.values();
            int unfreed = toRun.size();
            if(unfreed > 0) {
                System.out.println("[ERROR] Unfreed objects in CleanupManager.");
                System.out.println("[ERROR] This either indicates a memory leak due to hard");
                System.out.println("[ERROR] references to an object, or that a GC cycle has not run yet.");
                System.out.println("[ERROR] Please make sure you are careful with what you put on the Q.");
            }
            for(Runnable r : toRun) {
                System.out.println("[ERROR] Running cleanup for unfreed object: " + r);
                try {
                    r.run();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public static void update() {
        Reference<? extends Object> ref;
        while((ref = Q.poll()) != null) {
            Runnable r = toOnCleanup.get(ref);
            r.run();
            toOnCleanup.remove(ref);
        }
    }

    public static void runOnGc(Object obj, Runnable toRun) {
        /*
        Note that you need to be a bit careful with the Runnable passed here. If you create a lambda
        Runnable, it typically includes things such as "this" inside of the closure. You'll need to
        make sure you do not reference stuff that is part of the class that the lambda is created in,
        otherwise you might end up with NullPointer errors, as the Runnable will only be used when the
        passed object is fully removed from memory.

        ...actually, here's a good question: will the lambda closure prevent the garbage collector from
        cleaning up the object that created it? Will there be a hard reference to the object there?
        Testing is needed.
        ONLY IF YOU REFER TO A CLASS VARIABLE. BE SUPER CAREFUL WITH THIS OR YOU /WILL/ LEAK MEMORY.
         */
        PhantomReference<Object> phantom = new PhantomReference<>(obj, Q);
        toOnCleanup.put(phantom, toRun);
    }
}

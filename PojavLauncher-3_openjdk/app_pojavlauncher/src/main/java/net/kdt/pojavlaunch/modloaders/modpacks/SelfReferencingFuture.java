package net.kdt.pojavlaunch.modloaders.modpacks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SelfReferencingFuture {
    public interface FutureInterface {
        void run(Future<?> self);
    }

    private final FutureInterface task;
    private Future<?> future;

    public SelfReferencingFuture(FutureInterface task) {
        this.task = task;
    }

    public Future<?> startOnExecutor(ExecutorService executor) {
        future = executor.submit(() -> task.run(future));
        return future;
    }
}

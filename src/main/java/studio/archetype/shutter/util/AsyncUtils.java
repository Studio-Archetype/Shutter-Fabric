package studio.archetype.shutter.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public final class AsyncUtils {

    private static final List<TickJob<?>> jobs = new ArrayList<>();

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register((s) -> jobs.removeIf(TickJob::tick));
        Runtime.getRuntime().addShutdownHook(new Thread(AsyncUtils::cancelAll));
    }

    public static <T> TickJob<T> queueAsync(CompletableFuture<T> future, Consumer<T> onFinish) {
        TickJob<T> job = new TickJob<>(future, onFinish);
        jobs.add(job);
        return job;
    }

    public static <T> TickJob<T> queueAsync(CompletableFuture<T> future, Consumer<T> onFinish, Runnable onTick) {
        TickJob<T> job = new TickJob<>(future, onFinish, onTick);
        jobs.add(job);
        return job;
    }

    public static <T> TickJob<T> queueAsync(CompletableFuture<T> future, Consumer<T> onFinish, Consumer<Exception> onError) {
        TickJob<T> job = new TickJob<>(future, onFinish, onError);
        jobs.add(job);
        return job;
    }

    public static <T> TickJob<T> queueAsync(CompletableFuture<T> future, Consumer<T> onFinish, Consumer<Exception> onError, Runnable onTick) {
        TickJob<T> job = new TickJob<>(future, onFinish, onError, onTick);
        jobs.add(job);
        return job;
    }

    public static void cancelAll() {
        jobs.forEach(TickJob::cancel);
    }

    public static final class TickJob<T> {

        private final CompletableFuture<T> future;
        private final Consumer<T> onFinish;
        private final Consumer<Exception> onError;
        private final Runnable onTick;

        private TickJob(CompletableFuture<T> future, Consumer<T> onFinish) {
            this(future, onFinish, Throwable::printStackTrace, null);
        }

        private TickJob(CompletableFuture<T> future, Consumer<T> onFinish, Runnable onTick) {
            this(future, onFinish, Throwable::printStackTrace, onTick);
        }

        private TickJob(CompletableFuture<T> future, Consumer<T> onFinish, Consumer<Exception> onError) {
            this(future, onFinish, onError, null);
        }

        private TickJob(CompletableFuture<T> future, Consumer<T> onFinish, Consumer<Exception> onError, Runnable onTick) {
            this.future = future;
            this.onFinish = onFinish;
            this.onError = onError;
            this.onTick = onTick;
        }

        private boolean tick() {
            try {
                if(isDone()) {
                    this.onFinish.accept(future.get());
                    return true;
                } else {
                    if(onTick != null)
                        this.onTick.run();
                    return false;
                }
            } catch(InterruptedException | ExecutionException e) {
                this.onError.accept(e);
                return true;
            }
        }

        public boolean isDone() {
            return future.isDone();
        }

        public void cancel() {
            this.future.cancel(true);
        }
    }
}

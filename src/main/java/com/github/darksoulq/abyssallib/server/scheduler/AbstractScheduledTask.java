package com.github.darksoulq.abyssallib.server.scheduler;

import org.bukkit.entity.Entity;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class AbstractScheduledTask {
    protected final CompletableFuture<Void> completion = new CompletableFuture<>();
    protected volatile boolean cancelled = false;
    protected volatile boolean running = false;
    protected volatile Throwable failure = null;
    protected volatile Instant nextExecution = Instant.now();

    private final Supplier<Boolean> until;
    private final Supplier<Boolean> whileCond;
    private final Entity entity;

    public AbstractScheduledTask(Supplier<Boolean> until, Supplier<Boolean> whileCond, Entity entity) {
        this.until = until;
        this.whileCond = whileCond;
        this.entity = entity;
    }

    public Runnable getWrappedRunnable(Runnable action, long delayMillis, long periodMillis) {
        nextExecution = Instant.now().plusMillis(delayMillis);
        return () -> {
            if (cancelled) return;
            if (entity != null && !entity.isValid()) { cancel(); return; }
            if (until != null && until.get()) { cancel(); return; }
            if (whileCond != null && !whileCond.get()) { cancel(); return; }

            running = true;
            try {
                action.run();
            } catch (Throwable t) {
                failure = t;
                cancel();
                completion.completeExceptionally(t);
            } finally {
                running = false;
                if (!cancelled) {
                    if (periodMillis <= 0) {
                        cancel();
                    } else {
                        nextExecution = Instant.now().plusMillis(periodMillis);
                    }
                }
            }
        };
    }

    public void cancel() {
        cancelled = true;
        if (!completion.isDone()) completion.complete(null);
    }

    public boolean isCancelled() { return cancelled; }
    public boolean isRunning() { return running; }
    public Instant nextExecution() { return nextExecution; }
    public Optional<Throwable> failure() { return Optional.ofNullable(failure); }
    public CompletionStage<Void> completion() { return completion; }
}
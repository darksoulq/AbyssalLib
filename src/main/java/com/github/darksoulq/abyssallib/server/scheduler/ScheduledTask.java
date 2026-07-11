package com.github.darksoulq.abyssallib.server.scheduler;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ScheduledTask {
    void cancel();

    boolean isCancelled();

    boolean isRunning();

    Instant nextExecution();

    Optional<Throwable> failure();

    CompletionStage<Void> completion();
}
package com.github.darksoulq.abyssallib.server.cooldown;

import com.github.darksoulq.abyssallib.server.scheduler.Scheduler;
import com.github.darksoulq.abyssallib.server.scheduler.TimeUnit;
import net.kyori.adventure.key.Key;

import java.util.function.BiConsumer;

public sealed interface CooldownResult permits CooldownResult.Ready, CooldownResult.Cooling {

    boolean isReady();

    CooldownResult ifReady(Runnable action);

    CooldownResult ifCoolingLeft(BiConsumer<Long, TimeUnit> action);

    CooldownResult onExpire(Runnable action);

    CooldownResult onTick(long interval, TimeUnit unit, BiConsumer<Long, TimeUnit> action);

    record Ready() implements CooldownResult {
        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public CooldownResult ifReady(Runnable action) {
            action.run();
            return this;
        }

        @Override
        public CooldownResult ifCoolingLeft(BiConsumer<Long, TimeUnit> action) {
            return this;
        }

        @Override
        public CooldownResult onExpire(Runnable action) {
            return this;
        }

        @Override
        public CooldownResult onTick(long interval, TimeUnit unit, BiConsumer<Long, TimeUnit> action) {
            return this;
        }
    }

    record Cooling(long remaining, TimeUnit unit, CooldownScope scope, Key id, Cooldown cooldown, Scheduler scheduler) implements CooldownResult {
        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public CooldownResult ifReady(Runnable action) {
            return this;
        }

        @Override
        public CooldownResult ifCoolingLeft(BiConsumer<Long, TimeUnit> action) {
            action.accept(remaining, unit);
            return this;
        }

        @Override
        public CooldownResult onExpire(Runnable action) {
            scheduler.schedule(() -> {
                CooldownResult current = cooldown.test(scope, id);
                if (current.isReady()) {
                    action.run();
                } else if (current instanceof Cooling c) {
                    c.onExpire(action);
                }
            }).after(remaining, unit, cooldown.getClock()).once();
            return this;
        }

        @Override
        public CooldownResult onTick(long interval, TimeUnit tickUnit, BiConsumer<Long, TimeUnit> action) {
            scheduler.schedule(() -> {
                CooldownResult current = cooldown.test(scope, id);
                if (current instanceof Cooling c) {
                    action.accept(c.remaining(), c.unit());
                }
            })
            .repeatWhile(() -> !cooldown.test(scope, id).isReady())
            .repeatEvery(interval, tickUnit, cooldown.getClock());
            return this;
        }
    }
}
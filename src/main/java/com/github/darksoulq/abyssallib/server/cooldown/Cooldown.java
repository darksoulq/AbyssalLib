package com.github.darksoulq.abyssallib.server.cooldown;

import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.server.scheduler.Scheduler;
import com.github.darksoulq.abyssallib.server.scheduler.TimeUnit;
import net.kyori.adventure.key.Key;

public class Cooldown {

    private final CooldownManager manager;
    private final Clock clock;
    private final Scheduler scheduler;

    public Cooldown(Scheduler scheduler, Clock clock) {
        this.scheduler = scheduler;
        this.clock = clock;
        this.manager = new CooldownManager();
    }

    public Clock getClock() {
        return clock;
    }

    public CooldownResult acquire(CooldownScope context, Key id, long duration, TimeUnit unit, CooldownPolicy policy) {
        long now = clock.now();
        long expiry = manager.getExpiry(context, id);
        long durationInClockUnit = unit.convert(duration, clock.unit());

        if (now < expiry) {
            if (policy == CooldownPolicy.OVERRIDE) {
                manager.setExpiry(context, id, now + durationInClockUnit);
            } else if (policy == CooldownPolicy.ACCUMULATE) {
                manager.setExpiry(context, id, expiry + durationInClockUnit);
            }
            long newExpiry = manager.getExpiry(context, id);
            return new CooldownResult.Cooling(newExpiry - now, clock.unit(), context, id, this, scheduler);
        }

        manager.setExpiry(context, id, now + durationInClockUnit);
        return new CooldownResult.Ready();
    }

    public CooldownResult acquire(CooldownScope context, Key id, long duration, TimeUnit unit) {
        return acquire(context, id, duration, unit, CooldownPolicy.KEEP);
    }

    public CooldownResult acquire(CooldownScope context, CooldownType type) {
        return acquire(context, type.id(), type.defaultDuration(), type.defaultUnit(), type.defaultPolicy());
    }

    public CooldownResult test(CooldownScope context, Key id) {
        long now = clock.now();
        long expiry = manager.getExpiry(context, id);

        if (now < expiry) {
            return new CooldownResult.Cooling(expiry - now, clock.unit(), context, id, this, scheduler);
        }

        return new CooldownResult.Ready();
    }

    public void reset(CooldownScope context, Key id) {
        manager.setExpiry(context, id, 0);
    }

    public void reset(CooldownScope context) {
        manager.clear(context);
    }

    public void cleanup() {
        manager.cleanup(clock.now());
    }
}
package com.github.darksoulq.abyssallib.world.particle.timeline;

import com.github.darksoulq.abyssallib.world.particle.Transformer;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Timeline implements Transformer {

    private final long totalDuration;
    private final boolean loop;
    private final List<Keyframe> keyframes;

    private Timeline(long totalDuration, boolean loop, List<Keyframe> keyframes) {
        this.totalDuration = totalDuration;
        this.loop = loop;
        this.keyframes = keyframes;
        this.keyframes.sort(Comparator.comparingLong(k -> k.startTime));
    }

    @Override
    public Vector transform(Vector input, long globalTick) {
        long time = globalTick;
        if (loop && totalDuration > 0) {
            time = globalTick % totalDuration;
        }
        if (!loop && time >= totalDuration) {
            return input;
        }

        Vector current = input;

        for (Keyframe kf : keyframes) {
            if (time >= kf.startTime && time < kf.endTime) {
                long relativeTick = time - kf.startTime;
                current = kf.transformer.transform(current, relativeTick);
            }
        }

        return current;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Keyframe {
        final long startTime;
        final long endTime;
        final Transformer transformer;

        Keyframe(long start, long duration, Transformer transformer) {
            this.startTime = start;
            this.endTime = start + duration;
            this.transformer = transformer;
        }
    }

    public static class Builder {
        private long duration = 0;
        private boolean loop = true;
        private final List<Keyframe> keyframes = new ArrayList<>();

        public Builder duration(long ticks) {
            this.duration = ticks;
            return this;
        }

        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public Builder add(long start, long duration, Transformer transformer) {
            this.keyframes.add(new Keyframe(start, duration, transformer));
            if (start + duration > this.duration) {
                this.duration = start + duration;
            }
            return this;
        }

        public Timeline build() {
            return new Timeline(duration, loop, keyframes);
        }
    }
}
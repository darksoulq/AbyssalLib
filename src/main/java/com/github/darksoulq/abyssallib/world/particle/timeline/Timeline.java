package com.github.darksoulq.abyssallib.world.particle.timeline;

import com.github.darksoulq.abyssallib.world.particle.Transformer;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A composite {@link Transformer} that sequences multiple transformations over a temporal axis.
 * <p>
 * The Timeline manages a collection of {@link Keyframe}s, each containing a specific
 * transformer and a time window. During the transformation process, it determines
 * which transformers are active based on the current tick and applies them sequentially.
 * </p>
 */
public class Timeline implements Transformer {

    /** The total length of the timeline in server ticks. */
    private final long totalDuration;
    /** Whether the timeline should restart from zero once {@link #totalDuration} is reached. */
    private final boolean loop;
    /** The sorted list of keyframes defining the animation sequence. */
    private final List<Keyframe> keyframes;

    /**
     * Private constructor for the Timeline.
     *
     * @param totalDuration The total lifespan of the animation sequence in ticks.
     * @param loop          True if the animation should repeat indefinitely.
     * @param keyframes     The list of defined animation segments.
     */
    private Timeline(long totalDuration, boolean loop, List<Keyframe> keyframes) {
        this.totalDuration = totalDuration;
        this.loop = loop;
        this.keyframes = keyframes;
        this.keyframes.sort(Comparator.comparingLong(k -> k.startTime));
    }

    /**
     * Applies all active keyframe transformations to the input vector based on the global tick.
     * <p>
     * If {@link #loop} is enabled, the global tick is wrapped using a modulo operation
     * against the {@link #totalDuration}.
     * </p>
     *
     * @param input      The original coordinate {@link Vector}.
     * @param globalTick The current absolute age of the particle effect in ticks.
     * @return The resulting {@link Vector} after all valid keyframe transformations are applied.
     */
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

    /**
     * Creates a new builder instance for constructing a timeline.
     *
     * @return A new {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Internal representation of a specific animation segment.
     */
    private static class Keyframe {
        /** The tick at which this transformation begins. */
        final long startTime;
        /** The tick at which this transformation ends (exclusive). */
        final long endTime;
        /** The underlying logic to apply during this window. */
        final Transformer transformer;

        /**
         * @param start       The start tick.
         * @param duration    The duration of the window in ticks.
         * @param transformer The transformer logic.
         */
        Keyframe(long start, long duration, Transformer transformer) {
            this.startTime = start;
            this.endTime = start + duration;
            this.transformer = transformer;
        }
    }

    /**
     * Fluent builder for the {@link Timeline} class.
     */
    public static class Builder {
        /** The calculated or explicit duration of the total timeline. */
        private long duration = 0;
        /** The looping state. Defaults to true. */
        private boolean loop = true;
        /** The staging list for keyframes. */
        private final List<Keyframe> keyframes = new ArrayList<>();

        /**
         * Sets the total duration of the timeline.
         * Note: Adding a keyframe that exceeds this will automatically increase the duration.
         *
         * @param ticks The duration in ticks.
         * @return This builder.
         */
        public Builder duration(long ticks) {
            this.duration = ticks;
            return this;
        }

        /**
         * Sets whether the timeline should loop.
         *
         * @param loop True to loop, false to stop at the end.
         * @return This builder.
         */
        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        /**
         * Adds a transformation segment to the timeline.
         *
         * @param start       The tick at which the transformation should begin.
         * @param duration    The number of ticks the transformation should remain active.
         * @param transformer The {@link Transformer} logic to apply.
         * @return This builder.
         */
        public Builder add(long start, long duration, Transformer transformer) {
            this.keyframes.add(new Keyframe(start, duration, transformer));
            if (start + duration > this.duration) {
                this.duration = start + duration;
            }
            return this;
        }

        /**
         * Constructs the final {@link Timeline} instance.
         *
         * @return The configured Timeline.
         */
        public Timeline build() {
            return new Timeline(duration, loop, keyframes);
        }
    }
}
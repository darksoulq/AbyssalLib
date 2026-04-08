package com.github.darksoulq.abyssallib.world.advancement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Enumerates the visual border styles (frames) for advancements.
 */
public enum AdvancementFrame {
    /** The standard rectangular frame. */
    TASK,
    /** The rounded goal frame. */
    GOAL,
    /** The spiked challenge frame. */
    CHALLENGE;

    /**
     * Codec for serializing and deserializing advancement frames via enum names.
     */
    public static final Codec<AdvancementFrame> CODEC = Codec.enumCodec(AdvancementFrame.class);
}
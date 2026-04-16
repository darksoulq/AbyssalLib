package com.github.darksoulq.abyssallib.common.model.blockbench.animation;

import org.joml.Vector3f;

public record BBKeyframe(String uuid, String channel, float time, int color, BBEasing interpolation, boolean uniform,
                         Vector3f dataPoint, Vector3f bezierLeftTime, Vector3f bezierLeftValue,
                         Vector3f bezierRightTime, Vector3f bezierRightValue) {
}
package com.github.darksoulq.abyssallib.common.model.blockbench.animation;

import org.joml.Vector3f;

public class BBKeyframe {
    private final String uuid;
    private final String channel;
    private final float time;
    private final int color;
    private final BBEasing interpolation;
    private final boolean uniform;
    private final Vector3f dataPoint;
    private final Vector3f bezierLeftTime;
    private final Vector3f bezierLeftValue;
    private final Vector3f bezierRightTime;
    private final Vector3f bezierRightValue;

    public BBKeyframe(String uuid, String channel, float time, int color, BBEasing interpolation, boolean uniform,
                      Vector3f dataPoint, Vector3f bezierLeftTime, Vector3f bezierLeftValue,
                      Vector3f bezierRightTime, Vector3f bezierRightValue) {
        this.uuid = uuid;
        this.channel = channel;
        this.time = time;
        this.color = color;
        this.interpolation = interpolation;
        this.uniform = uniform;
        this.dataPoint = dataPoint;
        this.bezierLeftTime = bezierLeftTime;
        this.bezierLeftValue = bezierLeftValue;
        this.bezierRightTime = bezierRightTime;
        this.bezierRightValue = bezierRightValue;
    }

    public String getUuid() { return uuid; }
    public String getChannel() { return channel; }
    public float getTime() { return time; }
    public int getColor() { return color; }
    public BBEasing getInterpolation() { return interpolation; }
    public boolean isUniform() { return uniform; }
    public Vector3f getDataPoint() { return dataPoint; }
    public Vector3f getBezierLeftTime() { return bezierLeftTime; }
    public Vector3f getBezierLeftValue() { return bezierLeftValue; }
    public Vector3f getBezierRightTime() { return bezierRightTime; }
    public Vector3f getBezierRightValue() { return bezierRightValue; }
}
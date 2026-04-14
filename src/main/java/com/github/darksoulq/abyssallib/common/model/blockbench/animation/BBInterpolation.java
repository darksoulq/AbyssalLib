package com.github.darksoulq.abyssallib.common.model.blockbench.animation;

import org.joml.Vector3f;

public final class BBInterpolation {

    private BBInterpolation() {}

    public static void interpolate(BBKeyframe before, BBKeyframe a, BBKeyframe b, BBKeyframe after, float time, Vector3f dest) {
        if (a.getInterpolation() == BBEasing.STEP || b == null) {
            dest.set(a.getDataPoint());
            return;
        }

        float t = (time - a.getTime()) / (b.getTime() - a.getTime());

        switch (a.getInterpolation()) {
            case LINEAR -> dest.set(a.getDataPoint()).lerp(b.getDataPoint(), t);
            case SMOOTH -> {
                Vector3f p0 = before != null ? before.getDataPoint() : a.getDataPoint();
                Vector3f p3 = after != null ? after.getDataPoint() : b.getDataPoint();
                catmullRom(p0, a.getDataPoint(), b.getDataPoint(), p3, t, dest);
            }
            case BEZIER -> cubicBezier(a, b, t, dest);
            default -> dest.set(a.getDataPoint());
        }
    }

    private static void cubicBezier(BBKeyframe a, BBKeyframe b, float t, Vector3f dest) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        Vector3f p0 = a.getDataPoint();
        Vector3f p1 = a.getBezierRightValue() != null && a.getBezierRightValue().lengthSquared() > 0 ? a.getBezierRightValue() : p0;
        Vector3f p2 = b.getBezierLeftValue() != null && b.getBezierLeftValue().lengthSquared() > 0 ? b.getBezierLeftValue() : b.getDataPoint();
        Vector3f p3 = b.getDataPoint();

        dest.x = uuu * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + ttt * p3.x;
        dest.y = uuu * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + ttt * p3.y;
        dest.z = uuu * p0.z + 3 * uu * t * p1.z + 3 * u * tt * p2.z + ttt * p3.z;
    }

    private static void catmullRom(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t, Vector3f dest) {
        float t2 = t * t;
        float t3 = t2 * t;

        dest.x = 0.5f * ((2 * p1.x) + (-p0.x + p2.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 + (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3);
        dest.y = 0.5f * ((2 * p1.y) + (-p0.y + p2.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 + (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3);
        dest.z = 0.5f * ((2 * p1.z) + (-p0.z + p2.z) * t + (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t2 + (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t3);
    }
}
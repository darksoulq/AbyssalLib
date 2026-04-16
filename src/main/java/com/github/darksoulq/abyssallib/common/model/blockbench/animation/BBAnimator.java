package com.github.darksoulq.abyssallib.common.model.blockbench.animation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BBAnimator {
    private final String name;
    private final String uuid;
    private final String type;
    private final boolean rotationGlobal;
    private final boolean quaternionInterpolation;
    private final List<BBKeyframe> position = new ArrayList<>();
    private final List<BBKeyframe> rotation = new ArrayList<>();
    private final List<BBKeyframe> scale = new ArrayList<>();

    public BBAnimator(String name, String uuid, String type, boolean rotationGlobal, boolean quaternionInterpolation) {
        this.name = name;
        this.uuid = uuid;
        this.type = type;
        this.rotationGlobal = rotationGlobal;
        this.quaternionInterpolation = quaternionInterpolation;
    }

    public void addKeyframe(BBKeyframe keyframe) {
        switch (keyframe.channel()) {
            case "position" -> position.add(keyframe);
            case "rotation" -> rotation.add(keyframe);
            case "scale" -> scale.add(keyframe);
        }
    }

    public void sort() {
        position.sort(Comparator.comparingDouble(BBKeyframe::time));
        rotation.sort(Comparator.comparingDouble(BBKeyframe::time));
        scale.sort(Comparator.comparingDouble(BBKeyframe::time));
    }

    public String getName() { return name; }
    public String getUuid() { return uuid; }
    public String getType() { return type; }
    public boolean isRotationGlobal() { return rotationGlobal; }
    public boolean isQuaternionInterpolation() { return quaternionInterpolation; }
    public List<BBKeyframe> getPosition() { return position; }
    public List<BBKeyframe> getRotation() { return rotation; }
    public List<BBKeyframe> getScale() { return scale; }
}
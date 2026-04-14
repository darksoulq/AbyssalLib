package com.github.darksoulq.abyssallib.common.model.blockbench.animation;

import java.util.HashMap;
import java.util.Map;

public class BBAnimation {
    private final String uuid;
    private final String name;
    private final String loop;
    private final boolean override;
    private final float length;
    private final int snapping;
    private final String blendWeight;
    private final String startDelay;
    private final String loopDelay;
    private final Map<String, BBAnimator> animators = new HashMap<>();

    public BBAnimation(String uuid, String name, String loop, boolean override, float length, int snapping, String blendWeight, String startDelay, String loopDelay) {
        this.uuid = uuid;
        this.name = name;
        this.loop = loop;
        this.override = override;
        this.length = length;
        this.snapping = snapping;
        this.blendWeight = blendWeight;
        this.startDelay = startDelay;
        this.loopDelay = loopDelay;
    }

    public String getUuid() { return uuid; }
    public String getName() { return name; }
    public String getLoop() { return loop; }
    public boolean isOverride() { return override; }
    public float getLength() { return length; }
    public int getSnapping() { return snapping; }
    public String getBlendWeight() { return blendWeight; }
    public String getStartDelay() { return startDelay; }
    public String getLoopDelay() { return loopDelay; }
    public Map<String, BBAnimator> getAnimators() { return animators; }
}
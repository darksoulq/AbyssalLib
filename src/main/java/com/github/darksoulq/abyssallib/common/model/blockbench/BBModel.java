package com.github.darksoulq.abyssallib.common.model.blockbench;

import com.github.darksoulq.abyssallib.common.model.blockbench.animation.BBAnimation;
import com.github.darksoulq.abyssallib.common.model.blockbench.meta.BBMeta;
import com.github.darksoulq.abyssallib.common.model.blockbench.meta.BBResolution;
import com.github.darksoulq.abyssallib.common.model.blockbench.texture.BBTexture;
import com.github.darksoulq.abyssallib.common.model.blockbench.tree.BBElement;
import com.github.darksoulq.abyssallib.common.model.blockbench.tree.BBGroup;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BBModel {
    private BBMeta meta;
    private String name;
    private String modelIdentifier;
    private Vector3f visibleBox;
    private BBResolution resolution;

    private final List<BBGroup> rootGroups = new ArrayList<>();
    private final Map<String, BBGroup> groupCache = new HashMap<>();
    private final Map<String, BBElement> elements = new HashMap<>();
    private final Map<String, BBTexture> textures = new HashMap<>();
    private final Map<String, BBAnimation> animations = new HashMap<>();

    public BBMeta getMeta() { return meta; }
    public void setMeta(BBMeta meta) { this.meta = meta; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModelIdentifier() { return modelIdentifier; }
    public void setModelIdentifier(String modelIdentifier) { this.modelIdentifier = modelIdentifier; }

    public Vector3f getVisibleBox() { return visibleBox; }
    public void setVisibleBox(Vector3f visibleBox) { this.visibleBox = visibleBox; }

    public BBResolution getResolution() { return resolution; }
    public void setResolution(BBResolution resolution) { this.resolution = resolution; }

    public List<BBGroup> getRootGroups() { return rootGroups; }
    public Map<String, BBGroup> getGroupCache() { return groupCache; }
    public Map<String, BBElement> getElements() { return elements; }
    public Map<String, BBTexture> getTextures() { return textures; }
    public Map<String, BBAnimation> getAnimations() { return animations; }
}
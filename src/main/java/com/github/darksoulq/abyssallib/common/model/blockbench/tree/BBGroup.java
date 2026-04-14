package com.github.darksoulq.abyssallib.common.model.blockbench.tree;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BBGroup {
    private final String uuid;
    private final String name;
    private final boolean export;
    private final boolean locked;
    private final int color;
    private final boolean shade;
    private final boolean mirrorUv;
    private final boolean visibility;
    private final int autouv;
    private final Vector3f origin;
    private final Vector3f rotation;
    private final List<BBGroup> children = new ArrayList<>();
    private final List<String> childElements = new ArrayList<>();

    public BBGroup(String uuid, String name, boolean export, boolean locked, int color, boolean shade, boolean mirrorUv, boolean visibility, int autouv, Vector3f origin, Vector3f rotation) {
        this.uuid = uuid;
        this.name = name;
        this.export = export;
        this.locked = locked;
        this.color = color;
        this.shade = shade;
        this.mirrorUv = mirrorUv;
        this.visibility = visibility;
        this.autouv = autouv;
        this.origin = origin;
        this.rotation = rotation;
    }

    public String getUuid() { return uuid; }
    public String getName() { return name; }
    public boolean isExport() { return export; }
    public boolean isLocked() { return locked; }
    public int getColor() { return color; }
    public boolean isShade() { return shade; }
    public boolean isMirrorUv() { return mirrorUv; }
    public boolean isVisibility() { return visibility; }
    public int getAutouv() { return autouv; }
    public Vector3f getOrigin() { return origin; }
    public Vector3f getRotation() { return rotation; }
    public List<BBGroup> getChildren() { return children; }
    public List<String> getChildElements() { return childElements; }
}
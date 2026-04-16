package com.github.darksoulq.abyssallib.common.model.blockbench.tree;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class BBElement {
    private final String uuid;
    private final String name;
    private final String type;
    private final boolean boxUv;
    private final String renderOrder;
    private final boolean locked;
    private final boolean export;
    private final int color;
    private final int autouv;
    private final Vector3f from;
    private final Vector3f to;
    private final Vector3f origin;
    private final Vector3f rotation;
    private final Map<String, BBFace> faces = new HashMap<>();

    public BBElement(String uuid, String name, String type, boolean boxUv, String renderOrder, boolean locked, boolean export, int color, int autouv, Vector3f from, Vector3f to, Vector3f origin, Vector3f rotation) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.boxUv = boxUv;
        this.renderOrder = renderOrder;
        this.locked = locked;
        this.export = export;
        this.color = color;
        this.autouv = autouv;
        this.from = from;
        this.to = to;
        this.origin = origin;
        this.rotation = rotation;
    }

    public String getUuid() { return uuid; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isBoxUv() { return boxUv; }
    public String getRenderOrder() { return renderOrder; }
    public boolean isLocked() { return locked; }
    public boolean isExport() { return export; }
    public int getColor() { return color; }
    public int getAutouv() { return autouv; }
    public Vector3f getFrom() { return from; }
    public Vector3f getTo() { return to; }
    public Vector3f getOrigin() { return origin; }
    public Vector3f getRotation() { return rotation; }
    public Map<String, BBFace> getFaces() { return faces; }

    public record BBFace(Vector4f uv, String texture) {
    }
}
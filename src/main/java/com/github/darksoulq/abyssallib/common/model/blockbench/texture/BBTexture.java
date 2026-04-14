package com.github.darksoulq.abyssallib.common.model.blockbench.texture;

import java.util.Base64;

public class BBTexture {
    private final String uuid;
    private final String id;
    private final String name;
    private final String path;
    private final String folder;
    private final String namespace;
    private final String group;
    private final boolean particle;
    private final String renderMode;
    private final String renderSides;
    private final int fps;
    private final String frameOrderType;
    private final boolean visible;
    private final byte[] data;
    
    private final int uvWidth;
    private final int uvHeight;
    private final int frameTime;
    private final boolean frameInterpolate;
    private final int[] frames;

    public BBTexture(String uuid, String id, String name, String path, String folder, String namespace, String group, boolean particle, String renderMode, String renderSides, int fps, String frameOrderType, boolean visible, String source, 
                     int uvWidth, int uvHeight, int frameTime, boolean frameInterpolate, int[] frames) {
        this.uuid = uuid;
        this.id = id;
        this.name = name;
        this.path = path;
        this.folder = folder;
        this.namespace = namespace;
        this.group = group;
        this.particle = particle;
        this.renderMode = renderMode;
        this.renderSides = renderSides;
        this.fps = fps;
        this.frameOrderType = frameOrderType;
        this.visible = visible;
        this.uvWidth = uvWidth;
        this.uvHeight = uvHeight;
        this.frameTime = frameTime;
        this.frameInterpolate = frameInterpolate;
        this.frames = frames;

        if (source != null && source.startsWith("data:image/png;base64,")) {
            this.data = Base64.getDecoder().decode(source.substring(22));
        } else {
            this.data = new byte[0];
        }
    }

    public String getUuid() { return uuid; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPath() { return path; }
    public String getFolder() { return folder; }
    public String getNamespace() { return namespace; }
    public String getGroup() { return group; }
    public boolean isParticle() { return particle; }
    public String getRenderMode() { return renderMode; }
    public String getRenderSides() { return renderSides; }
    public int getFps() { return fps; }
    public String getFrameOrderType() { return frameOrderType; }
    public boolean isVisible() { return visible; }
    public byte[] getData() { return data; }
    public int getUvWidth() { return uvWidth; }
    public int getUvHeight() { return uvHeight; }
    public int getFrameTime() { return frameTime; }
    public boolean isFrameInterpolate() { return frameInterpolate; }
    public int[] getFrames() { return frames; }
}
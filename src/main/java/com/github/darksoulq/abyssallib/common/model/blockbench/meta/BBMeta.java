package com.github.darksoulq.abyssallib.common.model.blockbench.meta;

public class BBMeta {
    private final String formatVersion;
    private final String modelFormat;
    private final boolean boxUv;

    public BBMeta(String formatVersion, String modelFormat, boolean boxUv) {
        this.formatVersion = formatVersion;
        this.modelFormat = modelFormat;
        this.boxUv = boxUv;
    }

    public String getFormatVersion() { return formatVersion; }
    public String getModelFormat() { return modelFormat; }
    public boolean isBoxUv() { return boxUv; }
}
package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.common.color.ColorUtils;
import org.bukkit.Color;
import org.bukkit.util.Vector;

public class BilinearGradient implements ColorProvider {
    private final Color c00; 
    private final Color c10; 
    private final Color c01; 
    private final Color c11; 
    private final double scaleX;
    private final double scaleZ;

    public BilinearGradient(Color c00, Color c10, Color c01, Color c11, double scaleX, double scaleZ) {
        this.c00 = c00;
        this.c10 = c10;
        this.c01 = c01;
        this.c11 = c11;
        this.scaleX = scaleX;
        this.scaleZ = scaleZ;
    }

    @Override
    public Color get(Vector pos, double progress) {
        double u = (pos.getX() % scaleX) / scaleX;
        double v = (pos.getZ() % scaleZ) / scaleZ;
        
        if (u < 0) u += 1;
        if (v < 0) v += 1;

        Color top = ColorUtils.mix(c00, c10, u);
        Color bottom = ColorUtils.mix(c01, c11, u);
        
        return ColorUtils.mix(top, bottom, v);
    }
}
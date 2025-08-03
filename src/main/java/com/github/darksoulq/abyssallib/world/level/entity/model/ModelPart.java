package com.github.darksoulq.abyssallib.world.level.entity.model;

import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelPart {
    public final Model model;
    public boolean isVisible = true;
    public final List<ModelPart> children = new ArrayList<>();

    public ModelPart(Model model) {
        this.model = model;
    }
}

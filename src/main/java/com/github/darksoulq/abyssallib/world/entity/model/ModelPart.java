package com.github.darksoulq.abyssallib.world.entity.model;

import com.github.darksoulq.abyssallib.server.resource.asset.Model;

import java.util.ArrayList;
import java.util.List;

public class ModelPart {
    public final Model model;
    public boolean isVisible = true;
    public final List<ModelPart> children = new ArrayList<>();

    public ModelPart(Model model) {
        this.model = model;
    }
}

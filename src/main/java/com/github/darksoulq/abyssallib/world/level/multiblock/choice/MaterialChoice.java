package com.github.darksoulq.abyssallib.world.level.multiblock.choice;

import com.github.darksoulq.abyssallib.world.level.multiblock.MultiblockChoice;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class MaterialChoice extends MultiblockChoice {
    private final List<Material> material;

    public MaterialChoice(Material material) {
        this.material = List.of(material);
    }
    public MaterialChoice(List<Material> materials) {
        this.material = materials;
    }

    public List<Material> getChoices() {
        return material;
    }

    @Override
    public boolean matches(Block block) {
        return material.contains(block.getType());
    }
}

package com.github.darksoulq.abyssallib.world.multiblock.choice;

import com.github.darksoulq.abyssallib.world.multiblock.MultiblockChoice;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

/**
 * A multiblock choice implementation that matches against vanilla Bukkit Materials.
 * <p>
 * This choice is used when a multiblock pattern requires standard Minecraft blocks
 * rather than custom AbyssalLib blocks. It supports matching against a single
 * material or a list of multiple valid materials.
 */
public class MaterialChoice extends MultiblockChoice {

    /**
     * The list of vanilla materials that are considered valid for this choice.
     */
    private final List<Material> material;

    /**
     * Constructs a MaterialChoice that requires a single specific vanilla Material.
     *
     * @param material The material required by this choice.
     */
    public MaterialChoice(Material material) {
        this.material = List.of(material);
    }

    /**
     * Constructs a MaterialChoice that accepts any Material from a provided list.
     *
     * @param materials The list of acceptable vanilla materials.
     */
    public MaterialChoice(List<Material> materials) {
        this.material = materials;
    }

    /**
     * Retrieves the list of vanilla materials that satisfy this requirement.
     *
     * @return The list of valid material choices.
     */
    public List<Material> getChoices() {
        return material;
    }

    /**
     * Checks if the material of the given world block matches one of the expected materials.
     * <p>
     * This method directly compares the {@link Material} type of the {@link Block}
     * against the internal allowed list.
     *
     * @param block The Bukkit block to validate.
     * @return {@code true} if the block's material is in the allowed list; {@code false} otherwise.
     */
    @Override
    public boolean matches(Block block) {
        return material.contains(block.getType());
    }
}
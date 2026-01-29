package com.github.darksoulq.abyssallib.world.block.internal.structure;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StructureBlock extends CustomBlock {

    public StructureBlock(Identifier id) {
        super(id, Material.STRUCTURE_BLOCK);
    }

    @Override
    public Supplier<Item> getItem() {
        Item item = super.getItem().get();
        item.setData(new ItemModel(NamespacedKey.minecraft("structure_block")));
        return () -> item;
    }

    @Override
    public StructureBlockEntity createBlockEntity(Location loc) {
        return new StructureBlockEntity(this);
    }

    @Override
    public void onLoad() {
        if (getEntity() instanceof StructureBlockEntity sbe) {
            sbe.updateParticles();
        }
    }

    @Override
    public void onUnLoad() {
         if (getEntity() instanceof StructureBlockEntity sbe && sbe.particles != null) {
            sbe.particles.stop();
        }
    }

    @Override
    public ActionResult onBreak(Player player, Location loc, ItemStack tool) {
        if (getEntity() instanceof StructureBlockEntity sbe && sbe.particles != null) {
            sbe.particles.stop();
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult onDestroyedByExplosion(@Nullable Entity eCause, @Nullable Block blockCause) {
        return ActionResult.CANCEL;
    }

    @Override
    public void place(org.bukkit.block.Block block, boolean loading) {
        super.place(block, loading);
        if (!loading && getEntity() instanceof StructureBlockEntity sbe) {
            sbe.updateParticles();
        }
    }
}
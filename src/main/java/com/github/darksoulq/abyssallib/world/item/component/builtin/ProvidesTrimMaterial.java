package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class ProvidesTrimMaterial extends DataComponent<TrimMaterial> implements Vanilla {
    private static final Codec<ProvidesTrimMaterial> CODEC = ExtraCodecs.TRIM_MATERIAL.xmap(
            ProvidesTrimMaterial::new,
            ProvidesTrimMaterial::getValue
    );

    public ProvidesTrimMaterial(TrimMaterial material) {
        super(Identifier.of(DataComponentTypes.PROVIDES_TRIM_MATERIAL.key().asString()), material, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.PROVIDES_TRIM_MATERIAL, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.PROVIDES_TRIM_MATERIAL);
    }
}

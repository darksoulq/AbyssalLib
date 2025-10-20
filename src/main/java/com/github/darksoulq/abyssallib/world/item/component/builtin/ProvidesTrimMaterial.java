package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class ProvidesTrimMaterial extends DataComponent<TrimMaterial> implements Vanilla {
    private static final Codec<DataComponent<TrimMaterial>> CODEC = Codecs.KEY.xmap(
            n -> new ProvidesTrimMaterial(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getOrThrow(n)),
            p -> RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKey(p.value)
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

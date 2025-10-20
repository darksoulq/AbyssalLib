package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class Lore extends DataComponent<List<Component>> implements Vanilla {
    private static final Codec<DataComponent<List<Component>>> CODEC = Codecs.TEXT_COMPONENT.list().xmap(
            Lore::new,
            l -> l.value
    );

    public Lore(List<Component> lore) {
        super(Identifier.of(DataComponentTypes.LORE.key().asString()), lore, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.LORE);
    }
}

package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class Lore extends DataComponent<List<Component>> implements Vanilla {
    public static final Codec<Lore> CODEC = Codecs.TEXT_COMPONENT.list().xmap(
            Lore::new,
            Lore::getValue
    );
    public static final DataComponentType<Lore> TYPE = DataComponentType.valued(CODEC, v -> new Lore((ItemLore) v));

    public Lore(ItemLore lore) {
        super(lore.lines());
    }
    public Lore(List<Component> lore) {
        super(lore);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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

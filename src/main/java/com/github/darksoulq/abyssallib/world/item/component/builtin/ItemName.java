package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class ItemName extends DataComponent<Component> implements Vanilla {
    private static final Codec<DataComponent<Component>> CODEC = Codecs.TEXT_COMPONENT.xmap(
            ItemName::new,
            i -> i.value
    );

    public ItemName(Component name) {
        super(Identifier.of(DataComponentTypes.ITEM_NAME.key().asString()), name, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ITEM_NAME, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ITEM_NAME);
    }
}

package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class StoredEnchantments extends DataComponent<ItemEnchantments> implements Vanilla {
    private static final Codec<StoredEnchantments> CODEC = ExtraCodecs.ITEM_ENCHANTMENTS.xmap(
            StoredEnchantments::new,
            StoredEnchantments::getValue
    );

    public StoredEnchantments(ItemEnchantments name) {
        super(Identifier.of(DataComponentTypes.STORED_ENCHANTMENTS.key().asString()), name, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.STORED_ENCHANTMENTS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.STORED_ENCHANTMENTS);
    }
}

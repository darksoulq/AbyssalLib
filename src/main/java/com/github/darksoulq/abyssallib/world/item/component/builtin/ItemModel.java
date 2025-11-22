package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class ItemModel extends DataComponent<Key> implements Vanilla {
    private static final Codec<ItemModel> CODEC = Codecs.KEY.xmap(
            ItemModel::new,
            ItemModel::getValue
    );

    public ItemModel(Key id) {
        super(Identifier.of(DataComponentTypes.ITEM_MODEL.key().asString()), id, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ITEM_MODEL, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ITEM_MODEL);
    }
}

package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class ItemModel extends DataComponent<Key> implements Vanilla {
    public static final Codec<ItemModel> CODEC = Codecs.KEY.xmap(
            ItemModel::new,
            ItemModel::getValue
    );
    public static final DataComponentType<ItemModel> TYPE = DataComponentType.valued(CODEC, ItemModel::new);

    public ItemModel(Key id) {
        super(id);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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

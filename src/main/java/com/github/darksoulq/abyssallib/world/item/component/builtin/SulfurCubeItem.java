package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SulfurCubeContent;
import org.bukkit.inventory.ItemStack;

public class SulfurCubeItem extends DataComponent<ItemStack> implements Vanilla {
    public static final Codec<SulfurCubeItem> CODEC = Codecs.ITEM_STACK.xmap(
        SulfurCubeItem::new,
        SulfurCubeItem::getValue
    );
    public static final DataComponentType<SulfurCubeItem> TYPE = DataComponentType.valued(CODEC, content -> new SulfurCubeItem((SulfurCubeContent) content));

    public SulfurCubeItem(SulfurCubeContent content) {
        super(content.absorbedItem());
    }

    public SulfurCubeItem(ItemStack stack) {
        super(stack);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.SULFUR_CUBE_CONTENT, SulfurCubeContent.sulfurCubeContent(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.SULFUR_CUBE_CONTENT);
    }
}

package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class WrittenBookContents extends DataComponent<WrittenBookContent> implements Vanilla {
    public static final Codec<WrittenBookContents> CODEC = ExtraCodecs.WRITTEN_BOOK_CONTENT.xmap(
            WrittenBookContents::new,
            WrittenBookContents::getValue
    );
    public static final DataComponentType<WrittenBookContents> TYPE = DataComponentType.valued(CODEC, WrittenBookContents::new);

    public WrittenBookContents(WrittenBookContent contents) {
        super(contents);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.WRITTEN_BOOK_CONTENT);
    }
}

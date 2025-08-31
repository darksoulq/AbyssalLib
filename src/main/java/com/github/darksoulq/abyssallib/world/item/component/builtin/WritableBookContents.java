package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WritableBookContent;
import org.bukkit.inventory.ItemStack;

public class WritableBookContents extends DataComponent<WritableBookContent> implements Vanilla {
    private static final Codec<DataComponent<WritableBookContent>> CODEC = Codec.of(null, null);

    public WritableBookContents(WritableBookContent contents) {
        super(Identifier.of(DataComponentTypes.WRITABLE_BOOK_CONTENT.key().asString()), contents, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.WRITABLE_BOOK_CONTENT, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.WRITABLE_BOOK_CONTENT);
    }
}

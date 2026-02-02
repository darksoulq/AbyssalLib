package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WritableBookContent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class WritableBookContents extends DataComponent<WritableBookContent> implements Vanilla {
    public static final Codec<WritableBookContents> CODEC = ExtraCodecs.FILTERED_STRING.list().xmap(
            p -> new WritableBookContents(WritableBookContent.writeableBookContent().addFilteredPages(p).build()),
            w -> w.value.pages()
    );
    public static final DataComponentType<WritableBookContents> TYPE = DataComponentType.valued(CODEC, WritableBookContents::new);

    public WritableBookContents(WritableBookContent contents) {
        super(contents);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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

package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.inventory.ItemStack;

public class BundleContent extends DataComponent<BundleContents> implements Vanilla {
    private static final Codec<DataComponent<BundleContents>> CODEC = Codecs.ITEM_STACK.list().xmap(
            l -> new BundleContent(BundleContents.bundleContents(l)),
            c -> c.value.contents()
    );

    public BundleContent(BundleContents contents) {
        super(Identifier.of(DataComponentTypes.BUNDLE_CONTENTS.key().asString()), contents, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BUNDLE_CONTENTS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BUNDLE_CONTENTS);
    }
}

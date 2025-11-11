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

import java.util.List;

public class BundleContent extends DataComponent<List<ItemStack>> implements Vanilla {
    private static final Codec<BundleContent> CODEC = Codecs.ITEM_STACK.list().xmap(
            BundleContent::new,
            BundleContent::getValue
    );

    public BundleContent(List<ItemStack> contents) {
        super(Identifier.of(DataComponentTypes.BUNDLE_CONTENTS.key().asString()), contents, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BUNDLE_CONTENTS);
    }
}

package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

public class ProvidesBannerPatterns extends DataComponent<Key> implements Vanilla {
    private static final Codec<ProvidesBannerPatterns> CODEC = Codecs.KEY.xmap(
            ProvidesBannerPatterns::new,
            ProvidesBannerPatterns::getValue
    );

    public ProvidesBannerPatterns(Key patterns) {
        super(Identifier.of(DataComponentTypes.PROVIDES_BANNER_PATTERNS.key().asString()), patterns, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.PROVIDES_BANNER_PATTERNS, TagKey.create(RegistryKey.BANNER_PATTERN, value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.PROVIDES_BANNER_PATTERNS);
    }
}

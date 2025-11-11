package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MapDecorates extends DataComponent<Map<String, MapDecorations.DecorationEntry>> implements Vanilla {
    private static final Codec<MapDecorates> CODEC = Codec.map(Codecs.STRING, ExtraCodecs.MAP_DECO_ENTRY).xmap(
            MapDecorates::new,
            MapDecorates::getValue
    );

    public MapDecorates(MapDecorations decor) {
        super(Identifier.of(DataComponentTypes.MAP_DECORATIONS.key().asString()), decor.decorations(), CODEC);
    }
    public MapDecorates(Map<String, MapDecorations.DecorationEntry> decor) {
        super(Identifier.of(DataComponentTypes.MAP_DECORATIONS.key().asString()), decor, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAP_DECORATIONS, MapDecorations.mapDecorations(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAP_DECORATIONS);
    }
}

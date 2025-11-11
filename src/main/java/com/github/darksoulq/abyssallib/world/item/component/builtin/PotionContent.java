package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordCodecBuilder;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

public class PotionContent extends DataComponent<PotionContents> implements Vanilla {
    private static final Codec<PotionContent> CODEC = RecordCodecBuilder.create(
            Codec.enumCodec(PotionType.class).fieldOf("potion", d -> d.value.potion()),
            ExtraCodecs.COLOR.nullable().fieldOf("color", d -> d.value.customColor()),
            ExtraCodecs.POTION_EFFECT.list().fieldOf("customEffects", d -> d.value.customEffects()),
            Codecs.STRING.nullable().fieldOf("customName", d -> d.value.customName()),
            (potion, color, customEffects, customName) -> new PotionContent(PotionContents
                    .potionContents()
                    .potion(potion)
                    .customColor(color)
                    .addCustomEffects(customEffects)
                    .customName(customName)
                    .build())
    );

    public PotionContent(PotionContents contents) {
        super(Identifier.of(DataComponentTypes.POTION_CONTENTS.key().asString()), contents, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.POTION_CONTENTS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.POTION_CONTENTS);
    }
}

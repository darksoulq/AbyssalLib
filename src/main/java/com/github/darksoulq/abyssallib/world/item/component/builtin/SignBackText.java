package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SignText;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class SignBackText extends DataComponent<SignText> implements Vanilla {
    public static final Codec<SignBackText> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.TEXT_COMPONENT.list().validate(list -> {
            if (list.size() < 4) {
                return DataResult.error("SignFrontText list requires 4 elements, found " + list.size());
            }
            return DataResult.success(list.subList(0, 3));
        }).fieldOf("lines").forGetter((SignBackText frontText) -> frontText.getValue().lines()),
        Codec.enumCodec(DyeColor.class).fieldOf("color").forGetter((SignBackText frontText) -> frontText.getValue().color()),
        Codecs.BOOLEAN.fieldOf("has_glowing_text").forGetter((SignBackText frontText) -> frontText.getValue().hasGlowingText())

    ).apply(instance, (components, color, glowingText) -> new SignBackText(SignText.signText()
        .lines(components)
        .color(color)
        .hasGlowingText(glowingText)
        .build())
    ));

    public static final DataComponentType<SignBackText> TYPE = DataComponentType.valued(CODEC, SignBackText::new);

    public SignBackText(SignText text) {
        super(text);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.SIGN_TEXT_BACK, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.SIGN_TEXT_BACK);
    }
}

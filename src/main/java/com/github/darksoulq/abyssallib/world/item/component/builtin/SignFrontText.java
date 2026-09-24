package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SignText;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class SignFrontText extends DataComponent<SignText> implements Vanilla {
    public static final Codec<SignFrontText> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.TEXT_COMPONENT.list().validate(list -> {
            if (list.size() < 4) {
                return DataResult.error("SignFrontText list requires 4 elements, found " + list.size());
            }
            return DataResult.success(list.subList(0, 3));
        }).fieldOf("lines").forGetter((SignFrontText frontText) -> frontText.getValue().lines()),
        Codec.enumCodec(DyeColor.class).fieldOf("color").forGetter((SignFrontText frontText) -> frontText.getValue().color()),
        Codecs.BOOLEAN.fieldOf("has_glowing_text").forGetter((SignFrontText frontText) -> frontText.getValue().hasGlowingText())

    ).apply(instance, (components, color, glowingText) -> new SignFrontText(SignText.signText()
        .lines(components)
        .color(color)
        .hasGlowingText(glowingText)
        .build())
    ));

    public static final DataComponentType<SignFrontText> TYPE = DataComponentType.valued(CODEC, SignFrontText::new);

    public SignFrontText(SignText text) {
        super(text);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.SIGN_TEXT_FRONT, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.SIGN_TEXT_FRONT);
    }
}

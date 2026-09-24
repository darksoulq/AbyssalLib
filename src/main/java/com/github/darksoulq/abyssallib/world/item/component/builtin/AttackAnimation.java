package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class AttackAnimation extends DataComponent<SwingAnimation> implements Vanilla {
    public static final Codec<AttackAnimation> CODEC = RecordBuilder.create(instance -> instance.group(
        Codec.enumCodec(SwingAnimation.Animation.class).fieldOf("animation").forGetter((AttackAnimation animation) -> animation.getValue().type()),
        Codecs.INT.validate(duration -> duration < 0 ? DataResult.error("Duration of animation cannot be negative") : DataResult.success(duration))
            .fieldOf("duration").forGetter((AttackAnimation animation) -> animation.getValue().duration())
    ).apply(instance, (animation, duration) -> new AttackAnimation(SwingAnimation.swingAnimation()
        .type(animation)
        .duration(duration)
        .build())
    ));

    public static final DataComponentType<AttackAnimation> TYPE = DataComponentType.valued(CODEC, AttackAnimation::new);

    public AttackAnimation(SwingAnimation animation) {
        super(animation);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ATTACK_ANIMATION, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ATTACK_ANIMATION);
    }
}

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
import org.bukkit.inventory.ItemStack;

public class InteractAnimation extends DataComponent<SwingAnimation> implements Vanilla {
    public static final Codec<InteractAnimation> CODEC = RecordBuilder.create(instance -> instance.group(
        Codec.enumCodec(SwingAnimation.Animation.class).fieldOf("animation").forGetter((InteractAnimation animation) -> animation.getValue().type()),
        Codecs.INT.validate(duration -> duration < 0 ? DataResult.error("Duration of animation cannot be negative") : DataResult.success(duration))
            .fieldOf("duration").forGetter((InteractAnimation animation) -> animation.getValue().duration())
    ).apply(instance, (animation, duration) -> new InteractAnimation(SwingAnimation.swingAnimation()
        .type(animation)
        .duration(duration)
        .build())
    ));

    public static final DataComponentType<InteractAnimation> TYPE = DataComponentType.valued(CODEC, InteractAnimation::new);

    public InteractAnimation(SwingAnimation animation) {
        super(animation);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.INTERACT_ANIMATION, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.INTERACT_ANIMATION);
    }
}

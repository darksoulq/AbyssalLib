package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class ResolvingProfile extends DataComponent<ResolvableProfile> implements Vanilla {
    public static final Codec<ResolvingProfile> CODEC = ExtraCodecs.RESOLVABLE_PROFILE.xmap(
            ResolvingProfile::new,
            ResolvingProfile::getValue
    );

    public ResolvingProfile(ResolvableProfile profile) {
        super(Identifier.of(DataComponentTypes.PROFILE.key().asString()), profile, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.PROFILE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.PROFILE);
    }
}

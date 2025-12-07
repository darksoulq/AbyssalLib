package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Recipes extends DataComponent<List<Key>> implements Vanilla {
    public static final Codec<Recipes> CODEC = Codecs.KEY.list().xmap(
            Recipes::new,
            Recipes::getValue
    );

    public Recipes(List<Key> recipes) {
        super(Identifier.of(DataComponentTypes.RECIPES.key().asString()), recipes, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.RECIPES, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.RECIPES);
    }
}

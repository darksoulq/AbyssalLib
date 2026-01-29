package com.github.darksoulq.abyssallib.world.data.loot.function;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunction;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetLoreFunction extends LootFunction {
    public static final Codec<SetLoreFunction> CODEC = new Codec<>() {
        @Override
        public <D> SetLoreFunction decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<Component> lore = Codecs.TEXT_COMPONENT.list().decode(ops, map.get(ops.createString("lore")));
            return new SetLoreFunction(lore);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, SetLoreFunction value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("lore"), Codecs.TEXT_COMPONENT.list().encode(ops, value.lore));
            return ops.createMap(map);
        }
    };

    public static final LootFunctionType<SetLoreFunction> TYPE = () -> CODEC;

    private final List<Component> lore;

    public SetLoreFunction(List<Component> lore) {
        this.lore = lore;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}
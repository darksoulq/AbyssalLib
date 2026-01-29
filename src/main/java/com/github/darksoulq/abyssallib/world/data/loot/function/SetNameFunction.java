package com.github.darksoulq.abyssallib.world.data.loot.function;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunction;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class SetNameFunction extends LootFunction {
    public static final Codec<SetNameFunction> CODEC = new Codec<>() {
        @Override
        public <D> SetNameFunction decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            Component name = Codecs.TEXT_COMPONENT.decode(ops, map.get(ops.createString("name")));
            return new SetNameFunction(name);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, SetNameFunction value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("name"), Codecs.TEXT_COMPONENT.encode(ops, value.name));
            return ops.createMap(map);
        }
    };

    public static final LootFunctionType<SetNameFunction> TYPE = () -> CODEC;

    private final Component name;

    public SetNameFunction(Component name) {
        this.name = name;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        stack.setData(DataComponentTypes.CUSTOM_NAME, name);
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}
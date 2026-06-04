package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CustomData extends DataComponent<CompoundTag> implements Vanilla {

    public static final Codec<CustomData> CODEC = new Codec<>() {
        @Override
        public <D> DataResult<CustomData> decode(DynamicOps<D> ops, D input) {
            return ExtraCodecs.COMPOUND_TAG.decode(ops, input).map(CustomData::new);
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, CustomData component) {
            CompoundTag tag = component.getValue().copy();
            tag.remove("CustomData");
            return ExtraCodecs.COMPOUND_TAG.encode(ops, tag);
        }

        @Override
        public String describe() {
            return "CustomData";
        }
    };

    public static final DataComponentType<CustomData> TYPE = DataComponentType.valued(CODEC, CustomData::new);

    public CustomData(CompoundTag value) {
        super(value);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        net.minecraft.world.item.component.CustomData existingData = nms.get(DataComponents.CUSTOM_DATA);

        CompoundTag newTag = value.copy();
        newTag.remove("CustomData");

        if (existingData != null) {
            CompoundTag existingTag = existingData.copyTag();
            Tag customComponents = existingTag.get("CustomData");
            if (customComponents != null) {
                newTag.put("CustomData", customComponents);
            }
        }

        if (newTag.isEmpty()) {
            nms.remove(DataComponents.CUSTOM_DATA);
        } else {
            nms.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(newTag));
        }

        stack.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
    }

    @Override
    public void remove(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        net.minecraft.world.item.component.CustomData existingData = nms.get(DataComponents.CUSTOM_DATA);

        if (existingData != null) {
            CompoundTag existingTag = existingData.copyTag();
            Tag customComponents = existingTag.get("CustomData");

            if (customComponents != null) {
                CompoundTag keptTag = new CompoundTag();
                keptTag.put("CustomData", customComponents);
                nms.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(keptTag));
            } else {
                nms.remove(DataComponents.CUSTOM_DATA);
            }

            stack.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
        }
    }
}
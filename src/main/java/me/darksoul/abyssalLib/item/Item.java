package me.darksoul.abyssalLib.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.registry.Registry;
import me.darksoul.abyssalLib.util.ResourceLocation;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Item extends ItemStack {
    private final ResourceLocation id;

    public Item(ResourceLocation id, Material material) {
        super(material);
        this.id = id;
        this.setData(DataComponentTypes.ITEM_NAME, Component.translatable("item." + id.namespace() + "." + id.path()));
        this.setData(DataComponentTypes.ITEM_MODEL, id.toNamespace());
        writeIdTag();
    }

    private void writeIdTag() {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(this);
        CompoundTag tag = new CompoundTag();

        CompoundTag custom = tag.getCompoundOrEmpty("CustomData");
        custom.putString("AbyssalItemId", id.toString());
        tag.put("CustomData", custom);

        nms.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        ItemStack updated = CraftItemStack.asBukkitCopy(nms);
        this.setItemMeta(updated.getItemMeta());
    }

    public ResourceLocation getId() {
        return id;
    }

    public void onRightClick(ItemUseContext ctx) {}
    public void onLeftClick(ItemUseContext ctx) {}
    public void onUseOnBlock(ItemUseContext ctx) {}
    public void onUseEntity(ItemUseContext ctx) {}

    public void onInteract(ItemUseContext ctx) {
        if (ctx.event().orElse(null) instanceof org.bukkit.event.player.PlayerInteractEvent event) {
            switch (event.getAction()) {
                case RIGHT_CLICK_BLOCK -> {
                    onUseOnBlock(ctx);
                    onRightClick(ctx);
                }
                case RIGHT_CLICK_AIR -> onRightClick(ctx);
                case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> onLeftClick(ctx);
            }
        }
    }

    public void data(String key, String value) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(this);
        CustomData dta = nms.get(DataComponents.CUSTOM_DATA);
        if (dta == null) {
            return;
        }

        CompoundTag tag = dta.copyTag();
        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            if (!custom.contains(key)) {
                custom.putString(key, value);
                tag.put("CustomData", custom);
                nms.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                this.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
            }
        }
    }

    public String data(String key) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(this);
        CustomData data = nms.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            if (custom.getString(key).isPresent()) {
                return custom.getString(key).get();
            }
        }
        return null;
    }

    public static Item from(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;

        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData data = nms.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();

        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            if (custom.getString("AbyssalItemId").isPresent()) {
                String idStr = custom.getString("AbyssalItemId").get();
                if (idStr.isEmpty()) return null;

                ResourceLocation id = parseId(idStr);
                Registry.RegistryEntry<Item> entry = BuiltinRegistries.ITEMS.getEntry(id.toString());

                return entry != null ? entry.create(id.toString()) : null;
            }
        }
        return null;
    }


    private static ResourceLocation parseId(String raw) {
        String[] parts = raw.split(":", 2);
        if (parts.length != 2) throw new IllegalArgumentException("Invalid ID: " + raw);
        return new ResourceLocation(parts[0], parts[1]);
    }

    @Override
    public @NotNull Item clone() {
        return new Item(id, getType());
    }
}

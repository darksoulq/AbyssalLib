package com.github.darksoulq.abyssallib.world.level.item;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.ClickType;
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.data.CTag;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.data.tag.ItemTag;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Item implements Cloneable {
    private ItemStack stack;
    private final Identifier id;
    private final ItemSettings settings;
    public final Tooltip tooltip = new Tooltip();

    public Item(Identifier id, Material material) {
        stack = new ItemStack(material);
        this.id = id;
        for (DataComponentType dt : stack.getDataTypes()) {
            stack.unsetData(dt);
        }
        stack.setData(DataComponentTypes.ITEM_NAME, Component.translatable("item." + id.namespace() + "." + id.path()));
        stack.setData(DataComponentTypes.ITEM_MODEL, id.toNamespace());
        writeIdTag();
        this.settings = new ItemSettings(this);
    }

    private void writeIdTag() {
        CTag cont = getData();
        cont.set("AbyssalItemId", id.toString());
        setData(cont);
    }

    public Identifier getId() {
        return id;
    }

    public void createTooltip(Tooltip tooltip) {}

    public void updateTooltip() {
        createTooltip(tooltip);
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(tooltip.lines));
        stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                .hideTooltip(tooltip.hide)
                .hiddenComponents(tooltip.hiddenComponents)
                .build());
        if (tooltip.style != null) {
            stack.setData(DataComponentTypes.TOOLTIP_STYLE, tooltip.style.toKey());
        }
    }

    public ActionResult postHit(LivingEntity source, Entity target) {
        return ActionResult.PASS;
    }

    public ActionResult postMine(org.bukkit.block.Block block, Player miner) {
        return ActionResult.PASS;
    }

    public ActionResult useOnEntity(ClickType type, Player user, EquipmentSlot hand, Entity target) {
        return ActionResult.PASS;
    }

    public ActionResult useOnBlock(ClickType type, org.bukkit.block.Block block, BlockFace face, Player user, EquipmentSlot hand) {
        return ActionResult.PASS;
    }

    public void onCraft(Player player) {}

    public ActionResult onAnvilPrepare(AnvilContext ctx) {
        return ActionResult.CANCEL;
    }

    public CTag getData() {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData dta = nms.get(DataComponents.CUSTOM_DATA);
        if (dta == null) dta = CustomData.EMPTY;

        CompoundTag tag = dta.copyTag();
        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            return new CTag(custom);
        } else {
            tag.put("CustomData", new CompoundTag());
            return new CTag(tag.getCompound("CustomData").get());
        }
    }

    public void setData(CTag container) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData data = nms.get(DataComponents.CUSTOM_DATA);
        if (data == null) data = CustomData.EMPTY;
        CompoundTag tag = data.copyTag();
        tag.put("CustomData", container.toVanilla());
        data = CustomData.of(tag);
        nms.set(DataComponents.CUSTOM_DATA, data);
        ItemStack updated = CraftItemStack.asBukkitCopy(nms);
        stack.setItemMeta(updated.getItemMeta());
    }

    public ItemSettings getSettings() {
        return settings;
    }

    public ItemStack getStack() {
        return stack;
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

                Identifier id = Identifier.of(idStr);
                Item entry = Registries.ITEMS.get(id.toString()).clone();
                entry.stack = stack;
                return entry;
            }
        }
        return null;
    }

    public boolean hasTag(Identifier id) {
        ItemTag tag = Registries.ITEM_TAGS.get(id.toString());
        return tag.contains(this);
    }

    @Override
    public @NotNull Item clone() {
        try {
            Item cloned = (Item) super.clone();
            cloned.stack = this.stack.clone();
            cloned.tooltip.lines = new ArrayList<>(this.tooltip.lines);
            cloned.tooltip.hiddenComponents = new HashSet<>(this.tooltip.hiddenComponents);
            cloned.tooltip.hide = this.tooltip.hide;
            cloned.tooltip.style = this.tooltip.style;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Block asBlock(Item item) {
        if (!Registries.BLOCK_ITEMS.contains(item.getId().toString())) return null;
        return Registries.BLOCK_ITEMS.get(item.getId().toString());
    }

    public static class Tooltip {
        private boolean hide;
        public List<Component> lines = new ArrayList<>();
        public Set<DataComponentType> hiddenComponents = new HashSet<>();
        private Identifier style = null;

        public void hide(boolean v) {
            this.hide = v;
        }

        public void hide(DataComponentType type) {
            this.hiddenComponents.add(type);
        }

        public void line(Component component) {
            lines.add(component);
        }

        public void style(Identifier style) {
            this.style = style;
        }

        public Identifier style() {
            return this.style;
        }

        public boolean hidden() {
            return hide;
        }
    }
}

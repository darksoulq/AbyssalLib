package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.builtin.*;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Item implements Cloneable {
    private Identifier id;
    private ItemStack stack;
    private ComponentMap componentMap;
    public Tooltip tooltip = new Tooltip();

    private Item(ItemStack stack) {
        this.id = Identifier.of("unknown", "unknown");
        this.stack = stack;
        componentMap = new ComponentMap(this);
    }
    public Item(Identifier id, Material base) {
        this.id = id;
        stack = ItemStack.of(base);
        componentMap = new ComponentMap(this);
        for (Identifier cId : componentMap.getVanillaIds()) {
            componentMap.removeData(cId);
        }
        Integer size = stack.getData(DataComponentTypes.MAX_STACK_SIZE);
        if (size != null) setData(new MaxStackSize(size));
        setData(new ItemName(Component.translatable("item." + id.getNamespace() + "." + id.getPath())));
        setData(new ItemModel(id.asNamespacedKey()));
        setData(new CustomMarker(id));
    }

    public void createTooltip(Tooltip tooltip) {}
    public void updateTooltip() {
        setData(new Lore(ItemLore.lore(tooltip.lines)));
        setData(new DisplayTooltip(TooltipDisplay.tooltipDisplay()
                .hideTooltip(tooltip.hide)
                .hiddenComponents(tooltip.hiddenComponents).build()));
        if (tooltip.style != null) setData(new TooltipStyle(tooltip.style.asNamespacedKey()));
        else unsetData(TooltipStyle.class);
    }

    public void setData(DataComponent<?> component) {
        componentMap.setData(component);
    }
    public DataComponent<?> getData(Identifier id) {
        return componentMap.getData(id);
    }
    public DataComponent<?> getData(DataComponentType type) {
        return componentMap.getData(type);
    }
    public <T extends DataComponent<?>> DataComponent<?> getData(Class<T> clazz) {
        return clazz.cast(componentMap.getData(clazz));
    }
    public boolean hasData(Identifier id) {
        return componentMap.hasData(id);
    }
    public boolean hasData(DataComponentType type) {
        return componentMap.hasData(type);
    }
    public void unsetData(Identifier id) {
        componentMap.removeData(id);
    }
    public void unsetData(Class<? extends DataComponent> clazz) {
        componentMap.removeData(clazz);
    }
    public <T extends DataComponent<?>> boolean hasData(Class<T> clazz) {
        return componentMap.hasData(clazz);
    }
    public boolean hasTag(Identifier id) {
        if (!(Registries.TAGS.get(id.toString()) instanceof ItemTag tag)) {
            AbyssalLib.getInstance().getLogger().severe("Unknown tag: " + id);
            return false;
        }
        return tag.contains(stack);
    }

    public ActionResult postMine(LivingEntity source, Block target) {
        return ActionResult.PASS;
    }
    public ActionResult postHit(LivingEntity source, Entity target) {
        return ActionResult.PASS;
    }
    public ActionResult onUseOn(UseContext ctx) {
        return  ActionResult.PASS;
    }
    public void onUse(LivingEntity source, EquipmentSlot hand) {}
    public ActionResult onAnvilPrepare(AnvilContext ctx) {
        return ActionResult.PASS;
    }
    public void onCraftedBy(Player player) {
    }

    public Identifier getId() {
        return id;
    }
    public ItemStack getStack() {
        return stack;
    }
    public ComponentMap getComponentMap() {
        return componentMap;
    }

    public CTag getCTag() {
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
    public void setCTag(CTag container) {
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

    public static Item resolve(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        Item base = new Item(stack);
        if (!base.hasData(CustomMarker.class)) return null;
        Identifier id = (Identifier) base.getData(CustomMarker.class).value;
        if (id == null) return null;
        Item clone = Registries.ITEMS.get(id.toString()).clone();
        clone.stack = stack;
        clone.componentMap = new ComponentMap(clone);
        return clone;
    }
    public static CustomBlock asBlock(Item item) {
        if (!item.hasData(Identifier.of("abyssallib:block_item"))) return null;
        Identifier blockId = (Identifier) item.getData(Identifier.of("abyssallib:block_item")).value;
        return Registries.BLOCKS.get(blockId.toString()).clone();
    }

    @Override
    public Item clone() {
        try {
            Item item = (Item) super.clone();
            item.id = this.id;
            item.stack = this.stack.clone();
            item.componentMap = new ComponentMap(item);
            item.tooltip = new Tooltip();
            item.tooltip.hide = this.tooltip.hide;
            item.tooltip.style = this.tooltip.style;
            item.tooltip.lines = new ArrayList<>(this.tooltip.lines);
            item.tooltip.hiddenComponents = new HashSet<>(this.tooltip.hiddenComponents);
            return item;
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

    public static class Tooltip {
        private boolean hide;
        public List<Component> lines = new ArrayList<>();
        public Set<DataComponentType> hiddenComponents = new HashSet<>();
        private Identifier style = null;

        public void setVisible(boolean v) {
            this.hide = !v;
        }
        public void withHidden(DataComponentType type) {
            this.hiddenComponents.add(type);
        }
        public void addLine(Component component) {
            lines.add(component);
        }
        public void withStyle(Identifier style) {
            this.style = style;
        }

        public Identifier getStyle() {
            return this.style;
        }
        public boolean isVisible() {
            return hide;
        }
        public Component getLine(int index) {
            return lines.get(index);
        }
    }
}

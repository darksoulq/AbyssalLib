package com.github.darksoulq.abyssallib.world.menu;

import com.github.darksoulq.abyssallib.server.scheduler.ScheduledTask;
import com.github.darksoulq.abyssallib.world.menu.slot.Clickable;
import com.github.darksoulq.abyssallib.world.menu.slot.DelegatingSlot;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Experimental
public abstract class AbstractContainerMenu implements ContainerListener {
    public final int topSize;
    public final int maxSlots;
    public final List<Slot> slots;

    private final Map<Player, List<ItemStack>> lastSlots = new ConcurrentHashMap<>();
    private final List<TrackedDataSlot> trackedDataSlots = new ArrayList<>();
    private final List<Container> connectedContainers = new ArrayList<>();
    private final List<ScheduledTask> activeTasks = new CopyOnWriteArrayList<>();
    private final MenuType menuType;

    private final Set<Player> viewers = ConcurrentHashMap.newKeySet();
    private final Map<Player, InventoryView> bukkitViews = new ConcurrentHashMap<>();
    private final Map<Player, ItemStack> carriedItems = new ConcurrentHashMap<>();
    private int remoteDataSlotCount = 0;

    protected AbstractContainerMenu(MenuType menuType) {
        this.menuType = menuType;
        this.topSize = getTopInventorySize(menuType);
        this.maxSlots = this.topSize + 36;
        this.slots = new ArrayList<>(this.maxSlots);
    }

    private static int getTopInventorySize(MenuType type) {
        if (type == MenuType.GENERIC_9X1) return 9;
        if (type == MenuType.GENERIC_9X2) return 18;
        if (type == MenuType.GENERIC_9X3) return 27;
        if (type == MenuType.GENERIC_9X4) return 36;
        if (type == MenuType.GENERIC_9X5) return 45;
        if (type == MenuType.GENERIC_9X6) return 54;
        if (type == MenuType.GENERIC_3X3) return 9;
        if (type == MenuType.CRAFTER_3X3) return 9;
        if (type == MenuType.ANVIL) return 3;
        if (type == MenuType.BEACON) return 1;
        if (type == MenuType.BLAST_FURNACE) return 3;
        if (type == MenuType.BREWING_STAND) return 5;
        if (type == MenuType.CRAFTING) return 10;
        if (type == MenuType.ENCHANTMENT) return 2;
        if (type == MenuType.FURNACE) return 3;
        if (type == MenuType.GRINDSTONE) return 3;
        if (type == MenuType.HOPPER) return 5;
        if (type == MenuType.LECTERN) return 1;
        if (type == MenuType.LOOM) return 4;
        if (type == MenuType.MERCHANT) return 3;
        if (type == MenuType.SHULKER_BOX) return 27;
        if (type == MenuType.SMITHING) return 4;
        if (type == MenuType.SMOKER) return 3;
        if (type == MenuType.CARTOGRAPHY_TABLE) return 3;
        if (type == MenuType.STONECUTTER) return 2;
        throw new IllegalArgumentException("Unknown MenuType: " + type.getKey());
    }

    protected Slot addSlot(Slot slot) {
        if (this.slots.size() >= this.maxSlots) {
            throw new IllegalStateException("Maximum slot capacity reached (" + this.maxSlots + ")");
        }
        slot.index = this.slots.size();
        this.slots.add(slot);
        if (!this.connectedContainers.contains(slot.container)) {
            this.connectedContainers.add(slot.container);
            slot.container.addListener(this);
            MenuManager.linkMenuContainer(this, slot.container);
        }
        return slot;
    }

    protected void addPlayerSlots(Container playerContainer) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerContainer, col + row * 9 + 9));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerContainer, col));
        }
    }

    protected void setVanillaData(ContainerData data) {
        for (int i = 0; i < data.getCount(); ++i) {
            this.trackedDataSlots.add(new TrackedDataSlot(this.remoteDataSlotCount++, DataSlot.shared(data, i)));
        }
    }

    protected void addDataSlots(ContainerData data) {
        for (int i = 0; i < data.getCount(); ++i) {
            this.trackedDataSlots.add(new TrackedDataSlot(-1, DataSlot.shared(data, i)));
        }
    }

    protected DataSlot addDataSlot(DataSlot slot) {
        this.trackedDataSlots.add(new TrackedDataSlot(-1, slot));
        return slot;
    }

    public Slot getSlot(int index) {
        return index >= 0 && index < this.slots.size() ? this.slots.get(index) : null;
    }

    public abstract boolean stillValid(Player player);

    public boolean requiresTick() {
        return false;
    }

    protected void onOpen(Player player) {
    }

    protected void onClose(Player player) {
    }

    public void trackTask(ScheduledTask task) {
        this.activeTasks.add(task);
    }

    public void tick() {
        try {
            this.broadcastChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forceResync() {
        if (this.viewers.isEmpty()) return;
        try {
            for (Player player : this.viewers) {
                List<ItemStack> last = this.lastSlots.get(player);
                if (last != null) {
                    last.replaceAll(ignored -> null);
                }
                player.updateInventory();
            }
            for (TrackedDataSlot tracked : this.trackedDataSlots) {
                if (tracked.packetId >= 0) {
                    this.syncDataSlot(tracked.packetId, tracked.slot.get());
                }
            }
            this.broadcastChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InventoryView open(Player player, Component title) {
        InventoryView view = this.menuType.create(player, title);

        this.viewers.add(player);
        this.bukkitViews.put(player, view);

        List<ItemStack> pSlots = new ArrayList<>(this.maxSlots);
        for (int i = 0; i < this.maxSlots; i++) pSlots.add(null);
        this.lastSlots.put(player, pSlots);

        view.open();

        for (TrackedDataSlot tracked : this.trackedDataSlots) {
            int initialValue = tracked.slot.get();
            if (tracked.packetId >= 0) {
                this.syncDataSlotFor(player, tracked.packetId, initialValue);
            }
        }

        this.broadcastChanges();
        this.onOpen(player);
        return view;
    }

    public Set<Player> getViewers() {
        return this.viewers;
    }

    public void removeViewer(Player player) {
        this.onClose(player);

        for (Slot slot : this.slots) {
            ItemStack item = slot.getItem(player);
            if (item != null && !item.isEmpty() && slot.dropsOnClose(player, item)) {
                Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                for (ItemStack leftoverItem : leftover.values()) {
                    this.dropItem(player, leftoverItem);
                }
                slot.setItem(player, null);
            }
        }

        this.viewers.remove(player);
        this.bukkitViews.remove(player);
        this.lastSlots.remove(player);

        this.dropItem(player, this.carriedItems.remove(player));
        player.setItemOnCursor(null);
    }

    @Override
    public void containerChanged(Container container) {
        try {
            this.broadcastChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastChanges() {
        if (this.viewers.isEmpty()) return;

        try {
            int slotSize = this.slots.size();
            for (Player player : this.viewers) {
                InventoryView view = this.bukkitViews.get(player);
                List<ItemStack> last = this.lastSlots.get(player);
                if (view == null || last == null) continue;

                int topBoundary = view.countSlots();
                for (int i = 0; i < slotSize; ++i) {
                    ItemStack current = this.slots.get(i).getItem(player);
                    ItemStack previous = last.get(i);

                    if (!this.itemsEqual(previous, current)) {
                        ItemStack clone = current == null || current.isEmpty() ? null : current.clone();
                        last.set(i, clone);
                        if (i < topBoundary) {
                            view.setItem(i, clone);
                        }
                    }
                }
            }

            for (TrackedDataSlot tracked : this.trackedDataSlots) {
                if (tracked.slot.checkAndClearUpdateFlag() && tracked.packetId >= 0) {
                    this.syncDataSlot(tracked.packetId, tracked.slot.get());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void syncDataSlot(int index, int value) {
        for (Player player : this.viewers) {
            this.syncDataSlotFor(player, index, value);
        }
    }

    private void syncDataSlotFor(Player player, int index, int value) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        sp.connection.send(new ClientboundContainerSetDataPacket(sp.containerMenu.containerId, index, value));
    }

    public void setCarried(Player player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            this.carriedItems.remove(player);
            player.setItemOnCursor(null);
        } else {
            this.carriedItems.put(player, stack);
            player.setItemOnCursor(stack);
        }
    }

    public ItemStack getCarried(Player player) {
        return this.carriedItems.get(player);
    }

    public void dragged(Player player, DragType type, Set<Integer> rawSlots) {
        ItemStack cursor = this.getCarried(player);
        if (cursor == null || cursor.isEmpty()) return;

        int dragSize = rawSlots.size();
        if (dragSize == 0) return;

        for (int slotId : rawSlots) {
            Slot slot = this.getSlot(slotId);
            if (slot == null || !slot.mayPlace(player, cursor)) return;
            ItemStack current = slot.getItem(player);
            if (current != null && !current.isEmpty() && !this.itemsEqualIgnoreAmount(current, cursor)) return;
        }

        int amount = cursor.getAmount();
        int totalPlaced = 0;
        List<ItemStack> newStacks = new ArrayList<>(dragSize);

        for (int slotId : rawSlots) {
            Slot slot = this.getSlot(slotId);
            ItemStack current = slot.getItem(player);

            int currentAmount = current == null ? 0 : current.getAmount();
            int amountPerSlot = type == DragType.SINGLE ? 1 : amount / dragSize;
            int targetAmount = Math.min(currentAmount + amountPerSlot, slot.getMaxStackSize(cursor));

            totalPlaced += (targetAmount - currentAmount);

            ItemStack placed = cursor.clone();
            placed.setAmount(targetAmount);
            newStacks.add(placed);
        }

        if (totalPlaced > amount) return;

        int i = 0;
        for (int slotId : rawSlots) {
            this.getSlot(slotId).setItem(player, newStacks.get(i++));
        }

        cursor.setAmount(amount - totalPlaced);
        if (cursor.isEmpty() || cursor.getAmount() <= 0) {
            this.setCarried(player, null);
        } else {
            this.setCarried(player, cursor);
        }
    }

    public void hotbarSwap(int slotId, int hotbarButton, Player player) {
        Slot clicked = this.getSlot(slotId);
        if (clicked == null) return;

        Slot hotbarSlot = null;
        for (Slot s : this.slots) {
            if (s.container instanceof PlayerContainer && s.getContainerIndex() == hotbarButton) {
                hotbarSlot = s;
                break;
            }
        }
        if (hotbarSlot == null) return;

        ItemStack clickedItem = clicked.getItem(player);
        ItemStack hotbarItem = hotbarSlot.getItem(player);

        if ((clickedItem != null && !clickedItem.isEmpty()) && !clicked.mayPickup(player, clickedItem)) return;
        if ((hotbarItem != null && !hotbarItem.isEmpty()) && !hotbarSlot.mayPickup(player, hotbarItem)) return;
        if ((hotbarItem != null && !hotbarItem.isEmpty()) && (!clicked.mayPlace(player, hotbarItem) || hotbarItem.getAmount() > clicked.getMaxStackSize(hotbarItem)))
            return;
        if ((clickedItem != null && !clickedItem.isEmpty()) && (!hotbarSlot.mayPlace(player, clickedItem) || clickedItem.getAmount() > hotbarSlot.getMaxStackSize(clickedItem)))
            return;

        clicked.setItem(player, hotbarItem);
        hotbarSlot.setItem(player, clickedItem);

        if (clickedItem != null && !clickedItem.isEmpty()) hotbarSlot.onTake(player, clickedItem);
        if (hotbarItem != null && !hotbarItem.isEmpty()) clicked.onTake(player, hotbarItem);
    }

    public void offhandSwap(int slotId, Player player) {
        Slot clicked = this.getSlot(slotId);
        if (clicked == null) return;

        ItemStack clickedItem = clicked.getItem(player);
        ItemStack offhandItem = player.getInventory().getItemInOffHand();

        if ((clickedItem != null && !clickedItem.isEmpty()) && !clicked.mayPickup(player, clickedItem)) return;
        if (!offhandItem.isEmpty() && (!clicked.mayPlace(player, offhandItem) || offhandItem.getAmount() > clicked.getMaxStackSize(offhandItem)))
            return;

        clicked.setItem(player, offhandItem);
        player.getInventory().setItemInOffHand(clickedItem);

        if (!offhandItem.isEmpty()) clicked.onTake(player, offhandItem);
    }

    public void clicked(int slotId, ClickType clickType, Player player) {
        ItemStack cursor = this.getCarried(player);

        if (slotId < 0) {
            if (cursor != null && !cursor.isEmpty()) {
                boolean dropOne = clickType == ClickType.RIGHT || clickType == ClickType.WINDOW_BORDER_RIGHT || clickType == ClickType.DROP;
                int dropAmount = dropOne ? 1 : cursor.getAmount();

                ItemStack drop = cursor.clone();
                drop.setAmount(dropAmount);

                this.dropItem(player, drop);

                cursor.setAmount(cursor.getAmount() - dropAmount);
                if (cursor.isEmpty() || cursor.getAmount() <= 0) {
                    this.setCarried(player, null);
                } else {
                    this.setCarried(player, cursor);
                }
            }
            return;
        }

        Slot slot = this.getSlot(slotId);
        if (slot == null) return;

        Slot interactionTarget = slot;
        while (interactionTarget instanceof DelegatingSlot delegating) {
            Slot delegate = delegating.getDelegate();
            if (delegate == null) break;
            interactionTarget = delegate;
        }

        if (interactionTarget instanceof Clickable clickable) {
            try {
                clickable.onClick(new ClickContext(player, clickType, this, interactionTarget, cursor));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (clickType == ClickType.DOUBLE_CLICK) {
            if (cursor != null && !cursor.isEmpty()) {
                int maxCursorSize = cursor.getMaxStackSize();
                for (Slot s : this.slots) {
                    if (cursor.getAmount() >= maxCursorSize) break;
                    ItemStack sItem = s.getItem(player);
                    if (s.hasItem(player) && s.mayPickup(player, sItem) && sItem.isSimilar(cursor)) {
                        int toTake = Math.min(maxCursorSize - cursor.getAmount(), sItem.getAmount());
                        cursor.setAmount(cursor.getAmount() + toTake);
                        sItem.setAmount(sItem.getAmount() - toTake);

                        if (sItem.isEmpty() || sItem.getAmount() <= 0) {
                            s.setItem(player, null);
                        } else {
                            s.setItem(player, sItem);
                        }
                        s.onTake(player, sItem);
                    }
                }
            }
            return;
        }

        ItemStack current = slot.getItem(player);

        if (clickType == ClickType.MIDDLE && player.getGameMode() == GameMode.CREATIVE) {
            if (current != null && !current.isEmpty()) {
                ItemStack clone = current.clone();
                clone.setAmount(clone.getMaxStackSize());
                this.setCarried(player, clone);
            }
            return;
        }

        if (clickType.isShiftClick()) {
            this.quickMoveStack(player, slotId);
            return;
        }

        if (clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP) {
            if (current != null && !current.isEmpty() && slot.mayPickup(player, current)) {
                int dropAmount = clickType == ClickType.CONTROL_DROP ? current.getAmount() : 1;
                ItemStack drop = current.clone();
                drop.setAmount(dropAmount);

                this.dropItem(player, drop);

                current.setAmount(current.getAmount() - dropAmount);
                if (current.isEmpty() || current.getAmount() <= 0) {
                    slot.setItem(player, null);
                } else {
                    slot.setItem(player, current);
                }
                slot.onTake(player, drop);
            }
            return;
        }

        if (clickType.isLeftClick()) {
            if (cursor == null || cursor.isEmpty()) {
                if (current != null && !current.isEmpty() && slot.mayPickup(player, current)) {
                    this.setCarried(player, current.clone());
                    slot.setItem(player, null);
                    slot.onTake(player, current);
                }
                return;
            }

            if (slot.mayPlace(player, cursor)) {
                if (current == null || current.isEmpty()) {
                    int max = slot.getMaxStackSize(cursor);
                    if (cursor.getAmount() <= max) {
                        slot.setItem(player, cursor.clone());
                        this.setCarried(player, null);
                    } else {
                        ItemStack split = cursor.clone();
                        split.setAmount(max);
                        slot.setItem(player, split);
                        cursor.setAmount(cursor.getAmount() - max);
                        this.setCarried(player, cursor);
                    }
                } else if (this.itemsEqualIgnoreAmount(current, cursor)) {
                    int max = slot.getMaxStackSize(cursor);
                    int size = current.getAmount() + cursor.getAmount();
                    if (size <= max) {
                        current.setAmount(size);
                        slot.setItem(player, current);
                        this.setCarried(player, null);
                    } else if (current.getAmount() < max) {
                        cursor.setAmount(cursor.getAmount() - (max - current.getAmount()));
                        current.setAmount(max);
                        slot.setItem(player, current);
                        this.setCarried(player, cursor);
                    }
                } else if (slot.mayPickup(player, current) && cursor.getAmount() <= slot.getMaxStackSize(cursor)) {
                    this.setCarried(player, current.clone());
                    slot.setItem(player, cursor.clone());
                    slot.onTake(player, current);
                }
            }
            return;
        }

        if (clickType.isRightClick()) {
            if (cursor == null || cursor.isEmpty()) {
                if (current != null && !current.isEmpty() && slot.mayPickup(player, current)) {
                    int half = (int) Math.ceil(current.getAmount() / 2.0);
                    ItemStack newCursor = current.clone();
                    newCursor.setAmount(half);
                    this.setCarried(player, newCursor);

                    current.setAmount(current.getAmount() - half);
                    if (current.isEmpty() || current.getAmount() <= 0) {
                        slot.setItem(player, null);
                    } else {
                        slot.setItem(player, current);
                    }
                    slot.onTake(player, newCursor);
                }
                return;
            }

            if (slot.mayPlace(player, cursor)) {
                if (current == null || current.isEmpty()) {
                    ItemStack one = cursor.clone();
                    one.setAmount(1);
                    slot.setItem(player, one);
                    cursor.setAmount(cursor.getAmount() - 1);
                    if (cursor.isEmpty() || cursor.getAmount() <= 0) {
                        this.setCarried(player, null);
                    } else {
                        this.setCarried(player, cursor);
                    }
                } else if (this.itemsEqualIgnoreAmount(current, cursor)) {
                    int max = slot.getMaxStackSize(cursor);
                    if (current.getAmount() < max) {
                        current.setAmount(current.getAmount() + 1);
                        slot.setItem(player, current);
                        cursor.setAmount(cursor.getAmount() - 1);
                        if (cursor.isEmpty() || cursor.getAmount() <= 0) {
                            this.setCarried(player, null);
                        } else {
                            this.setCarried(player, cursor);
                        }
                    }
                } else if (slot.mayPickup(player, current) && cursor.getAmount() <= slot.getMaxStackSize(cursor)) {
                    this.setCarried(player, current.clone());
                    slot.setItem(player, cursor.clone());
                    slot.onTake(player, current);
                }
            }
        }
    }

    public abstract ItemStack quickMoveStack(Player player, int rawIndex);

    protected ItemStack standardTransfer(Player player, int rawIndex, int customContainerSize) {
        Slot clickedSlot = this.getSlot(rawIndex);
        if (clickedSlot == null || !clickedSlot.hasItem(player)) return null;

        ItemStack clickedItem = clickedSlot.getItem(player);
        if (!clickedSlot.mayPickup(player, clickedItem)) return null;

        ItemStack itemCopy = clickedItem.clone();

        if (rawIndex < customContainerSize) {
            if (!this.moveItemStackTo(player, clickedItem, customContainerSize, this.slots.size(), true)) return null;
        } else {
            if (!this.moveItemStackTo(player, clickedItem, 0, customContainerSize, false)) {
                int mainInvEnd = customContainerSize + 27;
                if (rawIndex < mainInvEnd) {
                    if (!this.moveItemStackTo(player, clickedItem, mainInvEnd, this.slots.size(), false)) return null;
                } else {
                    if (!this.moveItemStackTo(player, clickedItem, customContainerSize, mainInvEnd, false)) return null;
                }
            }
        }

        if (clickedItem.isEmpty() || clickedItem.getAmount() <= 0) {
            clickedSlot.setItem(player, null);
        } else {
            clickedSlot.setChanged();
        }

        if (clickedItem.getAmount() == itemCopy.getAmount()) return null;

        clickedSlot.onTake(player, clickedItem);
        return itemCopy;
    }

    protected boolean moveItemStackTo(Player player, ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean changed = false;
        int i = reverseDirection ? endIndex - 1 : startIndex;

        if (stack.getMaxStackSize() > 1) {
            while (!stack.isEmpty()) {
                if (reverseDirection ? i < startIndex : i >= endIndex) break;

                Slot slot = this.slots.get(i);
                ItemStack current = slot.getItem(player);

                if (current != null && !current.isEmpty() && this.itemsEqualIgnoreAmount(stack, current) && slot.mayPlace(player, stack)) {
                    int maxStack = slot.getMaxStackSize(stack);
                    int room = maxStack - current.getAmount();

                    if (room > 0) {
                        int toAdd = Math.min(stack.getAmount(), room);
                        current.setAmount(current.getAmount() + toAdd);
                        slot.setChanged();
                        stack.setAmount(stack.getAmount() - toAdd);
                        changed = true;
                    }
                }
                i += reverseDirection ? -1 : 1;
            }
        }

        if (!stack.isEmpty()) {
            i = reverseDirection ? endIndex - 1 : startIndex;

            while (!stack.isEmpty()) {
                if (reverseDirection ? i < startIndex : i >= endIndex) break;

                Slot slot = this.slots.get(i);
                ItemStack current = slot.getItem(player);

                if (current == null || current.isEmpty()) {
                    if (slot.mayPlace(player, stack)) {
                        int maxStack = slot.getMaxStackSize(stack);
                        if (stack.getAmount() <= maxStack) {
                            slot.setItem(player, stack.clone());
                            stack.setAmount(0);
                            changed = true;
                            break;
                        } else {
                            ItemStack split = stack.clone();
                            split.setAmount(maxStack);
                            slot.setItem(player, split);
                            stack.setAmount(stack.getAmount() - maxStack);
                            changed = true;
                        }
                    }
                }
                i += reverseDirection ? -1 : 1;
            }
        }

        return changed;
    }

    public void removed() {
        for (ScheduledTask task : this.activeTasks) {
            task.cancel();
        }
        this.activeTasks.clear();

        for (Container container : this.connectedContainers) {
            container.removeListener(this);
        }
        MenuManager.unlinkMenu(this);

        for (Map.Entry<Player, ItemStack> entry : this.carriedItems.entrySet()) {
            this.dropItem(entry.getKey(), entry.getValue());
        }
        this.carriedItems.clear();
        this.viewers.clear();
        this.bukkitViews.clear();
        this.lastSlots.clear();
    }

    protected void dropItem(Player player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;

        Location loc = player.getEyeLocation();
        loc.setY(loc.getY() - 0.3);

        Item dropped = player.getWorld().dropItem(loc, stack);
        dropped.setPickupDelay(40);

        float f = 0.3F;
        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();

        float f1 = (float) Math.sin(yaw * Math.PI / 180.0F);
        float f2 = (float) Math.cos(yaw * Math.PI / 180.0F);
        float f3 = (float) Math.sin(pitch * Math.PI / 180.0F);
        float f4 = (float) Math.cos(pitch * Math.PI / 180.0F);

        float x = -f1 * f4 * f;
        float y = -f3 * f + 0.1F;
        float z = f2 * f4 * f;

        dropped.setVelocity(new Vector(x, y, z));
    }

    private boolean itemsEqual(ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;
        if (a.getAmount() != b.getAmount()) return false;
        return a.equals(b);
    }

    private boolean itemsEqualIgnoreAmount(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;
        return a.isSimilar(b);
    }

    private record TrackedDataSlot(int packetId, DataSlot slot) {
    }
}
package com.github.darksoulq.abyssallib.world.menu;

import com.github.darksoulq.abyssallib.world.menu.slot.ReadOnly;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Experimental
public abstract class MappedContainerMenu extends AbstractContainerMenu {
    private final Map<Integer, Slot> mappedSlots = new HashMap<>();
    private final Container dummyContainer = new DummyContainer();
    private Container playerContainer;

    protected MappedContainerMenu(MenuType menuType) {
        super(menuType);
        this.playerContainer = new PlayerContainer();
    }

    protected void bindSlot(int visualIndex, Slot slot) {
        if (visualIndex >= 0 && visualIndex < this.topSize) {
            this.mappedSlots.put(visualIndex, slot);
        }
    }

    protected void bindVisual(int visualIndex, ItemStack stack) {
        if (visualIndex >= 0 && visualIndex < this.topSize) {
            VisualContainer vc = new VisualContainer(1);
            vc.setItem(null, 0, stack.clone());
            this.mappedSlots.put(visualIndex, new ReadOnly(vc, 0));
        }
    }

    protected void bindPlayerInventory(Container playerContainer) {
        this.playerContainer = playerContainer;
    }

    protected void build() {
        for (int i = 0; i < this.topSize; i++) {
            Slot slot = this.mappedSlots.get(i);
            this.addSlot(Objects.requireNonNullElseGet(slot, () -> new ReadOnly(this.dummyContainer, 0)));
        }

        if (this.playerContainer != null) {
            this.addPlayerSlots(this.playerContainer);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int rawIndex) {
        return this.standardTransfer(player, rawIndex, this.topSize);
    }
}
package com.github.darksoulq.abyssallib.world.menu;

import com.github.darksoulq.abyssallib.world.menu.slot.ReadOnly;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Experimental
public abstract class PatternMenu extends AbstractContainerMenu {
    private final Container dummyContainer = new DummyContainer();
    private Container playerContainer;

    private final Map<Character, ContainerMapping> containerMappings = new HashMap<>();
    private final Map<Character, Supplier<Slot>> slotMappings = new HashMap<>();

    protected PatternMenu(MenuType menuType) {
        super(menuType);
        this.playerContainer = new PlayerContainer();
        this.bindChar(' ', () -> new ReadOnly(this.dummyContainer, 0));
    }

    protected void bindChar(char c, Container container) {
        this.containerMappings.put(c, new ContainerMapping(container, Slot::new));
    }

    protected void bindChar(char c, Container container, SlotFactory factory) {
        this.containerMappings.put(c, new ContainerMapping(container, factory));
    }

    protected void bindChar(char c, Supplier<Slot> slotSupplier) {
        this.slotMappings.put(c, slotSupplier);
    }

    protected void bindChar(char c, ItemStack stack) {
        this.slotMappings.put(c, () -> {
            VisualContainer vc = new VisualContainer(1);
            vc.setItem(null, 0, stack.clone());
            return new ReadOnly(vc, 0);
        });
    }

    protected void bindPlayerInventory(Container playerContainer) {
        this.playerContainer = playerContainer;
    }

    protected void buildPattern(String... rows) {
        this.buildPattern(PatternBlueprint.compile(rows));
    }

    protected void buildPattern(String textBlock) {
        this.buildPattern(PatternBlueprint.compile(textBlock));
    }

    protected void buildPattern(PatternBlueprint blueprint) {
        Map<Container, Integer> containerIndices = new HashMap<>();
        int slotsAdded = 0;

        for (char c : blueprint.grid()) {
            if (slotsAdded >= this.topSize) {
                throw new IllegalStateException("Pattern definition exceeds provided topSize (" + this.topSize + ")");
            }

            if (this.containerMappings.containsKey(c)) {
                ContainerMapping mapping = this.containerMappings.get(c);
                int currentIndex = containerIndices.getOrDefault(mapping.container, 0);
                this.addSlot(mapping.factory.create(mapping.container, currentIndex));
                containerIndices.put(mapping.container, currentIndex + 1);
            } else if (this.slotMappings.containsKey(c)) {
                this.addSlot(this.slotMappings.get(c).get());
            } else {
                this.addSlot(new ReadOnly(this.dummyContainer, 0));
            }
            slotsAdded++;
        }

        while (slotsAdded < this.topSize) {
            this.addSlot(new ReadOnly(this.dummyContainer, 0));
            slotsAdded++;
        }

        if (this.playerContainer != null) {
            this.addPlayerSlots(this.playerContainer);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int rawIndex) {
        return this.standardTransfer(player, rawIndex, this.topSize);
    }

    public interface SlotFactory {
        Slot create(Container container, int index);
    }

    private record ContainerMapping(Container container, SlotFactory factory) {
    }

    public record PatternBlueprint(char[] grid) {
        public static PatternBlueprint compile(String... rows) {
            int length = 0;
            for (String row : rows) length += row.length();
            char[] grid = new char[length];
            int i = 0;
            for (String row : rows) {
                for (char c : row.toCharArray()) grid[i++] = c;
            }
            return new PatternBlueprint(grid);
        }

        public static PatternBlueprint compile(String textBlock) {
            String[] split = textBlock.replace("\r", "").trim().split("\n");
            int validRows = 0;
            for (String row : split) {
                if (!row.trim().isEmpty()) validRows++;
            }
            String[] cleanRows = new String[validRows];
            int i = 0;
            for (String row : split) {
                String trimmed = row.trim();
                if (!trimmed.isEmpty()) cleanRows[i++] = trimmed;
            }
            return compile(cleanRows);
        }
    }
}
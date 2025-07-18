package com.github.darksoulq.abyssallib.world.level.inventory.gui;

public record SlotPosition(GuiView.Segment segment, int index) {
    public static SlotPosition top(int slot) {
        return new SlotPosition(GuiView.Segment.TOP, slot);
    }

    public static SlotPosition bottom(int slot) {
        return new SlotPosition(GuiView.Segment.BOTTOM, slot);
    }
}

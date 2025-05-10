package io.github.darksoulq.abyssalLib.gui.slot;

import io.github.darksoulq.abyssalLib.gui.AbstractGui;

import java.util.ArrayList;
import java.util.List;

public class SlotHolder {
    public List<Slot> TOP = new ArrayList<>();
    public List<Slot> BOTTOM = new ArrayList<>();

    public void add(AbstractGui.Type type, Slot slot) {
        if (type.equals(AbstractGui.Type.TOP)) {
            TOP.add(slot);
        } else if (type.equals(AbstractGui.Type.BOTTOM)){
            BOTTOM.add(slot);
        }
    }

    public Slot get(AbstractGui.Type type, int index) {
        if (type.equals(AbstractGui.Type.TOP)) {
            for (Slot slot : TOP) {
                if (slot.index == index) {
                    return slot;
                }
            }
        } else if (type.equals(AbstractGui.Type.BOTTOM)) {
            for (Slot slot : BOTTOM) {
                return slot;
            }
        }
        return null;
    }
}

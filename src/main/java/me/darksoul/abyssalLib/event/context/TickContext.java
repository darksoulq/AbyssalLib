package me.darksoul.abyssalLib.event.context;

import me.darksoul.abyssalLib.gui.Slot;

import java.util.List;

public class TickContext {
    private final List<Slot> slots;
    private int progress;
    private final int maxProgress;

    public TickContext(List<Slot> slots, int progress, int maxProgress) {
        this.slots = slots;
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    public List<Slot> slots() {
        return slots;
    }

    public int progress() {
        return progress;
    }

    public void incrementProgress() {
        this.progress++;
    }

    public int maxProgress() {
        return maxProgress;
    }

    public boolean complete() {
        return progress >= maxProgress;
    }
}

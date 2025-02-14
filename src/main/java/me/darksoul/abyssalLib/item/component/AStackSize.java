package me.darksoul.abyssalLib.item.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.darksoul.abyssalLib.item.AItem;

public class AStackSize {
    public static void set(AItem item, int maxSize) {
        item.getItem().setData(DataComponentTypes.MAX_STACK_SIZE, maxSize);
    }
}

package com.github.darksoulq.abyssallib.world.item.internal;

import com.github.darksoulq.abyssallib.world.item.Item;
import net.kyori.adventure.key.Key;

public class GuiItem extends Item {
    public GuiItem(Key id) {
        super(id);
        setHidden(true);
    }
}

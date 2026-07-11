package com.github.darksoulq.abyssallib.world.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface MenuProvider {
    Component getDisplayName();

    AbstractContainerMenu createMenu(Player player);

    static MenuProvider simple(Component title, MenuFactory factory) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return title;
            }

            @Override
            public AbstractContainerMenu createMenu(Player player) {
                return factory.create(player);
            }
        };
    }

    @FunctionalInterface
    interface MenuFactory {
        AbstractContainerMenu create(Player player);
    }
}
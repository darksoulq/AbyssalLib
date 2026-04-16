package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.internal.*;

public final class EventRegistry {

    public static void init(AbyssalLib plugin) {
        AbyssalLib.EVENT_BUS = new EventBus(plugin);

        AbyssalLib.EVENT_BUS.register(new ChatInputHandler());
        AbyssalLib.EVENT_BUS.register(new PlayerEvents());
        AbyssalLib.EVENT_BUS.register(new EntityEvents());
        AbyssalLib.EVENT_BUS.register(new BlockEvents());
        AbyssalLib.EVENT_BUS.register(new ItemEvents());
        AbyssalLib.EVENT_BUS.register(new ServerEvents());
        AbyssalLib.EVENT_BUS.register(new GuiEvents());
        AbyssalLib.EVENT_BUS.register(new AdvancementEvents());
        AbyssalLib.EVENT_BUS.register(new StatisticEvents());
    }
}
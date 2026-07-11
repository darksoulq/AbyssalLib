package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.server.event.internal.ServerEvents;
import com.github.darksoulq.abyssallib.world.advancement.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancementModifier implements DeferredRegistryModifier {

    private final List<AdvancementHolder> newAdvancements = new ArrayList<>();
    private final Map<AdvancementHolder, Advancement> customAdvancementMap = new HashMap<>();

    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof Advancement customAdv) {
            AdvancementHolder holder = customAdv.toNMSHolder();
            newAdvancements.add(holder);
            customAdvancementMap.put(holder, customAdv);

            ServerAdvancementManager manager = MinecraftServer.getServer().getAdvancements();
            Map<Identifier, AdvancementHolder> mutableAdvancements = new HashMap<>(manager.advancements);
            mutableAdvancements.put(holder.id(), holder);
            manager.advancements = mutableAdvancements;
        }
    }

    @Override
    public void postApply() {
        if (!newAdvancements.isEmpty()) {
            ServerEvents.applyAdvancementLayout(newAdvancements, customAdvancementMap);
            newAdvancements.clear();
            customAdvancementMap.clear();
        }
    }
}
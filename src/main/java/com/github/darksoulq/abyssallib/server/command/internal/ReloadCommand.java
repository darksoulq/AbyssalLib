package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Bukkit;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.github.darksoulq.abyssallib.server.resource.ResourcePack.HASH_MAP;
import static com.github.darksoulq.abyssallib.server.resource.ResourcePack.UUID_MAP;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        super("reload");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.RELOAD));

        LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("commands");
        LiteralArgumentBuilder<CommandSourceStack> pack = Commands.literal("pack");
        LiteralArgumentBuilder<CommandSourceStack> translations = Commands.literal("translations");

        addSyntax(ctx -> {
            CommandBus.reloadAll();
            CommandUtil.reply(ctx, "Commands reloaded");
            return CommandResult.success();
        }, commands);

        addSyntax(ctx -> {
            refreshInternalPacks();
            List<ResourcePackInfo> rps = new ArrayList<>();
            loadRPInfos(rps, true);
            if (!rps.isEmpty()) {
                Bukkit.getServer().sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs(rps).build());
            }
            CommandUtil.reply(ctx, "Reload complete");
            return CommandResult.success();
        }, pack);

        addSyntax(ctx -> {
            ServerTranslator.reload();
            CommandUtil.reply(ctx, "Translations reloaded");
            return CommandResult.success();
        }, translations);
    }

    public static void loadRPInfos(List<ResourcePackInfo> rps, boolean reload) {
        if (reload) AbyssalLib.PACK_SERVER.loadThirdPartyPacks();
        for (String pluginId : AbyssalLib.PACK_SERVER.registeredPluginIDs()) {
            rps.add(ResourcePackInfo.resourcePackInfo()
                .id(UUID_MAP.get(pluginId))
                .uri(URI.create(AbyssalLib.PACK_SERVER.getUrl(pluginId)))
                .hash(HASH_MAP.get(pluginId))
                .build()
            );
        }
        for (String path : ResourcePack.EXTERNAL_CACHE) {
            rps.add(ResourcePackInfo.resourcePackInfo()
                .id(UUID_MAP.get(path))
                .uri(URI.create(AbyssalLib.PACK_SERVER.getUrl(path)))
                .hash(HASH_MAP.get(path))
                .build());
        }
    }

    public static void refreshInternalPacks() {
        for (String id : new HashSet<>(UUID_MAP.keySet())) {
            if (id.startsWith("external_")) continue;

            Path file = AbyssalLib.PACK_SERVER.getPath(id);
            if (file == null || !Files.exists(file)) continue;

            Try.of(() -> FileUtils.sha1(file))
                .onSuccess(hash -> {
                    HASH_MAP.put(id, hash);
                    UUID_MAP.put(id, UUID.randomUUID());
                })
                .onFailure(Throwable::printStackTrace);
        }
    }
}
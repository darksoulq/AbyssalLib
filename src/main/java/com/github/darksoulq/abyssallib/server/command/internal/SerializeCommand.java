package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.NbtOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.command.argument.EnumArgument;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SerializeCommand extends BaseCommand {

    public enum ExportFormat {
        NBT, JSON, YAML
    }

    public SerializeCommand() {
        super("serialize");

        LiteralArgumentBuilder<CommandSourceStack> itemLit = Commands.literal("item").requires(DefaultConditions.playerOnly().and(DefaultConditions.hasPerm(PluginPermissions.SERIALIZE_ITEM)));
        LiteralArgumentBuilder<CommandSourceStack> blockLit = Commands.literal("block").requires(DefaultConditions.playerOnly().and(DefaultConditions.hasPerm(PluginPermissions.SERIALIZE_BLOCK)));
        LiteralArgumentBuilder<CommandSourceStack> entityLit = Commands.literal("entity").requires(DefaultConditions.playerOnly().and(DefaultConditions.hasPerm(PluginPermissions.SERIALIZE_ENTITY)));
        RequiredArgumentBuilder<CommandSourceStack, ExportFormat> format = Commands.argument("format", EnumArgument.enumArg(ExportFormat.class));

        addSyntax(SerializeCommand::serializeItemExecutor, itemLit, format);
        addSyntax(SerializeCommand::serializeBlockExecutor, blockLit, format);
        addSyntax(SerializeCommand::serializeEntityExecutor, entityLit, format);
    }

    private static CommandResult serializeItemExecutor(CommandContext<CommandSourceStack> ctx) {
        Player player = CommandUtil.getPlayer(ctx);
        if (player == null) return CommandResult.failure();

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.isEmpty()) {
            CommandUtil.reply(ctx, "<red>You must be holding an item to serialize</red>");
            return CommandResult.failure();
        }

        ExportFormat format = ctx.getArgument("format", ExportFormat.class);
        return serializeAndSend(ctx, format, Codecs.ITEM_STACK, held);
    }

    private static CommandResult serializeBlockExecutor(CommandContext<CommandSourceStack> ctx) {
        Player player = CommandUtil.getPlayer(ctx);
        if (player == null) return CommandResult.failure();

        Block block = player.getTargetBlockExact(10);
        if (block == null) {
            CommandUtil.reply(ctx, "<red>You must be looking at a block to serialize</red>");
            return CommandResult.failure();
        }

        BlockInfo info = BlockInfo.resolve(block);
        ExportFormat format = ctx.getArgument("format", ExportFormat.class);
        return serializeAndSend(ctx, format, ExtraCodecs.BLOCK_INFO, info);
    }

    private static CommandResult serializeEntityExecutor(CommandContext<CommandSourceStack> ctx) {
        Player player = CommandUtil.getPlayer(ctx);
        if (player == null) return CommandResult.failure();

        Entity target = player.getTargetEntity(10);
        if (target == null) {
            CommandUtil.reply(ctx, "<red>You must be looking at an entity to serialize</red>");
            return CommandResult.failure();
        }

        ExportFormat format = ctx.getArgument("format", ExportFormat.class);
        String result;

        try {
            switch (format) {
                case NBT -> {
                    SavedEntity saved = SavedEntity.create(target, NbtOps.INSTANCE);
                    result = ExtraCodecs.SAVED_ENTITY.encode(NbtOps.INSTANCE, saved).toString();
                }
                case JSON -> {
                    SavedEntity saved = SavedEntity.create(target, JsonOps.INSTANCE);
                    result = JsonOps.INSTANCE.mapper.writeValueAsString(ExtraCodecs.SAVED_ENTITY.encode(JsonOps.INSTANCE, saved));
                }
                case YAML -> {
                    SavedEntity saved = SavedEntity.create(target, YamlOps.INSTANCE);
                    result = YamlOps.dump(ExtraCodecs.SAVED_ENTITY.encode(YamlOps.INSTANCE, saved));
                }
                default -> {
                    return CommandResult.failure();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CommandUtil.reply(ctx, "<red>Failed to serialize entity</red>");
            return CommandResult.failure();
        }

        CommandUtil.reply(ctx, "Entity successfully serialized! <click:copy_to_clipboard:'" + result.replace("'", "\\'") + "'><aqua><u>Click here to copy</u></aqua></click>");
        return CommandResult.success();
    }

    private static <T> CommandResult serializeAndSend(CommandContext<CommandSourceStack> ctx, ExportFormat format, Codec<T> codec, T value) {
        String result;
        try {
            switch (format) {
                case NBT -> result = codec.encode(NbtOps.INSTANCE, value).toString();
                case JSON -> result = JsonOps.INSTANCE.mapper.writeValueAsString(codec.encode(JsonOps.INSTANCE, value));
                case YAML -> result = YamlOps.dump(codec.encode(YamlOps.INSTANCE, value));
                default -> {
                    return CommandResult.failure();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CommandUtil.reply(ctx, "<red>Failed to serialize object</red>");
            return CommandResult.failure();
        }

        CommandUtil.reply(ctx, "Successfully serialized! <click:copy_to_clipboard:'" + result.replace("'", "\\'") + "'><aqua><u>Click here to copy</u></aqua></click>");
        return CommandResult.success();
    }
}
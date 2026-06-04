package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.ops.NbtOps;
import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.command.argument.EnumArgument;
import com.github.darksoulq.abyssallib.server.command.argument.NbtCompoundArgument;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.github.darksoulq.abyssallib.world.advancement.AdvancementFrame;
import com.github.darksoulq.abyssallib.world.advancement.Toast;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ToastCommand extends BaseCommand {

    public ToastCommand() {
        super("toast");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.TOAST_SEND));

        RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> targets = Commands.argument("targets", ArgumentTypes.players());
        LiteralArgumentBuilder<CommandSourceStack> held = Commands.literal("held").requires(DefaultConditions.playerOnly());
        LiteralArgumentBuilder<CommandSourceStack> custom = Commands.literal("custom");
        RequiredArgumentBuilder<CommandSourceStack, AdvancementFrame> frame = Commands.argument("frame", EnumArgument.enumArg(AdvancementFrame.class));
        RequiredArgumentBuilder<CommandSourceStack, String> title = Commands.argument("title", StringArgumentType.string());
        RequiredArgumentBuilder<CommandSourceStack, String> subtitle = Commands.argument("subtitle", StringArgumentType.string());
        RequiredArgumentBuilder<CommandSourceStack, CompoundTag> nbt = Commands.argument("nbt", NbtCompoundArgument.nbt());

        addSyntax(ctx -> sendToastExecutor(ctx, true, false), targets, held, frame, title);
        addSyntax(ctx -> sendToastExecutor(ctx, true, true), targets, held, frame, title, subtitle);
        addSyntax(ctx -> sendToastExecutor(ctx, false, false), targets, custom, frame, title, nbt);
        addSyntax(ctx -> sendToastExecutor(ctx, false, true), targets, custom, frame, title, nbt, subtitle);
    }

    private CommandResult sendToastExecutor(CommandContext<CommandSourceStack> ctx, boolean held, boolean hasSubtitle) throws CommandSyntaxException {
        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        ItemStack iconItem;

        if (held) {
            Player player = CommandUtil.getPlayer(ctx);
            if (player == null) {
                CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
                return CommandResult.failure();
            }
            iconItem = player.getInventory().getItemInMainHand();
        } else {
            CompoundTag nbt = ctx.getArgument("nbt", CompoundTag.class);
            DataResult<ItemStack> res = Codecs.ITEM_STACK.decode(NbtOps.INSTANCE, nbt);
            if (res.isError()) {
                CommandUtil.reply(ctx, "<red>Failed to parse custom NBT item</red>");
                return CommandResult.failure();
            }
            iconItem = res.getOrThrow();
        }

        AdvancementFrame advFrame = ctx.getArgument("frame", AdvancementFrame.class);
        String titleStr = ctx.getArgument("title", String.class);
        String subtitleStr = hasSubtitle ? ctx.getArgument("subtitle", String.class) : null;

        for (Player p : targets) {
            Component title = ServerTranslator.parseText(titleStr, p);
            Toast.Builder builder = Toast.builder()
                .titlle(title)
                .icon(iconItem)
                .frame(advFrame);

            if (hasSubtitle) {
                Component subtitle = ServerTranslator.parseText(subtitleStr, p);
                builder.subtitle(subtitle);
            }

            Toast toast = builder.build();
            toast.send(p);
        }

        if (targets.size() == 1) {
            CommandUtil.reply(ctx, "Toast sent to " + targets.get(0).getName());
        } else {
            CommandUtil.reply(ctx, "Toast sent to " + targets.size() + " players");
        }
        return CommandResult.success();
    }
}
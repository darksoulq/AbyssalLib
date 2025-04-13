package me.darksoul.abyssalLib.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;

public abstract class AbyssalCommand {
    public abstract String name();

    public abstract void register(LiteralArgumentBuilder<CommandSourceStack> root);

    public String fullCommandId(String modid) {
        return modid + ":" + name();
    }

    protected void sendFeedback(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().getSender().sendMessage(Component.text(message));
    }
}

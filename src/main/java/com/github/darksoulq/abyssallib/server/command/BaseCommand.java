package com.github.darksoulq.abyssallib.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class BaseCommand {

    private final String name;
    private final String[] aliases;
    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final List<LiteralArgumentBuilder<CommandSourceStack>> aliasBuilders;

    public BaseCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        this.root = LiteralArgumentBuilder.literal(name);
        this.aliasBuilders = new ArrayList<>();

        for (String alias : aliases) {
            this.aliasBuilders.add(LiteralArgumentBuilder.literal(alias));
        }
    }

    public void setRequirement(Predicate<CommandSourceStack> requirement) {
        this.root.requires(requirement);
        for (LiteralArgumentBuilder<CommandSourceStack> alias : this.aliasBuilders) {
            alias.requires(requirement);
        }
    }

    public void setDefaultExecutor(CommandExecutor executor) {
        com.mojang.brigadier.Command<CommandSourceStack> brigadierExecutor = ctx -> executor.execute(ctx).getValue();
        this.root.executes(brigadierExecutor);
        for (LiteralArgumentBuilder<CommandSourceStack> alias : this.aliasBuilders) {
            alias.executes(brigadierExecutor);
        }
    }

    @SafeVarargs
    public final void addSyntax(CommandExecutor executor, ArgumentBuilder<CommandSourceStack, ?>... arguments) {
        com.mojang.brigadier.Command<CommandSourceStack> brigadierExecutor = ctx -> executor.execute(ctx).getValue();

        if (arguments == null || arguments.length == 0) {
            setDefaultExecutor(executor);
            return;
        }

        ArgumentBuilder<CommandSourceStack, ?>[] copies = new ArgumentBuilder[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            copies[i] = copyNode(arguments[i]);
        }

        ArgumentBuilder<CommandSourceStack, ?> current = copies[copies.length - 1];
        current.executes(brigadierExecutor);

        for (int i = copies.length - 2; i >= 0; i--) {
            copies[i].then(current);
            current = copies[i];
        }

        this.root.then(current);
        for (LiteralArgumentBuilder<CommandSourceStack> alias : this.aliasBuilders) {
            alias.then(current);
        }
    }

    @SuppressWarnings("unchecked")
    private ArgumentBuilder<CommandSourceStack, ?> copyNode(ArgumentBuilder<CommandSourceStack, ?> original) {
        ArgumentBuilder<CommandSourceStack, ?> copy;

        if (original instanceof LiteralArgumentBuilder) {
            copy = LiteralArgumentBuilder.literal(((LiteralArgumentBuilder<CommandSourceStack>) original).getLiteral());
        } else {
            RequiredArgumentBuilder<CommandSourceStack, ?> req = (RequiredArgumentBuilder<CommandSourceStack, ?>) original;
            copy = RequiredArgumentBuilder.argument(req.getName(), req.getType());
            if (req.getSuggestionsProvider() != null) {
                ((RequiredArgumentBuilder<CommandSourceStack, ?>) copy).suggests(req.getSuggestionsProvider());
            }
        }

        copy.requires(original.getRequirement());

        if (original.getCommand() != null) {
            copy.executes(original.getCommand());
        }

        if (original.getRedirect() != null) {
            copy.forward(original.getRedirect(), original.getRedirectModifier(), original.isFork());
        }

        return copy;
    }

    public void addSubcommand(BaseCommand child) {
        this.root.then(child.getRoot());
        for (LiteralArgumentBuilder<CommandSourceStack> childAlias : child.getAliasBuilders()) {
            this.root.then(childAlias);
        }

        for (LiteralArgumentBuilder<CommandSourceStack> parentAlias : this.aliasBuilders) {
            parentAlias.then(child.getRoot());
            for (LiteralArgumentBuilder<CommandSourceStack> childAlias : child.getAliasBuilders()) {
                parentAlias.then(childAlias);
            }
        }
    }

    public LiteralArgumentBuilder<CommandSourceStack> getRoot() {
        return root;
    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getAliasBuilders() {
        return aliasBuilders;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }
}
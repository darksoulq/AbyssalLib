package com.github.darksoulq.abyssallib.server.placeholder;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PlaceholderContext {

    private final Player player;
    private final String[] rawArguments;
    private final PlaceholderArgument[] arguments;

    public PlaceholderContext(@Nullable Player player, String[] rawArguments) {
        this.player = player;
        this.rawArguments = rawArguments;
        this.arguments = new PlaceholderArgument[rawArguments.length];
        for (int i = 0; i < rawArguments.length; i++) {
            this.arguments[i] = new PlaceholderArgument(this, rawArguments[i]);
        }
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public int argsCount() {
        return arguments.length;
    }

    public boolean hasArgs() {
        return arguments.length > 0;
    }

    public String[] getRawArguments() {
        return rawArguments;
    }

    public String getRaw(int index, String def) {
        return (index >= 0 && index < rawArguments.length) ? rawArguments[index] : def;
    }

    public PlaceholderArgument arg(int index) {
        if (index >= 0 && index < arguments.length) {
            return arguments[index];
        }
        return new PlaceholderArgument(this, "");
    }
}
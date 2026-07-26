package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.server.command.BaseCommand;

public class InternalCommand extends BaseCommand {

    public InternalCommand() {
        super("abyssallib", "alib");

        addSubcommand(new GiveCommand());
        addSubcommand(new AttributeCommand());
        addSubcommand(new LocateCommand());
        addSubcommand(new SummonCommand());
        addSubcommand(new LootCommand());
        addSubcommand(new ToastCommand());
        addSubcommand(new SerializeCommand());
        addSubcommand(new StatisticsCommand());
        addSubcommand(new ReloadCommand());
        addSubcommand(new ContentCommand());
        addSubcommand(new PermissionsCommand());
    }
}
package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * An advancement reward that executes a command via the server console.
 * Placeholder "%player%" is substituted with the completing player's name.
 */
public class CommandReward implements AdvancementReward {

    /**
     * The codec used for serializing and deserializing the command reward.
     */
    public static final Codec<CommandReward> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.STRING.fieldOf("command").forGetter(CommandReward.class, p -> p.command)
    ).apply(instance, CommandReward::new)).describe("CommandReward");

    /**
     * The registered type definition for the command reward.
     */
    public static final RewardType<CommandReward> TYPE = () -> CODEC;

    private final String command;

    /**
     * Constructs a new CommandReward.
     *
     * @param command The command to execute. Use "%player%" as a target placeholder.
     */
    public CommandReward(String command) {
        this.command = command;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    /**
     * Grants the command reward by substituting placeholders and dispatching
     * the command through the Bukkit console sender.
     *
     * @param player The player receiving the reward.
     */
    @Override
    public void grant(Player player) {
        String parsed = command.replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
    }
}
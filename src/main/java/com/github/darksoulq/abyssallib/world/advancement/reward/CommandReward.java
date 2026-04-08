package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandReward implements AdvancementReward {

    public static final Codec<CommandReward> CODEC = new Codec<>() {
        @Override
        public <D> CommandReward decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            String command = Codecs.STRING.decode(ops, map.get(ops.createString("command")));
            return new CommandReward(command);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CommandReward value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("command"), Codecs.STRING.encode(ops, value.command)
            ));
        }
    };

    public static final RewardType<CommandReward> TYPE = () -> CODEC;

    private final String command;

    public CommandReward(String command) {
        this.command = command;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    @Override
    public void grant(Player player) {
        String parsed = command.replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
    }
}
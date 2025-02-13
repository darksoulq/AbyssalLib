package me.darksoul.abyssalLib.item.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import me.darksoul.abyssalLib.item.AItem;
import net.kyori.adventure.util.TriState;
import org.bukkit.block.BlockType;

public class ATool {
    public static void set(AItem item, ToolType type, float speed) {
        RegistryKeySet<BlockType> blocks = RegistryAccess.registryAccess().getRegistry(
                RegistryKey.BLOCK
        ).getTag(BlockTypeTagKeys.AIR);
        switch (type) {
            case PICKAXE:
                RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.BLOCK)
                        .getTag(BlockTypeTagKeys.MINEABLE_PICKAXE);
                break;
            case AXE:
                RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.BLOCK)
                        .getTag(BlockTypeTagKeys.MINEABLE_AXE);
                break;
            case SHOVEL:
                RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.BLOCK)
                        .getTag(BlockTypeTagKeys.MINEABLE_SHOVEL);
                break;
            case null, default:
                return;
        }

        Tool toolProps = Tool.tool().addRule(
                Tool.rule(
                        blocks,
                        speed,
                        TriState.TRUE
                )
        ).build();
        item.getItem().setData(DataComponentTypes.TOOL, toolProps);
    }

    public enum ToolType {
        PICKAXE,
        AXE,
        SHOVEL
    }
}

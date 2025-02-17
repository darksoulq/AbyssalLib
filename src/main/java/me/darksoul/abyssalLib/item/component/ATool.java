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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ATool {

    private final ItemStack item;
    private final Tool.Builder toolProps;
    private Collection<Tool.Rule> rules = new ArrayList<>();

    public ATool(@NotNull AItem aItem, ToolType type, float defaultSpeed) {
        item = aItem.getItem();
        toolProps = Tool.tool().defaultMiningSpeed(defaultSpeed);
        RegistryKeySet<BlockType> blocks = switch (type) {
            case PICKAXE -> RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.BLOCK)
                    .getTag(BlockTypeTagKeys.MINEABLE_PICKAXE);
            case AXE -> RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.BLOCK)
                    .getTag(BlockTypeTagKeys.MINEABLE_AXE);
            case SHOVEL -> RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.BLOCK)
                    .getTag(BlockTypeTagKeys.MINEABLE_SHOVEL);
            case null -> RegistryAccess.registryAccess().getRegistry(
                    RegistryKey.BLOCK
            ).getTag(BlockTypeTagKeys.AIR);
        };

        Tool.Rule defaultRule = Tool.rule(
                blocks,
                defaultSpeed,
                TriState.TRUE
        );
        rules.add(defaultRule);
    }

    public ATool removeDefaultRule() {
        rules.remove(0);
        return this;
    }
    public ATool rule(Tool.Rule rule) {
        rules.add(rule);
        return this;
    }
    public ATool damagePerBlock(int amount) {
        toolProps.damagePerBlock(amount);
        return this;
    }

    public void build() {
        toolProps.addRules(rules);
        item.setData(DataComponentTypes.TOOL, toolProps);
    }

//    public static void set(AItem item, ToolType type, float speed) {
//
//
//        Tool.Builder toolProps = Tool.tool().addRule(
//                Tool.rule(
//                        blocks,
//                        speed,
//                        TriState.TRUE
//                )
//        ).damagePerBlock(1);
//        item.getItem().setData(DataComponentTypes.TOOL, toolProps);
//    }

    public enum ToolType {
        PICKAXE,
        AXE,
        SHOVEL
    }
}

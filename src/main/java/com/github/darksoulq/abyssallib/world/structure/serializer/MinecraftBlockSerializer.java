package com.github.darksoulq.abyssallib.world.structure.serializer;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import net.kyori.adventure.text.Component;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles serialization and deserialization of Bukkit BlockData and TileStates.
 * <p>
 * This serializer bridges the gap between live world blocks and AbyssalLib's
 * dynamic data format, specifically handling inventories via Base64 and
 * sign text via Adventure components.
 */
public class MinecraftBlockSerializer {

    /**
     * Serializes standard block state properties.
     *
     * @param data The {@link BlockData} containing properties like rotation or age.
     * @param ops  The {@link DynamicOps} instance for data conversion.
     * @param <D>  The data format type.
     * @return A map of serialized block properties.
     */
    public static <D> Map<D, D> serialize(BlockData data, DynamicOps<D> ops) {
        return Adapter.save(ops, data);
    }

    /**
     * Loads serialized properties back into a BlockData instance.
     *
     * @param data The target {@link BlockData} to update.
     * @param map  The map containing serialized properties.
     * @param ops  The {@link DynamicOps} instance for data conversion.
     * @param <D>  The data format type.
     */
    public static <D> void deserialize(BlockData data, Map<D, D> map, DynamicOps<D> ops) {
        Adapter.load(ops, map, data);
    }

    /**
     * Serializes a block's Tile Entity data (TileState).
     * <p>
     * Currently supports:
     * <ul>
     * <li><b>Containers:</b> Inventories (Base64) and Custom Names.</li>
     * <li><b>Signs:</b> Front-side text lines as Adventure components.</li>
     * </ul>
     *
     * @param block The world {@link Block} to check for tile data.
     * @param ops   The {@link DynamicOps} instance for data conversion.
     * @param <D>   The data format type.
     * @return A map containing tile-specific data, or null if no tile data exists.
     */
    public static <D> Map<D, D> serializeTile(Block block, DynamicOps<D> ops) {
        BlockState state = block.getState();
        if (!(state instanceof TileState)) return null;

        Map<D, D> tileMap = new HashMap<>();

        if (state instanceof Container container) {
            tileMap.put(ops.createString("type"), ops.createString("container"));
            tileMap.put(ops.createString("items"), ops.createString(toBase64(container.getInventory())));
            if (container.customName() != null) {
                D nameData = Try.of(() -> Codecs.TEXT_COMPONENT.encode(ops, container.customName())).orElseThrow(e -> new RuntimeException(e.getMessage()));
                tileMap.put(ops.createString("custom_name"), nameData);
            }
        } else if (state instanceof Sign sign) {
            tileMap.put(ops.createString("type"), ops.createString("sign"));
            List<D> lines = new ArrayList<>();
            SignSide side = sign.getSide(Side.FRONT);
            for (int i = 0; i < 4; i++) {
                Component line = side.line(i);
                if (line.equals(Component.empty())) {
                    lines.add(Try.of(() -> Codecs.TEXT_COMPONENT.encode(ops, Component.empty())).orElseThrow(e -> new RuntimeException(e.getMessage())));
                } else {
                    lines.add(Try.of(() -> Codecs.TEXT_COMPONENT.encode(ops, line)).orElseThrow(e -> new RuntimeException(e.getMessage())));
                }
            }
            tileMap.put(ops.createString("lines"), ops.createList(lines));
        }

        return tileMap.isEmpty() ? null : tileMap;
    }

    /**
     * Applies serialized tile data back to a block in the world.
     *
     * @param block The target world {@link Block}.
     * @param data  The map containing serialized tile information.
     * @param ops   The {@link DynamicOps} instance for data conversion.
     * @param <D>   The data format type.
     */
    public static <D> void deserializeTile(Block block, Map<D, D> data, DynamicOps<D> ops) {
        if (data == null || data.isEmpty()) return;
        BlockState state = block.getState();
        String type = ops.getStringValue(data.get(ops.createString("type"))).orElse("");

        if ("container".equals(type) && state instanceof Container container) {
            ops.getStringValue(data.get(ops.createString("items"))).ifPresent(b64 -> {
                try { fromBase64(b64, container.getInventory()); } catch (Exception e) { e.printStackTrace(); }
            });
            D nameData = data.get(ops.createString("custom_name"));
            if (nameData != null) {
                Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, nameData)).onSuccess(container::customName);
            }
        } else if ("sign".equals(type) && state instanceof Sign sign) {
            ops.getList(data.get(ops.createString("lines"))).ifPresent(list -> {
                SignSide side = sign.getSide(Side.FRONT);
                for (int i = 0; i < 4 && i < list.size(); i++) {
                    int finalI = i;
                    Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, list.get(finalI))).onSuccess(line -> side.line(finalI, line));
                }
            });
        }
        state.update(true, false);
    }

    /**
     * Converts an inventory's contents to a Base64 encoded string.
     *
     * @param inventory The {@link Inventory} to serialize.
     * @return A Base64 string representing the item contents.
     */
    private static String toBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            org.bukkit.util.io.BukkitObjectOutputStream dataOutput = new org.bukkit.util.io.BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /**
     * Reconstructs an inventory from a Base64 encoded string.
     *
     * @param data      The Base64 string.
     * @param inventory The target {@link Inventory} to populate.
     * @throws Exception If deserialization or reading fails.
     */
    private static void fromBase64(String data, Inventory inventory) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        org.bukkit.util.io.BukkitObjectInputStream dataInput = new org.bukkit.util.io.BukkitObjectInputStream(inputStream);
        int size = dataInput.readInt();
        for (int i = 0; i < size; i++) {
            inventory.setItem(i, (ItemStack) dataInput.readObject());
        }
        dataInput.close();
    }
}
package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.bridge.BlockProvider;
import com.github.darksoulq.abyssallib.server.bridge.BridgeBlock;
import io.netty.handler.codec.CodecException;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class MinecraftProvider extends BlockProvider<BlockData> {
    public MinecraftProvider() {
        super("minecraft");
    }

    @Override
    public Identifier getId(BridgeBlock<BlockData> value) {
        return Identifier.of(value.id().getNamespace(), value.id().getPath());
    }

    @Override
    public BridgeBlock<BlockData> get(Identifier id) {
        Material mat = Material.valueOf(id.getPath().toUpperCase(Locale.ROOT));
        if (!mat.isBlock()) return null;
        BlockData data = mat.createBlockData();
        return new BridgeBlock<>(id, getPrefix(), data) {
            @Override
            public void place(Location location) {
                if (location.getBlock().getType() == Material.WATER && data instanceof Waterlogged wl) {
                    wl.setWaterlogged(true);
                }
                location.getBlock().setBlockData(data, false);
            }
        };
    }

    @Override
    public <D> Map<D, D> serializeData(BlockData value, DynamicOps<D> ops) {
        return Adapter.save(ops, value);
    }

    public <D> Map<D, D> serializeTileEntity(Block block, DynamicOps<D> ops) {
        BlockState state = block.getState();
        if (!(state instanceof TileState)) return null;

        Map<D, D> tileMap = new HashMap<>();

        if (state instanceof Container container) {
            tileMap.put(ops.createString("type"), ops.createString("container"));
            tileMap.put(ops.createString("items"), ops.createString(toBase64(container.getInventory())));
            if (container.customName() != null) {
                D nameData = Try.of(() -> Codecs.TEXT_COMPONENT.encode(ops, container.customName())).orElseThrow(e -> new CodecException(e.getMessage()));
                tileMap.put(ops.createString("custom_name"), nameData);
            }
        } else if (state instanceof Sign sign) {
            tileMap.put(ops.createString("type"), ops.createString("sign"));
            List<D> lines = new ArrayList<>();
            SignSide side = sign.getSide(Side.FRONT);
            for (int i = 0; i < 4; i++) {
                Component line = side.line(i);
                if (line.equals(Component.empty())) continue;
                lines.add(Try.of(() ->Codecs.TEXT_COMPONENT.encode(ops, line)).orElseThrow(e -> new CodecException(e.getMessage())));
            }
            tileMap.put(ops.createString("lines"), ops.createList(lines));
        }

        return tileMap.isEmpty() ? null : tileMap;
    }

    public <D> void deserializeTileEntity(Block block, Map<D, D> data, DynamicOps<D> ops) {
        if (data == null || data.isEmpty()) return;
        BlockState state = block.getState();
        String type = ops.getStringValue(data.get(ops.createString("type"))).orElse("");

        if ("container".equals(type) && state instanceof Container container) {
            ops.getStringValue(data.get(ops.createString("items"))).ifPresent(b64 -> {
                try { fromBase64(b64, container.getInventory()); } catch (Exception e) { e.printStackTrace(); }
            });
            D nameData = data.get(ops.createString("custom_name"));
            if (nameData != null) {
                Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, nameData)).onSuccess(container::customName).orElseThrow(e -> new CodecException(e.getMessage()));
            }
        } else if ("sign".equals(type) && state instanceof Sign sign) {
            ops.getList(data.get(ops.createString("lines"))).ifPresent(list -> {
                SignSide side = sign.getSide(Side.FRONT);
                for (int i = 0; i < 4 && i < list.size(); i++) {
                    int finalI = i;
                    Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, list.get(finalI))).onSuccess(line -> side.line(finalI, line))
                        .orElseThrow(e -> new CodecException(e.getMessage()));
                }
            });
        }
        state.update(true, false);
    }

    @Override
    public <T> BridgeBlock<BlockData> deserializeData(Map<T, T> data, BridgeBlock<BlockData> value, DynamicOps<T> ops) {
        if (!(value.value() instanceof BlockData base)) return null;
        Adapter.load(ops, data, base);

        return new BridgeBlock<>(value.id(), value.provider(), base) {
            @Override
            public void place(Location location) {
                if (location.getBlock().getType() == Material.WATER && base instanceof Waterlogged wl) {
                    wl.setWaterlogged(true);
                }
                location.getBlock().setBlockData(base, false);
            }
        };
    }

    private String toBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void fromBase64(String data, Inventory inventory) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        int size = dataInput.readInt();
        for (int i = 0; i < size; i++) {
            inventory.setItem(i, (ItemStack) dataInput.readObject());
        }
        dataInput.close();
    }
}
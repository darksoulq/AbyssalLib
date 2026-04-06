package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignTileAdapter extends TileAdapter<Sign> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Sign;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Sign value) throws Codec.CodecException {
        Map<D, D> signMap = new HashMap<>();

        List<D> frontLines = new ArrayList<>();
        SignSide front = value.getSide(Side.FRONT);
        for (Component c : front.lines()) frontLines.add(Codecs.TEXT_COMPONENT.encode(ops, c));
        signMap.put(ops.createString("front_lines"), ops.createList(frontLines));
        signMap.put(ops.createString("front_glowing"), Codecs.BOOLEAN.encode(ops, front.isGlowingText()));
        if (front.getColor() != null) signMap.put(ops.createString("front_color"), ops.createString(front.getColor().name()));

        List<D> backLines = new ArrayList<>();
        SignSide back = value.getSide(Side.BACK);
        for (Component c : back.lines()) backLines.add(Codecs.TEXT_COMPONENT.encode(ops, c));
        signMap.put(ops.createString("back_lines"), ops.createList(backLines));
        signMap.put(ops.createString("back_glowing"), Codecs.BOOLEAN.encode(ops, back.isGlowingText()));
        if (back.getColor() != null) signMap.put(ops.createString("back_color"), ops.createString(back.getColor().name()));

        return ops.createMap(signMap);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Sign sign)) return;
        Map<D, D> signMap = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Sign"));

        ops.getList(signMap.get(ops.createString("front_lines"))).ifPresent(list -> {
            SignSide side = sign.getSide(Side.FRONT);
            for (int i = 0; i < 4 && i < list.size(); i++) {
                int finalI = i;
                Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, list.get(finalI))).onSuccess(line -> side.line(finalI, line));
            }
        });
        D fColor = signMap.get(ops.createString("front_color"));
        if (fColor != null) ops.getStringValue(fColor).ifPresent(c -> Try.run(() -> sign.getSide(Side.FRONT).setColor(DyeColor.valueOf(c))));
        D fGlow = signMap.get(ops.createString("front_glowing"));
        if (fGlow != null) sign.getSide(Side.FRONT).setGlowingText(Try.of(() -> Codecs.BOOLEAN.decode(ops, fGlow)).orElse(false));

        ops.getList(signMap.get(ops.createString("back_lines"))).ifPresent(list -> {
            SignSide side = sign.getSide(Side.BACK);
            for (int i = 0; i < 4 && i < list.size(); i++) {
                int finalI = i;
                Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, list.get(finalI))).onSuccess(line -> side.line(finalI, line));
            }
        });
        D bColor = signMap.get(ops.createString("back_color"));
        if (bColor != null) ops.getStringValue(bColor).ifPresent(c -> Try.run(() -> sign.getSide(Side.BACK).setColor(DyeColor.valueOf(c))));
        D bGlow = signMap.get(ops.createString("back_glowing"));
        if (bGlow != null) sign.getSide(Side.BACK).setGlowingText(Try.of(() -> Codecs.BOOLEAN.decode(ops, bGlow)).orElse(false));
    }
}
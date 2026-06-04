package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Sign value) {
        Map<D, D> signMap = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        SignSide front = value.getSide(Side.FRONT);
        List<D> frontLines = new ArrayList<>();
        List<Component> fLines = front.lines();
        for (int i = 0; i < fLines.size(); i++) {
            DataResult<D> res = Codecs.TEXT_COMPONENT.encode(ops, fLines.get(i)).prependPath("front_lines[" + i + "]");
            if (res.isError()) warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            else {
                frontLines.add(res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }
        signMap.put(ops.createString("front_lines"), ops.createList(frontLines));

        DataResult<D> fGlowingRes = Codecs.BOOLEAN.encode(ops, front.isGlowingText()).prependPath("front_glowing");
        if (fGlowingRes.isError())
            warnings.add(fGlowingRes.dataError().orElseGet(() -> DataError.custom(fGlowingRes.error().get())));
        else {
            signMap.put(ops.createString("front_glowing"), fGlowingRes.getOrThrow());
            if (fGlowingRes.isPartial()) warnings.addAll(fGlowingRes.warnings());
        }

        if (front.getColor() != null)
            signMap.put(ops.createString("front_color"), ops.createString(front.getColor().name()));

        SignSide back = value.getSide(Side.BACK);
        List<D> backLines = new ArrayList<>();
        List<Component> bLines = back.lines();
        for (int i = 0; i < bLines.size(); i++) {
            DataResult<D> res = Codecs.TEXT_COMPONENT.encode(ops, bLines.get(i)).prependPath("back_lines[" + i + "]");
            if (res.isError()) warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            else {
                backLines.add(res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }
        signMap.put(ops.createString("back_lines"), ops.createList(backLines));

        DataResult<D> bGlowingRes = Codecs.BOOLEAN.encode(ops, back.isGlowingText()).prependPath("back_glowing");
        if (bGlowingRes.isError())
            warnings.add(bGlowingRes.dataError().orElseGet(() -> DataError.custom(bGlowingRes.error().get())));
        else {
            signMap.put(ops.createString("back_glowing"), bGlowingRes.getOrThrow());
            if (bGlowingRes.isPartial()) warnings.addAll(bGlowingRes.warnings());
        }

        if (back.getColor() != null)
            signMap.put(ops.createString("back_color"), ops.createString(back.getColor().name()));

        return warnings.isEmpty() ? DataResult.success(ops.createMap(signMap)) : DataResult.partial(ops.createMap(signMap), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Sign sign)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(signMap -> {
                List<DataError> warnings = new ArrayList<>();

                D fLinesData = signMap.get(ops.createString("front_lines"));
                if (fLinesData != null) {
                    ops.getList(fLinesData).ifPresent(list -> {
                        SignSide side = sign.getSide(Side.FRONT);
                        for (int i = 0; i < 4 && i < list.size(); i++) {
                            DataResult<Component> res = Codecs.TEXT_COMPONENT.decode(ops, list.get(i)).prependPath("front_lines[" + i + "]");
                            if (res.isError())
                                warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                            else {
                                side.line(i, res.getOrThrow());
                                if (res.isPartial()) warnings.addAll(res.warnings());
                            }
                        }
                    });
                }

                D fColor = signMap.get(ops.createString("front_color"));
                if (fColor != null) {
                    ops.getStringValue(fColor).ifPresent(c -> {
                        try {
                            sign.getSide(Side.FRONT).setColor(DyeColor.valueOf(c));
                        } catch (Exception ignored) {
                        }
                    });
                }

                D fGlow = signMap.get(ops.createString("front_glowing"));
                if (fGlow != null) {
                    DataResult<Boolean> res = Codecs.BOOLEAN.decode(ops, fGlow).prependPath("front_glowing");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else {
                        sign.getSide(Side.FRONT).setGlowingText(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D bLinesData = signMap.get(ops.createString("back_lines"));
                if (bLinesData != null) {
                    ops.getList(bLinesData).ifPresent(list -> {
                        SignSide side = sign.getSide(Side.BACK);
                        for (int i = 0; i < 4 && i < list.size(); i++) {
                            DataResult<Component> res = Codecs.TEXT_COMPONENT.decode(ops, list.get(i)).prependPath("back_lines[" + i + "]");
                            if (res.isError())
                                warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                            else {
                                side.line(i, res.getOrThrow());
                                if (res.isPartial()) warnings.addAll(res.warnings());
                            }
                        }
                    });
                }

                D bColor = signMap.get(ops.createString("back_color"));
                if (bColor != null) {
                    ops.getStringValue(bColor).ifPresent(c -> {
                        try {
                            sign.getSide(Side.BACK).setColor(DyeColor.valueOf(c));
                        } catch (Exception ignored) {
                        }
                    });
                }

                D bGlow = signMap.get(ops.createString("back_glowing"));
                if (bGlow != null) {
                    DataResult<Boolean> res = Codecs.BOOLEAN.decode(ops, bGlow).prependPath("back_glowing");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else {
                        sign.getSide(Side.BACK).setGlowingText(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}
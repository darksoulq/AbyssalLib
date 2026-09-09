package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.common.util.Either;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.data.tag.impl.BlockTag;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;

import java.util.Collections;
import java.util.List;

public class CustomToolType extends DataComponent<CustomToolType.ToolData> {

    public record ToolData(
        int miningLevel,
        float speed,
        boolean affectedByUnderwater,
        boolean affectedByFloating,
        List<Either<BlockType, Either<CustomBlock, BlockTag>>> suitableBlocks,
        List<Either<BlockType, Either<CustomBlock, BlockTag>>> unsuitableBlocks
    ) {
        public boolean isSuitable(Block bukkitBlock, CustomBlock customBlock) {
            String blockId = customBlock != null ? customBlock.getId().asString() : "minecraft:" + bukkitBlock.getType().name().toLowerCase();
            BlockType vanillaType = customBlock == null ? Registry.BLOCK.get(bukkitBlock.getType().getKey()) : null;

            if (unsuitableBlocks != null && !unsuitableBlocks.isEmpty()) {
                for (Either<BlockType, Either<CustomBlock, BlockTag>> target : unsuitableBlocks) {
                    if (matches(target, vanillaType, customBlock, blockId)) return false;
                }
            }

            if (suitableBlocks != null && !suitableBlocks.isEmpty()) {
                for (Either<BlockType, Either<CustomBlock, BlockTag>> target : suitableBlocks) {
                    if (matches(target, vanillaType, customBlock, blockId)) return true;
                }
                return false;
            }

            return true;
        }

        private boolean matches(Either<BlockType, Either<CustomBlock, BlockTag>> target, BlockType vanillaType, CustomBlock customBlock, String blockId) {
            return target.fold(
                bt -> bt.equals(vanillaType),
                right -> right.fold(
                    cb -> customBlock != null && cb.getId().equals(customBlock.getId()),
                    tag -> tag.getAll().contains(blockId)
                )
            );
        }
    }

    public static final Codec<Either<BlockType, Either<CustomBlock, BlockTag>>> BLOCK_TARGET_CODEC = Codecs.STRING.flatXmap(
        str -> {
            if (str.startsWith("#")) {
                Tag<?, ?> tag = Registries.TAGS.get(str.substring(1));
                if (tag instanceof BlockTag bt) {
                    return DataResult.success(Either.right(Either.right(bt)));
                }
                return DataResult.error(DataError.custom("Unknown block tag: " + str));
            } else if (str.startsWith("minecraft:")) {
                BlockType type = Registry.BLOCK.get(NamespacedKey.fromString(str));
                if (type != null) {
                    return DataResult.success(Either.left(type));
                }
                return DataResult.error(DataError.custom("Unknown vanilla block: " + str));
            } else {
                CustomBlock cb = Registries.BLOCKS.get(str);
                if (cb != null) {
                    return DataResult.success(Either.right(Either.left(cb)));
                }
                return DataResult.error(DataError.custom("Unknown custom block: " + str));
            }
        },
        (Either<BlockType, Either<CustomBlock, BlockTag>> either) -> either.fold(
            blockType -> DataResult.success(blockType.getKey().asString()),
            (Either<CustomBlock, BlockTag> right) -> right.fold(
                customBlock -> DataResult.success(customBlock.getId().asString()),
                blockTag -> DataResult.success("#" + blockTag.getId().asString())
            )
        )
    ).describe("BlockTarget");

    public static final Codec<ToolData> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("mining_level").forGetter(ToolData::miningLevel),
        Codecs.FLOAT.fieldOf("speed").forGetter(ToolData::speed),
        Codecs.BOOLEAN.fieldOf("affected_by_underwater").forGetter(ToolData::affectedByUnderwater),
        Codecs.BOOLEAN.fieldOf("affected_by_floating").forGetter(ToolData::affectedByFloating),
        BLOCK_TARGET_CODEC.list().optionalFieldOf("suitable_blocks", Collections.emptyList()).forGetter(ToolData::suitableBlocks),
        BLOCK_TARGET_CODEC.list().optionalFieldOf("unsuitable_blocks", Collections.emptyList()).forGetter(ToolData::unsuitableBlocks)
    ).apply(instance, ToolData::new)).describe("CustomToolData");

    public static final DataComponentType<CustomToolType> TYPE = DataComponentType.simple(CODEC.xmap(CustomToolType::new, CustomToolType::getValue));

    public CustomToolType(ToolData value) {
        super(value);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}
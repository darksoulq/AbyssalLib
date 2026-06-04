package com.github.darksoulq.abyssallib.world.structure;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.server.scheduler.ScheduledTask;
import com.github.darksoulq.abyssallib.server.util.regional.RegionalProcessor;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.NMSWorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a fully captured, transformable, and replayable structure definition.
 * This implementation supports palette-based block storage for deduplication,
 * relative positioning for blocks and entities, transformation via rotations and
 * mirrors, and both synchronous and asynchronous placement routines.
 */
public class Structure {

    /**
     * Palette of unique block state entries.
     * Each entry represents a unique block identifier and its optional state data to prevent
     * redundant data storage across the structure.
     */
    private final List<PaletteEntry> palette = new ArrayList<>();

    /**
     * Collection of blocks stored in this structure.
     * Each block acts as a lightweight pointer referencing an index within the {@link #palette}.
     */
    private final List<StructureBlock> blocks = new ArrayList<>();

    /**
     * Collection of entities stored in this structure, mapped with relative positions.
     */
    private final List<StructureEntity> entities = new ArrayList<>();

    /**
     * List of processors applied sequentially during block placement to alter or filter the layout.
     */
    private final List<StructureProcessor> processors = new ArrayList<>();

    /**
     * Dimensions of the structure encapsulated as width, height, and depth.
     */
    private Vector size;

    public Structure() {
    }

    public Structure(Vector size, List<PaletteEntry> palette, List<StructureBlock> blocks, List<StructureEntity> entities) {
        this.size = size;
        this.palette.addAll(palette);
        this.blocks.addAll(blocks);
        this.entities.addAll(entities);
    }

    /**
     * A lightweight data record describing a unique combination of block type and properties.
     *
     * @param id     The namespaced identifier for the block material or custom implementation.
     * @param states Serialized block-states object (nullable).
     */
    public record PaletteEntry(String id, @Nullable Object states) {
    }

    public static final Codec<PaletteEntry> PALETTE_ENTRY_CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.STRING.fieldOf("Name").forGetter(PaletteEntry.class, PaletteEntry::id),
        Codecs.PASSTHROUGH.nullable().optionalFieldOf("States", null).forGetter(PaletteEntry.class, PaletteEntry::states)
    ).apply(instance, PaletteEntry::new)).describe("PaletteEntry");

    /**
     * A structural marker indicating the position and index configuration of a discrete block.
     *
     * @param pos        The relative vector offset.
     * @param stateIndex The pointer index resolving to a definition inside the structure palette.
     * @param properties Extra custom NBT-like properties bound directly to the block logic.
     * @param nbt        Standard vanilla tile-entity data (nullable).
     */
    public record StructureBlock(Vector pos, int stateIndex, @Nullable Object properties, @Nullable Object nbt) {
    }

    public static final Codec<StructureBlock> STRUCTURE_BLOCK_CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.VECTOR_I.fieldOf("pos").forGetter(StructureBlock.class, StructureBlock::pos),
        Codecs.INT.fieldOf("state").forGetter(StructureBlock.class, StructureBlock::stateIndex),
        Codecs.PASSTHROUGH.nullable().optionalFieldOf("properties", null).forGetter(StructureBlock.class, StructureBlock::properties),
        Codecs.PASSTHROUGH.nullable().optionalFieldOf("nbt", null).forGetter(StructureBlock.class, StructureBlock::nbt)
    ).apply(instance, StructureBlock::new)).describe("StructureBlock");

    /**
     * A structural marker holding a serialized entity and its exact relative spatial location.
     *
     * @param pos    The floating-point {@link Vector} detailing position offsets.
     * @param entity The heavily abstracted {@link SavedEntity} container representing the deserialized mob or object.
     */
    public record StructureEntity(Vector pos, SavedEntity entity) {
    }

    public static final Codec<StructureEntity> STRUCTURE_ENTITY_CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.VECTOR_F.fieldOf("pos").forGetter(StructureEntity.class, StructureEntity::pos),
        ExtraCodecs.SAVED_ENTITY.fieldOf("data").forGetter(StructureEntity.class, StructureEntity::entity)
    ).apply(instance, StructureEntity::new)).describe("StructureEntity");

    public static final Codec<Structure> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.VECTOR_I.optionalFieldOf("size", new Vector(0, 0, 0)).forGetter(Structure.class, s -> s.size),
        PALETTE_ENTRY_CODEC.list().optionalFieldOf("palette", Collections.emptyList()).forGetter(Structure.class, s -> s.palette),
        STRUCTURE_BLOCK_CODEC.list().optionalFieldOf("blocks", Collections.emptyList()).forGetter(Structure.class, s -> s.blocks),
        STRUCTURE_ENTITY_CODEC.list().optionalFieldOf("entities", Collections.emptyList()).forGetter(Structure.class, s -> s.entities)
    ).apply(instance, Structure::new)).describe("Structure");

    /**
     * Captures all blocks within the defined region, excluding entities.
     *
     * @param corner1 The first corner {@link Location} bounding the region.
     * @param corner2 The opposite corner {@link Location} bounding the region.
     * @param origin  The designated origin {@link Location} utilized for relative positioning.
     */
    public void fill(@NotNull Location corner1, @NotNull Location corner2, @NotNull Location origin) {
        fill(corner1, corner2, origin, true);
    }

    /**
     * Captures blocks and optionally entities within the defined physical region.
     * Regional processor dynamically executes chunk-safe block mapping on Folia or Bukkit.
     *
     * @param corner1         The first corner {@link Location} bounding the region.
     * @param corner2         The opposite corner {@link Location} bounding the region.
     * @param origin          The origin {@link Location} used for zero-point relative alignment.
     * @param includeEntities True if entities within the bounding box should be captured and serialized.
     */
    @SuppressWarnings("unchecked")
    public void fill(@NotNull Location corner1, @NotNull Location corner2, @NotNull Location origin, boolean includeEntities) {
        palette.clear();
        blocks.clear();
        entities.clear();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        this.size = new Vector(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);

        int originX = origin.getBlockX();
        int originY = origin.getBlockY();
        int originZ = origin.getBlockZ();

        Map<PaletteEntry, Integer> paletteLookup = new HashMap<>();

        RegionalProcessor.processVolume(AbyssalLib.getInstance(), corner1, corner2, block -> {
            if (block.getType() == Material.STRUCTURE_VOID) {
                return;
            }

            BlockInfo info = BlockInfo.resolve(block);

            Object statesObj = null;
            Object propsObj = null;
            Object nbtObj = null;

            if (info.states() != null) statesObj = info.states();
            if (info.properties() != null) propsObj = info.properties();
            if (info.nbt() != null) nbtObj = info.nbt();

            PaletteEntry entry = new PaletteEntry(info.getAsString(), statesObj);

            int paletteIndex;
            synchronized (paletteLookup) {
                paletteIndex = paletteLookup.computeIfAbsent(entry, k -> {
                    palette.add(k);
                    return palette.size() - 1;
                });
            }

            synchronized (blocks) {
                blocks.add(new StructureBlock(
                    new Vector(block.getX() - originX, block.getY() - originY, block.getZ() - originZ),
                    paletteIndex,
                    propsObj,
                    nbtObj
                ));
            }

        }, () -> {
            if (includeEntities) {
                AbyssalLib.SCHEDULER.schedule(() -> {
                    BoundingBox box = BoundingBox.of(corner1, corner2);
                    for (org.bukkit.entity.Entity entity : corner1.getWorld().getNearbyEntities(box)) {
                        if (entity instanceof Player) {
                            continue;
                        }
                        Vector relativePos = entity.getLocation().toVector().subtract(origin.toVector());
                        SavedEntity savedEntity = SavedEntity.create(entity, JsonOps.INSTANCE);
                        synchronized (entities) {
                            this.entities.add(new StructureEntity(relativePos, savedEntity));
                        }
                    }
                }).region(origin).once();
            }
        });
    }

    /**
     * Places this structure asynchronously using a scheduled repeating task securely evaluating regional boundaries.
     *
     * @param plugin        The {@link Plugin} utilized for scheduling the task.
     * @param origin        The target placement origin {@link Location}.
     * @param rotation      The {@link StructureRotation} to apply.
     * @param mirror        The {@link Mirror} transformation to apply.
     * @param integrity     The survival chance [0.0 - 1.0] for each individual block.
     * @param blocksPerTick The maximum number of blocks to process per execution slice.
     * @return A {@link CompletableFuture} that resolves when the entire placement operation completes.
     */
    public CompletableFuture<Void> placeAsync(@NotNull Plugin plugin, @NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity, int blocksPerTick) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new Paster(origin, rotation, mirror, integrity, blocksPerTick, future).start(plugin);
        return future;
    }

    /**
     * Places this structure synchronously targeting regional thread execution requirements seamlessly.
     *
     * @param origin    The target placement origin {@link Location}.
     * @param rotation  The {@link StructureRotation} to apply.
     * @param mirror    The {@link Mirror} transformation to apply.
     * @param integrity The survival chance [0.0 - 1.0] for each individual block.
     */
    public void place(@NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        AbyssalLib.SCHEDULER.schedule(() -> {
            Random random = new Random();
            BlockData[] bakedData = new BlockData[palette.size()];
            CustomBlock[] bakedCustom = new CustomBlock[palette.size()];
            bakePalette(bakedData, bakedCustom, rotation, mirror);

            for (StructureBlock sb : blocks) {
                if (integrity < 1.0f && random.nextFloat() > integrity) {
                    continue;
                }
                processStructureBlock(null, origin, sb, rotation, mirror, bakedData, bakedCustom, null);
            }
            placeEntities(null, origin, rotation, mirror, entities, null);
        }).region(origin).once();
    }

    /**
     * Places this structure inside an asynchronous or virtual {@link WorldGenAccess} context.
     * Handles deferring out-of-bounds placements back to regional scheduled executions explicitly ensuring safe completion.
     *
     * @param level     The {@link WorldGenAccess} bridging chunk generation.
     * @param origin    The target placement origin {@link Location}.
     * @param rotation  The {@link StructureRotation} to apply.
     * @param mirror    The {@link Mirror} transformation to apply.
     * @param integrity The survival chance [0.0 - 1.0] for each individual block.
     */
    public void place(@NotNull WorldGenAccess level, @NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        Random random = new Random();
        List<StructureBlock> deferredBlocks = new ArrayList<>();
        List<StructureEntity> deferredEntities = new ArrayList<>();

        BlockData[] bakedData = new BlockData[palette.size()];
        CustomBlock[] bakedCustom = new CustomBlock[palette.size()];
        bakePalette(bakedData, bakedCustom, rotation, mirror);

        for (StructureBlock sb : blocks) {
            if (integrity < 1.0f && random.nextFloat() > integrity) {
                continue;
            }
            processStructureBlock(level, origin, sb, rotation, mirror, bakedData, bakedCustom, deferredBlocks);
        }

        placeEntities(level, origin, rotation, mirror, entities, deferredEntities);

        if (!deferredBlocks.isEmpty() || !deferredEntities.isEmpty()) {
            AbyssalLib.SCHEDULER.schedule(() -> {
                for (StructureBlock sb : deferredBlocks) {
                    processStructureBlock(null, origin, sb, rotation, mirror, bakedData, bakedCustom, null);
                }
                placeEntities(null, origin, rotation, mirror, deferredEntities, null);
            }).region(origin).once();
        }
    }

    /**
     * Appends a {@link StructureProcessor} to the placement pipeline.
     *
     * @param processor The {@link StructureProcessor} to add.
     */
    public void addProcessor(StructureProcessor processor) {
        processors.add(processor);
    }

    /**
     * Pre-calculates transformed BlockData instances for every entry in the palette.
     * This avoids redundant calculations during the placement loop.
     *
     * @param dataOut   The output array for baked vanilla {@link BlockData}.
     * @param customOut The output array for baked {@link CustomBlock} objects.
     * @param rotation  The specified {@link StructureRotation}.
     * @param mirror    The specified {@link Mirror} transform.
     */
    @SuppressWarnings("unchecked")
    private void bakePalette(BlockData[] dataOut, CustomBlock[] customOut, StructureRotation rotation, Mirror mirror) {
        for (int i = 0; i < palette.size(); i++) {
            PaletteEntry entry = palette.get(i);
            Object blockObj = null;
            if (entry.id().startsWith("minecraft:")) {
                try {
                    Material mat = Material.valueOf(entry.id().substring(10).toUpperCase());
                    if (mat.isBlock()) {
                        blockObj = mat.createBlockData();
                    }
                } catch (Exception ignored) {
                }
            } else {
                CustomBlock cb = Registries.BLOCKS.get(entry.id());
                if (cb != null) {
                    blockObj = cb.clone();
                }
            }

            if (blockObj != null) {
                ObjectNode statesNode = null;
                if (entry.states() != null && entry.states() instanceof ObjectNode n) {
                    statesNode = n;
                }

                BlockInfo temp = new BlockInfo(null, blockObj, statesNode, null, null);
                BlockData bd = WorldGenUtils.bakeData(temp, rotation, mirror);
                if (bd instanceof Leaves leaves) {
                    leaves.setPersistent(true);
                }
                dataOut[i] = bd;
                if (blockObj instanceof CustomBlock cb) {
                    customOut[i] = cb;
                }
            }
        }
    }

    /**
     * Core routine for evaluating, transforming, processing, and executing the placement of a single block.
     *
     * @param level       The generation context, or null for a live Bukkit world.
     * @param origin      The absolute origin {@link Location}.
     * @param sb          The lightweight {@link StructureBlock} reference.
     * @param rotation    The structural rotation logic.
     * @param mirror      The structural mirror logic.
     * @param bakedData   The cached array of vanilla block data.
     * @param bakedCustom The cached array of custom block references.
     * @param deferred    A collection to catch blocks that fall outside safe generation chunk boundaries.
     */
    private void processStructureBlock(WorldGenAccess level, Location origin, StructureBlock sb, StructureRotation rotation, Mirror mirror, BlockData[] bakedData, CustomBlock[] bakedCustom, List<StructureBlock> deferred) {
        if (sb.stateIndex() < 0 || sb.stateIndex() >= palette.size()) {
            return;
        }
        BlockData bd = bakedData[sb.stateIndex()];
        if (bd == null) {
            return;
        }

        Vector transformed = transform(sb.pos().clone(), mirror, rotation);
        Location target = origin.clone().add(transformed);

        if (target.getBlockY() < origin.getWorld().getMinHeight() || target.getBlockY() >= origin.getWorld().getMaxHeight()) {
            return;
        }

        if (level instanceof NMSWorldGenAccess nmsLevel && !nmsLevel.isInRegion(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
            if (deferred != null) {
                deferred.add(sb);
            }
            return;
        }

        ObjectNode propertiesNode = null;
        ObjectNode nbtNode = null;
        ObjectNode statesNode = null;

        if (sb.properties() != null && sb.properties() instanceof ObjectNode n)
            propertiesNode = n;
        if (sb.nbt() != null && sb.nbt() instanceof ObjectNode n) nbtNode = n;
        if (palette.get(sb.stateIndex()).states() != null && palette.get(sb.stateIndex()).states() instanceof ObjectNode n)
            statesNode = n;


        if (processors.isEmpty() && sb.nbt() == null && sb.properties() == null) {
            if (level == null && !target.isChunkLoaded()) {
                target.getChunk().load(true);
            }
            BlockData current = level != null ? level.getBlockData(target.getBlockX(), target.getBlockY(), target.getBlockZ()) : target.getBlock().getBlockData();
            if (current.matches(bd)) {
                return;
            }

            CustomBlock cb = bakedCustom[sb.stateIndex()];
            WorldGenUtils.placeBlock(level, target, new BlockInfo(transformed, cb != null ? cb : bd, statesNode, null, null), bd.clone(), cb != null ? cb.clone() : null);
        } else {
            CustomBlock cb = bakedCustom[sb.stateIndex()];
            BlockInfo original = new BlockInfo(null, cb != null ? cb : bd, statesNode, propertiesNode, nbtNode);
            BlockInfo current = new BlockInfo(transformed, cb != null ? cb : bd, statesNode, propertiesNode, nbtNode);

            for (StructureProcessor processor : processors) {
                current = (level != null) ? processor.process(level, origin, current, original) : processor.process(origin.getWorld(), origin, current, original);
                if (current == null) {
                    return;
                }
            }
            if (current.pos() == null) {
                return;
            }

            Location finalTarget = origin.clone().add(current.pos());
            if (finalTarget.getBlockY() < origin.getWorld().getMinHeight() || finalTarget.getBlockY() >= origin.getWorld().getMaxHeight()) {
                return;
            }

            if (level instanceof NMSWorldGenAccess nmsLevel && !nmsLevel.isInRegion(finalTarget.getBlockX(), finalTarget.getBlockY(), finalTarget.getBlockZ())) {
                final BlockInfo finalCurrent = current;
                AbyssalLib.SCHEDULER.schedule(() -> {
                    if (!finalTarget.isChunkLoaded()) {
                        finalTarget.getChunk().load(true);
                    }
                    BlockData newBd = WorldGenUtils.bakeData(finalCurrent, rotation, mirror);
                    CustomBlock newCb = finalCurrent.block() instanceof CustomBlock c ? c.clone() : null;
                    WorldGenUtils.placeBlock(null, finalTarget, finalCurrent, newBd, newCb);
                }).region(finalTarget).once();
                return;
            }

            if (level == null && !finalTarget.isChunkLoaded()) {
                finalTarget.getChunk().load(true);
            }
            BlockData newBd = WorldGenUtils.bakeData(current, rotation, mirror);
            CustomBlock newCb = current.block() instanceof CustomBlock c ? c.clone() : null;
            WorldGenUtils.placeBlock(level, finalTarget, current, newBd, newCb);
        }
    }

    /**
     * Transforms and spawns all stored entities tied to the structure.
     *
     * @param level    The generation context. Null if live Bukkit placement.
     * @param origin   The placement origin {@link Location}.
     * @param rotation The spatial rotation.
     * @param mirror   The spatial mirror transform.
     * @param ents     The source list of {@link StructureEntity} definitions.
     * @param deferred Collection to hold entities spawned out of bounds during async generation.
     */
    private void placeEntities(WorldGenAccess level, Location origin, StructureRotation rotation, Mirror mirror, List<StructureEntity> ents, List<StructureEntity> deferred) {
        for (StructureEntity se : ents) {
            Vector transformed = transformEntityPos(se.pos().clone(), mirror, rotation);
            Location target = origin.clone().add(transformed);

            if (target.getBlockY() < origin.getWorld().getMinHeight() || target.getBlockY() >= origin.getWorld().getMaxHeight()) {
                continue;
            }

            if (level instanceof NMSWorldGenAccess nmsLevel && !nmsLevel.isInRegion(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
                if (deferred != null) {
                    deferred.add(se);
                }
                continue;
            }

            if (level != null) {
                se.entity().spawn(level, target);
            } else {
                AbyssalLib.SCHEDULER.schedule(() -> {
                    if (!target.isChunkLoaded()) {
                        target.getChunk().load(true);
                    }
                    se.entity().spawn(target);
                }).region(target).once();
            }
        }
    }

    /**
     * Applies matrix transformations (mirroring and rotation) to a discrete block vector.
     *
     * @param pos      The original relative {@link Vector} position.
     * @param mirror   The applied {@link Mirror}.
     * @param rotation The applied {@link StructureRotation}.
     * @return The newly calculated offset {@link Vector}.
     */
    private Vector transform(Vector pos, Mirror mirror, StructureRotation rotation) {
        int x = pos.getBlockX();
        int y = pos.getBlockY();
        int z = pos.getBlockZ();

        int sizeX = size.getBlockX();
        int sizeZ = size.getBlockZ();

        switch (mirror) {
            case LEFT_RIGHT -> z = sizeZ - 1 - z;
            case FRONT_BACK -> x = sizeX - 1 - x;
        }

        int newX = x;
        int newZ = z;

        switch (rotation) {
            case CLOCKWISE_90 -> {
                newX = sizeZ - 1 - z;
                newZ = x;
            }
            case CLOCKWISE_180 -> {
                newX = sizeX - 1 - x;
                newZ = sizeZ - 1 - z;
            }
            case COUNTERCLOCKWISE_90 -> {
                newX = z;
                newZ = sizeX - 1 - x;
            }
        }

        return new Vector(newX, y, newZ);
    }

    /**
     * Applies floating-point matrix transformations to an entity's relative position.
     *
     * @param pos      The original relative {@link Vector} position.
     * @param mirror   The applied {@link Mirror}.
     * @param rotation The applied {@link StructureRotation}.
     * @return The newly calculated offset {@link Vector}.
     */
    private Vector transformEntityPos(Vector pos, Mirror mirror, StructureRotation rotation) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double sizeX = size.getBlockX();
        double sizeZ = size.getBlockZ();

        switch (mirror) {
            case LEFT_RIGHT -> z = sizeZ - z;
            case FRONT_BACK -> x = sizeX - x;
        }

        double newX = x;
        double newZ = z;

        switch (rotation) {
            case CLOCKWISE_90 -> {
                newX = sizeZ - z;
                newZ = x;
            }
            case CLOCKWISE_180 -> {
                newX = sizeX - x;
                newZ = sizeZ - z;
            }
            case COUNTERCLOCKWISE_90 -> {
                newX = z;
                newZ = sizeX - x;
            }
        }

        return new Vector(newX, y, newZ);
    }


    /**
     * Internal class responsible for throttling asynchronous structure placement safely across threads.
     */
    private class Paster {

        private final Location origin;
        private final StructureRotation rotation;
        private final Mirror mirror;
        private final float integrity;
        private final int limit;
        private final CompletableFuture<Void> future;
        private final Iterator<StructureBlock> iterator;
        private final Random random = new Random();
        private final BlockData[] bakedData;
        private final CustomBlock[] bakedCustom;
        private ScheduledTask activeTask;

        public Paster(Location origin, StructureRotation rotation, Mirror mirror, float integrity, int limit, CompletableFuture<Void> future) {
            this.origin = origin;
            this.rotation = rotation;
            this.mirror = mirror;
            this.integrity = integrity;
            this.limit = limit;
            this.future = future;
            this.iterator = blocks.iterator();
            this.bakedData = new BlockData[palette.size()];
            this.bakedCustom = new CustomBlock[palette.size()];
            bakePalette(bakedData, bakedCustom, rotation, mirror);
        }

        public void start(Plugin plugin) {
            activeTask = AbyssalLib.SCHEDULER.schedule(this::tick).region(origin).repeatEvery(1L, Clock.TICKS);
        }

        private void tick() {
            int count = 0;

            while (iterator.hasNext() && count < limit) {
                StructureBlock sb = iterator.next();
                if (integrity < 1.0f && random.nextFloat() > integrity) {
                    continue;
                }
                processStructureBlock(null, origin, sb, rotation, mirror, bakedData, bakedCustom, null);
                count++;
            }

            if (!iterator.hasNext()) {
                placeEntities(null, origin, rotation, mirror, entities, null);
                future.complete(null);
                if (activeTask != null) {
                    activeTask.cancel();
                }
            }
        }
    }
}
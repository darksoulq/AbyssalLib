package com.github.darksoulq.abyssallib.world.structure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.nms.NMSWorldGenAccess;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import com.github.darksoulq.abyssallib.world.structure.processor.impl.IntegrityProcessor;
import com.github.darksoulq.abyssallib.world.structure.serializer.AbyssalLibBlockSerializer;
import com.github.darksoulq.abyssallib.world.structure.serializer.MinecraftBlockSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a saved structure that can be captured from or placed into the world.
 * <p>
 * Structures consist of a palette of unique block states and a list of block positions
 * referencing that palette. This implementation handles both standard Minecraft blocks
 * and AbyssalLib's {@link CustomBlock} system, including tile entity data.
 */
public class Structure {
    /** The current data version for serialization compatibility. */
    private static final int DATA_VERSION = 1;
    /** Factory for creating JSON nodes during serialization. */
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    /** The unique collection of block states used in this structure. */
    private final List<PaletteEntry> palette = new ArrayList<>();
    /** The individual blocks making up the structure. */
    private final List<StructureBlock> blocks = new ArrayList<>();
    /** Pipeline of processors applied during the placement phase. */
    private final List<StructureProcessor> processors = new ArrayList<>();
    /** The bounding box dimensions of the structure. */
    private Vector size;

    /**
     * Captures a region of the world into this structure instance.
     * <p>
     * It scans the area defined by two corners, serializing block types, states,
     * and tile data (inventories, signs, etc.) relative to a provided origin point.
     *
     * @param corner1 The first corner of the region.
     * @param corner2 The second corner of the region.
     * @param origin  The location used as the (0,0,0) reference for saved blocks.
     */
    public void fill(@NotNull Location corner1, @NotNull Location corner2, @NotNull Location origin) {
        palette.clear();
        blocks.clear();

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

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = corner1.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.STRUCTURE_VOID) continue;

                    CustomBlock custom = CustomBlock.from(block);
                    String id;
                    Map<JsonNode, JsonNode> serialized;

                    if (custom != null) {
                        id = custom.getId().toString();
                        serialized = AbyssalLibBlockSerializer.serialize(custom, JsonOps.INSTANCE);
                    } else {
                        BlockData data = block.getBlockData();
                        id = "minecraft:" + data.getMaterial().name().toLowerCase();
                        serialized = MinecraftBlockSerializer.serialize(data, JsonOps.INSTANCE);
                    }

                    ObjectNode fullData = (ObjectNode) JsonOps.INSTANCE.createMap(serialized);
                    ObjectNode instanceData = FACTORY.objectNode();

                    if (custom == null) {
                        Map<JsonNode, JsonNode> tileData = MinecraftBlockSerializer.serializeTile(block, JsonOps.INSTANCE);
                        if (tileData != null) {
                            ObjectNode tileNode = (ObjectNode) JsonOps.INSTANCE.createMap(tileData);
                            instanceData.setAll(tileNode);
                        }
                    }

                    if (fullData.has("properties")) instanceData.set("properties", fullData.remove("properties"));
                    if (fullData.has("nbt")) instanceData.set("nbt", fullData.remove("nbt"));

                    PaletteEntry entry = new PaletteEntry(id, fullData);
                    int paletteIndex = paletteLookup.computeIfAbsent(entry, k -> {
                        palette.add(k);
                        return palette.size() - 1;
                    });

                    blocks.add(new StructureBlock(x - originX, y - originY, z - originZ, paletteIndex, instanceData.isEmpty() ? null : instanceData));
                }
            }
        }
    }

    /**
     * Pastes the structure into the world asynchronously using a task timer.
     *
     * @param plugin        The plugin instance to own the task.
     * @param origin        The location to place the structure origin.
     * @param rotation      The rotation to apply.
     * @param mirror        The mirror transformation to apply.
     * @param integrity     Placement probability (0.0 to 1.0).
     * @param blocksPerTick The number of blocks to place per server tick to mitigate lag.
     * @return A future that completes when the structure is fully placed.
     */
    public CompletableFuture<Void> placeAsync(@NotNull Plugin plugin, @NotNull Location origin,
                                              @NotNull StructureRotation rotation, @NotNull Mirror mirror,
                                              float integrity, int blocksPerTick) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Vector center = calculateCenter();
        new Paster(origin, rotation, mirror, integrity, blocksPerTick, future, center).runTaskTimer(plugin, 0L, 1L);
        return future;
    }

    /**
     * Pastes the structure into the world instantly.
     *
     * @param origin    The location for the structure origin.
     * @param rotation  The rotation to apply.
     * @param mirror    The mirror transformation.
     * @param integrity Placement probability.
     */
    public void place(@NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        Random random = new Random();
        Vector center = calculateCenter();
        for (StructureBlock sb : blocks) {
            if (integrity < 1.0f && random.nextFloat() > integrity) continue;
            placeBlock(origin.getWorld(), origin, sb, rotation, mirror, center);
        }
    }

    /**
     * Pastes the structure using a world generation accessor for safe chunk generation.
     *
     * @param level     The generation accessor.
     * @param origin    The placement origin.
     * @param rotation  The rotation to apply.
     * @param mirror    The mirror transformation.
     * @param integrity Placement probability.
     */
    public void place(@NotNull WorldGenAccess level, @NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        Random random = new Random();
        Vector center = calculateCenter();
        for (StructureBlock sb : blocks) {
            if (integrity < 1.0f && random.nextFloat() > integrity) continue;
            placeBlock(level, origin, sb, rotation, mirror, center);
        }
    }

    /**
     * Calculates the horizontal center of the structure for rotation math.
     *
     * @return A vector representing the horizontal pivot point.
     */
    private Vector calculateCenter() {
        if (blocks.isEmpty()) return new Vector(0, 0, 0);
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (StructureBlock b : blocks) {
            if (b.x < minX) minX = b.x;
            if (b.x > maxX) maxX = b.x;
            if (b.z < minZ) minZ = b.z;
            if (b.z > maxZ) maxZ = b.z;
        }
        return new Vector((minX + maxX) / 2.0, 0, (minZ + maxZ) / 2.0);
    }

    /**
     * Registers a processor to the structure's placement pipeline.
     *
     * @param processor The {@link StructureProcessor} to add.
     */
    public void addProcessor(StructureProcessor processor) {
        processors.add(processor);
    }

    /**
     * Internal logic for placing a single structure block into the world.
     *
     * @param worldOrLevel The world or generator accessor.
     * @param origin       The placement origin.
     * @param sb           The structure block data.
     * @param rotation     The applied rotation.
     * @param mirror       The applied mirror.
     * @param center       The rotation center pivot.
     */
    private void placeBlock(Object worldOrLevel, Location origin, StructureBlock sb, StructureRotation rotation, Mirror mirror, Vector center) {
        if (sb.stateIndex < 0 || sb.stateIndex >= palette.size()) return;

        Vector relativePos = new Vector(sb.x, sb.y, sb.z);
        Vector transformedPos = transform(relativePos, mirror, rotation, center);
        Location target = origin.clone().add(transformedPos);
        PaletteEntry entry = palette.get(sb.stateIndex);

        Object blockObject = null;
        if (entry.id.startsWith("minecraft:")) {
            String matName = entry.id.substring("minecraft:".length()).toUpperCase();
            try {
                Material mat = Material.valueOf(matName);
                if (mat.isBlock()) blockObject = mat.createBlockData();
            } catch (IllegalArgumentException ignored) {}
        } else {
            CustomBlock base = Registries.BLOCKS.get(entry.id);
            if (base != null) blockObject = base.clone();
        }

        if (blockObject == null) return;

        ObjectNode nbt = sb.nbt;
        ObjectNode combinedData = entry.stateData;
        BlockInfo original = new BlockInfo(relativePos, blockObject, combinedData, nbt);
        BlockInfo current = new BlockInfo(transformedPos, blockObject, combinedData, nbt);

        for (StructureProcessor processor : processors) {
            if (worldOrLevel instanceof WorldGenAccess level) {
                current = processor.process(level, origin, current, original);
            } else {
                current = processor.process((org.bukkit.World) worldOrLevel, origin, current, original);
            }
            if (current == null) return;
        }

        try {
            blockObject = current.block();
            nbt = current.nbt();
            combinedData = current.combinedData();

            ObjectNode fullData = combinedData != null ? combinedData.deepCopy() : FACTORY.objectNode();
            if (nbt != null) {
                nbt.fields().forEachRemaining(f -> fullData.set(f.getKey(), f.getValue()));
            }
            Map<JsonNode, JsonNode> mapData = JsonOps.INSTANCE.getMap(fullData).orElse(Collections.emptyMap());

            if (blockObject instanceof CustomBlock cb) {
                BlockData bd = cb.getMaterial().createBlockData();
                AbyssalLibBlockSerializer.deserializeBlockData(mapData, JsonOps.INSTANCE, bd);
                if (mirror != Mirror.NONE) bd.mirror(mirror);
                if (rotation != StructureRotation.NONE) bd.rotate(rotation);

                if (worldOrLevel instanceof NMSWorldGenAccess level) {
                    level.setBlock(target.getBlockX(), target.getBlockY(), target.getBlockZ(), cb, bd);
                } else {
                    if (target.getBlock().getType() == Material.WATER && bd instanceof Waterlogged wl) wl.setWaterlogged(true);
                    target.getBlock().setBlockData(bd, false);
                    cb.place(target.getBlock(), false);
                }

                AbyssalLibBlockSerializer.deserializeEntity(cb, mapData, JsonOps.INSTANCE);
                cb.onLoad();

            } else if (blockObject instanceof BlockData bd) {
                MinecraftBlockSerializer.deserialize(bd, mapData, JsonOps.INSTANCE);
                if (mirror != Mirror.NONE) bd.mirror(mirror);
                if (rotation != StructureRotation.NONE) bd.rotate(rotation);

                if (worldOrLevel instanceof NMSWorldGenAccess level) {
                    level.setBlock(target.getBlockX(), target.getBlockY(), target.getBlockZ(), bd);
                } else {
                    if (target.getBlock().getType() == Material.WATER && bd instanceof Waterlogged wl) wl.setWaterlogged(true);
                    target.getBlock().setBlockData(bd, false);

                    if (nbt != null) {
                        JsonOps.INSTANCE.getMap(nbt).ifPresent(nbtMap -> MinecraftBlockSerializer.deserializeTile(target.getBlock(), nbtMap, JsonOps.INSTANCE));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies geometric transformations to a position vector.
     *
     * @param pos      The initial relative position.
     * @param mirror   The mirroring mode.
     * @param rotation The rotation mode.
     * @param center   The pivot center for rotation.
     * @return The transformed position vector.
     */
    private Vector transform(Vector pos, Mirror mirror, StructureRotation rotation, Vector center) {
        double x = pos.getX() - center.getX();
        double z = pos.getZ() - center.getZ();

        switch (mirror) {
            case LEFT_RIGHT -> z = -z;
            case FRONT_BACK -> x = -x;
        }

        double newX = x;
        double newZ = z;

        switch (rotation) {
            case CLOCKWISE_90 -> {
                newX = -z;
                newZ = x;
            }
            case CLOCKWISE_180 -> {
                newX = -x;
                newZ = -z;
            }
            case COUNTERCLOCKWISE_90 -> {
                newX = z;
                newZ = -x;
            }
        }

        return new Vector(Math.round(newX + center.getX()), pos.getY(), Math.round(newZ + center.getZ()));
    }

    /**
     * Serializes the structure into a JSON object.
     *
     * @return The {@link ObjectNode} representing the structure data.
     */
    public ObjectNode serialize() {
        ObjectNode root = FACTORY.objectNode();
        root.put("DataVersion", DATA_VERSION);
        root.putPOJO("size", new int[]{size.getBlockX(), size.getBlockY(), size.getBlockZ()});

        ArrayNode paletteArray = root.putArray("palette");
        for (PaletteEntry entry : palette) {
            ObjectNode node = paletteArray.addObject();
            node.put("Name", entry.id);
            if (!entry.stateData.isEmpty()) node.set("Properties", entry.stateData);
        }

        ArrayNode blockArray = root.putArray("blocks");
        for (StructureBlock sb : blocks) {
            ObjectNode b = blockArray.addObject();
            b.putArray("pos").add(sb.x).add(sb.y).add(sb.z);
            b.put("state", sb.stateIndex);
            if (sb.nbt != null) b.set("nbt", sb.nbt);
        }

        if (!processors.isEmpty()) {
            try {
                JsonNode procNode = StructureProcessor.CODEC.list().encode(JsonOps.INSTANCE, processors);
                root.set("processors", procNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return root;
    }

    /**
     * Deserializes a structure from a JSON object.
     *
     * @param root The structure data.
     * @return A new {@link Structure} instance.
     */
    public static Structure deserialize(JsonNode root) {
        Structure structure = new Structure();

        if (root.has("size")) {
            JsonNode s = root.get("size");
            structure.size = new Vector(s.get(0).asInt(), s.get(1).asInt(), s.get(2).asInt());
        }

        if (root.has("palette")) {
            for (JsonNode node : root.get("palette")) {
                structure.palette.add(new PaletteEntry(
                    node.get("Name").asText(),
                    node.has("Properties") ? (ObjectNode) node.get("Properties") : FACTORY.objectNode()
                ));
            }
        }

        if (root.has("blocks")) {
            for (JsonNode node : root.get("blocks")) {
                JsonNode pos = node.get("pos");
                structure.blocks.add(new StructureBlock(
                    pos.get(0).asInt(), pos.get(1).asInt(), pos.get(2).asInt(),
                    node.get("state").asInt(),
                    node.has("nbt") ? (ObjectNode) node.get("nbt") : null
                ));
            }
        }

        if (root.has("processors")) {
            try {
                List<StructureProcessor> procs = StructureProcessor.CODEC.list().decode(JsonOps.INSTANCE, root.get("processors"));
                for (StructureProcessor p : procs) structure.addProcessor(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return structure;
    }

    /**
     * Task runnable for throttled structure placement over multiple server ticks.
     */
    private class Paster extends BukkitRunnable {
        private final Location origin;
        private final StructureRotation rotation;
        private final Mirror mirror;
        private final int limit;
        private final CompletableFuture<Void> future;
        private final Iterator<StructureBlock> iterator;
        private final Vector center;

        /**
         * Constructs the paster task.
         *
         * @param origin    Placement origin.
         * @param rotation  Placement rotation.
         * @param mirror    Placement mirror.
         * @param integrity Placement probability.
         * @param limit     Blocks to place per tick.
         * @param future    Completable future to notify of completion.
         * @param center    Transformation pivot.
         */
        public Paster(Location origin, StructureRotation rotation, Mirror mirror, float integrity, int limit, CompletableFuture<Void> future, Vector center) {
            this.origin = origin;
            this.rotation = rotation;
            this.mirror = mirror;
            this.limit = limit;
            this.future = future;
            this.center = center;
            this.iterator = blocks.iterator();

            if (integrity < 1.0f) {
                addProcessor(new IntegrityProcessor(integrity));
            }
        }

        /**
         * Runs the placement loop for a single server tick.
         */
        @Override
        public void run() {
            int count = 0;
            while (iterator.hasNext() && count < limit) {
                StructureBlock sb = iterator.next();
                placeBlock(origin.getWorld(), origin, sb, rotation, mirror, center);
                count++;
            }
            if (!iterator.hasNext()) {
                future.complete(null);
                cancel();
            }
        }
    }

    /**
     * Represents a unique block state in the structure palette.
     *
     * @param id        The namespaced block ID.
     * @param stateData The visual property data (rotation, etc.).
     */
    private record PaletteEntry(String id, ObjectNode stateData) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PaletteEntry that = (PaletteEntry) o;
            return Objects.equals(id, that.id) && Objects.equals(stateData, that.stateData);
        }
        @Override public int hashCode() { return Objects.hash(id, stateData); }
    }

    /**
     * Represents an individual block within the structure volume.
     *
     * @param x          X-offset from origin.
     * @param y          Y-offset from origin.
     * @param z          Z-offset from origin.
     * @param stateIndex The index of the block state in the palette.
     * @param nbt        Optional tile entity/property data.
     */
    private record StructureBlock(int x, int y, int z, int stateIndex, @Nullable ObjectNode nbt) {}
}
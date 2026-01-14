package com.github.darksoulq.abyssallib.world.block.internal.structure;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.BlockEntity;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.block.property.Property;
import com.github.darksoulq.abyssallib.world.particle.Generator;
import com.github.darksoulq.abyssallib.world.particle.Particles;
import com.github.darksoulq.abyssallib.world.particle.impl.Renderers;
import com.github.darksoulq.abyssallib.world.structure.Structure;
import com.github.darksoulq.abyssallib.world.structure.internal.StructureLoader;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class StructureBlockEntity extends BlockEntity {

    public final Property<String> structureName = new Property<>(Codecs.STRING, "default:structure");
    public final Property<Integer> offsetX = new Property<>(Codecs.INT, 0);
    public final Property<Integer> offsetY = new Property<>(Codecs.INT, 1);
    public final Property<Integer> offsetZ = new Property<>(Codecs.INT, 0);
    public final Property<Integer> sizeX = new Property<>(Codecs.INT, 5);
    public final Property<Integer> sizeY = new Property<>(Codecs.INT, 5);
    public final Property<Integer> sizeZ = new Property<>(Codecs.INT, 5);
    public final Property<Float> integrity = new Property<>(Codecs.FLOAT, 1.0f);
    public final Property<Boolean> showBoundingBox = new Property<>(Codecs.BOOLEAN, true);

    public Property<StructureMode> mode = new Property<>(Codec.enumCodec(StructureMode.class), StructureMode.LOAD);
    public Property<StructureRotation> rotation = new Property<>(Codec.enumCodec(StructureRotation.class), StructureRotation.NONE);
    public Property<Mirror> mirror = new Property<>(Codec.enumCodec(Mirror.class), Mirror.NONE);

    Particles particles;

    public StructureBlockEntity(CustomBlock block) {
        super(block);
    }

    public void updateParticles() {
        if (particles != null) particles.stop();
        if (mode.get() == StructureMode.LOAD) {
            return;
        }

        if (!showBoundingBox.get()) return;

        Location origin = getBlock().getLocation().clone().add(offsetX.get(), offsetY.get(), offsetZ.get());
        Vector size = new Vector(sizeX.get(), sizeY.get(), sizeZ.get());

        Generator boxGenerator = tick -> {
            List<Vector> points = new ArrayList<>();
            double x = size.getX();
            double y = size.getY();
            double z = size.getZ();
            double step = 0.5;

            for (double i = 0; i <= x; i += step) { points.add(new Vector(i, 0, 0)); points.add(new Vector(i, y, 0)); points.add(new Vector(i, 0, z)); points.add(new Vector(i, y, z)); }
            for (double i = 0; i <= y; i += step) { points.add(new Vector(0, i, 0)); points.add(new Vector(x, i, 0)); points.add(new Vector(0, i, z)); points.add(new Vector(x, i, z)); }
            for (double i = 0; i <= z; i += step) { points.add(new Vector(0, 0, i)); points.add(new Vector(0, y, i)); points.add(new Vector(x, 0, i)); points.add(new Vector(x, y, i)); }
            return points;
        };

        particles = Particles.builder()
            .origin(origin)
            .shape(boxGenerator)
            .render(new Renderers.ImageRenderer(0.5f, Color.WHITE))
            .interval(10)
            .build();

        particles.start();
    }

    private void validateName() {
        String name = structureName.get();
        if (!name.contains(":")) {
            structureName.set("default:" + name);
        }
    }

    public boolean save() {
        if (mode.get() != StructureMode.SAVE) return false;
        validateName();
        Identifier id = Identifier.of(structureName.get());

        Location origin = getBlock().getLocation();
        Location corner1 = origin.clone().add(offsetX.get(), offsetY.get(), offsetZ.get());
        Location corner2 = corner1.clone().add(sizeX.get() - 1, sizeY.get() - 1, sizeZ.get() - 1);

        Structure structure = new Structure();
        structure.fill(corner1, corner2, corner1);

        Registries.STRUCTURES.remove(id.toString());
        Registries.STRUCTURES.register(id.toString(), structure);

        return StructureLoader.save(id, structure);
    }

    public boolean load() {
        if (mode.get() != StructureMode.LOAD) return false;
        validateName();
        Identifier id = Identifier.of(structureName.get());

        try {
            Structure structure = Registries.STRUCTURES.get(id.toString());
            if (structure == null) return false;

            Location target = getBlock().getLocation().clone().add(offsetX.get(), offsetY.get(), offsetZ.get());
            structure.placeAsync(AbyssalLib.getInstance(), target, rotation.get(), mirror.get(), integrity.get(), 200);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
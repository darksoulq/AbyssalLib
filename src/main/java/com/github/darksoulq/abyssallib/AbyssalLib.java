package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.common.energy.EnergyNetwork;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique.*;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.Metrics;
import com.github.darksoulq.abyssallib.server.bridge.BlockBridge;
import com.github.darksoulq.abyssallib.server.bridge.ItemBridge;
import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.data.Datapack;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.internal.*;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.PackServer;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.server.translation.internal.LanguageLoader;
import com.github.darksoulq.abyssallib.server.util.HookConstants;
import com.github.darksoulq.abyssallib.world.block.Blocks;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.data.loot.LootDefaults;
import com.github.darksoulq.abyssallib.world.data.tag.TagLoader;
import com.github.darksoulq.abyssallib.world.data.tag.TagType;
import com.github.darksoulq.abyssallib.world.entity.DamageType;
import com.github.darksoulq.abyssallib.world.gen.feature.Features;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifiers;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.internal.GuiTextures;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.Components;
import com.github.darksoulq.abyssallib.world.item.internal.ItemPredicateLoader;
import com.github.darksoulq.abyssallib.world.item.internal.ItemTicker;
import com.github.darksoulq.abyssallib.world.multiblock.internal.MultiblockManager;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class AbyssalLib extends JavaPlugin {
    public static final String PLUGIN_ID = "abyssallib";
    private static AbyssalLib INSTANCE;
    public static Logger LOGGER;
    public static PluginConfig CONFIG;
    public static PackServer PACK_SERVER;
    public static EventBus EVENT_BUS;
    public static DamageType.Registrar DAMAGE_TYPE_REGISTRAR;
    public static Datapack.Registrar DATAPACK_REGISTRAR;

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOGGER = getLogger();
        HookConstants.load();
        if (!new File(LanguageLoader.LANG_FOLDER.toFile(), "en_us.properties").exists()) saveResource("lang/en_us.properties", false);

        ItemBridge.setup();
        BlockBridge.setup();
        Components.DATA_COMPONENTS_VANILLA.apply();
        Components.DATA_COMPONENTS.apply();
        ItemPredicateLoader.loadPredicates();
        Blocks.BLOCKS.apply();
        Items.ITEMS.apply();
        registerBlockDataAdapters();
        TagLoader.register(TagType.ITEM);
        TagLoader.register(TagType.BLOCK);

        PlacementModifiers.PLACEMENT_MODIFIERS.apply();
        Features.FEATURES.apply();
        LootDefaults.LOOT_FUNCTION_TYPES.apply();
        LootDefaults.LOOT_CONDITION_TYPES.apply();

        CONFIG = new PluginConfig();
        CONFIG.cfg.save();

        FileUtils.createDirectories(new File(getDataFolder(), "recipes"));
        RecipeLoader.loadFolder(new File(AbyssalLib.getInstance().getDataFolder(), "recipes"));

        EVENT_BUS = new EventBus(this);

        EVENT_BUS.register(new ChatInputHandler());
        EVENT_BUS.register(new PlayerEvents());
        EVENT_BUS.register(new EntityEvents());
        EVENT_BUS.register(new BlockEvents());
        EVENT_BUS.register(new MultiblockEvents());
        EVENT_BUS.register(new ItemEvents());
        EVENT_BUS.register(new ServerEvents());
        EVENT_BUS.register(new GuiEvents());

        GuiManager.init(this);
        ItemTicker.start();
        EnergyNetwork.init();

        PACK_SERVER = new PackServer();
        if (CONFIG.rp.enabled.get()) {
            EVENT_BUS.register(new PackEvent());
            PACK_SERVER.start(CONFIG.rp.ip.get(), CONFIG.rp.port.get());
        }

        if (CONFIG.metrics.get()) {
            new Metrics(this, 25772);
        }

        ResourcePack rp = new ResourcePack(this, PLUGIN_ID);
        Namespace ns = rp.namespace("abyssallib");
        ns.icon();

        TextOffset.init(ns);
        GuiTextures.init(ns);
        createItemDef("invisible", ns);
        createItemDef("forward", ns);
        createItemDef("backward", ns);
        createItemDef("close", ns);
        createItemDef("checkmark", ns);

        createItemDef("bounding_toggle", ns);
        createItemDef("name_structure", ns);
        createItemDef("integrity", ns);
        createItemDef("load_structure", ns);
        createItemDef("mirror", ns);
        createItemDef("rotate", ns);
        createItemDef("save", ns);
        createItemDef("size_x", ns);
        createItemDef("size_y", ns);
        createItemDef("size_z", ns);
        createItemDef("x", ns);
        createItemDef("y", ns);
        createItemDef("z", ns);

        rp.register(false);
    }

    @Override
    public void onDisable() {
        BlockManager.save();
        MultiblockManager.save();
        EnergyNetwork.save();
        if (PACK_SERVER.isEnabled()) {
            PACK_SERVER.stop();
        }
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }

    private void createItemDef(String name, Namespace ns) {
        Texture tex = ns.texture("item/" + name);
        Model model = ns.model(name, false);
        model.parent("minecraft:item/generated");
        model.texture("layer0", tex);

        Selector.Model sel = new Selector.Model(model);
        ns.itemDefinition(name, sel, false);
    }

    private void registerBlockDataAdapters() {
        Adapter.register("age", new AgeableAdapter());
        Adapter.register("power", new AnaloguePowerableAdapter());
        Adapter.register("attached", new AttachableAdapter());
        Adapter.register("half", new BisectedAdapter());
        Adapter.register("dusted", new BrushableAdapter());
        Adapter.register("facing", new DirectionalAdapter());
        Adapter.register("face", new FaceAttachableAdapter());
        Adapter.register("hanging", new HangableAdapter());
        Adapter.register("hatch", new HatchableAdapter());
        Adapter.register("level", new LevelledAdapter());
        Adapter.register("lit", new LightableAdapter());
        Adapter.register("faces", new MultipleFacingAdapter());
        Adapter.register("open", new OpenableAdapter());
        Adapter.register("axis", new OrientableAdapter());
        Adapter.register("powered", new PowerableAdapter());
        Adapter.register("rotation", new RotatableAdapter());
        Adapter.register("segment_amount", new SegmentableAdapter());
        Adapter.register("side_chain", new SideChainingAdapter());
        Adapter.register("snowy", new SnowableAdapter());
        Adapter.register("waterlogged", new WaterloggedAdapter());
        // Unique
        Adapter.register("has_bottle", new BrewingStandAdapter());
        Adapter.register("drag", new BubbleColumnAdapter());
        Adapter.register("bites", new CakeAdapter());
        Adapter.register("berries", new CaveVinesPlantAdapter());
        Adapter.register("copper_golem_pose", new CopperGolemStatueAdapter());
        Adapter.register("moisture", new FarmlandAdapter());
        Adapter.register("tip", new HangingMossAdapter());
        Adapter.register("orientation", new CrafterOrientationAdapter(), new JigsawAdapter());
        Adapter.register("has_record", new JukeboxAdapter());
        Adapter.register("charges", new RespawnAnchorAdapter());
        Adapter.register("stage", new SaplingAdapter());
        Adapter.register("bloom", new SculkCatalystAdapter());
        Adapter.register("layers", new SnowAdapter());
        Adapter.register("mode", new StructureBlockAdapter(), new TestBlockAdapter());
        Adapter.register("unstable", new TNTAdapter());
        Adapter.register("trial_spawner_state", new TrialSpawnerStateAdapter());
        Adapter.register("ominous", new TrialSpawnerOminousAdapter());
        Adapter.register("bottom", new MossyCarpetBottomAdapter());
        Adapter.register("height", new MossyCarpetHeightAdapter());
        Adapter.register("crafting", new CrafterStateAdapter());
        Adapter.register("triggered", new CrafterPowerAdapter());
    }
}

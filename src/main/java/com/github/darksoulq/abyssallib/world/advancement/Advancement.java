package com.github.darksoulq.abyssallib.world.advancement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.advancement.criterion.AdvancementCriterion;
import com.github.darksoulq.abyssallib.world.advancement.reward.AdvancementReward;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a custom advancement within the AbyssalLib framework.
 * This class handles the serialization, NMS conversion, and evaluation logic
 * for player-driven progress and achievements.
 */
public class Advancement {

    /**
     * The codec responsible for serializing and deserializing advancements.
     * Supports nested criteria, rewards, and display information.
     */
    @SuppressWarnings("unchecked")
    public static final Codec<Advancement> CODEC = new Codec<>() {
        /**
         * Decodes an Advancement from a serialized format.
         *
         * @param <D> The data format type.
         * @param ops The provider for reading data.
         * @param input The raw serialized data.
         * @return The reconstructed Advancement instance.
         * @throws CodecException If the data is malformed or types are unknown.
         */
        @Override
        public <D> Advancement decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for Advancement"));
            Key id = Codecs.KEY.decode(ops, map.get(ops.createString("id")));
            Builder builder = builder(id);

            D parentData = map.get(ops.createString("parent"));
            if (parentData != null) {
                builder.parent(Codecs.KEY.decode(ops, parentData));
            }

            D displayData = map.get(ops.createString("display"));
            if (displayData != null) {
                builder.display(AdvancementDisplay.CODEC.decode(ops, displayData));
            }

            D criteriaData = map.get(ops.createString("criteria"));
            if (criteriaData != null) {
                Map<D, D> criteriaMap = ops.getMap(criteriaData).orElseThrow();
                for (Map.Entry<D, D> entry : criteriaMap.entrySet()) {
                    String name = Codecs.STRING.decode(ops, entry.getKey());
                    Map<D, D> critNode = ops.getMap(entry.getValue()).orElseThrow();
                    String typeId = Codecs.STRING.decode(ops, critNode.get(ops.createString("type")));
                    Codec<AdvancementCriterion> codec = (Codec<AdvancementCriterion>) Registries.CRITERION.get(typeId).getCodec();
                    builder.criterion(name, codec.decode(ops, entry.getValue()));
                }
            }

            D rewardsData = map.get(ops.createString("rewards"));
            if (rewardsData != null) {
                for (D rewardNode : ops.getList(rewardsData).orElseThrow()) {
                    Map<D, D> rewardMap = ops.getMap(rewardNode).orElseThrow();
                    String typeId = Codecs.STRING.decode(ops, rewardMap.get(ops.createString("type")));
                    Codec<AdvancementReward> codec = (Codec<AdvancementReward>) Registries.REWARDS.get(typeId).getCodec();
                    builder.reward(codec.decode(ops, rewardNode));
                }
            }

            return builder.build();
        }

        /**
         * Encodes an Advancement into a serialized format.
         *
         * @param <D> The data format type.
         * @param ops The provider for creating data.
         * @param value The Advancement instance to serialize.
         * @return The serialized representation.
         * @throws CodecException If inner codecs fail or registries are missing IDs.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, Advancement value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("id"), Codecs.KEY.encode(ops, value.id));
            if (value.parent != null) {
                map.put(ops.createString("parent"), Codecs.KEY.encode(ops, value.parent));
            }
            if (value.display != null) {
                map.put(ops.createString("display"), AdvancementDisplay.CODEC.encode(ops, value.display));
            }

            if (!value.criteria.isEmpty()) {
                Map<D, D> criteriaMap = new HashMap<>();
                for (Map.Entry<String, AdvancementCriterion> entry : value.criteria.entrySet()) {
                    String typeId = Registries.CRITERION.getId(entry.getValue().getType());
                    Codec<AdvancementCriterion> codec = (Codec<AdvancementCriterion>) entry.getValue().getType().getCodec();
                    D encoded = codec.encode(ops, entry.getValue());
                    Map<D, D> encodedMap = new HashMap<>(ops.getMap(encoded).orElseThrow());
                    encodedMap.put(ops.createString("type"), Codecs.STRING.encode(ops, typeId));
                    criteriaMap.put(Codecs.STRING.encode(ops, entry.getKey()), ops.createMap(encodedMap));
                }
                map.put(ops.createString("criteria"), ops.createMap(criteriaMap));
            }

            if (!value.rewards.isEmpty()) {
                List<D> rewardsList = new ArrayList<>();
                for (AdvancementReward reward : value.rewards) {
                    String typeId = Registries.REWARDS.getId(reward.getType());
                    Codec<AdvancementReward> codec = (Codec<AdvancementReward>) reward.getType().getCodec();
                    D encoded = codec.encode(ops, reward);
                    Map<D, D> encodedMap = new HashMap<>(ops.getMap(encoded).orElseThrow());
                    encodedMap.put(ops.createString("type"), Codecs.STRING.encode(ops, typeId));
                    rewardsList.add(ops.createMap(encodedMap));
                }
                map.put(ops.createString("rewards"), ops.createList(rewardsList));
            }

            return ops.createMap(map);
        }
    };

    /** The unique key for this advancement. */
    private final Key id;
    /** The key of the parent advancement, or null if root. */
    private final Key parent;
    /** Visual configuration for the advancement menu. */
    private final AdvancementDisplay display;
    /** Map of logical criteria required to complete this advancement. */
    private final Map<String, AdvancementCriterion> criteria;
    /** List of rewards granted upon completion. */
    private final List<AdvancementReward> rewards;

    /** Cached Bukkit representation. */
    private transient org.bukkit.advancement.Advancement cachedBukkitAdvancement;
    /** Flag to prevent repeated failed cache lookups. */
    private transient boolean cacheAttempted = false;

    /**
     * Constructs a new Advancement.
     *
     * @param id The unique identifier.
     * @param parent The parent identifier (nullable).
     * @param display The display info (nullable).
     * @param criteria The criteria map.
     * @param rewards The rewards list.
     */
    public Advancement(Key id, Key parent, AdvancementDisplay display, Map<String, AdvancementCriterion> criteria, List<AdvancementReward> rewards) {
        this.id = id;
        this.parent = parent;
        this.display = display;
        this.criteria = criteria;
        this.rewards = rewards;
    }

    /**
     * Resolves a Bukkit advancement to its custom counterpart.
     *
     * @param bukkitAdvancement The Bukkit advancement to resolve.
     * @return The custom Advancement, or null if not registered.
     */
    public static Advancement resolve(org.bukkit.advancement.Advancement bukkitAdvancement) {
        if (bukkitAdvancement == null) return null;
        return Registries.ADVANCEMENTS.get(bukkitAdvancement.getKey().asString());
    }

    /**
     * Retrieves the progress of a specific player toward this advancement.
     *
     * @param player The player to check.
     * @return The progress wrapper, or null if the advancement is not loaded.
     */
    public CustomAdvancementProgress getProgress(Player player) {
        if (cachedBukkitAdvancement == null && !cacheAttempted) {
            cacheAttempted = true;
            cachedBukkitAdvancement = Bukkit.getAdvancement(new NamespacedKey(id.namespace(), id.value()));
        }
        if (cachedBukkitAdvancement == null) return null;
        return new CustomAdvancementProgress(this, player, player.getAdvancementProgress(cachedBukkitAdvancement));
    }

    /** @return The unique identifier Key. */
    public Key getId() { return id; }
    /** @return The parent identifier Key. */
    public Key getParent() { return parent; }
    /** @return The display configuration. */
    public AdvancementDisplay getDisplay() { return display; }
    /** @return The map of criteria. */
    public Map<String, AdvancementCriterion> getCriteria() { return criteria; }
    /** @return The list of rewards. */
    public List<AdvancementReward> getRewards() { return rewards; }

    /**
     * Evaluates all criteria for a player without a specific event.
     *
     * @param player The player to evaluate.
     */
    public void evaluate(Player player) {
        evaluate(player, null);
    }

    /**
     * Evaluates criteria for a player, optionally filtered by a triggering event.
     *
     * @param player The player to evaluate.
     * @param event The triggering event, or null for general evaluation.
     */
    public void evaluate(Player player, Event event) {
        CustomAdvancementProgress progress = getProgress(player);
        if (progress == null || progress.isDone()) return;

        for (Map.Entry<String, AdvancementCriterion> entry : criteria.entrySet()) {
            boolean met = event == null ? entry.getValue().isMet(player) : entry.getValue().isMet(player, event);
            if (met) {
                if (!progress.getAwardedCriteria().contains(entry.getKey())) {
                    progress.awardCriterion(entry.getKey());
                }
            }
        }
    }

    /**
     * Converts this instance to a Minecraft NMS Advancement.
     *
     * @return The NMS Advancement instance.
     */
    public net.minecraft.advancements.Advancement toNMS() {
        Optional<Identifier> parentId = parent != null
            ? Optional.of(Identifier.fromNamespaceAndPath(parent.namespace(), parent.value()))
            : Optional.empty();

        Optional<DisplayInfo> displayInfo = Optional.empty();
        if (display != null) {
            AdvancementType advType = switch (display.getFrame()) {
                case GOAL -> AdvancementType.GOAL;
                case CHALLENGE -> AdvancementType.CHALLENGE;
                default -> AdvancementType.TASK;
            };

            Optional<ClientAsset.ResourceTexture> background = display.getBackground() != null
                ? Optional.of(new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath(display.getBackground().namespace(), display.getBackground().value())))
                : Optional.empty();

            DisplayInfo info = new DisplayInfo(
                CraftItemStack.asNMSCopy(display.getIcon()),
                PaperAdventure.asVanilla(display.getTitle()),
                PaperAdventure.asVanilla(display.getDescription()),
                background,
                advType,
                display.isShowToast(),
                display.isAnnounceToChat(),
                display.isHidden()
            );

            if (!Float.isNaN(display.getX()) && !Float.isNaN(display.getY())) {
                info.setLocation(display.getX(), display.getY());
            }
            displayInfo = Optional.of(info);
        }

        Map<String, Criterion<?>> nmsCriteria = new HashMap<>();
        if (criteria.isEmpty()) {
            nmsCriteria.put("impossible", new Criterion<>(CriteriaTriggers.IMPOSSIBLE, new ImpossibleTrigger.TriggerInstance()));
        } else {
            for (String critKey : criteria.keySet()) {
                nmsCriteria.put(critKey, new Criterion<>(CriteriaTriggers.IMPOSSIBLE, new ImpossibleTrigger.TriggerInstance()));
            }
        }

        return new net.minecraft.advancements.Advancement(
            parentId,
            displayInfo,
            AdvancementRewards.EMPTY,
            nmsCriteria,
            AdvancementRequirements.allOf(nmsCriteria.keySet()),
            false
        );
    }

    /**
     * Wraps the NMS advancement into a Holder.
     *
     * @return The AdvancementHolder instance.
     */
    public AdvancementHolder toNMSHolder() {
        Identifier nmsId = Identifier.fromNamespaceAndPath(id.namespace(), id.value());
        return new AdvancementHolder(nmsId, toNMS());
    }

    /**
     * Creates a new builder for an Advancement.
     *
     * @param id The identifier for the advancement.
     * @return A new Builder instance.
     */
    public static Builder builder(Key id) { return new Builder(id); }

    /**
     * Builder class for creating Advancement instances.
     */
    public static class Builder {
        private final Key id;
        private Key parent;
        private AdvancementDisplay display;
        private final Map<String, AdvancementCriterion> criteria = new HashMap<>();
        private final List<AdvancementReward> rewards = new ArrayList<>();

        /** @param id The advancement unique key. */
        public Builder(Key id) { this.id = id; }
        /** @param parent The parent key. @return builder. */
        public Builder parent(Key parent) { this.parent = parent; return this; }
        /** @param display Visual info. @return builder. */
        public Builder display(AdvancementDisplay display) { this.display = display; return this; }
        /** @param name Criterion name. @param criterion Logic. @return builder. */
        public Builder criterion(String name, AdvancementCriterion criterion) { this.criteria.put(name, criterion); return this; }
        /** @param reward Award to grant. @return builder. */
        public Builder reward(AdvancementReward reward) { this.rewards.add(reward); return this; }
        /** @return New Advancement. */
        public Advancement build() { return new Advancement(id, parent, display, criteria, rewards); }
    }
}
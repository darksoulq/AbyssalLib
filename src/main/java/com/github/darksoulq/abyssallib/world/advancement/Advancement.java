package com.github.darksoulq.abyssallib.world.advancement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.advancement.criterion.AdvancementCriterion;
import com.github.darksoulq.abyssallib.world.advancement.reward.AdvancementReward;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.*;
//? if <=26.1.2 {
/*import net.minecraft.advancements.criterion.ImpossibleTrigger;
*///?} else {
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
//?}
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
//? if >1.21.11 {
import net.minecraft.world.item.ItemStackTemplate;
//?}
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.*;

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
    public static final Codec<Advancement> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.KEY.fieldOf("id").forGetter(Advancement.class, Advancement::getId),
        Codecs.KEY.nullable().optionalFieldOf("parent", null).forGetter(Advancement.class, Advancement::getParent),
        AdvancementDisplay.CODEC.nullable().optionalFieldOf("display", null).forGetter(Advancement.class, Advancement::getDisplay),
        Codec.map(Codecs.STRING, AdvancementCriterion.CODEC).optionalFieldOf("criteria", Collections.emptyMap()).forGetter(Advancement.class, Advancement::getCriteria),
        AdvancementReward.CODEC.list().optionalFieldOf("rewards", Collections.emptyList()).forGetter(Advancement.class, Advancement::getRewards)
    ).apply(instance, Advancement::new)).describe("Advancement");

    /**
     * The unique key for this advancement.
     */
    private final Key id;
    /**
     * The key of the parent advancement, or null if root.
     */
    private final Key parent;
    /**
     * Visual configuration for the advancement menu.
     */
    private final AdvancementDisplay display;
    /**
     * Map of logical criteria required to complete this advancement.
     */
    private final Map<String, AdvancementCriterion> criteria;
    /**
     * List of rewards granted upon completion.
     */
    private final List<AdvancementReward> rewards;

    /**
     * Cached Bukkit representation.
     */
    private transient org.bukkit.advancement.Advancement cachedBukkitAdvancement;
    /**
     * Flag to prevent repeated failed cache lookups.
     */
    private transient boolean cacheAttempted = false;

    /**
     * Constructs a new Advancement.
     *
     * @param id       The unique identifier.
     * @param parent   The parent identifier (nullable).
     * @param display  The display info (nullable).
     * @param criteria The criteria map.
     * @param rewards  The rewards list.
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

    /**
     * @return The unique identifier Key.
     */
    public Key getId() {
        return id;
    }

    /**
     * @return The parent identifier Key.
     */
    public Key getParent() {
        return parent;
    }

    /**
     * @return The display configuration.
     */
    public AdvancementDisplay getDisplay() {
        return display;
    }

    /**
     * @return The map of criteria.
     */
    public Map<String, AdvancementCriterion> getCriteria() {
        return criteria;
    }

    /**
     * @return The list of rewards.
     */
    public List<AdvancementReward> getRewards() {
        return rewards;
    }

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
     * @param event  The triggering event, or null for general evaluation.
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
                //? if >=26.1.2 {
                ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(display.getIcon())),
                //?} else {
                /*CraftItemStack.asNMSCopy(display.getIcon()),
                 *///?}
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
    public static Builder builder(Key id) {
        return new Builder(id);
    }

    /**
     * Builder class for creating Advancement instances.
     */
    public static class Builder {
        private final Key id;
        private Key parent;
        private AdvancementDisplay display;
        private final Map<String, AdvancementCriterion> criteria = new HashMap<>();
        private final List<AdvancementReward> rewards = new ArrayList<>();

        /**
         * @param id The advancement unique key.
         */
        public Builder(Key id) {
            this.id = id;
        }

        /**
         * @param parent The parent key.
         * @return builder.
         **/
        public Builder parent(Key parent) {
            this.parent = parent;
            return this;
        }

        /**
         * @param display Visual info.
         * @return builder.
         **/
        public Builder display(AdvancementDisplay display) {
            this.display = display;
            return this;
        }

        /**
         * @param name Criterion name. @param criterion Logic.
         * @return builder.
         **/
        public Builder criterion(String name, AdvancementCriterion criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        /**
         * @param reward Award to grant.
         * @return builder.
         **/
        public Builder reward(AdvancementReward reward) {
            this.rewards.add(reward);
            return this;
        }

        /**
         * @return New Advancement.
         */
        public Advancement build() {
            return new Advancement(id, parent, display, criteria, rewards);
        }
    }
}
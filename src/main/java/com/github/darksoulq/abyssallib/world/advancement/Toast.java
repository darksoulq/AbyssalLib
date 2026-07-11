package com.github.darksoulq.abyssallib.world.advancement;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.Identifier;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A utility for sending transient "Toast" notifications to players.
 * This class leverages the advancement packet system to display pop-up messages
 * in the top-right corner of the screen without adding actual permanent advancements.
 */
public class Toast {

    /**
     * The first line of text displayed in the toast.
     */
    private final Component title;

    /**
     * The second line of text displayed in the toast (nullable).
     */
    private final Component subtitle;

    /**
     * The item stack used as the visual icon for the notification.
     */
    private final ItemStack icon;

    /**
     * The border frame style used for the notification.
     */
    private final AdvancementFrame frame;

    /**
     * Constructs a new Toast notification.
     *
     * @param title    The primary header text.
     * @param subtitle The sub-header text.
     * @param icon     The icon to display.
     * @param frame    The visual {@link AdvancementFrame}.
     */
    public Toast(Component title, Component subtitle, ItemStack icon, AdvancementFrame frame) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.frame = frame;
    }

    /**
     * Sends the toast notification to a specific player.
     * This method generates a temporary "fake" advancement, sends completion packets
     * to trigger the UI toast, and schedules a removal packet to clean up the client state.
     *
     * @param player The {@link Player} who should receive the toast.
     */
    public void send(Player player) {
        Key toastKey = Key.key(AbyssalLib.PLUGIN_ID, "toast_" + UUID.randomUUID().toString().replace("-", ""));

        Component title = this.title;
        if (subtitle != null) {
            title = title.append(Component.newline()).append(subtitle);
        }

        Advancement fakeAdv = Advancement.builder(toastKey)
            .display(AdvancementDisplay.builder()
                .title(title)
                .description(Component.text(" "))
                .icon(icon)
                .frame(frame)
                .showToast(true)
                .announceToChat(false)
                .hidden(true)
                .background(Key.key("minecraft:textures/gui/advancements/backgrounds/stone.png"))
                .build())
            .build();

        AdvancementHolder nmsHolder = fakeAdv.toNMSHolder();
        Identifier nmsId = nmsHolder.id();

        AdvancementProgress progress = new AdvancementProgress();
        progress.update(nmsHolder.value().requirements());
        for (String crit : nmsHolder.value().criteria().keySet()) {
            progress.getCriterion(crit).grant();
        }

        ClientboundUpdateAdvancementsPacket addPacket = new ClientboundUpdateAdvancementsPacket(
            false,
            List.of(nmsHolder),
            Set.of(),
            Map.of(),
            true
        );

        ClientboundUpdateAdvancementsPacket progressPacket = new ClientboundUpdateAdvancementsPacket(
            false,
            List.of(),
            Set.of(),
            Map.of(nmsId, progress),
            true
        );

        ClientboundUpdateAdvancementsPacket removePacket = new ClientboundUpdateAdvancementsPacket(
            false,
            List.of(),
            Set.of(nmsId),
            Map.of(),
            false
        );

        ((CraftPlayer) player).getHandle().connection.send(addPacket);
        ((CraftPlayer) player).getHandle().connection.send(progressPacket);

        AbyssalLib.SCHEDULER.schedule(() -> {
            if (player.isOnline()) {
                ((CraftPlayer) player).getHandle().connection.send(removePacket);
            }
        }).after(100L, Clock.TICKS).once();
    }

    /**
     * Creates a new builder instance for constructing Toasts.
     *
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A fluent builder for creating {@link Toast} notifications.
     */
    public static class Builder {
        private Component title;
        private Component subtitle;
        private ItemStack icon;
        private AdvancementFrame frame = AdvancementFrame.TASK;

        /**
         * Sets the first line of text.
         *
         * @param line1 Primary header.
         * @return This builder.
         */
        public Builder titlle(Component line1) {
            this.title = line1;
            return this;
        }

        /**
         * Sets the second line of text.
         *
         * @param line2 Sub-header.
         * @return This builder.
         */
        public Builder subtitle(Component line2) {
            this.subtitle = line2;
            return this;
        }

        /**
         * Sets the icon for the toast.
         *
         * @param icon The {@link ItemStack} icon.
         * @return This builder.
         */
        public Builder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the frame border style.
         *
         * @param frame Visual frame.
         * @return This builder.
         */
        public Builder frame(AdvancementFrame frame) {
            this.frame = frame;
            return this;
        }

        /**
         * Finalizes the construction of the Toast.
         *
         * @return A new {@link Toast} instance.
         */
        public Toast build() {
            return new Toast(title, subtitle, icon, frame);
        }
    }
}
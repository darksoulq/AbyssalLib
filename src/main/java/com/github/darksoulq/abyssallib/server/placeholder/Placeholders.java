package com.github.darksoulq.abyssallib.server.placeholder;

import com.github.darksoulq.abyssallib.common.serialization.ops.StringOps;
import com.github.darksoulq.abyssallib.server.placeholder.expression.AbstractBooleanPlaceholder;
import com.github.darksoulq.abyssallib.server.placeholder.expression.AbstractDoublePlaceholder;
import com.github.darksoulq.abyssallib.server.placeholder.expression.AbstractStringPlaceholder;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.attribute.AttributeModifier;
import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

public class Placeholders {

    public static final DeferredRegistry<Placeholder<?>> PLACEHOLDERS = DeferredRegistry.create(Registries.PLACEHOLDERS, "abyssallib");

    public static final Placeholder<?> PLAYER_NAME = PLACEHOLDERS.register("player_name", id -> new AbstractStringPlaceholder(id) {
        @Override
        public PlaceholderResult<String> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getName());
        }
    });

    public static final Placeholder<?> PLAYER_UUID = PLACEHOLDERS.register("player_uuid", id -> new AbstractStringPlaceholder(id) {
        @Override
        public PlaceholderResult<String> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getUniqueId().toString());
        }
    });

    public static final Placeholder<?> PLAYER_WORLD = PLACEHOLDERS.register("player_world", id -> new AbstractStringPlaceholder(id) {
        @Override
        public PlaceholderResult<String> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getWorld().getKey().asString());
        }
    });

    public static final Placeholder<?> PLAYER_GAMEMODE = PLACEHOLDERS.register("player_gamemode", id -> new AbstractStringPlaceholder(id) {
        @Override
        public PlaceholderResult<String> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getGameMode().name());
        }
    });

    public static final Placeholder<?> PLAYER_LOCALE = PLACEHOLDERS.register("player_locale", id -> new AbstractStringPlaceholder(id) {
        @Override
        public PlaceholderResult<String> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.locale().toString());
        }
    });

    public static final Placeholder<?> PLAYER_PING = PLACEHOLDERS.register("player_ping", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success((double) player.getPing());
        }
    });

    public static final Placeholder<?> PLAYER_HEALTH = PLACEHOLDERS.register("player_health", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getHealth());
        }
    });

    public static final Placeholder<?> PLAYER_MAX_HEALTH = PLACEHOLDERS.register("player_max_health", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            if (player == null) return PlaceholderResult.empty();
            AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
            return attr != null ? PlaceholderResult.success(attr.getValue()) : PlaceholderResult.empty();
        }
    });

    public static final Placeholder<?> PLAYER_FOOD = PLACEHOLDERS.register("player_food", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success((double) player.getFoodLevel());
        }
    });

    public static final Placeholder<?> PLAYER_SATURATION = PLACEHOLDERS.register("player_saturation", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success((double) player.getSaturation());
        }
    });

    public static final Placeholder<?> PLAYER_LEVEL = PLACEHOLDERS.register("player_level", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success((double) player.getLevel());
        }
    });

    public static final Placeholder<?> PLAYER_X = PLACEHOLDERS.register("player_x", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getLocation().getX());
        }
    });

    public static final Placeholder<?> PLAYER_Y = PLACEHOLDERS.register("player_y", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getLocation().getY());
        }
    });

    public static final Placeholder<?> PLAYER_Z = PLACEHOLDERS.register("player_z", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.getLocation().getZ());
        }
    });

    public static final Placeholder<?> SERVER_TPS = PLACEHOLDERS.register("server_tps", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            return PlaceholderResult.success(Bukkit.getTPS()[0]);
        }
    });

    public static final Placeholder<?> SERVER_ONLINE = PLACEHOLDERS.register("server_online", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            return PlaceholderResult.success((double) Bukkit.getOnlinePlayers().size());
        }
    });

    public static final Placeholder<?> SERVER_MAX_PLAYERS = PLACEHOLDERS.register("server_max_players", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            return PlaceholderResult.success((double) Bukkit.getMaxPlayers());
        }
    });

    public static final Placeholder<?> IS_SNEAKING = PLACEHOLDERS.register("is_sneaking", id -> new AbstractBooleanPlaceholder(id) {
        @Override
        public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.isSneaking());
        }
    });

    public static final Placeholder<?> IS_SPRINTING = PLACEHOLDERS.register("is_sprinting", id -> new AbstractBooleanPlaceholder(id) {
        @Override
        public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.isSprinting());
        }
    });

    public static final Placeholder<?> IS_FLYING = PLACEHOLDERS.register("is_flying", id -> new AbstractBooleanPlaceholder(id) {
        @Override
        public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.isFlying());
        }
    });

    public static final Placeholder<?> IS_OP = PLACEHOLDERS.register("is_op", id -> new AbstractBooleanPlaceholder(id) {
        @Override
        public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            return player == null ? PlaceholderResult.empty() : PlaceholderResult.success(player.isOp());
        }
    });

    public static final Placeholder<?> ATTRIBUTE = PLACEHOLDERS.register("attribute", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            if (player == null || !context.hasArgs()) return PlaceholderResult.empty();

            String attrId = context.getRaw(0, "").toLowerCase(Locale.ROOT);
            String type = context.getRaw(1, "value").toLowerCase(Locale.ROOT);

            NamespacedKey key = attrId.contains(":") ? NamespacedKey.fromString(attrId) : NamespacedKey.minecraft(attrId);
            if (key == null) return PlaceholderResult.empty();

            Attribute attribute = org.bukkit.Registry.ATTRIBUTE.get(key);
            if (attribute == null) return PlaceholderResult.error("Invalid vanilla attribute");

            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) return PlaceholderResult.empty();

            return switch (type) {
                case "base" -> PlaceholderResult.success(instance.getBaseValue());
                case "modifier" -> {
                    String modKey = context.getRaw(2, "");
                    if (modKey.isEmpty()) yield PlaceholderResult.empty();
                    NamespacedKey targetKey = NamespacedKey.fromString(modKey);
                    if (targetKey == null) yield PlaceholderResult.empty();
                    
                    for (org.bukkit.attribute.AttributeModifier mod : instance.getModifiers()) {
                        if (mod.getKey().equals(targetKey)) {
                            yield PlaceholderResult.success(mod.getAmount());
                        }
                    }
                    yield PlaceholderResult.empty();
                }
                default -> PlaceholderResult.success(instance.getValue());
            };
        }
    });

    public static final Placeholder<?> STATISTIC = PLACEHOLDERS.register("statistic", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            if (player == null || !context.hasArgs()) return PlaceholderResult.empty();

            String statId = context.getRaw(0, "").toUpperCase(Locale.ROOT);
            try {
                org.bukkit.Statistic statistic = org.bukkit.Statistic.valueOf(statId);
                return PlaceholderResult.success((double) player.getStatistic(statistic));
            } catch (IllegalArgumentException e) {
                return PlaceholderResult.error("Invalid vanilla statistic");
            }
        }
    });

    public static final Placeholder<?> CUSTOM_ATTRIBUTE = PLACEHOLDERS.register("custom_attribute", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            if (player == null || !context.hasArgs()) return PlaceholderResult.empty();

            String attrId = context.getRaw(0, "");
            String type = context.getRaw(1, "value").toLowerCase(Locale.ROOT);
            EntityAttributes attributes = EntityAttributes.of(player);

            com.github.darksoulq.abyssallib.world.data.attribute.Attribute<Double> dummy =
                new com.github.darksoulq.abyssallib.world.data.attribute.Attribute<>(attrId, Double.class, 0.0);
            
            if (!attributes.has(dummy)) return PlaceholderResult.empty();

            return switch (type) {
                case "base" -> {
                    Double base = attributes.getBaseValue(dummy);
                    yield base != null ? PlaceholderResult.success(base) : PlaceholderResult.empty();
                }
                case "modifier" -> {
                    String modKeyStr = context.getRaw(2, "");
                    if (modKeyStr.isEmpty()) yield PlaceholderResult.empty();
                    Key modKey = Key.key(modKeyStr);
                    
                    Map<Key, AttributeModifier<Double>> modifiers = dummy.getModifiers();
                    AttributeModifier<Double> mod = modifiers.get(modKey);
                    yield mod != null ? PlaceholderResult.success(mod.getValue()) : PlaceholderResult.empty();
                }
                default -> {
                    Double val = attributes.get(dummy);
                    yield val != null ? PlaceholderResult.success(val) : PlaceholderResult.empty();
                }
            };
        }
    });

    public static final Placeholder<?> CUSTOM_STATISTIC = PLACEHOLDERS.register("custom_statistic", id -> new AbstractDoublePlaceholder(id) {
        @Override
        public PlaceholderResult<Double> resolve(PlaceholderContext context) {
            Player player = context.getPlayer();
            if (player == null || !context.hasArgs()) return PlaceholderResult.empty();

            try {
                Statistic stat = Statistic.CODEC.decode(StringOps.INSTANCE, context.getRaw(0, ""));
                return PlaceholderResult.success((double) PlayerStatistics.of(player).get(stat));
            } catch (Exception e) {
                return PlaceholderResult.error("Invalid custom statistic format");
            }
        }
    });
}
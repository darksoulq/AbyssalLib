package com.github.darksoulq.abyssallib.server.util;

/**
 * A utility class containing constant string definitions for plugin permissions.
 * <p>
 * This class organizes permission nodes into logical sub-groups to maintain a
 * hierarchical structure and improve code readability.
 */
public class PermissionConstants {

    /** The root prefix for all AbyssalLib permission nodes. */
    public static final String ROOT = "abyssallib.";

    /**
     * Permission nodes related to item management and distribution.
     */
    public static final class Items {
        /** Permission to use the give command for custom items. */
        public static final String GIVE = ROOT + "items.give";
    }

    /**
     * Permission nodes related to viewing or modifying entity attributes.
     */
    public static final class Attributes {
        /** Permission to retrieve attribute data from entities. */
        public static final String GET = ROOT + "attributes.get";
    }

    /**
     * Permission nodes related to entity spawning and manipulation.
     */
    public static final class Entity {
        /** Permission to summon custom entities into the world. */
        public static final String SUMMON = ROOT + "entity.summon";
    }

    /**
     * Permission nodes related to player and server statistics.
     */
    public static final class Statistics {
        /** Permission to view one's own statistics. */
        public static final String VIEW_SELF = ROOT + "statistics.view.self";
        /** Permission to view statistics of any player on the server. */
        public static final String VIEW_ALL  = ROOT + "statistics.view.all";

        /** Permission to open the personal statistics menu. */
        public static final String MENU_SELF = ROOT + "statistics.menu.self";
        /** Permission to open the statistics menu for any player. */
        public static final String MENU_ALL  = ROOT + "statistics.menu.all";
    }

    /**
     * General administrative and maintenance permission nodes.
     */
    public static final class Other {
        /** Permission to reload the plugin configuration and language files. */
        public static final String RELOAD = ROOT + "reload";
    }

    /**
     * Permission nodes related to content browsing and discovery.
     */
    public static final class Content {
        /** Permission to browse the registry of custom items. */
        public static final String ITEMS_VIEW = ROOT + "content.items.view";
    }
}
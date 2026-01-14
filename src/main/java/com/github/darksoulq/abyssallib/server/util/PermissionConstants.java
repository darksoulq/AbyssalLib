package com.github.darksoulq.abyssallib.server.util;

public class PermissionConstants {
    public static final String ROOT = "abyssallib.";

    public static final class Items {
        public static final String GIVE = ROOT + "items.give";
    }

    public static final class Attributes {
        public static final String GET = ROOT + "attributes.get";
    }

    public static final class Entity {
        public static final String SUMMON = ROOT + "entity.summon";
    }

    public static final class Statistics {
        public static final String VIEW_SELF = ROOT + "statistics.view.self";
        public static final String VIEW_ALL  = ROOT + "statistics.view.all";

        public static final String MENU_SELF = ROOT + "statistics.menu.self";
        public static final String MENU_ALL  = ROOT + "statistics.menu.all";
    }

    public static final class Other {
        public static final String RELOAD = ROOT + "reload";
    }

    public static final class Content {
        public static final String ITEMS_VIEW = ROOT + "content.items.view";
    }
}
package com.github.darksoulq.abyssallib.server.permission.internal;

import com.github.darksoulq.abyssallib.server.permission.PermissionNamespace;
import com.github.darksoulq.abyssallib.server.permission.PermissionNode;
import org.bukkit.permissions.PermissionDefault;

public class PluginPermissions {

    public static final PermissionNamespace NAMESPACE = PermissionNamespace.create("abyssallib");

    public static final PermissionNode ITEMS_GIVE = NAMESPACE.register("items.give", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.OP)
        .description("Allows giving items to players and taking items from the ItemMenu"));
    public static final PermissionNode ATTRIBUTES_GET = NAMESPACE.register("attributes.get", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.OP)
        .description("Allows reading entity attributes"));
    public static final PermissionNode ENTITY_SUMMON = NAMESPACE.register("entity.summon", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.OP)
        .description("Allows summoning entities"));
    public static final PermissionNode LOOT_SET = NAMESPACE.register("loot.set", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));

    public static final PermissionNode STATISTICS_VIEW_SELF = NAMESPACE.register("statistics.view.self", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.TRUE)
        .description("Allows viewing your own statistics in chat"));
    public static final PermissionNode STATISTICS_VIEW_ALL = NAMESPACE.register("statistics.view.all", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.OP)
        .description("Allows viewing other players' statistics in chat"));
    public static final PermissionNode STATISTICS_MENU_SELF = NAMESPACE.register("statistics.menu.self", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.TRUE)
        .description("Allows opening your own statistics menu"));
    public static final PermissionNode STATISTICS_MENU_ALL = NAMESPACE.register("statistics.menu.all", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.OP)
        .description("Allows opening other player's statistics menu"));

    public static final PermissionNode RELOAD = NAMESPACE.register("reload", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.OP)
        .description("Allows reloading commands, lang files, resource packs."));
    public static final PermissionNode CONTENT_ITEMS_VIEW = NAMESPACE.register("content.items.view", n -> new PermissionNode(n)
        .defaultValue(PermissionDefault.OP)
        .description(" Allows opening the content items menu"));

    public static final PermissionNode PERMISSIONS_EDIT = NAMESPACE.register("permissions.edit", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
    public static final PermissionNode PERMISSIONS_WEB = NAMESPACE.register("permissions.web", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
}
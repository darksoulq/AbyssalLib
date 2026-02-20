package com.github.darksoulq.abyssallib.server.permission.internal;

import com.github.darksoulq.abyssallib.server.permission.PermissionNamespace;
import com.github.darksoulq.abyssallib.server.permission.PermissionNode;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import org.bukkit.permissions.PermissionDefault;

public class PluginPermissions {

    public static final PermissionNamespace NAMESPACE = PermissionNamespace.create("abyssallib");

    public static final Holder<PermissionNode> ITEMS_GIVE = NAMESPACE.register("items.give", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
    public static final Holder<PermissionNode> ATTRIBUTES_GET = NAMESPACE.register("attributes.get", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
    public static final Holder<PermissionNode> ENTITY_SUMMON = NAMESPACE.register("entity.summon", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
    
    public static final Holder<PermissionNode> STATISTICS_VIEW_SELF = NAMESPACE.register("statistics.view.self", n -> new PermissionNode(n).defaultValue(PermissionDefault.TRUE));
    public static final Holder<PermissionNode> STATISTICS_VIEW_ALL = NAMESPACE.register("statistics.view.all", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
    public static final Holder<PermissionNode> STATISTICS_MENU_SELF = NAMESPACE.register("statistics.menu.self", n -> new PermissionNode(n).defaultValue(PermissionDefault.TRUE));
    public static final Holder<PermissionNode> STATISTICS_MENU_ALL = NAMESPACE.register("statistics.menu.all", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));

    public static final Holder<PermissionNode> RELOAD = NAMESPACE.register("reload", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
    public static final Holder<PermissionNode> CONTENT_ITEMS_VIEW = NAMESPACE.register("content.items.view", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));

    public static final Holder<PermissionNode> PERMISSIONS_EDIT = NAMESPACE.register("permissions.edit", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
    public static final Holder<PermissionNode> PERMISSIONS_WEB = NAMESPACE.register("permissions.web", n -> new PermissionNode(n).defaultValue(PermissionDefault.OP));
}
package com.github.darksoulq.abyssallib.server.command;

import com.github.darksoulq.abyssallib.server.permission.PermissionNode;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public final class DefaultConditions {
    public static Predicate<CommandSourceStack> playerOnly() {
        return src -> src.getExecutor() instanceof Player;
    }

    public static Predicate<CommandSourceStack> entityOnly() {
        return src -> src.getExecutor() != null;
    }

    public static Predicate<CommandSourceStack> consoleOnly() {
        return src -> src.getSender() instanceof ConsoleCommandSender;
    }

    public static Predicate<CommandSourceStack> nonConsole() {
        return src -> !(src.getSender() instanceof ConsoleCommandSender);
    }

    public static Predicate<CommandSourceStack> executorPresent() {
        return src -> src.getExecutor() != null;
    }

    public static Predicate<CommandSourceStack> hasPerm(String permission) {
        return src -> src.getSender().hasPermission(permission);
    }

    public static Predicate<CommandSourceStack> hasPerm(PermissionNode permission) {
        return src -> src.getSender().hasPermission(permission.getNode());
    }

    public static Predicate<CommandSourceStack> hasAnyPerm(String... permissions) {
        return src -> {
            for (String perm : permissions) if (src.getSender().hasPermission(perm)) return true;
            return false;
        };
    }

    public static Predicate<CommandSourceStack> hasAnyPerm(PermissionNode... permissions) {
        return src -> {
            for (PermissionNode perm : permissions) if (src.getSender().hasPermission(perm.getNode())) return true;
            return false;
        };
    }

    public static Predicate<CommandSourceStack> hasAllPerms(String... permissions) {
        return src -> {
            for (String perm : permissions) if (!src.getSender().hasPermission(perm)) return false;
            return true;
        };
    }

    @SafeVarargs
    public static Predicate<CommandSourceStack> hasAllPerms(Holder<PermissionNode>... permissions) {
        return src -> {
            for (Holder<PermissionNode> perm : permissions)
                if (!src.getSender().hasPermission(perm.get().getNode())) return false;
            return true;
        };
    }

    public static Predicate<CommandSourceStack> and(Predicate<CommandSourceStack> a, Predicate<CommandSourceStack> b) {
        return a.and(b);
    }

    public static Predicate<CommandSourceStack> or(Predicate<CommandSourceStack> a, Predicate<CommandSourceStack> b) {
        return a.or(b);
    }

    public static Predicate<CommandSourceStack> not(Predicate<CommandSourceStack> predicate) {
        return predicate.negate();
    }
}
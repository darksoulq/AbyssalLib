package com.github.darksoulq.abyssallib.server.permission.internal;

import com.github.darksoulq.abyssallib.common.database.relational.mariadb.BatchQuery;
import com.github.darksoulq.abyssallib.common.database.relational.mariadb.Database;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionStorage;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MariadbPermissionStorage implements PermissionStorage {
    private final Database db;

    public MariadbPermissionStorage(Database db) {
        this.db = db;
    }

    @Override
    public void init() {
        db.transaction(exec -> {
            exec.create("permission_groups").ifNotExists().column("id", "VARCHAR(64)").primaryKey("id").column("weight", "INT").execute();
            exec.create("permission_group_parents").ifNotExists().column("group_id", "VARCHAR(64)").column("parent_id", "VARCHAR(64)").execute();
            exec.create("permission_group_nodes").ifNotExists().column("group_id", "VARCHAR(64)").column("node", "VARCHAR(255)").column("val", "BOOLEAN").column("expiry", "BIGINT").execute();
            exec.create("permission_users").ifNotExists().column("uuid", "VARCHAR(36)").primaryKey("uuid").column("name", "VARCHAR(16)").execute();
            exec.create("permission_user_groups").ifNotExists().column("uuid", "VARCHAR(36)").column("group_id", "VARCHAR(64)").execute();
            exec.create("permission_user_nodes").ifNotExists().column("uuid", "VARCHAR(36)").column("node", "VARCHAR(255)").column("val", "BOOLEAN").column("expiry", "BIGINT").execute();
        });
    }

    @Override
    public void loadGroups() {
        db.executor().table("permission_groups").select(rs -> {
            PermissionGroup group = new PermissionGroup(rs.getString("id"));
            group.setWeight(rs.getInt("weight"));
            Registries.PERMISSION_GROUPS.register(group.getId(), group);
            return null;
        });
        db.executor().table("permission_group_parents").select(rs -> {
            PermissionGroup group = Registries.PERMISSION_GROUPS.get(rs.getString("group_id"));
            if (group != null) group.parents.put(rs.getString("parent_id"), new Node(rs.getString("parent_id")));
            return null;
        });
        db.executor().table("permission_group_nodes").select(rs -> {
            PermissionGroup group = Registries.PERMISSION_GROUPS.get(rs.getString("group_id"));
            if (group != null) group.permissions.put(rs.getString("node"), new Node(rs.getString("node"), rs.getBoolean("val"), rs.getLong("expiry")));
            return null;
        });
    }

    @Override
    public PermissionUser loadUser(UUID uuid) {
        PermissionUser user = new PermissionUser(uuid);
        db.executor().table("permission_users").where("uuid = ?", uuid.toString()).select(rs -> {
            user.setName(rs.getString("name"));
            return null;
        });
        db.executor().table("permission_user_groups").where("uuid = ?", uuid.toString()).select(rs -> {
            user.parents.put(rs.getString("group_id"), new Node(rs.getString("group_id")));
            return null;
        });
        db.executor().table("permission_user_nodes").where("uuid = ?", uuid.toString()).select(rs -> {
            user.permissions.put(rs.getString("node"), new Node(rs.getString("node"), rs.getBoolean("val"), rs.getLong("expiry")));
            return null;
        });
        return user;
    }

    @Override
    public UUID getUuidFromName(String name) {
        List<String> uuids = db.executor().table("permission_users").where("name = ?", name).limit(1).select(rs -> rs.getString("uuid"));
        if (!uuids.isEmpty()) return UUID.fromString(uuids.get(0));
        return null;
    }

    @Override
    public Map<UUID, String> getKnownUsers() {
        Map<UUID, String> map = new HashMap<>();
        db.executor().table("permission_users").select(rs -> {
            map.put(UUID.fromString(rs.getString("uuid")), rs.getString("name"));
            return null;
        });
        return map;
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        db.transaction(exec -> {
            exec.table("permission_groups").replace().value("id", group.getId()).value("weight", group.getWeight()).execute();
            exec.table("permission_group_parents").delete().where("group_id = ?", group.getId()).execute();
            BatchQuery parentBatch = exec.table("permission_group_parents").batch("group_id", "parent_id").insert();
            for (Node parent : group.getParentNodes()) parentBatch.add(group.getId(), parent.getKey());
            parentBatch.execute();
            exec.table("permission_group_nodes").delete().where("group_id = ?", group.getId()).execute();
            BatchQuery nodeBatch = exec.table("permission_group_nodes").batch("group_id", "node", "val", "expiry").insert();
            for (Node node : group.getNodes()) nodeBatch.add(group.getId(), node.getKey(), node.getValue(), node.getExpiry());
            nodeBatch.execute();
        });
    }

    @Override
    public void saveUser(PermissionUser user) {
        db.transaction(exec -> {
            exec.table("permission_users").replace().value("uuid", user.getUuid().toString()).value("name", user.getName()).execute();
            exec.table("permission_user_groups").delete().where("uuid = ?", user.getUuid().toString()).execute();
            BatchQuery groupBatch = exec.table("permission_user_groups").batch("uuid", "group_id").insert();
            for (Node parent : user.getParentNodes()) groupBatch.add(user.getUuid().toString(), parent.getKey());
            groupBatch.execute();
            exec.table("permission_user_nodes").delete().where("uuid = ?", user.getUuid().toString()).execute();
            BatchQuery nodeBatch = exec.table("permission_user_nodes").batch("uuid", "node", "val", "expiry").insert();
            for (Node node : user.getNodes()) nodeBatch.add(user.getUuid().toString(), node.getKey(), node.getValue(), node.getExpiry());
            nodeBatch.execute();
        });
    }

    @Override
    public void deleteGroup(String id) {
        db.transaction(exec -> {
            exec.table("permission_groups").delete().where("id = ?", id).execute();
            exec.table("permission_group_parents").delete().where("group_id = ?", id).execute();
            exec.table("permission_group_parents").delete().where("parent_id = ?", id).execute();
            exec.table("permission_group_nodes").delete().where("group_id = ?", id).execute();
            exec.table("permission_user_groups").delete().where("group_id = ?", id).execute();
        });
    }

    @Override
    public void saveGroupAsync(PermissionGroup group) {
        db.getAsyncPool().execute(() -> saveGroup(group));
    }

    @Override
    public void saveUserAsync(PermissionUser user) {
        db.getAsyncPool().execute(() -> saveUser(user));
    }

    @Override
    public void deleteGroupAsync(String id) {
        db.getAsyncPool().execute(() -> deleteGroup(id));
    }

    @Override
    public void shutdown() {
        try {
            db.disconnect();
        } catch (Exception ignored) {}
    }
}
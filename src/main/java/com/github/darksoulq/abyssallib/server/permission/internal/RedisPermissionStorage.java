package com.github.darksoulq.abyssallib.server.permission.internal;

import com.github.darksoulq.abyssallib.common.database.nosql.redis.Database;
import com.github.darksoulq.abyssallib.common.database.nosql.redis.PipelineExecutor;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionStorage;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RedisPermissionStorage implements PermissionStorage {
    private final Database db;
    private final Gson gson = new Gson();

    public RedisPermissionStorage(Database db) {
        this.db = db;
    }

    @Override
    public void init() {
    }

    @Override
    public void loadGroups() {
        Set<String> groupIds = db.executor().smembers("permission_groups_set");
        for (String id : groupIds) {
            String json = db.executor().get("permission_group:" + id);
            if (json != null) {
                PermissionGroup group = new PermissionGroup(id);
                JsonObject obj = gson.fromJson(json, JsonObject.class);
                group.setWeight(obj.has("weight") ? obj.get("weight").getAsInt() : 0);
                
                if (obj.has("parents")) {
                    JsonArray parents = obj.getAsJsonArray("parents");
                    for (int i = 0; i < parents.size(); i++) {
                        JsonObject pObj = parents.get(i).getAsJsonObject();
                        group.parents.put(pObj.get("key").getAsString(), new Node(pObj.get("key").getAsString(), true, pObj.get("expiry").getAsLong()));
                    }
                }
                
                if (obj.has("nodes")) {
                    JsonArray nodes = obj.getAsJsonArray("nodes");
                    for (int i = 0; i < nodes.size(); i++) {
                        JsonObject nObj = nodes.get(i).getAsJsonObject();
                        group.permissions.put(nObj.get("key").getAsString(), new Node(nObj.get("key").getAsString(), nObj.get("val").getAsBoolean(), nObj.get("expiry").getAsLong()));
                    }
                }
                
                Registries.PERMISSION_GROUPS.register(id, group);
            }
        }
    }

    @Override
    public PermissionUser loadUser(UUID uuid) {
        PermissionUser user = new PermissionUser(uuid);
        String json = db.executor().get("permission_user:" + uuid.toString());
        if (json != null) {
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            user.setName(obj.has("name") ? obj.get("name").getAsString() : null);
            
            if (obj.has("parents")) {
                JsonArray parents = obj.getAsJsonArray("parents");
                for (int i = 0; i < parents.size(); i++) {
                    JsonObject pObj = parents.get(i).getAsJsonObject();
                    user.parents.put(pObj.get("key").getAsString(), new Node(pObj.get("key").getAsString(), true, pObj.get("expiry").getAsLong()));
                }
            }
            
            if (obj.has("nodes")) {
                JsonArray nodes = obj.getAsJsonArray("nodes");
                for (int i = 0; i < nodes.size(); i++) {
                    JsonObject nObj = nodes.get(i).getAsJsonObject();
                    user.permissions.put(nObj.get("key").getAsString(), new Node(nObj.get("key").getAsString(), nObj.get("val").getAsBoolean(), nObj.get("expiry").getAsLong()));
                }
            }
        }
        return user;
    }

    @Override
    public UUID getUuidFromName(String name) {
        String uuidStr = db.executor().hget("permission_name_to_uuid", name.toLowerCase());
        if (uuidStr != null) {
            return UUID.fromString(uuidStr);
        }
        return null;
    }

    @Override
    public Map<UUID, String> getKnownUsers() {
        Map<UUID, String> map = new HashMap<>();
        Map<String, String> nameToUuid = db.executor().hgetAll("permission_name_to_uuid");
        for (Map.Entry<String, String> entry : nameToUuid.entrySet()) {
            map.put(UUID.fromString(entry.getValue()), entry.getKey());
        }
        return map;
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        JsonObject obj = new JsonObject();
        obj.addProperty("weight", group.getWeight());
        
        JsonArray parents = new JsonArray();
        for (Node p : group.getParentNodes()) {
            JsonObject pObj = new JsonObject();
            pObj.addProperty("key", p.getKey());
            pObj.addProperty("expiry", p.getExpiry());
            parents.add(pObj);
        }
        obj.add("parents", parents);
        
        JsonArray nodes = new JsonArray();
        for (Node n : group.getNodes()) {
            JsonObject nObj = new JsonObject();
            nObj.addProperty("key", n.getKey());
            nObj.addProperty("val", n.getValue());
            nObj.addProperty("expiry", n.getExpiry());
            nodes.add(nObj);
        }
        obj.add("nodes", nodes);

        PipelineExecutor pipeline = db.executor().pipeline();
        pipeline.sadd("permission_groups_set", group.getId());
        pipeline.set("permission_group:" + group.getId(), gson.toJson(obj));
        pipeline.execute();
    }

    @Override
    public void saveUser(PermissionUser user) {
        JsonObject obj = new JsonObject();
        if (user.getName() != null) {
            obj.addProperty("name", user.getName());
        }
        
        JsonArray parents = new JsonArray();
        for (Node p : user.getParentNodes()) {
            JsonObject pObj = new JsonObject();
            pObj.addProperty("key", p.getKey());
            pObj.addProperty("expiry", p.getExpiry());
            parents.add(pObj);
        }
        obj.add("parents", parents);
        
        JsonArray nodes = new JsonArray();
        for (Node n : user.getNodes()) {
            JsonObject nObj = new JsonObject();
            nObj.addProperty("key", n.getKey());
            nObj.addProperty("val", n.getValue());
            nObj.addProperty("expiry", n.getExpiry());
            nodes.add(nObj);
        }
        obj.add("nodes", nodes);

        PipelineExecutor pipeline = db.executor().pipeline();
        pipeline.set("permission_user:" + user.getUuid().toString(), gson.toJson(obj));
        if (user.getName() != null) {
            pipeline.hset("permission_name_to_uuid", user.getName().toLowerCase(), user.getUuid().toString());
        }
        pipeline.execute();
    }

    @Override
    public void deleteGroup(String id) {
        PipelineExecutor pipeline = db.executor().pipeline();
        pipeline.srem("permission_groups_set", id);
        pipeline.del("permission_group:" + id);
        pipeline.execute();
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
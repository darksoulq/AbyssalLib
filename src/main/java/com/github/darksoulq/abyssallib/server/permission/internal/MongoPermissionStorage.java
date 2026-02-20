package com.github.darksoulq.abyssallib.server.permission.internal;

import com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionStorage;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MongoPermissionStorage implements PermissionStorage {
    private final Database db;

    public MongoPermissionStorage(Database db) {
        this.db = db;
    }

    @Override
    public void init() {
    }

    @Override
    public void loadGroups() {
        db.executor().collection("permission_groups").select(doc -> {
            PermissionGroup group = new PermissionGroup(doc.getString("_id"));
            group.setWeight(doc.getInteger("weight", 0));
            
            List<Document> parents = doc.getList("parents", Document.class, new ArrayList<>());
            for (Document pDoc : parents) {
                group.parents.put(pDoc.getString("key"), new Node(pDoc.getString("key"), true, pDoc.getLong("expiry")));
            }
            
            List<Document> nodes = doc.getList("nodes", Document.class, new ArrayList<>());
            for (Document nDoc : nodes) {
                group.permissions.put(nDoc.getString("key"), new Node(nDoc.getString("key"), nDoc.getBoolean("val"), nDoc.getLong("expiry")));
            }
            
            Registries.PERMISSION_GROUPS.register(group.getId(), group);
            return null;
        });
    }

    @Override
    public PermissionUser loadUser(UUID uuid) {
        PermissionUser user = new PermissionUser(uuid);
        db.executor().collection("permission_users").filter("_id", uuid.toString()).select(doc -> {
            user.setName(doc.getString("name"));
            
            List<Document> parents = doc.getList("parents", Document.class, new ArrayList<>());
            for (Document pDoc : parents) {
                user.parents.put(pDoc.getString("key"), new Node(pDoc.getString("key"), true, pDoc.getLong("expiry")));
            }
            
            List<Document> nodes = doc.getList("nodes", Document.class, new ArrayList<>());
            for (Document nDoc : nodes) {
                user.permissions.put(nDoc.getString("key"), new Node(nDoc.getString("key"), nDoc.getBoolean("val"), nDoc.getLong("expiry")));
            }
            return null;
        });
        return user;
    }

    @Override
    public UUID getUuidFromName(String name) {
        Document doc = db.executor().collection("permission_users").filter("name", name).first(d -> d);
        if (doc != null) {
            return UUID.fromString(doc.getString("_id"));
        }
        return null;
    }

    @Override
    public Map<UUID, String> getKnownUsers() {
        Map<UUID, String> map = new HashMap<>();
        db.executor().collection("permission_users").select(doc -> {
            map.put(UUID.fromString(doc.getString("_id")), doc.getString("name"));
            return null;
        });
        return map;
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        Document doc = new Document("_id", group.getId())
                .append("weight", group.getWeight());
                
        List<Document> parents = new ArrayList<>();
        for (Node p : group.getParentNodes()) {
            parents.add(new Document("key", p.getKey()).append("expiry", p.getExpiry()));
        }
        doc.append("parents", parents);
        
        List<Document> nodes = new ArrayList<>();
        for (Node n : group.getNodes()) {
            nodes.add(new Document("key", n.getKey()).append("val", n.getValue()).append("expiry", n.getExpiry()));
        }
        doc.append("nodes", nodes);

        db.executor().collection("permission_groups").replace().filter("_id", group.getId()).values(doc).upsert(true).execute();
    }

    @Override
    public void saveUser(PermissionUser user) {
        Document doc = new Document("_id", user.getUuid().toString())
                .append("name", user.getName());
                
        List<Document> parents = new ArrayList<>();
        for (Node p : user.getParentNodes()) {
            parents.add(new Document("key", p.getKey()).append("expiry", p.getExpiry()));
        }
        doc.append("parents", parents);
        
        List<Document> nodes = new ArrayList<>();
        for (Node n : user.getNodes()) {
            nodes.add(new Document("key", n.getKey()).append("val", n.getValue()).append("expiry", n.getExpiry()));
        }
        doc.append("nodes", nodes);

        db.executor().collection("permission_users").replace().filter("_id", user.getUuid().toString()).values(doc).upsert(true).execute();
    }

    @Override
    public void deleteGroup(String id) {
        db.executor().collection("permission_groups").delete().filter("_id", id).execute();
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
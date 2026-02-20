package com.github.darksoulq.abyssallib.server.permission.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionNode;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionWebServer {

    private HttpServer server;
    private boolean enabled = false;
    private final Gson gson = new Gson();

    private final Map<String, Long> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT = 15 * 60 * 1000;

    public void start(String protocol, String host, int port) {
        try {
            if ("https".equalsIgnoreCase(protocol)) {
                HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(host, port), 0);
                try {
                    SSLContext sslContext = SSLContext.getDefault();
                    httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));
                    server = httpsServer;
                } catch (Exception e) {
                    AbyssalLib.getInstance().getLogger().severe("Failed to initialize HTTPS context: " + e.getMessage());
                    return;
                }
            } else {
                server = HttpServer.create(new InetSocketAddress(host, port), 0);
            }

            server.createContext("/", exchange -> {
                try {
                    String path = exchange.getRequestURI().getPath();
                    if (path.equals("/")) path = "/index.html";
                    if (path.startsWith("/api/")) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }

                    File file = new File(AbyssalLib.getInstance().getDataFolder(), "permission" + path);
                    if (!file.exists() || file.isDirectory()) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }

                    String mime = "text/plain";
                    if (path.endsWith(".html")) mime = "text/html; charset=UTF-8";
                    else if (path.endsWith(".css")) mime = "text/css; charset=UTF-8";
                    else if (path.endsWith(".js")) mime = "application/javascript; charset=UTF-8";

                    byte[] bytes = Files.readAllBytes(file.toPath());
                    exchange.getResponseHeaders().add("Content-Type", mime);
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                }
            });

            server.createContext("/api/registry", exchange -> {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                if (!checkSession(exchange)) return;

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    JsonObject root = new JsonObject();

                    JsonArray permsArray = new JsonArray();
                    for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
                        JsonObject po = new JsonObject();
                        po.addProperty("node", perm.getName());
                        po.addProperty("desc", perm.getDescription());
                        permsArray.add(po);
                    }
                    for (PermissionNode pNode : Registries.PERMISSIONS.getAll().values()) {
                        JsonObject po = new JsonObject();
                        po.addProperty("node", pNode.getNode());
                        po.addProperty("desc", pNode.getDescription() != null ? pNode.getDescription() : "");
                        permsArray.add(po);
                    }
                    root.add("permissions", permsArray);

                    JsonArray customNamespaces = new JsonArray();
                    for (String key : Registries.PERMISSIONS.getAll().keySet()) {
                        String ns = key.contains(".") ? key.substring(0, key.indexOf('.')) : key;
                        if (!customNamespaces.contains(new com.google.gson.JsonPrimitive(ns))) {
                            customNamespaces.add(ns);
                        }
                    }
                    root.add("namespaces", customNamespaces);

                    sendJson(exchange, 200, root);
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            server.createContext("/api/data", exchange -> {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                if (!checkSession(exchange)) return;

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    JsonObject root = new JsonObject();

                    JsonArray groupsArray = new JsonArray();
                    for (PermissionGroup group : Registries.PERMISSION_GROUPS.getAll().values()) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("id", group.getId());
                        obj.addProperty("weight", group.getWeight());

                        JsonArray parents = new JsonArray();
                        group.getParentNodes().forEach(n -> {
                            JsonObject pObj = new JsonObject();
                            pObj.addProperty("key", n.getKey());
                            pObj.addProperty("expiry", n.getExpiry());
                            parents.add(pObj);
                        });
                        obj.add("parents", parents);

                        JsonArray nodes = new JsonArray();
                        group.getNodes().forEach(n -> {
                            JsonObject nObj = new JsonObject();
                            nObj.addProperty("key", n.getKey());
                            nObj.addProperty("value", n.getValue());
                            nObj.addProperty("expiry", n.getExpiry());
                            nodes.add(nObj);
                        });
                        obj.add("nodes", nodes);
                        groupsArray.add(obj);
                    }

                    JsonArray usersArray = new JsonArray();
                    Map<UUID, String> knownUsers = AbyssalLib.PERMISSION_MANAGER.getKnownUsers();
                    for (Map.Entry<UUID, String> entry : knownUsers.entrySet()) {
                        UUID uuid = entry.getKey();
                        PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getLoadedUser(uuid);
                        boolean isOnline = Bukkit.getPlayer(uuid) != null;

                        JsonObject obj = new JsonObject();
                        obj.addProperty("uuid", uuid.toString());
                        obj.addProperty("name", entry.getValue() != null ? entry.getValue() : uuid.toString());
                        obj.addProperty("online", isOnline);

                        if (user != null) {
                            JsonArray parents = new JsonArray();
                            user.getParentNodes().forEach(n -> {
                                JsonObject pObj = new JsonObject();
                                pObj.addProperty("key", n.getKey());
                                pObj.addProperty("expiry", n.getExpiry());
                                parents.add(pObj);
                            });
                            obj.add("parents", parents);

                            JsonArray nodes = new JsonArray();
                            user.getNodes().forEach(n -> {
                                JsonObject nObj = new JsonObject();
                                nObj.addProperty("key", n.getKey());
                                nObj.addProperty("value", n.getValue());
                                nObj.addProperty("expiry", n.getExpiry());
                                nodes.add(nObj);
                            });
                            obj.add("nodes", nodes);
                        } else {
                            obj.add("parents", new JsonArray());
                            obj.add("nodes", new JsonArray());
                            obj.addProperty("lazy", true);
                        }
                        usersArray.add(obj);
                    }

                    root.add("groups", groupsArray);
                    root.add("users", usersArray);

                    sendJson(exchange, 200, root);
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            server.createContext("/api/action", exchange -> {
                if (!checkSession(exchange)) return;

                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    JsonObject req = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), JsonObject.class);
                    String action = req.get("action").getAsString();

                    try {
                        switch (action) {
                            case "setGroupWeight" -> {
                                PermissionGroup g = Registries.PERMISSION_GROUPS.get(req.get("id").getAsString());
                                if (g != null) {
                                    g.setWeight(req.get("weight").getAsInt());
                                    g.save();
                                }
                            }
                            case "addGroupParent" -> {
                                PermissionGroup g = Registries.PERMISSION_GROUPS.get(req.get("id").getAsString());
                                if (g != null) {
                                    long exp = req.has("expiry") ? req.get("expiry").getAsLong() : 0L;
                                    g.addParent(new Node(req.get("parent").getAsString(), true, exp));
                                    g.save();
                                }
                            }
                            case "removeGroupParent" -> {
                                PermissionGroup g = Registries.PERMISSION_GROUPS.get(req.get("id").getAsString());
                                if (g != null) {
                                    g.removeParent(req.get("parent").getAsString());
                                    g.save();
                                }
                            }
                            case "setGroupNode" -> {
                                PermissionGroup g = Registries.PERMISSION_GROUPS.get(req.get("id").getAsString());
                                if (g != null) {
                                    long exp = req.has("expiry") ? req.get("expiry").getAsLong() : 0L;
                                    g.setPermission(new Node(req.get("node").getAsString(), req.get("value").getAsBoolean(), exp));
                                    g.save();
                                }
                            }
                            case "removeGroupNode" -> {
                                PermissionGroup g = Registries.PERMISSION_GROUPS.get(req.get("id").getAsString());
                                if (g != null) {
                                    g.unsetPermission(req.get("node").getAsString());
                                    g.save();
                                }
                            }
                            case "addUserParent" -> {
                                PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(UUID.fromString(req.get("uuid").getAsString()));
                                long exp = req.has("expiry") ? req.get("expiry").getAsLong() : 0L;
                                u.addParent(new Node(req.get("parent").getAsString(), true, exp));
                                u.save();
                            }
                            case "removeUserParent" -> {
                                PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(UUID.fromString(req.get("uuid").getAsString()));
                                u.removeParent(req.get("parent").getAsString());
                                u.save();
                            }
                            case "setUserNode" -> {
                                PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(UUID.fromString(req.get("uuid").getAsString()));
                                long exp = req.has("expiry") ? req.get("expiry").getAsLong() : 0L;
                                u.setPermission(new Node(req.get("node").getAsString(), req.get("value").getAsBoolean(), exp));
                                u.save();
                            }
                            case "removeUserNode" -> {
                                PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(UUID.fromString(req.get("uuid").getAsString()));
                                u.unsetPermission(req.get("node").getAsString());
                                u.save();
                            }
                            case "createGroup" -> {
                                String newId = req.get("id").getAsString();
                                if (!Registries.PERMISSION_GROUPS.contains(newId)) {
                                    PermissionGroup ng = new PermissionGroup(newId);
                                    Registries.PERMISSION_GROUPS.register(newId, ng);
                                    ng.save();
                                }
                            }
                            case "deleteGroup" -> {
                                String id = req.get("id").getAsString();
                                if (Registries.PERMISSION_GROUPS.contains(id)) {
                                    AbyssalLib.PERMISSION_MANAGER.deleteGroup(id);
                                }
                            }
                            case "loadUser" -> {
                                AbyssalLib.PERMISSION_MANAGER.getUser(UUID.fromString(req.get("uuid").getAsString()));
                            }
                            case "endSession" -> {
                                String query = exchange.getRequestURI().getQuery();
                                String token = query.split("token=")[1].split("&")[0];
                                sessions.remove(token);
                            }
                        }
                        sendJson(exchange, 200, new JsonObject());
                    } catch (Exception e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            server.setExecutor(null);
            server.start();
            this.enabled = true;
            AbyssalLib.getInstance().getLogger().info("Permission Web Editor started at " + protocol + "://" + host + ":" + port);

            Bukkit.getScheduler().runTaskTimerAsynchronously(AbyssalLib.getInstance(), () -> {
                long now = System.currentTimeMillis();
                sessions.entrySet().removeIf(e -> now > e.getValue());
            }, 1200L, 1200L);

        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().severe("PermissionWebServer failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String createSession() {
        String token = UUID.randomUUID().toString();
        sessions.put(token, System.currentTimeMillis() + SESSION_TIMEOUT);
        return token;
    }

    private boolean checkSession(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("token=")) {
            exchange.sendResponseHeaders(401, -1);
            return false;
        }
        String token = query.split("token=")[1].split("&")[0];
        if (!sessions.containsKey(token)) {
            exchange.sendResponseHeaders(403, -1);
            return false;
        }
        sessions.put(token, System.currentTimeMillis() + SESSION_TIMEOUT);
        return true;
    }

    private void sendJson(HttpExchange exchange, int code, JsonObject element) throws IOException {
        byte[] res = gson.toJson(element).getBytes();
        exchange.sendResponseHeaders(code, res.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(res);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
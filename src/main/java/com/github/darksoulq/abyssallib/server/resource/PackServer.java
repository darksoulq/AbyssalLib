package com.github.darksoulq.abyssallib.server.resource;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipFile;

public class PackServer {
    private String protocol;
    private String host;
    private int port;
    private HttpServer server;
    private final Map<String, Path> registeredPaths = new HashMap<>();
    private boolean enabled = false;

    public void start(String protocol, String host, int port) {
        try {
            this.protocol = protocol;
            this.host = host;
            this.port = port;

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
                String path = exchange.getRequestURI().getPath();

                String[] parts = path.split("/");
                if (parts.length != 3 || !parts[2].equals("resourcepack.zip")) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                String pluginId = parts[1];
                Path file = registeredPaths.get(pluginId);
                if (file == null || !Files.exists(file)) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                exchange.getResponseHeaders().add("Content-Type", "application/zip");
                exchange.sendResponseHeaders(200, Files.size(file));
                try (OutputStream os = exchange.getResponseBody()) {
                    Files.copy(file, os);
                }
            });

            server.setExecutor(null);
            server.start();
            this.enabled = true;
            AbyssalLib.getInstance().getLogger().info("Hosting resource packs at " + protocol + "://" + host + ":" + port);
        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().severe("ResourcePackServer failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadThirdPartyPacks() {
        int loaded = 0;
        ResourcePack.EXTERNAL_CACHE.clear();
        registeredPaths.keySet().removeIf(id -> id.startsWith("external_"));
        ResourcePack.UUID_MAP.keySet().removeIf(id -> id.startsWith("external_"));
        ResourcePack.HASH_MAP.keySet().removeIf(id -> id.startsWith("external_"));

        for (String value : AbyssalLib.CONFIG.rp.externalPacks.get()) {
            String packId = "external_" + loaded;
            Path path = Path.of(value);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                AbyssalLib.LOGGER.warning( String.format("Skipping external resource pack (not found or not a file): %s", value));
                continue;
            }
            if (!value.toLowerCase().endsWith(".zip")) {
                AbyssalLib.LOGGER.warning( String.format("Skipping external resource pack (not a zip): %s", value));
                continue;
            }
            try (ZipFile ignored = new ZipFile(path.toFile())) {
            } catch (Exception e) {
                AbyssalLib.LOGGER.warning(String.format("Skipping external resource pack (invalid zip): %s", value));
                continue;
            }

            String hash = FileUtils.sha1(path);
            registeredPaths.put(packId, path);
            ResourcePack.EXTERNAL_CACHE.add(packId);
            ResourcePack.UUID_MAP.put(packId, UUID.randomUUID());
            ResourcePack.HASH_MAP.put(packId, hash);
            loaded++;
        }
        AbyssalLib.LOGGER.info(String.format("Loaded %d External Resource Packs", loaded));
    }


    public void stop() {
        if (server != null) server.stop(0);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void registerResourcePack(String pluginid, Path resourcePackFile) {
        registeredPaths.put(pluginid, resourcePackFile);
        AbyssalLib.getInstance().getLogger().info("Registered resource pack for /" + pluginid + "/resourcepack.zip");
    }
    public void unregisterResourcePack(String pluginid) {
        registeredPaths.remove(pluginid);
        AbyssalLib.getInstance().getLogger().info("Unregistered resource pack for /" + pluginid + "/resourcepack.zip");
    }

    public String getUrl(String pluginId) {
        return protocol + "://" + host + ":" + port + "/" + pluginId + "/resourcepack.zip";
    }
    public Path getPath(String pluginId) {
        return registeredPaths.get(pluginId);
    }

    public Set<String> registeredPluginIDs() {
        return registeredPaths.keySet();
    }
}
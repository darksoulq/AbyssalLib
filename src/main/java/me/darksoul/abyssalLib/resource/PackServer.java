package me.darksoul.abyssalLib.resource;

import com.sun.net.httpserver.HttpServer;
import me.darksoul.abyssalLib.AbyssalLib;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PackServer {

    private String host;
    private int port;
    private HttpServer server;
    private final Map<String, Path> registeredPaths = new HashMap<>();

    public void start(String host, int port) {
        try {
            this.host = host;
            this.port = port;
            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();

                String[] parts = path.split("/");
                if (parts.length != 3 || !parts[2].equals("resourcepack.zip")) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                String modid = parts[1];
                Path file = registeredPaths.get(modid);
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
            AbyssalLib.getInstance().getLogger().info("Hosting resource packs at http://" + host + ":" + port);
        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().severe("ResourcePackServer failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    public void registerResourcePack(String modid, Path resourcePackFile) {
        registeredPaths.put(modid, resourcePackFile);
        AbyssalLib.getInstance().getLogger().info("Registered resource pack for /" + modid + "/resourcepack.zip");
    }

    public String getUrl(String modid) {
        return "http://" + host + ":" + port + "/" + modid + "/resourcepack.zip";
    }

    public Set<String> registeredModIDs() {
        return registeredPaths.keySet();
    }
}

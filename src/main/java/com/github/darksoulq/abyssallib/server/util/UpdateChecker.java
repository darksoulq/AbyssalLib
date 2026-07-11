package com.github.darksoulq.abyssallib.server.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class UpdateChecker {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Plugin plugin;
    private final String slug;
    private final boolean isModrinth;
    private final Set<String> ignoredModifiers;

    public record UpdateResult(Version version, String link) {
    }

    public UpdateChecker(Plugin plugin, String slug, boolean isModrinth, String... ignoredModifiers) {
        this.plugin = plugin;
        this.slug = slug;
        this.isModrinth = isModrinth;
        this.ignoredModifiers = new HashSet<>();
        for (String modifier : ignoredModifiers) {
            this.ignoredModifiers.add(modifier.toLowerCase());
        }
    }

    public void check(Consumer<UpdateResult> onNewVersion) {
        AbyssalLib.SCHEDULER.schedule(() -> {
            try {
                String minecraftVersion = Bukkit.getMinecraftVersion();
                Version current = new Version(plugin.getPluginMeta().getVersion());

                UpdateResult latest = isModrinth ? fetchModrinth(minecraftVersion) : fetchGitHub();

                if (latest != null && latest.version().compareTo(current) > 0) {
                    onNewVersion.accept(latest);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        }).async().once();
    }

    private boolean isAllowed(String version) {
        String lower = version.toLowerCase();
        for (String modifier : ignoredModifiers) {
            if (lower.contains(modifier)) {
                return false;
            }
        }
        return true;
    }

    private UpdateResult fetchGitHub() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/" + slug + "/releases"))
            .header("User-Agent", plugin.getName() + "/UpdateChecker")
            .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        JsonNode list = MAPPER.readTree(response.body());
        if (!list.isArray() || list.isEmpty()) return null;

        for (JsonNode node : list) {
            String versionNode = node.get("tag_name").asText();
            if (isAllowed(versionNode)) {
                return new UpdateResult(new Version(versionNode), node.get("html_url").asText());
            }
        }
        return null;
    }

    private UpdateResult fetchModrinth(String mcVersion) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String encodedVersions = URLEncoder.encode("[\"" + mcVersion + "\"]", StandardCharsets.UTF_8);
        String url = "https://api.modrinth.com/v2/project/" + slug + "/version?game_versions=" + encodedVersions;

        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
            .header("User-Agent", plugin.getName() + "/UpdateChecker")
            .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            plugin.getLogger().warning("Modrinth API returned status code: " + response.statusCode());
            return null;
        }

        JsonNode list = MAPPER.readTree(response.body());
        if (!list.isArray() || list.isEmpty()) return null;

        for (JsonNode node : list) {
            String versionNode = node.get("version_number").asText();
            if (isAllowed(versionNode)) {
                return new UpdateResult(new Version(versionNode), node.get("files").get(0).get("url").asText());
            }
        }
        return null;
    }
}
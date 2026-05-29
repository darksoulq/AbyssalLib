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
import java.util.function.Consumer;

public class UpdateChecker {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Plugin plugin;
    private final String slug;
    private final boolean isModrinth;

    public record UpdateResult(Version version, String link) {}

    public UpdateChecker(Plugin plugin, String slug, boolean isModrinth) {
        this.plugin = plugin;
        this.slug = slug;
        this.isModrinth = isModrinth;
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

    private UpdateResult fetchGitHub() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/" + slug + "/releases/latest"))
            .header("User-Agent", plugin.getName() + "/UpdateChecker")
            .build();

        JsonNode root = MAPPER.readTree(client.send(req, HttpResponse.BodyHandlers.ofString()).body());
        return new UpdateResult(new Version(root.get("tag_name").asText()), root.get("html_url").asText());
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

        JsonNode latest = list.get(0);
        return new UpdateResult(new Version(latest.get("version_number").asText()), latest.get("files").get(0).get("url").asText());
    }
}
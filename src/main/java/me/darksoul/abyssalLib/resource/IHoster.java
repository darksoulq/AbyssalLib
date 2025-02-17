package me.darksoul.abyssalLib.resource;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class IHoster implements Listener {
    private final File FILE = new File(AbyssalLib.getInstance().getDataFolder(), "pack/generated.zip");
    private static final String FILE_LINK = "http://" + AbyssalLib.getInstance()
            .getConfig().getString("host.ip", "localhost") + ":" + AbyssalLib.getInstance()
            .getConfig().getInt("host.port", 8080) + "/generated.zip";
    private final HttpServer server;

    public IHoster() throws IOException {
        server = HttpServer.create(new InetSocketAddress(AbyssalLib.getInstance()
                .getConfig().getString("host.ip", "localhost"), AbyssalLib.getInstance()
                .getConfig().getInt("host.port", 8080)), 0);
        server.createContext("/", SimpleFileServer.createFileHandler(FILE.toPath().getParent()));
        server.start();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String sha1 = "";
        try {
            sha1 = FileUtils.getSHA1(FILE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.getPlayer().setResourcePack(FILE_LINK, sha1, true);
    }
}

package me.darksoul.abyssalLib.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    /**
     * Retrieves a list of all file paths inside a plugin's resources folder asynchronously.
     *
     * @param plugin   The plugin to search within.
     * @param basePath (Optional) A subdirectory inside resources/ to filter results. Use "" for all files.
     * @return A CompletableFuture containing the list of file paths inside the resources folder.
     */
    public static CompletableFuture<List<String>> getFilePathList(Plugin plugin, String basePath) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> files = new ArrayList<>();
            String resourcePath = basePath.isEmpty() ? "" : (basePath.endsWith("/") ? basePath : basePath + "/");

            try {
                URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
                if (jarUrl == null) return files;

                try (JarFile jarFile = new JarFile(jarUrl.getPath())) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        String entryName = entries.nextElement().getName();
                        if (entryName.startsWith("resources/" + resourcePath) && !entryName.endsWith("/")) {
                            files.add(entryName.substring(10)); // Remove "resources/" part
                        }
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to read plugin resources: " + e.getMessage());
            }

            return files;
        }, runnable -> Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    public static void createDirectories(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Failed to create directory: " + dir.getPath());
        }
    }

    public static void saveFile(InputStream inputStream, File file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFolder(File folder, File zipFile) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            addFileToZip(folder, "", zos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFileToZip(File file, String parentFolder, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                addFileToZip(subFile, parentFolder + file.getName() + "/", zos);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry zipEntry = new ZipEntry(parentFolder + file.getName());
                zos.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }
        }
    }
}

package me.darksoul.abyssalLib.util;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    /**
     * Retrieves a list of all file paths inside a plugin's resources folder synchronously.
     *
     * @param plugin   The plugin to search within.
     * @param basePath (Optional) A subdirectory inside resources/ to filter results. Use "" for all files.
     * @return A list of file paths inside the resources folder.
     */
    public static List<String> getFilePathList(Plugin plugin, String basePath) {
        List<String> files = new ArrayList<>();
        String resourcePath = basePath.isEmpty() ? "" : (basePath.endsWith("/") ? basePath : basePath + "/");

        try {
            URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            if (jarUrl == null) {
                return files;
            }

            File jarFile = new File(jarUrl.toURI());
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String entryName = entries.nextElement().getName();
                    if (entryName.startsWith(resourcePath) && !entryName.endsWith("/")) {
                        files.add(entryName);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read plugin resources: " + e.getMessage());
        }

        return files;
    }

    /**
     * Creates the specified directory and any missing parent directories.
     *
     * @param dir The directory to create.
     */
    public static void createDirectories(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Failed to create directory: " + dir.getPath());
        }
    }

    /**
     * Saves an InputStream to a specified file.
     *
     * @param inputStream The input stream to read from.
     * @param file        The file to save the data into.
     */
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

    /**
     * Compresses a folder into a ZIP archive.
     *
     * @param folder  The folder to compress.
     * @param zipFile The output ZIP file.
     */
    public static void zipFolder(File folder, File zipFile) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            String basePath = folder.getAbsolutePath();
            addFileToZip(folder, basePath, zos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFileToZip(File file, String basePath, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                addFileToZip(subFile, basePath, zos);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                String relativePath = file.getAbsolutePath().substring(basePath.length() + 1);
                zos.putNextEntry(new ZipEntry(relativePath.replace("\\", "/")));

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
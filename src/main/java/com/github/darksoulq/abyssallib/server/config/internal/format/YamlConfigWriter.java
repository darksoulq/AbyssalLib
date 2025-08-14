package com.github.darksoulq.abyssallib.server.config.internal.format;

import java.io.File;
import java.io.IOException;

public final class YamlConfigWriter implements ConfigWriter {
    private final YamlWriter writer = new YamlWriter();

    @Override
    public void writeConfig(Class<?> clazz, File file) throws IOException {
        writer.writeConfig(clazz, file);
    }
}

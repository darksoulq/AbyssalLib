package com.github.darksoulq.abyssallib.server.config.internal.format;

import java.io.File;
import java.io.IOException;

public final class JsonConfigWriter implements ConfigWriter {
    private final JsonWriter writer = new JsonWriter();

    @Override
    public void writeConfig(Class<?> clazz, File file) throws IOException {
        writer.writeConfig(clazz, file);
    }
}

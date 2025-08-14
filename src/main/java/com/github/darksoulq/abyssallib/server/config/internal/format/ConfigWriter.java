package com.github.darksoulq.abyssallib.server.config.internal.format;

import java.io.File;
import java.io.IOException;

public interface ConfigWriter {
    void writeConfig(Class<?> clazz, File file) throws IOException;
}

package com.github.darksoulq.abyssallib.server.config.internal.format;

import java.util.Map;

public interface ConfigFormat {
    Map<String,Object> parse(String text) throws Exception;
    String dump(Map<String,Object> data, Map<String,String[]> comments) throws Exception;
}

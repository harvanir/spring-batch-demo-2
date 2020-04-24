package org.harvanir.batch.springbatch.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Harvan Irsyadi
 */
public class ObjectMapperUtils {

    private ObjectMapperUtils() {
    }

    /**
     * Perform silently.
     */
    public static String writeValueAsString(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Perform silently.
     */
    public static <T> T readValue(ObjectMapper objectMapper, String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
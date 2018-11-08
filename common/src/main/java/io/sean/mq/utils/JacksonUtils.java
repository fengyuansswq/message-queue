package io.sean.mq.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * @author : dadu
 * @since : 2017-12-08
 */
public class JacksonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper mapper() {
        return objectMapper;
    }

    public static final String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception ex) {
            throw new RuntimeException("failed to serialize to json for : " + o, ex);
        }
    }

    public static final <T> T fromJson(String json, Class<T> clazz) {
        if (Strings.isNullOrEmpty(json)) {
            return null;
        } else {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (Exception ex) {
                throw new RuntimeException("failed to serialize [" + json + "] to " + clazz, ex);
            }
        }
    }

    public static final <T> T convert(Object src, Class<T> dest) {
        return objectMapper.convertValue(src, dest);
    }

}

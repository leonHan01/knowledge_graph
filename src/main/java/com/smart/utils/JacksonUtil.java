package com.smart.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.StringWriter;

public class JacksonUtil {


    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object target) {
        try {
            StringWriter sw = new StringWriter();
            JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
            mapper.writeValue(gen, target);
            gen.close();
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

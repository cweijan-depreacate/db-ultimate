package github.cweijan.ultimate.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import github.cweijan.ultimate.json.deserializer.LocalDateDeserializer;
import github.cweijan.ultimate.json.deserializer.LocalDateTimeDeserializer;
import github.cweijan.ultimate.json.deserializer.LocalTimeDeserializer;
import github.cweijan.ultimate.json.serializer.LocalDateSerializer;
import github.cweijan.ultimate.json.serializer.LocalDateTimeSerializer;
import github.cweijan.ultimate.json.serializer.LocalTimeSerializer;
import github.cweijan.ultimate.util.Log;
import github.cweijan.ultimate.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class Json {
    private static final ObjectMapper mapper;
    private static final ObjectMapper withEmptyMapper;

    private Json() {
    }

    static {
        mapper = new ObjectMapper();
        withEmptyMapper = new ObjectMapper();
        SimpleModule dateModule = new SimpleModule();
        dateModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        dateModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        dateModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        dateModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        dateModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        dateModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        mapper.registerModule(dateModule);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        withEmptyMapper.setSerializationInclusion(Include.ALWAYS);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象转换成json
     * @param originalObject 要转换的对象
     */
    public static String toJson(Object originalObject) {

        if(originalObject==null)return null;

        String json=null;
        try {
            json = mapper.writeValueAsString(originalObject);
        } catch (Exception e) {
            Log.error("toJson error:", e);
        }

        return json;
    }


    /**
     * 将对象转换成json,并包含空属性
     * @param originalObject 要转换的对象
     */
    public static String toJsonWithEmpty(Object originalObject) {

        if(originalObject==null)return null;
        String json=null;
        try {
            json = withEmptyMapper.writeValueAsString(originalObject);
        } catch (Exception e) {
            Log.error("toJson error:", e);
        }

        return json;
    }


    /**
     * 根据子key获取子json
     *
     * @param json json字符串
     * @param key  json字符串子key
     * @return 子json
     */
    public static String get(String json, String key) {

        if (StringUtils.isEmpty(json) || StringUtils.isEmpty(key)) return null;

        String var3;
        try {
            var3 = mapper.readValue(json, JsonNode.class).get(key).textValue();
        } catch (IOException var5) {
            Log.info(var5.getMessage());
            var3 = null;
        }

        return var3;
    }

    /**
     * 将json转成List
     *
     * @param json      json字符串
     * @param valueType list泛型
     */
    public static <T> List<T> parseList(String json, Class<T> valueType) {

        if (StringUtils.isEmpty(json) || valueType == null) return null;

        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, valueType);

        List<T> objectList;
        try {
            objectList = mapper.readValue(json, javaType);
        } catch (Exception e) {
            Log.error("parseList error:" + e.getMessage(), e);
            objectList = null;
        }

        return objectList;
    }

    /**
     * 将json转成指定的类对象
     */
    public static <T> T parse(String json, Class<T> type) {

        if (StringUtils.isEmpty(json) || type == null) return null;

        T result;
        try {
            result = mapper.readValue(json, type);
        } catch (Exception e) {
            Log.error("parse error:" + e.getMessage(), e);
            result = null;
        }

        return result;
    }


}

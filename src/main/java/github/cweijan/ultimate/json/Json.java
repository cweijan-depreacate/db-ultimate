package github.cweijan.ultimate.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import github.cweijan.ultimate.json.deserializer.LocalDateTimeDeserializer;
import github.cweijan.ultimate.json.serializer.LocalDateTimeSerializer;
import github.cweijan.ultimate.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Json{

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectMapper withEmptyMapper = new ObjectMapper();

    static{
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        withEmptyMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        simpleModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer());
        mapper.registerModule(simpleModule);
        //       反序列化忽略json未知字段
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static Logger logger = LoggerFactory.getLogger(Json.class);

    /**
     将对象转换成json
     */
    public static String objectToJson(Object object){

        String json = null;
        try{
            json = mapper.writeValueAsString(object);
        } catch(Exception e){
            logger.error("objectToJson error:", e);
        }

        return json;
    }

    public static String get(Object jsonObject, Object... keys){

        String json;
        if(jsonObject instanceof String){
            json = jsonObject.toString();
        } else{
            json = objectToJson(jsonObject);
        }

        try{
            JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
            for(Object key : keys){
                jsonNode = jsonNode.get(key.toString());
            }
            return jsonNode.textValue();
        } catch(IOException e){
            Log.info(e.getMessage());
        }

        return null;
    }

    public static String objectToJsonWithEmpty(Object object){

        String json = null;
        try{
            json = withEmptyMapper.writeValueAsString(object);
        } catch(Exception e){
            logger.error("objectToJson error:", e);
        }

        return json;
    }

    /**
     将json转成List

     @param json      json字符串
     @param valueType list泛型
     */
    public static <T> List<T> jsonToList(String json, Class<T> valueType){

        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, valueType);
        try{
            return mapper.readValue(json, javaType);
        } catch(Exception e){
            logger.error("jsonToList error:" + e.getMessage(), e);
        }

        return null;
    }

    /**
     将json转成指定的类对象
     */
    public static <T> T jsonToObject(String json, Class<T> type){

        T object = null;
        try{
            object = mapper.readValue(json, type);
        } catch(Exception e){
            logger.error("jsonToObject error:" + e.getMessage(), e);
        }
        return object;
    }
}

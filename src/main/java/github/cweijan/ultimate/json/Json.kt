package github.cweijan.ultimate.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import github.cweijan.ultimate.json.deserializer.LocalDateDeserializer
import github.cweijan.ultimate.json.deserializer.LocalDateTimeDeserializer
import github.cweijan.ultimate.json.deserializer.LocalTimeDeserializer
import github.cweijan.ultimate.json.serializer.LocalDateSerializer
import github.cweijan.ultimate.json.serializer.LocalDateTimeSerializer
import github.cweijan.ultimate.json.serializer.LocalTimeSerializer
import github.cweijan.ultimate.util.Log
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object Json {

    private val mapper = ObjectMapper()
    private val withEmptyMapper = ObjectMapper()

    init {
        val dateModule = SimpleModule()
        dateModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
        dateModule.addSerializer(LocalDate::class.java, LocalDateSerializer())
        dateModule.addSerializer(LocalTime::class.java, LocalTimeSerializer())
        dateModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
        dateModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer())
        dateModule.addDeserializer(LocalTime::class.java, LocalTimeDeserializer())
        mapper.registerModule(dateModule)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        withEmptyMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
        // 视空字符传为null
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        //       反序列化忽略json未知字段
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    /**
     * 将对象转换成json
     */
    fun objectToJson(`object`: Any): String? {

        return try {
            mapper.writeValueAsString(`object`)
        } catch (e: Exception) {
            Log.error("objectToJson error:", e)
            null
        }
    }

    operator fun get(jsonObject: Any, key: String): String? {

        val json = if (jsonObject is String) jsonObject.toString() else objectToJson(jsonObject)

        return try {
            mapper.readValue(json, JsonNode::class.java).get(key).textValue()
        } catch (e: IOException) {
            Log.info(e.message); null
        }
    }

    fun objectToJsonWithEmpty(`object`: Any): String? {

        return try {
            withEmptyMapper.writeValueAsString(`object`)
        } catch (e: Exception) {
            Log.error("objectToJson error:", e); null
        }
    }

    /**
     * 将json转成List
     *
     * @param json      json字符串
     * @param valueType list泛型
     */
    fun <T> jsonToList(json: String, valueType: Class<T>): List<T>? {
        val javaType = mapper.typeFactory.constructParametricType(List::class.java, valueType);
        return try {
            mapper.readValue<List<T>>(json, javaType)
        } catch (e: Exception) {
            Log.error("jsonToList error:" +e.message, e); null
        }
    }

    /**
     * 将json转成指定的类对象
     */
    fun <T> jsonToObject(json: String, type: Class<T>): T? {
        return try {
            mapper.readValue(json, type)
        } catch (e: Exception) {
            Log.error("jsonToObject error:" +e.message, e)
            null
        }
    }
}

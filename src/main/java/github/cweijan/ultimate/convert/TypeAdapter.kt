package github.cweijan.ultimate.convert

import github.cweijan.ultimate.util.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

object TypeAdapter {

    private val NUMBER_TYPE = Arrays.asList("chat", "short", "int", "float", "double", "long", String::class.java.name, Integer::class.java.name, Character::class.java.name, Short::class.java.name, Int::class.java.name, Float::class.java.name, Double::class.java.name, Long::class.java.name, Date::class.java.name)
    private val CHARACTER_TYPE = Arrays.asList("java.lang.String", "chat", String::class.java.name, Char::class.java.name)
    private val DATE_TYPE = Arrays.asList("java.time.LocalTime", "java.time.LocalDateTime", "java.time.LocalDate", "java.util.Date")

    fun isAdapterType(typeName: String): Boolean {
        return NUMBER_TYPE.contains(typeName) || CHARACTER_TYPE.contains(typeName) || DATE_TYPE.contains(typeName)
    }

    fun getDefaultValue(fieldType: String): Any {
        return when {
            CHARACTER_TYPE.contains(fieldType) -> "''"
            NUMBER_TYPE.contains(fieldType) -> 0
            else -> "''"
        }
    }

    fun convertFieldValue(fieldValue: Any): Any {
        return convertFieldValue(fieldValue::class.java.name,fieldValue)
    }
    /**
     * 根据相应的类型返回第二个参数的类型
     */
    fun convertFieldValue(fieldType: String, fieldValue: Any): Any {
        return when {
            NUMBER_TYPE.contains(fieldType) -> fieldValue
            CHARACTER_TYPE.contains(fieldType) -> "'$fieldValue'"
            Date::class.java.name == fieldType -> "'${DateUtils.formatDate((fieldValue as Date), "yyyy-MM-dd HH:mm:ss")}'"
            LocalDateTime::class.java.name == fieldType -> "'${(fieldValue as LocalDateTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}'"
            LocalDate::class.java.name == fieldType -> "'${(fieldValue as LocalDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}'"
            LocalTime::class.java.name == fieldType -> "'${(fieldValue as LocalTime).format(DateTimeFormatter.ofPattern("HH:mm:ss"))}'"
            else -> "'$fieldValue'"
        }
    }

}

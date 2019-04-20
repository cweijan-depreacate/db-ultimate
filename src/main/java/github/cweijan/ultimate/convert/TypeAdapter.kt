package github.cweijan.ultimate.convert

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.util.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
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

    fun converToJavaDateValue(componentClass: Class<*>, fieldName:String, timeObject: Any): Any? {
        val columnInfo = TableInfo.getComponent(componentClass).getColumnInfoByFieldName(fieldName)
        val dateFormat = columnInfo.dateFormat
        val dateType = columnInfo.fieldType
        val resultTime = DateUtils.getDateFormat(dateFormat).parse(timeObject.toString())
        return when (dateType) {
            Date::class.java -> resultTime
            LocalDateTime::class.java -> LocalDateTime.ofInstant(resultTime.toInstant(), ZoneId.systemDefault())
            LocalDate::class.java -> LocalDateTime.ofInstant(resultTime.toInstant(), ZoneId.systemDefault()).toLocalDate()
            LocalTime::class.java -> LocalDateTime.ofInstant(resultTime.toInstant(), ZoneId.systemDefault()).toLocalTime()
            else -> null
        }
    }

    /**
     * 对值进行sql兼容处理
     */
    fun convertToSqlValue(componentClass: Class<*>,fieldName:String,fieldValue: Any): Any {
        val dateFormat = TableInfo.getComponent(componentClass).getColumnInfoByFieldName(fieldName).dateFormat
        val fieldType = fieldValue::class.java.name
        return when {
            NUMBER_TYPE.contains(fieldType) -> fieldValue
            CHARACTER_TYPE.contains(fieldType) -> "'$fieldValue'"
            Date::class.java.name == fieldType -> "'${DateUtils.getDateFormat(dateFormat).format((fieldValue as Date))}'"
            LocalDateTime::class.java.name == fieldType -> "'${(fieldValue as LocalDateTime).format(DateUtils.getDateTimeFormatter(dateFormat))}'"
            LocalDate::class.java.name == fieldType -> "'${(fieldValue as LocalDate).format(DateUtils.getDateTimeFormatter(dateFormat))}'"
            LocalTime::class.java.name == fieldType -> "'${(fieldValue as LocalTime).format(DateUtils.getDateTimeFormatter(dateFormat))}'"
            else -> "'$fieldValue'"
        }
    }

    fun isDateType(fieldType: String?): Boolean {
        return DATE_TYPE.contains(fieldType)
    }

}

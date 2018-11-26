package github.cweijan.ultimate.convert

import github.cweijan.ultimate.util.DateUtils
import github.cweijan.ultimate.util.Log
import java.util.*

//todo 这里的类型判断有问题,查看是否有其他方法可以代替
object TypeAdapter {

    private val SIMPLE_TYPE = Arrays.asList("chat", "short", "int", "float", "double", "long", String::class.java.name, Integer::class.java.name, Character::class.java.name, Short::class.java.name, Int::class.java.name, Float::class.java.name, Double::class.java.name, Long::class.java.name, Date::class.java.name)

    private val CHARACTER_TYPE = Arrays.asList("java.lang.String", "chat", String::class.java.name, Char::class.java.name)

    public fun isCharacterType(typeName: String): Boolean {

        return CHARACTER_TYPE.contains(typeName)
    }

    private fun isDateType(typeName: String): Boolean {

        return Date::class.java.name == typeName
    }

    fun isSimpleType(typeName: String): Boolean {

        return SIMPLE_TYPE.contains(typeName)
    }

    fun getDefaultValue(fieldType: String): Any {
        return when {
            isCharacterType(fieldType) -> "''"
            isSimpleType(fieldType) -> 0
            else -> "''"
        }
    }

    /**
     * 根据相应的类型返回第二个参数的类型
     */
    fun convertFieldValue(fieldType: String, fieldValue: Any?): Any {

        return if (isCharacterType(fieldType)) {
            "'${fieldValue?.toString()}'"
        } else if (isDateType(fieldType)) {
            if (fieldValue == null) "" else "'${DateUtils.formatDate((fieldValue as Date))}'"
        } else if (isSimpleType(fieldType)) {
            if (fieldValue == null) 0 else Integer.parseInt(fieldValue.toString())
        } else {
            "'${fieldValue?.toString()}'"
        }
    }

    fun checkNumericType(type: Class<*>): Boolean {

        return false
    }
}
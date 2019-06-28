package github.cweijan.ultimate.convert

import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.util.DateUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.collections.ArrayList

object TypeAdapter {

    private val NUMBER_TYPE = mutableListOf("byte", "short", "int", "float", "double", "long", JavaType.Byte, JavaType.Integer, JavaType.Short, JavaType.Float, JavaType.Double, JavaType.Long)
    private val BOOLEAN_TYPE = mutableListOf(JavaType.Boolean, "boolean")
    private val BLOB_TYPE = mutableListOf(JavaType.byteArray)
    val CHARACTER_TYPE: MutableList<String> = mutableListOf(JavaType.String, "chat", JavaType.Character)
    val DATE_TYPE: MutableList<String> = mutableListOf("java.time.LocalTime", "java.time.LocalDateTime", "java.time.LocalDate", "java.util.Date")

    fun isAdapterType(type: Class<*>): Boolean {
        val typeName = type.name
        return NUMBER_TYPE.contains(typeName) || CHARACTER_TYPE.contains(typeName) || DATE_TYPE.contains(typeName)
                || BOOLEAN_TYPE.contains(typeName) || BLOB_TYPE.contains(typeName) || type.isEnum
    }

    fun getAllField(componentClass: Class<*>?): List<Field> {

        val arrayList = ArrayList<Field>()
        if (componentClass == null) return arrayList
        arrayList.addAll(componentClass.declaredFields.filter { !Modifier.isStatic(it.modifiers) })
        arrayList.addAll(getAllField(componentClass.superclass))

        return arrayList
    }

    fun convertJavaObject(componentClass: Class<*>, field: Field, javaObject: Any?): Any? {
        if (javaObject == null) return null

        val fieldType = field.type
        if (fieldType.isEnum && CHARACTER_TYPE.contains(javaObject::class.java.name)) {
            return EnumConvert.valueOfEnum(fieldType, javaObject.toString())
        }
        if (DATE_TYPE.contains(fieldType.name)) {
            TableInfo.getComponent(componentClass).getColumnInfoByFieldName(field.name)?.run {
                return DateUtils.toDateObject(javaObject.toString(), fieldType, this.dateFormat)
            }
        }

        return javaObject
    }

    /**
     * convertAdapter
     */
    fun convertAdapter(componentClass: Class<*>, fieldName: String, fieldValue: Any?): Any {
        if (fieldValue == null) return ""
        if (fieldValue::class.java.isEnum) {
            return (fieldValue as Enum<*>).name
        }
        val columnInfo = TableInfo.getComponent(componentClass).getColumnInfoByFieldName(fieldName)
        val dateFormat: String = columnInfo?.dateFormat ?: DateUtils.DEFAULT_PATTERN
        return DateUtils.toDateString(fieldValue, dateFormat) ?: fieldValue
    }

    fun contentWrapper(contentObject: Any?): String {
        contentObject?.let {
            if (it !is String) {
                return "'$it'"
            }
            val content = it.toString()
            return if (content.startsWith("'") && content.endsWith("'")) {
                content
            } else {
                "'$content'"
            }
        }
        return ""
    }

}

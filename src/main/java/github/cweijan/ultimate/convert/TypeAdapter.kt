package github.cweijan.ultimate.convert

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.util.DateUtils
import github.cweijan.ultimate.util.Json
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

object TypeAdapter {

    private val NUMBER_TYPE = mutableListOf("java.math.BigInteger", "byte", "short", "int", "float", "double", "long", JavaType.Byte, JavaType.Integer, JavaType.Short, JavaType.Float, JavaType.Double, JavaType.Long)
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

    /**
     * 转换驼峰命名为下划线
     */
    fun convertHumpToUnderLine(hump: String?): String? {
        hump ?: return null
        val regex = Regex("([a-z])([A-Z]+)")
        val replacement = "$1_$2"
        return hump.replace(regex, replacement).toLowerCase()
    }

    fun convertJavaObject(componentClass: Class<*>, field: Field, javaObject: Any?): Any? {

        val fieldType = field.type
        if (javaObject == null) {
            if (fieldType.isPrimitive) {
                return 0
            }
            return null
        }

        field.getAnnotation(Blob::class.java)?.run {
            val listGenericType = field.genericType as ParameterizedType
            val listActualTypeArguments = listGenericType.actualTypeArguments
            val valueType = if (listActualTypeArguments != null && listActualTypeArguments.isNotEmpty()) {
                listActualTypeArguments[0] as Class<*>
            } else {
                Any::class.java
            }
            javaObject as ByteArray
            return if (field.type.isAssignableFrom(List::class.java)) {
                Json.parseList(String(javaObject), valueType)
            } else {
                Json.parse(String(javaObject), field.type)
            }
        }

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

        if (DATE_TYPE.contains(fieldValue::class.java.name)) {
            val dateFormat: String = TableInfo.getComponent(componentClass).getColumnInfoByFieldName(fieldName)?.dateFormat
                    ?: DateUtils.DEFAULT_PATTERN
            return DateUtils.toDateString(fieldValue, dateFormat) ?: fieldValue
        }

        return fieldValue
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

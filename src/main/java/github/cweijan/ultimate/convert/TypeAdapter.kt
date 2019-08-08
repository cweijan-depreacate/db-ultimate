package github.cweijan.ultimate.convert

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.util.DateUtils
import github.cweijan.ultimate.util.Json
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType


object TypeAdapter {

    val NUMBER_TYPE = mutableListOf("byte", "short", "int", "float", "double", "long", JavaType.Byte, JavaType.Integer, JavaType.Short, JavaType.Float, JavaType.Double, JavaType.Long)
    private val BOOLEAN_TYPE = mutableListOf(JavaType.Boolean, "boolean")
    val CHARACTER_TYPE: MutableList<String> = mutableListOf(JavaType.String, "chat", JavaType.Character)
    val DATE_TYPE: MutableList<String> = mutableListOf("java.time.LocalTime", "java.time.LocalDateTime", "java.time.LocalDate", "java.util.Date")
    private val BIG_TYPE: MutableList<String> = mutableListOf("java.math.BigInteger","java.math.BigDecimal")

    @JvmStatic
    fun isAdapterType(type: Class<*>): Boolean {
        val typeName = type.name
        return NUMBER_TYPE.contains(typeName) || CHARACTER_TYPE.contains(typeName) || DATE_TYPE.contains(typeName)
                || BOOLEAN_TYPE.contains(typeName) || JavaType.BYTE_ARRAY_TYPE.contains(typeName) || type.isEnum
                || BIG_TYPE.contains(typeName)
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

    private const val UNDERLINE = "_"
    fun underlineToHump(para: String): String {
        val result = StringBuilder()
        val a = para.split(UNDERLINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (s in a) {
            if (!para.contains(UNDERLINE)) {
                result.append(s)
                continue
            }
            if (result.isEmpty()) {
                result.append(s.toLowerCase())
            } else {
                result.append(s.substring(0, 1).toUpperCase())
                result.append(s.substring(1).toLowerCase())
            }
        }
        return result.toString()
    }

    fun convertJavaObject(componentClass: Class<*>?, field: Field, javaObject: Any?): Any? {

        val fieldType = field.type
        if (javaObject == null) {
            if (fieldType.isPrimitive) {
                return 0
            }
            return null
        }

        if(Collection::class.java.isAssignableFrom(fieldType)){
            val valueType = getGenericType(field)
            return Json.parseCollection(javaObject.toString(),fieldType as Class<Collection<*>>,valueType)
        }

        field.getAnnotation(Blob::class.java)?.run {
            val valueType = getGenericType(field)
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
            TableInfo.getComponent(componentClass, true)?.getColumnInfoByFieldName(field.name)?.run {
                return DateUtils.toDateObject(javaObject.toString(), fieldType, this.dateFormat)
            }
        }

        return javaObject
    }

    private fun getGenericType(field: Field): Class<out Any> {
        return try {
            val listActualTypeArguments = (field.genericType as ParameterizedType).actualTypeArguments
            if (listActualTypeArguments != null && listActualTypeArguments.isNotEmpty()) {
                listActualTypeArguments[0] as Class<*>
            } else {
                Any::class.java
            }
        } catch (e: Exception) {
            Any::class.java
        }
    }

    /**
     * convertAdapter
     */
    fun convertAdapter(componentClass: Class<*>?, fieldName: String?, fieldValue: Any?): Any {

        fieldValue ?: return ""

        if (fieldValue::class.java.isEnum) {
            return (fieldValue as Enum<*>).name
        }

        if(fieldValue is Collection<*>){
            return Json.toJson(fieldValue)
        }

        if (DATE_TYPE.contains(fieldValue::class.java.name)) {
            val columnInfo = TableInfo.getComponent(componentClass, true)?.getColumnInfoByFieldName(fieldName)
            val dateFormat=if(columnInfo?.fieldType==fieldValue::class.java){
                columnInfo.dateFormat
            }else{
                getDefaultFormat(fieldValue::class.java)
            }
            return DateUtils.toDateString(fieldValue, dateFormat) ?: fieldValue
        }

        return fieldValue
    }

    fun getDefaultFormat(type: Class<*>): String {
        return when(type.name){
            "java.time.LocalDate"-> "yyyy-MM-dd"
            "java.time.LocalTime"-> "HH:mm:ss"
            else -> "yyyy-MM-dd HH:mm:ss"
        }
    }

    /**
     * convertAdapter
     */
    @JvmStatic
    fun convertLuceneAdapter(fieldValue: Any?): Any? {
        if (fieldValue == null) return null

        if (fieldValue::class.java.isEnum) {
            return (fieldValue as Enum<*>).name
        }

        if (JavaType.DATE_TYPE.contains(fieldValue::class.java.name)) {
            return DateUtils.convertDateToLong(fieldValue)?.toString()
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

package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.lucene.type.LuceneDocument
import github.cweijan.ultimate.core.lucene.type.LuceneField
import github.cweijan.ultimate.util.DateUtils
import github.cweijan.ultimate.util.Json
import org.apache.lucene.document.*
import org.apache.lucene.search.SortField

/**
 * @author cweijan
 * @version 2019/7/16/016 15:20
 */
object LuceneHelper {

    private val cacheMap = HashMap<Class<*>, List<java.lang.reflect.Field>>()
    private fun getSearchFields(componentClass: Class<*>): List<java.lang.reflect.Field> {

        if (cacheMap.containsKey(componentClass)) return cacheMap[componentClass]!!

        val luceneSearch = componentClass.getAnnotation(LuceneDocument::class.java)
        cacheMap[componentClass] = componentClass.declaredFields.filter { it.getAnnotation(LuceneField::class.java) != null || luceneSearch.value.contains(it.name) }
        return cacheMap[componentClass]!!
    }

    fun objectToDocument(documentObject: Any?): Document? {

        documentObject ?: return null

        val luceneDocument = documentObject.javaClass.getAnnotation(LuceneDocument::class.java)
                ?: throw RuntimeException("索引类必须配置LuceneSearch注解!")

        val document = Document()
        for (field in getSearchFields(documentObject::class.java)) {

            field.isAccessible = true

            val fieldName = getClassKey(documentObject::class.java, field.name)
            val fieldValue = (if (TypeAdapter.isAdapterType(field.type)) TypeAdapter.convertLuceneAdapter(field.get(documentObject)) else Json.toJson(field.get(documentObject)))
                    ?: continue

            val luceneField = field.getAnnotation(LuceneField::class.java)
            if (luceneField == null || luceneField.index) {
                val store = if (luceneField == null || luceneField.store) Field.Store.YES else Field.Store.NO
                val tokenize = luceneField?.tokenize ?: luceneDocument.tokenize
                val noTokenize = if (luceneField != null && !luceneField.tokenize) true else !luceneDocument.tokenize
                when {
                    //TODO 浮点数现在会有问题
                    TypeAdapter.NUMBER_TYPE.contains(field.type.name) || TypeAdapter.LUCENE_DATE_TYPE.contains(field.type.name) -> {
                        document.add(NumericDocValuesField(fieldName, fieldValue.toLong()))
                        document.add(StoredField(fieldName, fieldValue.toLong()))
                    }
                    tokenize -> document.add(TextField(fieldName, fieldValue, store))
                    noTokenize -> document.add(StringField(fieldName, fieldValue, store))
                }
            } else {
                document.add(StoredField(fieldName, fieldValue))
            }

        }

        return document
    }

    fun getClassKey(clazz: Class<*>, key: String): String {
        return clazz.name + "_" + key
    }

    fun getFieldName(clazz: Class<*>, fieldKey: String): String {
        return getClassKey(clazz, fieldKey).replace(clazz.name + "_", "")
    }

    fun <T> documentToObject(document: Document?, objectClass: Class<T>): T? {

        if (document == null) return null

        val data = HashMap<String, Any?>()
        for (field in document.fields) {
            val fieldName = field.name().replace(objectClass.name + "_", "")
            val objectField = objectClass.getDeclaredField(fieldName)
            if (TypeAdapter.LUCENE_DATE_TYPE.contains(objectField.type.name)) {
                data[fieldName] = DateUtils.convertLongToDate(field.numericValue().toLong(), objectField.type)
            } else {
                data[fieldName] = field.stringValue()
            }
        }

        return Json.parse(Json.toJson(data), objectClass)
    }

    fun getSortFieldType(componentClass: Class<*>, fieldName: String): SortField.Type {

        val columnField = componentClass.getDeclaredField(fieldName)
        columnField.isAccessible = true
        return when {
            TypeAdapter.NUMBER_TYPE.contains(columnField.type.name) ||
                    TypeAdapter.LUCENE_DATE_TYPE.contains(columnField.type.name) -> SortField.Type.LONG
            else -> throw java.lang.RuntimeException("该Field:$fieldName 不支持排序！")
        }
    }

}

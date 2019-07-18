package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.lucene.type.LuceneDocument
import github.cweijan.ultimate.core.lucene.type.LuceneField
import github.cweijan.ultimate.util.Json
import org.apache.lucene.document.*

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

        val luceneSearch = documentObject.javaClass.getAnnotation(LuceneDocument::class.java)?: throw RuntimeException("索引类必须配置LuceneSearch注解!")

        val document = Document()
        for (field in getSearchFields(documentObject::class.java)) {

            field.isAccessible = true

            val fieldName = getClassKey(documentObject::class.java, field.name)
            val fieldValue = (if (TypeAdapter.isAdapterType(field.type)) TypeAdapter.convertAdapter(field.get(documentObject)) else Json.toJson(field.get(documentObject))).toString()

            val luceneField = field.getAnnotation(LuceneField::class.java)
            if (luceneField == null) {
                if (luceneSearch.tokenize)
                    document.add(TextField(fieldName, fieldValue, Field.Store.YES))
                else
                    document.add(StringField(fieldName, fieldValue, Field.Store.YES))
                continue
            }
            if (luceneField.index) {
                val store = if (luceneField.store) Field.Store.YES else Field.Store.NO
                when {
                    luceneField.tokenize && TypeAdapter.NUMBER_TYPE.contains(field.type.name) -> document.add(NumericDocValuesField(fieldName, fieldValue.toLong()))
                    luceneField.tokenize -> document.add(TextField(fieldName, fieldValue, store))
                    !luceneField.tokenize -> document.add(StringField(fieldName, fieldValue, store))
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

    fun <T> documentToObject(document: Document?, objectClass: Class<T>): T? {

        if (document == null) return null

        val data = HashMap<String, String>()
        for (field in document.fields) {
            data[field.name()] = field.stringValue()
        }

        return Json.parse(Json.toJson(data), objectClass)
    }

}

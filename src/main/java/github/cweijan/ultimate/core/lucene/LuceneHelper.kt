package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.annotation.Column
import github.cweijan.ultimate.annotation.Exclude
import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.lucene.type.LuceneDocument
import github.cweijan.ultimate.core.lucene.type.LuceneField
import github.cweijan.ultimate.util.DateUtils
import github.cweijan.ultimate.util.Json
import org.apache.lucene.document.*
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.search.SortField

/**
 * @author cweijan
 * @version 2019/7/16/016 15:20
 */
object LuceneHelper {

    fun objectToDocument(documentObject: Any?): Document? {

        documentObject ?: return null

        val luceneDocument = documentObject.javaClass.getAnnotation(LuceneDocument::class.java)
                ?: throw RuntimeException("索引类必须配置LuceneDocument注解!")

        val document = Document()
        for (field in documentObject.javaClass.declaredFields) {

            field.isAccessible = true

            val fieldName = field.name
            if (field.getAnnotation(Column::class.java)?.length ?: 0 > 1000
                    || field.getAnnotation(Exclude::class.java) != null
                    || field.getAnnotation(Blob::class.java) != null
                    || Collection::class.java.isAssignableFrom(field.type)) {
                continue
            }
            val fieldValue = (if (TypeAdapter.isAdapterType(field.type)) TypeAdapter.convertLuceneAdapter(field.get(documentObject)) else Json.toJson(field.get(documentObject)))
                    ?: continue

            val luceneField = field.getAnnotation(LuceneField::class.java)
            if (luceneField == null || luceneField.index) {
                val store = if (luceneField == null || luceneField.store) Field.Store.YES else Field.Store.NO
                val tokenize = luceneField?.tokenize ?: luceneDocument.tokenize
                val noTokenize = if (luceneField != null && !luceneField.tokenize) true else !luceneDocument.tokenize
                val numberFieldType = FieldType()
                numberFieldType.setStored(true)
                numberFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
                when {
                    mutableListOf("float", JavaType.Float).contains(field.type.name) -> {
                        document.add(FloatDocValuesField(fieldName, fieldValue as Float))
                        document.add(Field(fieldName,fieldValue.toString(),numberFieldType))
                    }
                    mutableListOf("double", JavaType.Double).contains(field.type.name) -> {
                        document.add(DoubleDocValuesField(fieldName, fieldValue as Double))
                        document.add(Field(fieldName,fieldValue.toString(),numberFieldType))
                    }
                    TypeAdapter.NUMBER_TYPE.contains(field.type.name) || JavaType.DATE_TYPE.contains(field.type.name) -> {
                        document.add(NumericDocValuesField(fieldName, fieldValue.toString().toLong()))
                        document.add(Field(fieldName,fieldValue.toString(),numberFieldType))
                    }
                    JavaType.BYTE_ARRAY_TYPE.contains(field.type.name) -> {
                        document.add(StoredField(fieldName, fieldValue as ByteArray))
                    }
                    tokenize -> document.add(TextField(fieldName, fieldValue.toString(), store))
                    noTokenize -> document.add(StringField(fieldName, fieldValue.toString(), store))
                }
            } else {
                document.add(StoredField(fieldName, fieldValue.toString()))
            }

        }

        return document
    }

    fun <T> documentToObject(document: Document?, objectClass: Class<T>): T? {

        if (document == null) return null

        val data = HashMap<String, Any?>()
        for (field in document.fields) {
            val fieldName = field.name()
            val objectField = objectClass.getDeclaredField(fieldName)
            when (objectField.type.name) {
                "java.time.LocalDateTime", "java.util.Date" -> {
                    data[fieldName] = DateUtils.convertLongToDate(field.numericValue()?.toLong(), objectField.type)
                }
                JavaType.byteArray, JavaType.ByteArray -> {
                    data[fieldName] = field.binaryValue()?.bytes
                }
                else -> data[fieldName] = field.stringValue()
            }
        }

        return Json.parse(Json.toJson(data), objectClass)
    }

    fun getSortFieldType(componentClass: Class<*>, fieldName: String): SortField.Type {

        val columnField = componentClass.getDeclaredField(fieldName)
        columnField.isAccessible = true
        return when {
            TypeAdapter.NUMBER_TYPE.contains(columnField.type.name) ||
                    JavaType.DATE_TYPE.contains(columnField.type.name) -> SortField.Type.LONG
            else -> throw java.lang.RuntimeException("该Field:$fieldName 不支持排序！")
        }
    }

}

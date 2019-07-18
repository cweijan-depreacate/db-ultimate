package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.annotation.query.OrderBy
import github.cweijan.ultimate.annotation.query.Search
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.lucene.type.LuceneDocument
import github.cweijan.ultimate.core.page.Pagination
import github.cweijan.ultimate.util.StringUtils

/**
 * @author cweijan
 * @version 2019/7/16/016 22:39
 */
class LuceneQuery<T>
private constructor(val componentClass: Class<out T>, private val searchFields: Array<String>) {

    val searhLazy = lazy { LinkedHashMap<String, MutableList<String>>() }
    val searchOperation: MutableMap<String, MutableList<String>>by searhLazy

    val orderByLazy = lazy { return@lazy ArrayList<String>() }
    val orderByList: MutableList<String> by orderByLazy

    var offset: Int? = null
        private set
        get() {
            field?.run { return this }
            if (this.page != null && this.pageSize != 0) {
                return if (page!! <= 0) 0 else (page!! - 1) * (pageSize ?: 100)
            }
            return 0
        }
    var page: Int? = null
        private set
    var pageSize: Int? = null
        private set

    fun page(page: Int?): LuceneQuery<T> {

        this.page = page
        return this
    }

    fun offset(offset: Int?): LuceneQuery<T> {

        this.offset = offset
        return this
    }

    fun pageSize(limit: Int?): LuceneQuery<T> {

        this.pageSize = limit
        return this
    }

    fun limit(limit: Int?): LuceneQuery<T> {

        this.pageSize = limit
        return this
    }

    fun pageList(): Pagination<T> {
        return indexService.search(searchFields, this)
    }

    fun getLuceneSearch(): String {
        return LuceneQueryGenerator.generateOperationSql(this)
    }

    fun all():LuceneQuery<T>{
        searchOperation["*"] = mutableListOf("*")
        return this
    }

    fun search(column: String?, content: Any?): LuceneQuery<T> {

        column?:return this
        val realColumn=LuceneHelper.getClassKey(componentClass,column)

        content?.let {
            if (content.javaClass == String::class.java && StringUtils.isEmpty(content as String)) return this
            searchOperation[realColumn] = searchOperation[realColumn] ?: ArrayList()
            searchOperation[realColumn]!!.add("*$it*")
        }

        return this
    }

    fun index(component: T) {

        LuceneHelper.objectToDocument(component)?.run { indexService.addDocument(this) }

    }


    @JvmOverloads
    fun orderBy(column: String?, desc: Boolean = false): LuceneQuery<T> {

        column ?: return this
        orderByList.add("$column${if (desc) " desc" else ""}")

        return this
    }

    fun read(paramObject: Any?): LuceneQuery<T> {
        paramObject ?: return this
        for (field in TypeAdapter.getAllField(paramObject::class.java)) {
            field.isAccessible = true
            var fieldName = field.name
            field.get(paramObject)?.let {
                field.getAnnotation(Search::class.java)?.run { if (this.value != "") fieldName = this.value;search(fieldName, it) }
                field.getAnnotation(OrderBy::class.java)?.run { if (this.value != "") fieldName = this.value; orderBy(fieldName) }
            }
        }
        return this
    }

    companion object {
        @JvmStatic
        fun <T> of(componentClass: Class<T>): LuceneQuery<T> {

            val luceneSearch = componentClass.getAnnotation(LuceneDocument::class.java)
                    ?: throw RuntimeException("索引类必须配置LuceneConfig注解!")

            return LuceneQuery(componentClass, luceneSearch.value)
        }

        @JvmStatic
        lateinit var indexService: IndexService

        @JvmStatic
        fun init(path: String) {
            indexService = IndexService(path)
        }

    }
}

package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.annotation.query.NotQuery
import github.cweijan.ultimate.annotation.query.OrderBy
import github.cweijan.ultimate.annotation.query.pagination.Page
import github.cweijan.ultimate.annotation.query.pagination.PageSize
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.lucene.type.LuceneDocument
import github.cweijan.ultimate.core.page.Pagination
import github.cweijan.ultimate.util.StringUtils
import org.apache.lucene.index.Term
import org.apache.lucene.search.SortField
import org.apache.lucene.search.TermQuery

/**
 * @author cweijan
 * @version 2019/7/16/016 22:39
 */
class LuceneQuery<T>
private constructor(val componentClass: Class<out T>, private val searchFields: Array<String>) {

    val searhLazy = lazy { LinkedHashMap<String, MutableList<String>>() }
    val searchOperation: MutableMap<String, MutableList<String>>by searhLazy

    val sortLazy = lazy { return@lazy ArrayList<SortField>() }
    val sortFieldList: MutableList<SortField> by sortLazy

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

        page ?: return this
        this.page = if (page <= 0) 1 else page
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

    fun list(): Pagination<T> {
        return indexService.search(searchFields, this)
    }

    fun getByPrimaryKey(value: Any?): T? {

        value ?: return null

        val primaryKeyField = componentClass.getAnnotation(LuceneDocument::class.java).primaryKeyField

        val query = TermQuery(Term(LuceneHelper.getClassKey(componentClass, primaryKeyField), value.toString()))
        val searchByQuery = indexService.searchByQuery(query, 1)
        if (searchByQuery != null) {
            return LuceneHelper.documentToObject(searchByQuery[0], componentClass)
        }

        return null
    }

    fun getLuceneSearch(): String {
        return LuceneQueryGenerator.generateOperationSql(this)
    }

    fun all(): LuceneQuery<T> {
        searchOperation["*"] = mutableListOf("*")
        return this
    }

    fun search(column: String?, content: Any?): LuceneQuery<T> {

        column ?: return this
        val realColumn = LuceneHelper.getClassKey(componentClass, column)

        content?.let {
            if (content.javaClass == String::class.java && StringUtils.isEmpty(content as String)) return this
            searchOperation[realColumn] = searchOperation[realColumn] ?: ArrayList()
            searchOperation[realColumn]!!.add("*${TypeAdapter.convertLuceneAdapter(it)}*")
        }

        return this
    }

    fun orderBy(column: String?): LuceneQuery<T> {

        column ?: return this
        sortFieldList.add(SortField(LuceneHelper.getClassKey(componentClass, column), LuceneHelper.getSortFieldType(componentClass, column)))

        return this
    }

    fun orderDescBy(column: String?): LuceneQuery<T> {

        column ?: return this
        sortFieldList.add(SortField(LuceneHelper.getClassKey(componentClass, column), LuceneHelper.getSortFieldType(componentClass, column), true))

        return this
    }

    fun read(paramObject: Any?): LuceneQuery<T> {
        paramObject ?: return this
        for (field in TypeAdapter.getAllField(paramObject::class.java)) {
            field.isAccessible = true
            var fieldName = field.name
            field.get(paramObject)?.let {
                field.getAnnotation(NotQuery::class.java)?.run { return@let }
                field.getAnnotation(Page::class.java)?.run { page(it.toString().toInt());return@let }
                field.getAnnotation(PageSize::class.java)?.run { pageSize(it.toString().toInt());return@let }
                field.getAnnotation(OrderBy::class.java)?.run { if (this.value != "") fieldName = this.value; orderBy(fieldName);return@let }
                if (TableInfo.getComponent(componentClass,true)?.getColumnInfoByFieldName(fieldName) != null)
                        search(fieldName, it)
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
        fun index(component: Any) {
            LuceneHelper.objectToDocument(component)?.run { indexService.addDocument(this) }
        }

        @JvmStatic
        lateinit var indexService: IndexService

        @JvmStatic
        fun init(path: String) {
            indexService = IndexService(path)
        }

    }
}
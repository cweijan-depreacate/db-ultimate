package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.annotation.Exclude
import github.cweijan.ultimate.annotation.OneToMany
import github.cweijan.ultimate.annotation.OneToOne
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.set

/**
 * Lucene查询类
 * @author cweijan
 * @version 2019/7/16/016 22:39
 */
class LuceneQuery<T>
private constructor(val componentClass: Class<out T>, private val searchFields: Array<String>) {

    val searchLazy = lazy { LinkedHashMap<String, MutableList<String>>() }
    val searchOperation: MutableMap<String, MutableList<String>>by searchLazy

    val notEqualsLazy = lazy { LinkedHashMap<String, MutableList<String>>() }
    val notEqualsOperation: MutableMap<String, MutableList<String>>by notEqualsLazy

    val orEqLazy = lazy { HashMap<String, MutableList<String>>() }
    val orEqualsOperation: MutableMap<String, MutableList<String>>by orEqLazy

    val sortLazy = lazy { return@lazy ArrayList<SortField>() }
    val sortFieldList: MutableList<SortField> by sortLazy
    var searchFullContent: String? = null

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

    fun get(): T? {
        val list = list()
        if (list.isNotEmpty()) return list[0]
        return null
    }

    fun list(): List<T> {
        return pageList().list
    }

    fun pageList(): Pagination<T> {
        return indexService.search(searchFields, this)
    }

    fun deleteByPrimaryKey(value: Any?) {
        value ?: return

        val name = TableInfo.getComponent(componentClass).primaryField!!.name
        indexService.deleteByQuery(TermQuery(Term(name, value.toString())), componentClass)

    }

    fun getByPrimaryKey(value: Any?): T? {

        value ?: return null

        val primaryName = TableInfo.getComponent(componentClass).primaryField!!.name

        val query = TermQuery(Term(primaryName, value.toString()))
        val searchByQuery = indexService.searchByQuery(query, 1, componentClass)
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

    /**
     * 搜索指定列，该方法同等与search
     */
    fun eq(column: String?, content: Any?): LuceneQuery<T> {
        column ?: return this

        put(searchOperation, column, content)

        return this
    }

    /**
     * lucene not 查询
     */
    fun notEq(column: String?, content: Any?): LuceneQuery<T> {
        column ?: return this

        put(notEqualsOperation, column, content)

        return this
    }

    /**
     * 搜索指定列
     */
    fun search(column: String?, content: Any?): LuceneQuery<T> {

        column ?: return this

        put(searchOperation, column, content)

        return this
    }

    /**
     * lucene not 查询
     */
    fun orEq(column: String?, content: Any?): LuceneQuery<T> {
        column ?: return this

        put(orEqualsOperation, column, content)

        return this
    }

    private fun put(map: MutableMap<String, MutableList<String>>, column: String, content: Any?) {
        content?.let {
            if (content.javaClass == String::class.java && StringUtils.isEmpty(content as String)) return
            map[column] = map[column] ?: ArrayList()
            map[column]!!.add("*${TypeAdapter.convertLuceneAdapter(it)}*")
        }
    }

    /**
     * 搜索配置在LuceneDocument注解的field
     * @see LuceneDocument
     */
    fun searchFull(content: Any?): LuceneQuery<T> {
        content ?: return this
        searchFullContent = if (content.toString().contains("*")) content.toString() else "*$content*"

        return this
    }

    fun orderBy(column: String?): LuceneQuery<T> {

        column ?: return this
        sortFieldList.add(SortField(column, LuceneHelper.getSortFieldType(componentClass, column)))

        return this
    }

    fun orderDescBy(column: String?): LuceneQuery<T> {

        column ?: return this
        sortFieldList.add(SortField(column, LuceneHelper.getSortFieldType(componentClass, column), true))

        return this
    }

    fun read(paramObject: Any?): LuceneQuery<T> {
        paramObject ?: return this
        for (field in TypeAdapter.getAllField(paramObject::class.java)) {
            field.isAccessible = true
            var fieldName = field.name
            field.get(paramObject)?.let {
                field.getAnnotation(Exclude::class.java)?.run { return@let }
                field.getAnnotation(OneToOne::class.java)?.run { return@let }
                field.getAnnotation(OneToMany::class.java)?.run { return@let }
                field.getAnnotation(Page::class.java)?.run { page(it.toString().toInt());return@let }
                field.getAnnotation(PageSize::class.java)?.run { pageSize(it.toString().toInt());return@let }
                field.getAnnotation(OrderBy::class.java)?.run { if (this.value != "") fieldName = this.value; orderBy(fieldName);return@let }
                if (TableInfo.getComponent(componentClass, true)?.getColumnInfoByFieldName(fieldName) != null)
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

        /**
         * 更新对象索引
         */
        @JvmStatic
        fun updateIndex(value: Any?) {
            value ?: return

            val name = TableInfo.getComponent(value::class.java).primaryField!!.name
            val primaryValue = TableInfo.getComponent(value::class.java).getPrimaryValue(value).toString()

            LuceneHelper.objectToDocument(value)?.run {
                indexService.updateDocument(Term(name, primaryValue), this, value::class.java)
            }

        }

        /**
         * 对对象进行Lucene索引，集合、配置了Blob和Exclude注解的Field不会被索引
         */
        @JvmStatic
        fun index(component: Any?) {
            component ?: return
            LuceneHelper.objectToDocument(component)?.run { indexService.addDocument(this, component.javaClass) }
        }

        @JvmStatic
        lateinit var indexService: IndexService

        @JvmStatic
        fun init(path: String) {
            indexService = IndexService(path)
        }

    }
}

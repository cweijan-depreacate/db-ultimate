package github.cweijan.ultimate.core

import github.cweijan.ultimate.annotation.query.*
import github.cweijan.ultimate.annotation.query.pagination.Offset
import github.cweijan.ultimate.annotation.query.pagination.Page
import github.cweijan.ultimate.annotation.query.pagination.PageSize
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * @param isAutoConvert convertCamelToUnderScore
 */
class Query<T>
private constructor(val componentClass: Class<out T>, private var isAutoConvert: Boolean = false) {

    var component: ComponentInfo = TableInfo.getComponent(componentClass)

    private val params: MutableList<String> by lazy {
        return@lazy ArrayList<String>()
    }
    private var column: String? = null
    val joinTables: MutableList<String> by lazy {
        return@lazy ArrayList<String>()
    }
    var orderBy: String? = null
        private set
    var offset: Int? = null
        private set
    var page: Int? = null
        private set
    var pageSize: Int? = null
        private set
    var alias: String? = null
        get() {
            return when {
                field != null -> " $field"
                component.tableAlias != null -> " " + component.tableAlias
                else -> ""
            }
        }

    val updateMap: MutableMap<String, String>by lazy {
        return@lazy HashMap<String, String>()
    }

    val notEqualsOperation: MutableMap<String, MutableList<String>>by lazy {
        HashMap<String, MutableList<String>>()
    }

    val orNotEqualsOperation: MutableMap<String, MutableList<String>>by lazy {
        HashMap<String, MutableList<String>>()
    }

    val equalsOperation: MutableMap<String, MutableList<String>>by lazy {
        HashMap<String, MutableList<String>>()
    }

    val orEqualsOperation: MutableMap<String, MutableList<String>>by lazy {
        HashMap<String, MutableList<String>>()
    }

    val searchOperation: MutableMap<String, MutableList<String>>by lazy {
        HashMap<String, MutableList<String>>()
    }

    val orSearchOperation: MutableMap<String, MutableList<String>> by lazy {
        HashMap<String, MutableList<String>>()
    }

    fun addParam(param: String?): Query<T> {

        param?.let { params.add(it) }

        return this
    }

    fun getParams(): Array<String>? {
        return params.toTypedArray()
    }

    fun join(clazz: Class<*>): Query<T> {

        val foreignComponent = TableInfo.getComponent(clazz)
        val foreignTableName = foreignComponent.tableName
        val foreignKeyInfo = component.getForeignKey(clazz)

        val tableAlias = component.tableAlias ?: component.tableName
        val foreignTableAlias = foreignComponent.tableAlias ?: foreignTableName

        val segment = " join $foreignTableName $foreignTableAlias on $tableAlias.${foreignKeyInfo.foreignKey}=$foreignTableAlias.${foreignKeyInfo.joinKey} "
        joinTables.add(segment)

        return this
    }

    private fun getOperationList(map: MutableMap<String, MutableList<String>>, key: String): MutableList<String>? {

        map[key] = map[key] ?: ArrayList()

        return map[key]
    }

    private fun convert(column: String): String {
        var covertColumn = column

        if (isAutoConvert) {
            val regex = "([a-z])([A-Z]+)"
            val replacement = "$1_$2"
            covertColumn = covertColumn.replace(regex.toRegex(), replacement).toLowerCase()
        }

        return covertColumn
    }

    fun update(column: String, value: Any?): Query<T> {

        value?.let { updateMap[component.getColumnNameByFieldName(column) ?: convert(column)] = TypeAdapter.covertToDateString(componentClass,column,it) }
        return this
    }

    private fun put(map: MutableMap<String, MutableList<String>>, column: String, value: Any?) {

        val operationList = getOperationList(map, component.getColumnNameByFieldName(column) ?: convert(column))
        operationList!!.add(TypeAdapter.covertToDateString(componentClass,column,value!!))
        map[column] = operationList
    }

    fun notEquals(column: String, value: Any?): Query<T> {
        value?.let { put(notEqualsOperation, column, it) }
        return this
    }

    fun orNotEquals(column: String, value: Any?): Query<T> {
        value?.let { put(orNotEqualsOperation, column, it) }
        return this
    }

    fun search(column: String, content: Any?): Query<T> {

        content?.let { put(searchOperation, column, "%$it%") }
        return this
    }

    fun orSearch(column: String, value: Any?): Query<T> {
        value?.let { put(orSearchOperation, column, "%$it%") }
        return this
    }

    fun equals(column: String, value: Any?): Query<T> {

        value?.let { put(equalsOperation, column, it) }
        return this
    }

    fun orEquals(column: String, value: Any?): Query<T> {

        value?.let { put(orEqualsOperation, column, it) }
        return this
    }

    fun pageSize(limit: Int?): Query<T> {

        this.pageSize = limit
        return this
    }

    fun page(page: Int?): Query<T> {

        this.page = page
        return this
    }

    fun offset(offset: Int?): Query<T> {

        this.offset = offset
        return this
    }

    fun setColumn(column: String?): Query<T> {

        column?.let { this.column = convert(column) }
        return this
    }

    fun orderBy(orderBy: String?): Query<T> {

        this.orderBy = orderBy
        return this
    }

    fun getColumn(): String? {

        if (column.equals("")) return null

        return column
    }

    fun readObject(paramObject: Any): Query<T> {
        if (paramObject is Map<*, *>) {
            paramObject.forEach { key, value ->
                value?.let {
                    this.equals(key!!.toString(), it)
                }
            }
        } else {
            for (field in TypeAdapter.getAllField(paramObject::class.java)) {
                field.isAccessible = true
                var fieldName = field.name
                field.get(paramObject)?.let {
                    var haveCondition = false
                    field.getAnnotation(NotQuery::class.java)?.run { return@let }
                    field.getAnnotation(Page::class.java)?.run { haveCondition = true; page(it.toString().toInt()) }
                    field.getAnnotation(Offset::class.java)?.run { haveCondition = true; offset(it.toString().toInt()) }
                    field.getAnnotation(PageSize::class.java)?.run { haveCondition = true; pageSize(it.toString().toInt()) }
                    field.getAnnotation(Equals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; equals(fieldName, it) }
                    field.getAnnotation(OrEquals::class.java)?.run {if (this.value != "") fieldName = this.value; haveCondition = true; orEquals(fieldName, it) }
                    field.getAnnotation(NotEquals::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; notEquals(fieldName, it) }
                    field.getAnnotation(OrNotEquals::class.java)?.run {if (this.value != "") fieldName = this.value; haveCondition = true; orNotEquals(fieldName, it) }
                    field.getAnnotation(Search::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; search(fieldName, it) }
                    field.getAnnotation(OrSearch::class.java)?.run {if (this.value != "") fieldName = this.value; haveCondition = true; orSearch(fieldName, it) }
                    if (!haveCondition) equals(fieldName, it)
                }
            }
        }
        return this
    }

    fun list(): List<T> {
        return core.find(this)
    }

    fun get(): T? {
        return core.getByQuery(this)
    }

    fun executeUpdate() {
        core.update(this)
    }

    fun delete() {
        core.delete(this)
    }

    companion object {
        @JvmStatic
        fun <T> of(componentClass: Class<T>): Query<T> {

            return Query(componentClass)
        }

        @JvmStatic
        lateinit var core: DbUltimate

    }

}

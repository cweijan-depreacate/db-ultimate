package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import org.fest.reflect.core.Reflection
import java.util.*

/**
 * @param isAutoConvert convertCamelToUnderScore
 */
class Query<T>
private constructor(val componentClass: Class<out T>, private var isAutoConvert: Boolean = false) {

    var component: ComponentInfo = TableInfo.getComponent(componentClass)

    private val equalsMap: MutableMap<String, MutableList<String>> by lazy {
        return@lazy HashMap<String, MutableList<String>>()
    }
    private val orEqualsMap: MutableMap<String, MutableList<String>>by lazy {
        return@lazy HashMap<String, MutableList<String>>()
    }
    private val notEqualsMap: MutableMap<String, MutableList<String>>by lazy {
        return@lazy HashMap<String, MutableList<String>>()
    }
    private val likeMap: MutableMap<String, MutableList<String>>by lazy {
        return@lazy HashMap<String, MutableList<String>>()
    }

    private val params: MutableList<String> by lazy {
        return@lazy ArrayList<String>()
    }
    private val updateMap: MutableMap<String, String>by lazy {
        return@lazy HashMap<String, String>()
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
    var limit: Int? = null
        private set
    var tableAlias: String? = null

    val updateList: Map<String, String>?
        get() = updateMap

    val notEqualsOperation: Map<String, List<String>>?
        get() = notEqualsMap

    val equalsOperation: Map<String, List<String>>?
        get() = equalsMap

    val orEqualsOperation: Map<String, List<String>>?
        get() = orEqualsMap

    val searchOperation: Map<String, List<String>>?
        get() = likeMap

    val alias: String?
        get() {
            return when {
                this.tableAlias != null -> " $tableAlias"
                component.tableAlias != null -> " " + component.tableAlias
                else -> ""
            }
        }

    fun addParam(param: String?): Query<T> {

        param?.let { params.add(it) }

        return this
    }

    fun getParams(): Array<String>? {
        return params.toTypedArray()
    }

    fun join(sql: String): Query<T> {

        val segment = " join $sql "
        joinTables.add(segment)
        return this
    }

    fun join(clazz: Class<*>): Query<T> {

        val foreignComponent = TableInfo.getComponent(clazz)
        val foreignTableName = foreignComponent.tableName
        val foreignKeyInfo = component.getForeignKey(clazz)

        val tableAlias = component.tableAlias ?: component.tableName
        val foreignTableAlias = foreignComponent.tableAlias ?: foreignTableName

        join(" $foreignTableName $foreignTableAlias on $tableAlias.${foreignKeyInfo.foreignKey}=$foreignTableAlias.${foreignKeyInfo.joinKey} ")
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

        value?.let { updateMap[convert(column)] = it.toString() }
        return this
    }

    private fun put(map: MutableMap<String, MutableList<String>>, column: String, value: Any?) {

        val operationList = getOperationList(map, convert(column))
        operationList!!.add(value!!.toString())
        map[column] = operationList
    }

    fun notEquals(column: String, value: Any?): Query<T> {
        value?.let { put(notEqualsMap, column, it) }
        return this
    }

    fun equals(column: String, value: Any?): Query<T> {

        value?.let { put(equalsMap, column, it) }
        return this
    }

    fun like(column: String, content: Any?): Query<T> {

        content?.let { put(likeMap, column, "%$it%") }
        return this
    }

    fun orEquals(column: String, value: Any?): Query<T> {

        value?.let { put(orEqualsMap, column, it) }
        return this
    }

    fun limit(limit: Int?): Query<T> {

        this.limit = limit
        return this
    }

    fun start(page: Int?): Query<T> {

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

    companion object {
        @JvmStatic
        fun <T> of(componentClass: Class<T>): Query<T> {

            return Query(componentClass)
        }

    }

    fun readObject(paramObject: Any): Query<T> {
        if (paramObject is Map<*, *>) {
            paramObject.forEach { key, value ->
                value?.let {
                    this.equals(key!!.toString(), TypeAdapter.convertToSqlValue(componentClass,key.toString(),it))
                }
            }
        } else {
            for (field in paramObject::class.java.declaredFields) {
                field.isAccessible = true
                Reflection.field(field.name).ofType(field.type).`in`(paramObject).get()?.let {
                    this.equals(field.name, TypeAdapter.convertToSqlValue(componentClass,field.name,it))
                }
            }
        }
        return this
    }

}

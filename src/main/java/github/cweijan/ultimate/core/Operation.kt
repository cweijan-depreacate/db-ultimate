package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.util.Log
import org.fest.reflect.core.Reflection
import java.util.*

/**
 * @param isAutoConvert convertCamelToUnderScore
 */
class Operation<T>
private constructor(var componentClass: Class<out T>, private var isAutoConvert: Boolean = false) {

    private val equalsMap: MutableMap<String, MutableList<String>> by lazy {
        return@lazy HashMap<String, MutableList<String>>()
    }
    private val orEqualsMap: MutableMap<String, MutableList<String>>by lazy {
        return@lazy HashMap<String, MutableList<String>>()
    }
    private val notEqualsMap: MutableMap<String, MutableList<String>>by lazy {
        return@lazy HashMap<String, MutableList<String>>()
    }
    private val searchMap: MutableMap<String, MutableList<String>>by lazy {
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
    var start: Int? = null
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
        get() = searchMap

    val alias: String?
        get() {
            return when {
                this.tableAlias != null -> " $tableAlias"
                TableInfo.getComponent(componentClass).tableAlias != null -> " " + TableInfo.getComponent(componentClass).tableAlias
                else -> ""
            }
        }

    fun addParam(param: String) {

        params.add(param)
    }

    fun getParams(): Array<String>? {
        return params.toTypedArray()
    }

    @JvmOverloads
    fun join(table: String, alias: String? = "", onOperation: String) {

        val segment = " join $table $alias on $onOperation "
        joinTables.add(segment)
    }

    @JvmOverloads
    fun <T> join(clazz: Class<T>, alias: String? = TableInfo.getComponent(clazz).tableAlias, onOperation: String) {
        join(TableInfo.getComponent(clazz).tableName, alias, onOperation)
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

    fun update(column: String, value: Any) {

        updateMap[convert(column)] = value.toString()
    }

    private fun put(map: MutableMap<String, MutableList<String>>, column: String, value: Any?) {

        val operationList = getOperationList(map, convert(column))
        operationList!!.add(value!!.toString())
        map[column] = operationList
    }

    fun notEquals(column: String, value: Any) {

        put(notEqualsMap, column, value)
    }

    fun equals(column: String, value: Any) {

        put(equalsMap, column, value)
    }

    fun search(column: String, content: Any) {

        put(searchMap, column, "%$content%")
    }

    fun orEquals(column: String, value: Any) {

        put(orEqualsMap, column, value)
    }

    fun limit(limit: Int?) {

        this.limit = limit
    }

    fun start(start: Int?) {

        this.start = start
    }

    fun setColumn(column: String) {

        this.column = convert(column)
    }

    fun orderBy(orderBy: String) {

        this.orderBy = orderBy
    }

    fun getColumn(): String? {

        if (column.equals("")) return null

        return column
    }

    companion object {
        @JvmStatic
        fun <T> build(componentClass: Class<T>): Operation<T> {

            return Operation(componentClass)
        }

        @JvmStatic
        fun <T : Any> build(component: T): Operation<T> {

            val operation = Operation(component::class.java)
            val componentInfo = TableInfo.getComponent(component::class.java)
            val fields = componentInfo.componentClass.declaredFields
            for (field in fields) {
                try {
                    field.isAccessible = true
                    val fieldValue: Any? = Reflection.field(field.name).ofType(field.type).`in`(component).get()
                    if (fieldValue == null || componentInfo.isExcludeField(field)) {
                        continue
                    }
                    operation.equals(componentInfo.getColumnNameByFieldName(field.name), TypeAdapter.convertFieldValue(field.type.name, fieldValue))
                } catch (e: IllegalAccessException) {
                    Log.logger.error(e.message, e)
                }

            }
            return operation
        }

    }

}

package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.util.Log
import org.fest.reflect.core.Reflection
import java.util.*

/**
 * @param isAutoConvert convertCamelToUnderScore
 */
class Operation<T>
private constructor(val componentClass: Class<out T>, private var isAutoConvert: Boolean = false) {

    var component:ComponentInfo = TableInfo.getComponent(componentClass)

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
        get() = likeMap

    val alias: String?
        get() {
            return when {
                this.tableAlias != null -> " $tableAlias"
                component.tableAlias != null -> " " + component.tableAlias
                else -> ""
            }
        }

    fun addParam(param: String) {

        params.add(param)
    }

    fun getParams(): Array<String>? {
        return params.toTypedArray()
    }

    fun join(sql: String) {

        val segment = " join $sql "
        joinTables.add(segment)
    }

    fun join(clazz: Class<*>) {

        val foreignComponent = TableInfo.getComponent(clazz)
        val foreignTableName = foreignComponent.tableName
        val foreignKeyInfo = component.getForeignKey(clazz)

        val tableAlias=component.tableAlias?:component.tableName
        val foreignTableAlias = foreignComponent.tableAlias?:foreignTableName

        join(" $foreignTableName $foreignTableAlias on $tableAlias.${foreignKeyInfo.foreignKey}=$foreignTableAlias.${foreignKeyInfo.joinKey} ")
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

    fun like(column: String, content: Any) {

        put(likeMap, column, "%$content%")
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

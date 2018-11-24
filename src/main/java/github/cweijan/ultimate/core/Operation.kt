package github.cweijan.ultimate.core

import java.util.ArrayList
import java.util.HashMap

class Operation
/**
 * @param autoConvert convertCamelToUnderScore
 */
@JvmOverloads constructor(var isAutoConvert: Boolean = false) {

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
    private val joinTables: MutableList<String> by lazy {
        return@lazy ArrayList<String>()
    }
    private val params: MutableList<String> by lazy {
        return@lazy ArrayList<String>()
    }
    private val updateMap: MutableMap<String, String>by lazy {
        return@lazy HashMap<String, String>()
    }
    var orderBy: String? = null
        private set
    private var column: String? = null
    var start: Int? = null
        private set
    var limit: Int? = null
        private set

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

    fun addParam(param: String) {

        params.add(param)
    }

    fun getParams(): Array<String>? {
        return params.toTypedArray()
    }

    fun join(table: String, alias: String, onOperation: String) {

        val segment = "join $table $alias on $onOperation"
        joinTables.add(segment)
    }

    private fun getOperationList(map: MutableMap<String, MutableList<String>>, key: String): MutableList<String>? {

        map[key]=map[key]?:ArrayList()

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

        return column ?: "*"
    }
}

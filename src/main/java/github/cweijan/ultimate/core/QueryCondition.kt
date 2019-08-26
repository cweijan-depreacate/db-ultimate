package github.cweijan.ultimate.core

import github.cweijan.ultimate.convert.TypeAdapter
import java.util.HashMap

internal class QueryCondition {

    private var params: MutableList<Any> = ArrayList()
    var page: Int? = null
    var pageSize: Int? = null
    var offset: Int? = null
        get() {
            if (field != null) return field
            if (this.page != null && this.pageSize != 0) {
                return if (page!! <= 0) 0 else (page!! - 1) * (pageSize ?: 100)
            }
            return null
        }


    val joinLazy = lazy { return@lazy ArrayList<String>() }
    val joinTables: MutableList<String> by joinLazy

    var whereSql: String? = null

    val orderByLazy = lazy { return@lazy ArrayList<String>() }
    val orderByList: MutableList<String> by orderByLazy

    val updateLazy = lazy { return@lazy HashMap<String, Any>() }
    val updateMap: MutableMap<String, Any>by updateLazy

    val isNullLazy = lazy { return@lazy ArrayList<String>() }
    val isNullList: MutableList<String> by isNullLazy

    val isNotNullLazy = lazy { return@lazy ArrayList<String>() }
    val isNotNullList: MutableList<String> by isNotNullLazy

    val greatEqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val greatEqualsOperation: MutableMap<String, MutableList<Any>>by greatEqLazy

    val lessEqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val lessEqualsOperation: MutableMap<String, MutableList<Any>>by lessEqLazy

    val eqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val equalsOperation: MutableMap<String, MutableList<Any>>by eqLazy

    val inLazy = lazy { HashMap<String, MutableList<*>>() }
    val inOperation: MutableMap<String, MutableList<*>>by inLazy

    val orEqLazy = lazy { HashMap<String, MutableList<Any>>() }
    val orEqualsOperation: MutableMap<String, MutableList<Any>>by orEqLazy

    val notEqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val notEqualsOperation: MutableMap<String, MutableList<Any>>by notEqLazy

    val orNotEqLazy = lazy { HashMap<String, MutableList<Any>>() }
    val orNotEqualsOperation: MutableMap<String, MutableList<Any>>by orNotEqLazy

    val searchLazy = lazy { HashMap<String, MutableList<Any>>() }
    val searchOperation: MutableMap<String, MutableList<Any>>by searchLazy

    val sumLazy = lazy { return@lazy HashMap<String, String>() }
    val sumMap: MutableMap<String, String>by sumLazy

    val countLazy = lazy { return@lazy HashMap<String, String>() }
    val countMap: MutableMap<String, String>by countLazy

    val avgLazy = lazy { return@lazy HashMap<String, String>() }
    val avgMap: MutableMap<String, String>by avgLazy

    val maxLazy = lazy { return@lazy HashMap<String, String>() }
    val maxMap: MutableMap<String, String>by maxLazy

    val minLazy = lazy { return@lazy HashMap<String, String>() }
    val minMap: MutableMap<String, String>by minLazy

    val showColumnLazy = lazy { return@lazy ArrayList<String>() }
    val showColumnList: MutableList<String> by showColumnLazy

    val groupLazy = lazy { return@lazy ArrayList<String>() }
    val groupByList: MutableList<String> by groupLazy

    val havingLazy = lazy { return@lazy ArrayList<String>() }
    val havingSqlList: MutableList<String> by havingLazy


    fun generateColumns(): String? {

        var columnSql = ""
        if (countLazy.isInitialized()) countMap.forEach { (columnName, showColumnName) -> columnSql += "COUNT(DISTINCT $columnName) $showColumnName," }
        if (sumLazy.isInitialized()) sumMap.forEach { (columnName, showColumnName) -> columnSql += "SUM($columnName) $showColumnName," }
        if (avgLazy.isInitialized()) avgMap.forEach { (columnName, showColumnName) -> columnSql += "AVG($columnName) $showColumnName," }
        if (minLazy.isInitialized()) minMap.forEach { (columnName, showColumnName) -> columnSql += "MIN($columnName) $showColumnName," }
        if (maxLazy.isInitialized()) maxMap.forEach { (columnName, showColumnName) -> columnSql += "MAX($columnName) $showColumnName," }
        if (showColumnLazy.isInitialized()) showColumnList.forEach { columnName -> columnSql += "$columnName," }
        if (columnSql.lastIndexOf(",") != -1) {
            columnSql = columnSql.substring(0, columnSql.lastIndexOf(","))
        }

        return if (columnSql == "") null else columnSql
    }


    fun getOperationList(map: MutableMap<String, MutableList<Any>>, key: String): MutableList<Any>? {

        map[key] = map[key] ?: ArrayList()

        return map[key]
    }

    fun addParam(param: Any?){

        param?.let { params.add(it) }

    }


    fun getParams(): Array<Any>? {
        return params.toTypedArray()
    }

    fun consumeParams(): Array<Any>? {
        val array = params.toTypedArray()
        params = ArrayList()
        return array
    }


}
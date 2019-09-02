package github.cweijan.ultimate.core

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.annotation.Exclude
import github.cweijan.ultimate.annotation.OneToMany
import github.cweijan.ultimate.annotation.OneToOne
import github.cweijan.ultimate.annotation.query.*
import github.cweijan.ultimate.annotation.query.pagination.Page
import github.cweijan.ultimate.annotation.query.pagination.PageSize
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.core.page.Pagination
import github.cweijan.ultimate.util.Json
import github.cweijan.ultimate.util.StringUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

internal class QueryCondition(var component: ComponentInfo) {

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

    val alias: String = " " + (component.tableAlias ?: "")
    var whereSql: String? = null

    val orderByLazy = lazy { return@lazy ArrayList<String>() }
    val orderByList: MutableList<String> by orderByLazy

    val updateLazy = lazy { return@lazy HashMap<String, Any>() }
    val updateMap: MutableMap<String, Any> by updateLazy

    val isNullLazy = lazy { return@lazy ArrayList<String>() }
    val isNullList: MutableList<String> by isNullLazy

    val isNotNullLazy = lazy { return@lazy ArrayList<String>() }
    val isNotNullList: MutableList<String> by isNotNullLazy

    val greatEqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val greatEqualsOperation: MutableMap<String, MutableList<Any>> by greatEqLazy

    val lessEqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val lessEqualsOperation: MutableMap<String, MutableList<Any>> by lessEqLazy

    val eqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val equalsOperation: MutableMap<String, MutableList<Any>> by eqLazy

    val inLazy = lazy { HashMap<String, MutableList<*>>() }
    val inOperation: MutableMap<String, MutableList<*>> by inLazy

    val orEqLazy = lazy { HashMap<String, MutableList<Any>>() }
    val orEqualsOperation: MutableMap<String, MutableList<Any>> by orEqLazy

    val notEqLazy = lazy { LinkedHashMap<String, MutableList<Any>>() }
    val notEqualsOperation: MutableMap<String, MutableList<Any>> by notEqLazy

    val orNotEqLazy = lazy { HashMap<String, MutableList<Any>>() }
    val orNotEqualsOperation: MutableMap<String, MutableList<Any>> by orNotEqLazy

    val searchLazy = lazy { HashMap<String, MutableList<Any>>() }
    val searchOperation: MutableMap<String, MutableList<Any>> by searchLazy

    val sumLazy = lazy { return@lazy HashMap<String, String>() }
    val sumMap: MutableMap<String, String> by sumLazy

    val countLazy = lazy { return@lazy HashMap<String, String>() }
    val countMap: MutableMap<String, String> by countLazy

    val avgLazy = lazy { return@lazy HashMap<String, String>() }
    val avgMap: MutableMap<String, String> by avgLazy

    val maxLazy = lazy { return@lazy HashMap<String, String>() }
    val maxMap: MutableMap<String, String> by maxLazy

    val minLazy = lazy { return@lazy HashMap<String, String>() }
    val minMap: MutableMap<String, String> by minLazy

    val showColumnLazy = lazy { return@lazy ArrayList<String>() }
    val showColumnList: MutableList<String> by showColumnLazy

    val groupLazy = lazy { return@lazy ArrayList<String>() }
    val groupByList: MutableList<String> by groupLazy

    val havingLazy = lazy { return@lazy ArrayList<String>() }
    val havingSqlList: MutableList<String> by havingLazy

    /**
     * 封装分页对象
     * @param list 数据
     * @param total 数据总量
     */
    fun <T> calculatePagination(list: List<T>, total: Int?): Pagination<T> {
        val pagination = Pagination<T>()
        pagination.total = total ?: 0
        pagination.pageSize = this.pageSize

        //计算总页数
        if (pagination.pageSize != null) {
            pagination.totalPage = pagination.total / pagination.pageSize
            if (pagination.total % pagination.pageSize != 0) {
                pagination.totalPage++
            }
        } else pagination.totalPage = 1
        //计算当前页
        pagination.current = this.page ?: this.offset?.run {
            when {
                this == 0 -> 1
                pagination.total % this == 0 -> pagination.total / this
                else -> (pagination.total / this) + 1
            }
        } ?: 1
        pagination.startPage = pagination.current
        pagination.list = list
        return pagination
    }

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

    fun addParam(param: Any?) {

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

    private fun getColumnName(column: String) = component.getColumnNameByFieldName(column)
            ?: TypeAdapter.convertHumpToUnderLine(column)!!

    private fun put(map: MutableMap<String, MutableList<Any>>, column: String, value: Any?) {

        value ?: return
        val tableColumn = getColumnName(column)
        val operationList = getOperationList(map, tableColumn)
        operationList!!.add(TypeAdapter.convertAdapter(component.componentClass, column, value))
    }

    private fun mapPut(map: MutableMap<String, String>, column: String?, value: String) {
        column?.let {
            val columnName = getColumnName(column)
            map[columnName] = value
        }
    }

    private fun listAdd(list: MutableList<String>, column: String?) {
        column ?: return
        if (StringUtils.isEmpty(column)) return
        list.add(getColumnName(column))
    }

    // map->list
    fun notEq(column: String, value: Any?) = put(notEqualsOperation, column, value)

    fun orNotEq(column: String, value: Any?) = put(orNotEqualsOperation, column, value)
    fun le(column: String, value: Any?) = put(lessEqualsOperation, column, value)
    fun eq(column: String, value: Any?) = put(equalsOperation, column, value)
    fun ge(column: String, value: Any?) = put(greatEqualsOperation, column, value)
    fun orEq(column: String, value: Any?) = put(orEqualsOperation, column, value)

    // statistic
    fun avg(column: String?, avgColumnName: String? = null) = mapPut(avgMap, column, avgColumnName ?: "${column}Avg")

    fun min(column: String?, minColumnName: String? = null) = mapPut(minMap, column, minColumnName ?: "${column}Min")
    fun max(column: String?, maxColumnName: String? = null) = mapPut(maxMap, column, maxColumnName ?: "${column}Max")
    fun sum(column: String?, sumColumnName: String? = null) = mapPut(sumMap, column, sumColumnName ?: "${column}Sum")
    fun countDistinct(column: String?, countColumnName: String? = null) = mapPut(countMap, column, countColumnName
            ?: "${column}CountDistinct")

    // list-add
    fun isNull(column: String?) = listAdd(isNullList, column)

    fun isNotNull(column: String?) = listAdd(isNotNullList, column)
    fun orderBy(column: String?) = listAdd(orderByList, column)
    fun groupBy(column: String?) = listAdd(groupByList, column)
    fun addShowColumn(column: String?) = listAdd(showColumnList, column)
    fun having(havingSql: String?) = listAdd(havingSqlList, havingSql)


    fun like(column: String, content: Any?) {
        content?.let {
            if (content.javaClass == String::class.java && StringUtils.isEmpty(content as String)) return
            put(searchOperation, column, "%$it%")
        }
    }

    /**
     * in查询
     */
    fun in0(column: String, value: MutableList<*>?) {

        value?.let {
            inOperation[getColumnName(column)] = value
        }
    }


    fun orderDescBy(column: String?) {
        column ?: return
        orderByList.add("${getColumnName(column)} desc")
    }

    /**
     * 对指定列进行更新
     */
    fun update(column: String, value: Any?) {

        value?.let {
            val convertColumnName = getColumnName(column)
            component.getColumnInfoByColumnName(convertColumnName)?.field?.getAnnotation(Blob::class.java)?.run {
                updateMap[convertColumnName] = Json.toJson(value).toByteArray()
                return@let
            }
            updateMap[convertColumnName] = TypeAdapter.convertAdapter(component.componentClass, column, it)
        }
    }

    fun read(vararg paramArray: Any?) {
        paramArray.forEach { paramObject ->
            paramObject ?: return@forEach
            when {
                TypeAdapter.isAdapterType(paramObject::class.java) -> {
                    throw RuntimeException("必须为对象类型!")
                }
                paramObject is Map<*, *> -> {
                    paramObject.forEach { (key, value) ->
                        value?.let {
                            this.eq(key!!.toString(), it)
                        }
                    }
                }
                else -> {
                    for (field in TypeAdapter.getAllField(paramObject::class.java)) {
                        field.isAccessible = true
                        var fieldName = field.name
                        field.get(paramObject)?.let {
                            field.getAnnotation(Exclude::class.java)?.run { return@let }
                            field.getAnnotation(OneToOne::class.java)?.run { return@let }
                            field.getAnnotation(OneToMany::class.java)?.run { return@let }
                            if (field.type == String::class.java && it as String == "") return@let
                            var haveCondition = false
                            field.getAnnotation(Page::class.java)?.run { haveCondition = true; page = it.toString().toInt() }
                            field.getAnnotation(PageSize::class.java)?.run { haveCondition = true; pageSize = it.toString().toInt() }
                            field.getAnnotation(Equals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; eq(fieldName, it) }
                            field.getAnnotation(OrEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orEq(fieldName, it) }
                            field.getAnnotation(NotEquals::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; notEq(fieldName, it) }
                            field.getAnnotation(OrNotEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orNotEq(fieldName, it) }
                            field.getAnnotation(GreatEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; ge(fieldName, it) }
                            field.getAnnotation(LessEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; le(fieldName, it) }
                            field.getAnnotation(Search::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; like(fieldName, it) }
                            field.getAnnotation(OrderBy::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orderBy(fieldName) }
                            if (!haveCondition && component.getColumnInfoByFieldName(fieldName) != null)
                                eq(fieldName, it)
                        }
                    }

                }
            }
        }

    }

}
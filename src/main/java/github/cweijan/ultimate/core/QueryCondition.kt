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
import github.cweijan.ultimate.core.query.QueryConjunct
import github.cweijan.ultimate.core.query.QueryType
import github.cweijan.ultimate.core.query.QueryType.*
import github.cweijan.ultimate.util.Json
import github.cweijan.ultimate.util.StringUtils
import java.util.*
import kotlin.collections.ArrayList
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

    val showColumn: StringBuilder = StringBuilder()
    val andCondition: StringBuilder = StringBuilder()
    var andParam: MutableList<Any> = ArrayList()
    val orCondition: StringBuilder = StringBuilder()
    var orParam: MutableList<Any> = ArrayList()

    val orderByLazy = lazy { return@lazy ArrayList<String>() }
    val orderByList: MutableList<String> by orderByLazy

    val updateLazy = lazy { return@lazy HashMap<String, Any>() }
    val updateMap: MutableMap<String, Any> by updateLazy

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

        if (showColumn.lastIndexOf(",") != -1) {
            return showColumn.substring(0, showColumn.lastIndexOf(","))
        }

        return "*"
    }

    fun addParam(param: Any?) {
        param?.let { params.add(it) }
    }

    fun consumeParams(): Array<Any>? {
        val tempParam = ArrayList<Any>()
        tempParam.addAll(params)
        tempParam.addAll(andParam)
        tempParam.addAll(orParam)
        return tempParam.toTypedArray()
    }

    private fun getColumnName(column: String) = component.getColumnNameByFieldName(column)
            ?: TypeAdapter.convertHumpToUnderLine(column)!!

    private fun addCondition(column: String, queryType: QueryType, value: Any?,
                             conditionList: StringBuilder, paramList: MutableList<Any>, conjuct: String) {
        value ?: return
        val tableColumn = getColumnName(column)
        val adapterValue = TypeAdapter.convertAdapter(component.componentClass, column, value)
        conditionList.append("$conjuct " + when (queryType) {
            isNull, isNotNull -> "$tableColumn ${queryType.code} "
            else -> "$tableColumn ${queryType.code} ? "
        })
        when (queryType) {
            isNull, isNotNull -> return
            like -> paramList.add("%$adapterValue%")
            else -> paramList.add(adapterValue)
        }
    }

    fun addAndCondition(column: String, queryType: QueryType, value: Any?) = addCondition(column, queryType, value, andCondition, andParam, QueryConjunct.AND)
    fun addOrCondition(column: String, queryType: QueryType, value: Any?) = addCondition(column, queryType, value, orCondition, orParam, QueryConjunct.OR)

    private fun addColumn(pattern: String?, vararg value: String?) {
        pattern?.let {
            showColumn.append(String.format(pattern,*value))
        }
    }

    private fun listAdd(list: MutableList<String>, column: String?) {
        column ?: return
        if (StringUtils.isEmpty(column)) return
        list.add(getColumnName(column))
    }

    // statistic
    fun avg(column: String?, avgColumnName: String? = null) = addColumn("AVG(%s) %s,", column, avgColumnName ?: "${column}Avg")

    fun min(column: String?, minColumnName: String? = null) = addColumn("MIN(%s) %s,", column, minColumnName ?: "${column}Min")
    fun max(column: String?, maxColumnName: String? = null) = addColumn("MAX(%s) %s,", column, maxColumnName ?: "${column}Max")
    fun sum(column: String?, sumColumnName: String? = null) = addColumn("SUM(%s) %s,", column, sumColumnName ?: "${column}Sum")
    fun countDistinct(column: String?, countColumnName: String? = null) = addColumn("COUNT(DISTINCT %s) %s,", column, countColumnName
            ?: "${column}CountDistinct")

    // list-add
    fun orderBy(column: String?) = listAdd(orderByList, column)

    fun groupBy(column: String?) = listAdd(groupByList, column)
    fun addShowColumn(column: String?) = addColumn(getColumnName(column!!)+",")
    fun having(havingSql: String?) = listAdd(havingSqlList, havingSql)

    /**
     * in查询
     */
    fun `in`(column: String, valueList: MutableList<*>?) {

        valueList ?: return
        val tableColumn = getColumnName(column)
        andCondition.append("${QueryConjunct.AND} $tableColumn in (")
        valueList.forEachIndexed { index, value ->
            if (index == 0)
                andCondition.append("?")
            else
                andCondition.append(",?")
            andParam.add(TypeAdapter.convertAdapter(component.componentClass, column, value))
        }
        andCondition.append(")")

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
                            addAndCondition(key!!.toString(), equals, it)
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
                            field.getAnnotation(Equals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; addAndCondition(fieldName, equals, it) }
                            field.getAnnotation(OrEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; addOrCondition(fieldName, equals, it) }
                            field.getAnnotation(NotEquals::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; addAndCondition(fieldName, not_equlas, it) }
                            field.getAnnotation(OrNotEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; addOrCondition(fieldName, not_equlas, it) }
                            field.getAnnotation(GreatEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; addAndCondition(fieldName, great_equlas, it) }
                            field.getAnnotation(LessEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; addAndCondition(fieldName, less_equals, it) }
                            field.getAnnotation(Search::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; addAndCondition(fieldName, like, it) }
                            field.getAnnotation(OrderBy::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orderBy(fieldName) }
                            if (!haveCondition && component.getColumnInfoByFieldName(fieldName) != null)
                                addAndCondition(fieldName, equals, it)
                        }
                    }

                }
            }
        }

    }

}
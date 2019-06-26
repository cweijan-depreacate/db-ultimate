package github.cweijan.ultimate.core

import github.cweijan.ultimate.annotation.query.*
import github.cweijan.ultimate.annotation.query.pagination.Offset
import github.cweijan.ultimate.annotation.query.pagination.Page
import github.cweijan.ultimate.annotation.query.pagination.PageSize
import github.cweijan.ultimate.core.component.ComponentScan
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.extra.ExtraData
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.debug.HotSwapSupport
import github.cweijan.ultimate.core.excel.ExcelOperator
import github.cweijan.ultimate.core.excel.ExcludeExcel
import github.cweijan.ultimate.util.Json
import github.cweijan.ultimate.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * @param isAutoConvert convertCamelToUnderScore
 */
open class Query<T>
internal constructor(val componentClass: Class<out T>, private var isAutoConvert: Boolean = true) {

    private var methodName: String? = null
    var component: ComponentInfo = TableInfo.getComponent(componentClass)

    private var column: String? = null
    private var params: MutableList<Any> = ArrayList()

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

    val joinLazy = lazy { return@lazy ArrayList<String>() }
    val joinTables: MutableList<String> by joinLazy

    val orderByLazy = lazy { return@lazy ArrayList<String>() }
    val orderByList: MutableList<String> by orderByLazy

    val updateLazy = lazy { return@lazy HashMap<String, Any>() }
    val updateMap: MutableMap<String, Any>by updateLazy

    val greatEqLazy = lazy { HashMap<String, MutableList<String>>() }
    val greatEqualsOperation: MutableMap<String, MutableList<String>>by greatEqLazy

    val lessEqLazy = lazy { HashMap<String, MutableList<String>>() }
    val lessEqualsOperation: MutableMap<String, MutableList<String>>by lessEqLazy

    val eqLazy = lazy { HashMap<String, MutableList<String>>() }
    val equalsOperation: MutableMap<String, MutableList<String>>by eqLazy

    val orEqLazy = lazy { HashMap<String, MutableList<String>>() }
    val orEqualsOperation: MutableMap<String, MutableList<String>>by orEqLazy

    val notEqLazy = lazy { HashMap<String, MutableList<String>>() }
    val notEqualsOperation: MutableMap<String, MutableList<String>>by notEqLazy

    val orNotEqLazy = lazy { HashMap<String, MutableList<String>>() }
    val orNotEqualsOperation: MutableMap<String, MutableList<String>>by orNotEqLazy

    val searchLazy = lazy { HashMap<String, MutableList<String>>() }
    val searchOperation: MutableMap<String, MutableList<String>>by searchLazy

    val orSearchLazy = lazy { HashMap<String, MutableList<String>>() }
    val orSearchOperation: MutableMap<String, MutableList<String>> by orSearchLazy

    private val sumLazy = lazy { return@lazy HashMap<String, String>() }
    private val sumMap: MutableMap<String, String>by sumLazy

    private val countLazy = lazy { return@lazy HashMap<String, String>() }
    private val countMap: MutableMap<String, String>by countLazy

    private val avgLazy = lazy { return@lazy HashMap<String, String>() }
    private val avgMap: MutableMap<String, String>by avgLazy

    private val maxLazy = lazy { return@lazy HashMap<String, String>() }
    private val maxMap: MutableMap<String, String>by maxLazy

    private val minLazy = lazy { return@lazy HashMap<String, String>() }
    private val minMap: MutableMap<String, String>by minLazy

    private val showColumnLazy = lazy { return@lazy ArrayList<String>() }
    private val showColumnList: MutableList<String> by showColumnLazy

    val groupLazy = lazy { return@lazy ArrayList<String>() }
    val groupByList: MutableList<String> by groupLazy

    val havingLazy = lazy { return@lazy ArrayList<String>() }
    val havingSqlList: MutableList<String> by havingLazy

    fun addParam(param: Any?): Query<T> {

        param?.let { params.add(it) }

        return this
    }

    fun getParams(): Array<Any>? {
        return params.toTypedArray()
    }

    fun consumeParams(): Array<Any>? {
        val array = params.toTypedArray()
        params = ArrayList()
        return array
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

    protected fun convert(column: String): String {
        var covertColumn = column

        if (isAutoConvert) {
            val regex = "([a-z])([A-Z]+)"
            val replacement = "$1_$2"
            covertColumn = covertColumn.replace(regex.toRegex(), replacement).toLowerCase()
        }

        return covertColumn
    }

    fun statistic(): List<Map<String, Any>> {
        return db.executeSqlOfMapList(db.sqlGenerator.generateSelectSql(this), this.consumeParams())
    }

    @JvmOverloads
    fun sum(column: String?, sumColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            sumMap[columnName] = sumColumnName ?: "${columnName}Sum"
        }
        return this
    }

    @JvmOverloads
    fun countDistinct(column: String?, countColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            countMap[columnName] = countColumnName ?: "${columnName}CountDistinct"
        }
        return this
    }

    @JvmOverloads
    fun avg(column: String?, avgColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            avgMap[columnName] = avgColumnName ?: "${columnName}Avg"
        }
        return this
    }

    @JvmOverloads
    fun min(column: String?, minColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            minMap[columnName] = minColumnName ?: "${columnName}Min"
        }
        return this
    }

    @JvmOverloads
    fun max(column: String?, maxColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            maxMap[columnName] = maxColumnName ?: "${columnName}Max"
        }
        return this
    }

    fun groupBy(column: String): Query<T> {
        groupByList.add(getColumnName(column))
        return this
    }

    fun addShowColumn(column: String): Query<T> {
        showColumnList.add(getColumnName(column))
        return this
    }

    fun having(havingSql: String): Query<T> {
        havingSqlList.add(havingSql)
        return this
    }

    fun update(column: String, value: Any?): Query<T> {

        value?.let {
            val columnName = component.getColumnNameByFieldName(column)
            updateMap[columnName ?: convert(column)] = TypeAdapter.convertAdapter(componentClass, column, it)
        }
        return this
    }

    private fun put(map: MutableMap<String, MutableList<String>>, column: String, value: Any?) {

        val tableColumn = getColumnName(column)
        val operationList = getOperationList(map, tableColumn)
        operationList!!.add(TypeAdapter.convertAdapter(componentClass, column, value).toString())
        map[tableColumn] = operationList
    }

    protected fun getColumnName(column: String) = component.getColumnNameByFieldName(column) ?: convert(column)

    fun notEq(column: String, value: Any?): Query<T> {
        value?.let { put(notEqualsOperation, column, it) }
        return this
    }

    fun orNotEq(column: String, value: Any?): Query<T> {
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

    /**
     * great equals then
     */
    fun ge(column: String, value: Any?): Query<T> {

        value?.let { put(greatEqualsOperation, column, it) }
        return this
    }

    /**
     * less equals then
     */
    fun le(column: String, value: Any?): Query<T> {

        value?.let { put(lessEqualsOperation, column, it) }
        return this
    }

    fun eq(column: String, value: Any?): Query<T> {

        value?.let { put(equalsOperation, column, it) }
        return this
    }

    fun orEq(column: String, value: Any?): Query<T> {

        value?.let { put(orEqualsOperation, column, it) }
        return this
    }

    fun count():Int{
        return db.getCount(this)
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

    fun orderBy(column: String?, desc: Boolean = false): Query<T> {

        column ?: return this
        orderByList.add("${getColumnName(column)}${if (desc) " desc" else ""}")

        return this
    }

    fun getColumn(): String? {

        if (column.equals("")) return null

        return column
    }

    fun listJson(): String? {
        return Json.toJson(list())
    }

    fun getJson(): String? {
        return Json.toJson(get())
    }

    fun getFromJson(json: String?): T? {
        json ?: return null
        return Json.parse(json, componentClass)
    }

    fun listFromJson(json: String?): List<T>? {
        json ?: return null
        return Json.parseList(json, componentClass)
    }

    fun inputExcel(inputPath: String): List<T> {
        return inputExcel(FileInputStream(File(inputPath)))
    }

    fun inputExcel(inputStream: InputStream): List<T> {
        return ExcelOperator.inputExcel(inputStream, componentClass)
    }

    fun ouputExcel(exportPath: String): Boolean {

        val dataList = list()
        val values = Array(dataList.size) { ArrayList<Any?>() }
        val headers = component.fieldColumnInfoMap.keys.filter { key ->
            val field = componentClass.getDeclaredField(key)
            field.getAnnotation(ExcludeExcel::class.java)?.run { return@filter false }
            field.isAccessible = true
            dataList.forEachIndexed { dataIndex, data ->
                values[dataIndex].add(
                        TypeAdapter.convertAdapter(componentClass, field.name, field.get(data) ?: ""))
            }
            return@filter true
        }.map { key -> component.fieldColumnInfoMap[key]!!.excelHeader }.toTypedArray()

        return ExcelOperator.outputExcel(headers, values, exportPath)
    }

    fun readObject(paramObject: Any?): Query<T> {
        paramObject ?: return this
        if (paramObject is Map<*, *>) {
            paramObject.forEach { key, value ->
                value?.let {
                    this.eq(key!!.toString(), it)
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
                    field.getAnnotation(Equals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; eq(fieldName, it) }
                    field.getAnnotation(OrEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orEq(fieldName, it) }
                    field.getAnnotation(NotEquals::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; notEq(fieldName, it) }
                    field.getAnnotation(OrNotEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orNotEq(fieldName, it) }
                    field.getAnnotation(GreatEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; ge(fieldName, it) }
                    field.getAnnotation(LessEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; le(fieldName, it) }
                    field.getAnnotation(Search::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; search(fieldName, it) }
                    field.getAnnotation(OrSearch::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orSearch(fieldName, it) }
                    field.getAnnotation(OrderBy::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orderBy(fieldName) }
                    if (!haveCondition && component.getColumnInfoByFieldName(fieldName)!=null)
                        eq(fieldName, it)
                }
            }
        }
        return this
    }

    fun list(): List<T> {

        methodName?.run { Log.debug("Execute method $methodName ") }
        return db.find(this)
    }

    fun get(): T? {
        methodName?.run { Log.debug("Execute method $methodName ") }
        return db.getByQuery(this)
    }

    fun executeUpdate() {
        methodName?.run { Log.debug("Execute method $methodName ") }
        db.update(this)
    }

    fun executeDelete() {
        methodName?.run { Log.debug("Execute method $methodName ") }
        db.delete(this)
    }

    fun name(methodName: String?) {
        this.methodName = methodName
    }


    fun generateColumns(): String? {

        var columnSql = ""
        if (countLazy.isInitialized()) countMap.forEach { columnName, showColumnName -> columnSql += "COUNT(DISTINCT $columnName) $showColumnName," }
        if (sumLazy.isInitialized()) sumMap.forEach { columnName, showColumnName -> columnSql += "SUM($columnName) $showColumnName," }
        if (avgLazy.isInitialized()) avgMap.forEach { columnName, showColumnName -> columnSql += "AVG($columnName) $showColumnName," }
        if (minLazy.isInitialized()) minMap.forEach { columnName, showColumnName -> columnSql += "MIN($columnName) $showColumnName," }
        if (maxLazy.isInitialized()) maxMap.forEach { columnName, showColumnName -> columnSql += "MAX($columnName) $showColumnName," }
        if (showColumnLazy.isInitialized()) showColumnList.forEach { columnName -> columnSql += "$columnName," }
        if (columnSql.lastIndexOf(",") != -1) {
            columnSql = columnSql.substring(0, columnSql.lastIndexOf(","))
        }

        return if (columnSql == "") null else columnSql
    }

    companion object {
        @JvmStatic
        fun <T> of(componentClass: Class<T>): Query<T> {

            return Query(componentClass)
        }

        @JvmStatic
        lateinit var db: DbUltimate

        @JvmStatic
        fun init(dbConfig: DbConfig) {
            if (dbConfig.develop) {
                HotSwapSupport.startHotSwapListener(dbConfig)
            }
            dbConfig.scanPackage?.run { ComponentScan.scan(this.split(",")) }
            val dbInitialer = DBInitialer(dbConfig)
            dbInitialer.initalerTable()
            val extraData = ComponentInfo.init(ExtraData::class.java)
            dbInitialer.createTable(extraData)
            db = DbUltimate(dbConfig)
        }

    }


}


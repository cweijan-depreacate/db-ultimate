package github.cweijan.ultimate.core

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.annotation.Exclude
import github.cweijan.ultimate.annotation.OneToMany
import github.cweijan.ultimate.annotation.OneToOne
import github.cweijan.ultimate.annotation.query.*
import github.cweijan.ultimate.annotation.query.pagination.Page
import github.cweijan.ultimate.annotation.query.pagination.PageSize
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.ComponentScan
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.core.excel.ExcelOperator
import github.cweijan.ultimate.core.excel.ExcludeExcel
import github.cweijan.ultimate.core.extra.GroupFunction
import github.cweijan.ultimate.core.page.Pagination
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.util.Json
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Ultimate封装查询对象
 */
open class Query<T>
internal constructor(val componentClass: Class<out T>) {

    internal val queryCondition = QueryCondition()
    var component: ComponentInfo = TableInfo.getComponent(componentClass)

    private var methodName: String? = null
    var alias: String? = null
        get() {
            return when {
                field != null -> " $field"
                component.tableAlias != null -> " " + component.tableAlias
                else -> ""
            }
        }

    fun join(sql: String): Query<T> {

        val segment = " join $sql "
        queryCondition.joinTables.add(segment)
        return this
    }


    /**
     * 执行统计
     */
    fun statistic(): List<Map<String, Any>> {
        return db.findBySql(db.sqlGenerator.generateSelectSql(this), this.queryCondition.consumeParams(), Map::class.java) as List<Map<String, Any>>
    }

    @JvmOverloads
    fun sum(column: String?, sumColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            queryCondition.sumMap[columnName] = sumColumnName ?: "${columnName}Sum"
        }
        return this
    }

    @JvmOverloads
    fun countDistinct(column: String?, countColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            queryCondition.countMap[columnName] = countColumnName ?: "${columnName}CountDistinct"
        }
        return this
    }

    @JvmOverloads
    fun avg(column: String?, avgColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            queryCondition.avgMap[columnName] = avgColumnName ?: "${columnName}Avg"
        }
        return this
    }

    @JvmOverloads
    fun min(column: String?, minColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            queryCondition.minMap[columnName] = minColumnName ?: "${columnName}Min"
        }
        return this
    }

    @JvmOverloads
    fun max(column: String?, maxColumnName: String? = null): Query<T> {
        column?.let {
            val columnName = getColumnName(column)
            queryCondition.maxMap[columnName] = maxColumnName ?: "${columnName}Max"
        }
        return this
    }

    /**
     * 直接拼接where语句
     * @param whereSql 条件语句
     */
    fun where(whereSql: String?): Query<T> {
        whereSql ?: return this
        queryCondition.whereSql = whereSql
        return this
    }

    /**
     * 根据指定列进行分组
     * @param column 指定列
     */
    fun groupBy(column: String): Query<T> {
        queryCondition.groupByList.add(getColumnName(column))
        return this
    }

    /**
     * 统计接口增加显示Column
     */
    fun addShowColumn(column: String): Query<T> {
        queryCondition.showColumnList.add(getColumnName(column))
        return this
    }

    /**
     * having语句片段
     */
    fun having(havingSql: String): Query<T> {
        queryCondition.havingSqlList.add(havingSql)
        return this
    }

    /**
     * 对指定列进行更新
     */
    fun update(column: String, value: Any?): Query<T> {

        value?.let {
            val convertColumnName = TypeAdapter.convertHumpToUnderLine(column)!!
            component.getColumnInfoByColumnName(convertColumnName)?.field?.getAnnotation(Blob::class.java)?.run {
                queryCondition.updateMap[convertColumnName] = Json.toJson(value).toByteArray()
                return@let
            }
            queryCondition.updateMap[convertColumnName] = TypeAdapter.convertAdapter(componentClass, column, it)
        }
        return this
    }

    private fun put(map: MutableMap<String, MutableList<Any>>, column: String, value: Any?) {

        val tableColumn = getColumnName(column)
        val operationList = queryCondition.getOperationList(map, tableColumn)
        operationList!!.add(TypeAdapter.convertAdapter(componentClass, column, value))
    }

    protected fun getColumnName(column: String) = component.getColumnNameByFieldName(column)
            ?: TypeAdapter.convertHumpToUnderLine(column)!!

    /**
     * !=查询
     */
    fun notEq(column: String, value: Any?): Query<T> {
        value?.let { put(queryCondition.notEqualsOperation, column, it) }
        return this
    }

    /**
     * or !=查询
     */
    fun orNotEq(column: String, value: Any?): Query<T> {
        value?.let { put(queryCondition.orNotEqualsOperation, column, it) }
        return this
    }

    /**
     * like查询
     */
    fun like(column: String, content: Any?): Query<T> {

        content?.let { put(queryCondition.searchOperation, column, "%$it%") }
        return this
    }

    /**
     * like查询
     */
    fun search(column: String, content: Any?): Query<T> {

        content?.let {
            if (content.javaClass == String::class.java && StringUtils.isEmpty(content as String)) return this
            put(queryCondition.searchOperation, column, "%$it%")
        }
        return this
    }

    /**
     * great equals then
     */
    fun ge(column: String, value: Any?): Query<T> {

        value?.let { put(queryCondition.greatEqualsOperation, column, it) }
        return this
    }

    /**
     * less equals then, sql column<=relationClass
     */
    fun le(column: String, value: Any?): Query<T> {

        value?.let { put(queryCondition.lessEqualsOperation, column, it) }
        return this
    }

    /**
     * =查询
     */
    fun eq(column: String, value: Any?): Query<T> {

        value?.let { put(queryCondition.equalsOperation, column, it) }
        return this
    }

    /**
     * in查询
     */
    fun in0(column: String, value: MutableList<*>?): Query<T> {

        value?.let {
            val tableColumn = getColumnName(column)
            queryCondition.inOperation[tableColumn] = value
        }
        return this
    }

    fun orEq(column: String, value: Any?): Query<T> {

        value?.let { put(queryCondition.orEqualsOperation, column, it) }
        return this
    }

    fun count(): Int {
        return db.getCount(this)
    }

    fun pageSize(limit: Int?): Query<T> {

        this.queryCondition.pageSize = limit
        return this
    }

    fun limit(limit: Int?): Query<T> {

        this.queryCondition.pageSize = limit
        return this
    }

    /**
     * 设置页码
     */
    fun page(page: Int?): Query<T> {

        this.queryCondition.page = page
        return this
    }

    /**
     * 列为空查询，该查询直接拼接sql，需要防止sql注入
     */
    fun isNull(column: String?): Query<T> {
        column ?: return this
        queryCondition.isNullList.add(getColumnName(column))
        return this
    }

    /**
     * 列不为空查询，该查询直接拼接sql，需要防止sql注入
     */
    fun isNotNull(column: String?): Query<T> {
        column ?: return this
        queryCondition.isNotNullList.add(getColumnName(column))
        return this
    }

    fun orderBy(column: String?): Query<T> {

        column ?: return this
        queryCondition.orderByList.add(getColumnName(column))

        return this
    }

    fun orderDescBy(column: String?): Query<T> {

        column ?: return this
        queryCondition.orderByList.add("${getColumnName(column)} desc")

        return this
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

    fun read(paramArray: Array<Any>?): Query<T> {
        paramArray?.let {
            paramArray.forEach { param -> this.read(param) }
        }
        return this
    }


    fun read(paramObject: Any?): Query<T> {
        paramObject ?: return this
        when {
            TypeAdapter.isAdapterType(paramObject::class.java)->{
                throw RuntimeException("必须对对象类型!")
            }
            paramObject is Map<*, *> -> {
                paramObject.forEach { key, value ->
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
                        var haveCondition = false
                        field.getAnnotation(Exclude::class.java)?.run { return@let }
                        field.getAnnotation(OneToOne::class.java)?.run { return@let }
                        field.getAnnotation(OneToMany::class.java)?.run { return@let }
                        if (field.type == String::class.java) {
                            if (it as String == "") return@let
                        }
                        field.getAnnotation(Page::class.java)?.run { haveCondition = true; page(it.toString().toInt()) }
                        field.getAnnotation(PageSize::class.java)?.run { haveCondition = true; pageSize(it.toString().toInt()) }
                        field.getAnnotation(Equals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; eq(fieldName, it) }
                        field.getAnnotation(OrEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orEq(fieldName, it) }
                        field.getAnnotation(NotEquals::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; notEq(fieldName, it) }
                        field.getAnnotation(OrNotEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orNotEq(fieldName, it) }
                        field.getAnnotation(GreatEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; ge(fieldName, it) }
                        field.getAnnotation(LessEquals::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; le(fieldName, it) }
                        field.getAnnotation(Search::class.java)?.run { if (this.value != "") fieldName = this.value;haveCondition = true; search(fieldName, it) }
                        field.getAnnotation(OrderBy::class.java)?.run { if (this.value != "") fieldName = this.value; haveCondition = true; orderBy(fieldName) }
                        if (!haveCondition && component.getColumnInfoByFieldName(fieldName) != null)
                            eq(fieldName, it)
                    }
                }

            }
        }
        return this
    }

    /**
     * 执行查询,返回list
     */
    fun list(): List<T> {

        methodName?.run { Log.debug("Execute method $methodName ") }
        return db.find(this)
    }

    /**
     * 分页查询, 返回{@link Pagination}对象
     * @param page 页码
     * @param pageSize 每页数量
     */
    fun pageList(page: Int?, pageSize: Int?): Pagination<T> {
        page?.let { this.queryCondition.page = page }
        pageSize?.let { this.queryCondition.pageSize = pageSize }
        return pageList()
    }

    /**
     * 偏移查询,返回{@link Pagination}对象
     * @offset 偏移量
     * @limit 最大数量
     */
    fun offsetList(offset: Int?, limit: Int?): Pagination<T> {
        offset?.let { this.queryCondition.offset = offset }
        limit?.let { this.queryCondition.pageSize = limit }
        return pageList()
    }


    /**
     * 查询,返回{@link Pagination}对象
     */
    fun pageList(): Pagination<T> {

        methodName?.run { Log.debug("Execute method $methodName ") }
        val pagination = Pagination<T>()
        pagination.total = db.getCount(this)
        pagination.pageSize = this.queryCondition.pageSize

        //计算总页数
        if (pagination.pageSize != null) {
            pagination.totalPage = pagination.total / pagination.pageSize
            if (pagination.total % pagination.pageSize != 0) {
                pagination.totalPage++
            }
        } else pagination.totalPage = 1
        //计算当前页
        pagination.current = this.queryCondition.page ?: this.queryCondition.offset?.run {
            when {
                this == 0 -> 1
                pagination.total % this == 0 -> pagination.total / this
                else -> (pagination.total / this) + 1
            }
        } ?: 1
        pagination.startPage = pagination.current

        pagination.list = db.find(this)
        return pagination
    }

    /**
     * 查询一条记录
     */
    fun get(): T? {
        methodName?.run { Log.debug("Execute method $methodName ") }
        return db.getByQuery(this)
    }

    /**
     * 根据条件执行更新
     */
    fun executeUpdate() {
        methodName?.run { Log.debug("Execute method $methodName ") }
        db.update(this)
    }

    /**
     * 根据条件执行删除操作
     */
    fun executeDelete() {
        methodName?.run { Log.debug("Execute method $methodName ") }
        db.delete(this)
    }

    /**
     * 标注方法名字
     */
    fun name(methodName: String?) {
        this.methodName = methodName
    }

    /**
     * 设置偏移量
     */
    fun offset(offset: Int?): Query<T> {
        this.queryCondition.offset = offset
        return this
    }

    companion object {

        /**
         * 根据class创建Query对象
         */
        @JvmStatic
        fun <T> of(componentClass: Class<T>): Query<T> {

            return Query(componentClass)
        }

        /**
         * 底层Api对象
         */
        @JvmStatic
        lateinit var db: DbUltimate

        /**
         * 初始化Db-Ultimate
         */
        @JvmStatic
        fun init(dbConfig: DbConfig) {
            db = DbUltimate(dbConfig)
            if (dbConfig.develop) {
                TableInfo.enableDevelopMode()
            }

            ComponentInfo.init(GroupFunction::class.java)
            dbConfig.scanPackage?.run { ComponentScan.scan(this.split(",")) }
            DBInitialer(dbConfig).initalerTable()

        }

    }


}


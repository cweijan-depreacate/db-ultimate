package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.ComponentScan
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.convert.TypeConvert
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.debug.HotSwapSupport
import github.cweijan.ultimate.generator.GeneratorAdapter
import github.cweijan.ultimate.generator.SqlGenerator
import github.cweijan.ultimate.util.Log
import java.sql.ResultSet

/**
 * 核心Api,用于Crud操作
 */
class DbUltimate(dbConfig: DbConfig) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig)
    private var sqlGenerator: SqlGenerator = GeneratorAdapter(dbConfig).generator

    init {
        if (dbConfig.develop) {
            HotSwapSupport.startHotSwapListener(dbConfig)
        }
        ComponentScan.scan(dbConfig.scanPackage!!.split(","))
        DBInitialer(dbConfig).initalerTable()
    }

    @JvmOverloads
    fun <T> executeSqlOf(sql: String, params: Array<String>? = null, clazz: Class<T>): T? {

        return TypeConvert.resultSetToBean(sqlExecutor.executeSql(sql, params)!!, clazz)

    }

    @JvmOverloads
    fun <T> executeSqlOfList(sql: String, params: Array<String>? = null, clazz: Class<T>): List<T> {

        return TypeConvert.resultSetToBeanList(sqlExecutor.executeSql(sql, params)!!, clazz)
    }

    @JvmOverloads
    fun <T> executeSqlOfMap(sql: String, params: Array<String>? = null): Map<String, Any>? {

        return TypeConvert.resultSetToMap(sqlExecutor.executeSql(sql, params)!!)
    }

    @JvmOverloads
    fun <T> executeSqlOfMapList(sql: String, params: Array<String>? = null): List<Map<String, Any>> {

        return TypeConvert.resultSetToMapList(sqlExecutor.executeSql(sql, params)!!)
    }

    private fun executeSql(sql: String, params: Array<String>? = null): ResultSet? {

        return sqlExecutor.executeSql(sql, params)

    }

    fun <T> getBySql(sql: String, params: Array<String>?, clazz: Class<T>): T? {

        val resultSet = sqlExecutor.executeSql(sql, params)
        val bean = TypeConvert.resultSetToBean(resultSet!!, clazz)
        resultSet.close()
        return bean
    }

    fun <T> getBySql(sql: String, clazz: Class<T>): T? {

        return getBySql(sql, null, clazz)
    }

    fun <T> getCount(operation: Operation<T>): Int {

        val sql = sqlGenerator.generateCountSql(operation.component, operation)

        return getBySql(sql, Int::class.java)!!
    }

    @JvmOverloads
    fun <T> get(operation: Operation<T>, columns: String = ""): T? {

        operation.limit(1)
        operation.setColumn(columns)
        val sql = sqlGenerator.generateSelectSql(operation.component, operation)

        return getBySql(sql, operation.getParams(), operation.componentClass)
    }

    @JvmOverloads
    fun <T : Any> get(component: T, columns: String = ""): T? {

        return get(Operation.build(component), columns)
    }

    @JvmOverloads
    fun <T> getBy(clazz: Class<T>, column: String, value: String, columns: String? = null): T? {

        val operation = Operation.build(clazz)
        columns?.let { operation.setColumn(it) }

        operation.equals(TableInfo.getComponent(clazz).getColumnNameByFieldName(column), value)

        return get(operation)
    }

    fun <T> findBySql(sql: String, params: Array<String>?, clazz: Class<T>): List<T> {

        val resultSet = sqlExecutor.executeSql(sql, params)
        val beanList = TypeConvert.resultSetToBeanList(resultSet!!, clazz)
        resultSet.close()
        return beanList
    }

    fun <T> findBySql(sql: String, clazz: Class<T>): List<T> {

        return findBySql(sql, null, clazz)
    }

    @JvmOverloads
    fun <T> find(clazz: Class<T>, page: Int = 1, pageSize: Int = 0, columns: String = ""): List<T> {

        return find(Operation.build(clazz), page, pageSize, columns)
    }

    @JvmOverloads
    fun <T : Any> find(component: T, page: Int = 1, pageSize: Int = 100, columns: String = ""): List<T> {

        return find(Operation.build(component), page, pageSize, columns)
    }

    @JvmOverloads
    fun <T : Any> findByObject(clazz: Class<T>, paramObject: Any, page: Int = 1, pageSize: Int = 100, columns: String = ""): List<T> {

        val operation = Operation.build(clazz)
        if (paramObject is Map<*, *>) {
            paramObject.forEach { key, value ->
                if(value==null){
                    return@forEach
                }
                operation.equals(key.toString(), TypeAdapter.convertFieldValue(value))
            }
        } else {
            for (declaredField in paramObject::class.java.declaredFields) {
                declaredField.isAccessible = true
                val fieldValue = declaredField.get(paramObject) ?: continue
                operation.equals(declaredField.name, TypeAdapter.convertFieldValue(fieldValue))
            }
        }

        return find(operation, page, pageSize, columns)
    }

    @JvmOverloads
    fun <T> find(operation: Operation<T>, page: Int = 1, pageSize: Int = 100, columns: String = ""): List<T> {

        if (pageSize != 0) {
            val start = if (page <= 0) 0 else (page - 1) * pageSize
            operation.start(start)
            operation.limit(pageSize)
        }
        operation.setColumn(columns)
        val sql = sqlGenerator.generateSelectSql(operation.component, operation)
        return findBySql(sql, operation.getParams(), operation.componentClass)
    }

    @JvmOverloads
    fun <T> findBy(clazz: Class<T>, column: String, value: String, columns: String? = null): List<T> {

        val operation = Operation.build(clazz)
        columns?.let { operation.setColumn(it) }

        operation.equals(TableInfo.getComponent(clazz).getColumnNameByFieldName(column), value)

        return find(operation)
    }

    /**
     * 插入对象,只插入非空属性
     *
     * @param component 实体对象e
     */
    fun insert(component: Any) {

        val sql = sqlGenerator.generateInsertSql(component)
        executeSql(sql)
    }

    fun insertList(componentList: List<Any>) {

        for (t in componentList) {
            insert(t)
        }
    }

    fun insertOfUpdate(component: Any) {
        val componentInfo = TableInfo.getComponent(component.javaClass)
        val sql = if (componentInfo.getPrimaryValue(component) == null) {
            sqlGenerator.generateInsertSql(component)
        } else {
            sqlGenerator.generateUpdateSql(component)
        }
        executeSql(sql)
    }

    fun <T> delete(operation: Operation<T>) {

        val sql = sqlGenerator.generateDeleteSql(operation.component, operation)
        executeSql(sql, operation.getParams())
    }

    fun update(component: Any) {

        try {
            val sql = sqlGenerator.generateUpdateSql(component)
            executeSql(sql)
        } catch (e: IllegalAccessException) {
            logger.error(e.message, e)
        }

    }

    fun <T> update(operation: Operation<T>) {

        val sql = sqlGenerator.generateUpdateSql(operation.component, operation)
        executeSql(sql, operation.getParams())
    }

    companion object {

        private val logger = Log.logger
    }
}
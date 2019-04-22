package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.ComponentScan
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.convert.TypeConvert
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.debug.HotSwapSupport
import github.cweijan.ultimate.exception.TooManyResultException
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
        Query.core = this
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

    @JvmOverloads
    fun <T> getBySql(sql: String, params: Array<String>? = null, clazz: Class<T>): T? {

        val resultSet = sqlExecutor.executeSql(sql, params)!!
        resultSet.last()
        val rowCount = resultSet.row
        resultSet.beforeFirst()
        if (rowCount > 1) {
            resultSet.close()
            throw TooManyResultException("Expect 1 result,but fond $rowCount")
        } else {
            val bean = TypeConvert.resultSetToBean(resultSet, clazz)
            resultSet.close()
            return bean
        }
    }

    fun <T> getCount(query: Query<T>): Int {

        val sql = sqlGenerator.generateCountSql(query.component, query)

        return getBySql(sql, null, Int::class.java)!!
    }

    fun <T> getByQuery(query: Query<T>): T? {

        val sql = sqlGenerator.generateSelectSql(query.component, query)

        return getBySql(sql, query.getParams(), query.componentClass)
    }

    fun <T> getByPrimaryKey(clazz: Class<T>, value: String): T? {

        return getByQuery(Query.of(clazz).eq(TableInfo.getComponent(clazz).primaryKey!!, value))
    }

    @JvmOverloads
    fun <T> findBySql(sql: String, params: Array<String>? = null, clazz: Class<T>): List<T> {

        val resultSet = sqlExecutor.executeSql(sql, params)
        val beanList = TypeConvert.resultSetToBeanList(resultSet!!, clazz)
        resultSet.close()
        return beanList
    }

    fun <T> find(query: Query<T>): List<T> {

        if (query.page != null && query.pageSize != 0) {
            val start = if (query.page!! <= 0) 0 else (query.page!! - 1) * (query.pageSize ?: 100)
            query.offset(start)
        }

        val sql = sqlGenerator.generateSelectSql(query.component, query)
        return findBySql(sql, query.getParams(), query.componentClass)
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

    fun <T> batchDelete(query: Query<T>, privateKeyList: List<Any>) {
        privateKeyList.forEach { privateKey -> query.eq(query.component.primaryKey!!, privateKey) }
    }

    fun <T> batchDelete(query: Query<T>, privateKeys: Array<Any>) {
        privateKeys.forEach { privateKey -> query.eq(query.component.primaryKey!!, privateKey) }
    }

    fun <T> delete(query: Query<T>) {

        val sql = sqlGenerator.generateDeleteSql(query.component, query)
        executeSql(sql, query.getParams())
    }

    fun update(component: Any) {

        try {
            val sql = sqlGenerator.generateUpdateSql(component)
            executeSql(sql)
        } catch (e: IllegalAccessException) {
            Log.error(e.message, e)
        }

    }

    fun <T> update(query: Query<T>) {

        val sql = sqlGenerator.generateUpdateSql(query.component, query)
        executeSql(sql, query.getParams())
    }

}
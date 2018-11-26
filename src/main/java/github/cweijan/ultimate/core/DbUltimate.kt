package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.ComponentScan
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.convert.TypeConvert
import github.cweijan.ultimate.db.DBInitialer
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.generator.GeneratorAdapter
import github.cweijan.ultimate.generator.SqlGenerator
import github.cweijan.ultimate.util.Log
import java.sql.ResultSet

/**
 * 核心Api,用于Crud操作
 */
class DbUltimate(private val dbConfig: DbConfig) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig)
    private val sqlGenerator: SqlGenerator? = GeneratorAdapter(dbConfig).generator

    init {
        DBInitialer(dbConfig).initalerTable()
        ComponentScan.scan(dbConfig.scanPackage!!)
    }

    fun executeSql(sql: String, params: Array<String>? = null): ResultSet? {

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

    fun <T> findBySql(sql: String, params: Array<String>?, clazz: Class<T>): List<T> {

        val resultSet = sqlExecutor.executeSql(sql, params)
        val beanList = TypeConvert.resultSetToBeanList(resultSet!!, clazz)
        resultSet.close()
        return beanList
    }

    fun <T> findBySql(sql: String, clazz: Class<T>): List<T> {

        return findBySql(sql, null, clazz)
    }

    operator fun <T> get(operation: Operation, clazz: Class<T>): T? {

        var sql = sqlGenerator!!.generateSelectSql(TableInfo.getComponent(clazz), operation)
        sql += " limit 1"
        return getBySql(sql, operation.getParams(), clazz)
    }

    fun <T> getByPrimaryKey(primary: Any, clazz: Class<T>): T? {

        val operation = Operation()
        operation.equals("id", primary)

        return get(operation, clazz)
    }

    fun <T> find(operation: Operation, clazz: Class<T>): List<T> {

        val sql = sqlGenerator!!.generateSelectSql(TableInfo.getComponent(clazz), operation)
        return findBySql(sql, operation.getParams(), clazz)
    }

    /**
     * 插入对象,属性为空则插入null值
     *
     * @param component 实体对象e
     */
    fun insert(component: Any) {

        val sql = sqlGenerator!!.generateInsertSql(component)
        executeSql(sql)
    }

    fun insertList(list: List<Any>) {

        for (t in list) {
            insert(t)
        }
    }

    fun delete(operation: Operation, clazz: Class<*>) {

        val sql = sqlGenerator!!.generateDeleteSql(TableInfo.getComponent(clazz), operation)
        executeSql(sql, operation.getParams())
    }

    fun update(component: Any) {

        try {
            val sql = sqlGenerator!!.generateUpdateSql(component)
            executeSql(sql)
        } catch (e: IllegalAccessException) {
            logger.error(e.message, e)
        }

    }

    fun update(operation: Operation, clazz: Class<*>) {

        val sql = sqlGenerator!!.generateUpdateSql(TableInfo.getComponent(clazz), operation)
        executeSql(sql, operation.getParams())
    }

    companion object {

        private val logger = Log.logger
    }
}
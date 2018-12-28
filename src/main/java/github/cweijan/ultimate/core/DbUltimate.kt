package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.ComponentScan
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.convert.TypeConvert
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.debug.HotSwapSupport
import github.cweijan.ultimate.generator.GeneratorAdapter
import github.cweijan.ultimate.generator.SqlGenerator
import github.cweijan.ultimate.util.Log
import java.sql.ResultSet
import java.util.stream.IntStream

/**
 * 核心Api,用于Crud操作
 */
class DbUltimate(dbConfig: DbConfig) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig)
    private var sqlGenerator: SqlGenerator = GeneratorAdapter(dbConfig).generator

    init {
        if(dbConfig.develop){
            HotSwapSupport.startHotSwapListener()
        }
        ComponentScan.scan(dbConfig.scanPackage!!)
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

        val sql = sqlGenerator.generateCountSql(TableInfo.getComponent(operation.componentClass), operation)

        return getBySql(sql, Int::class.java)!!
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

    operator fun <T> get(operation: Operation<T>): T? {

        operation.limit(1)
        val sql = sqlGenerator.generateSelectSql(TableInfo.getComponent(operation.componentClass), operation)

        return getBySql(sql, operation.getParams(), operation.componentClass)
    }

    fun <T> getByPrimaryKey(primary: Any, clazz: Class<T>): T? {

        val operation = Operation.build(clazz)
        operation.equals("id", primary)

        return get(operation)
    }

    fun <T> find(operation: Operation<T>): List<T> {

        val sql = sqlGenerator.generateSelectSql(TableInfo.getComponent(operation.componentClass), operation)
        return findBySql(sql, operation.getParams(), operation.componentClass)
    }

    @JvmOverloads
    fun <T> findBy(clazz: Class<T>, column: String, value: String, columns: String = ""): List<T> {

        return findBy(clazz, arrayOf(column), arrayOf(value), columns)
    }

    @JvmOverloads
    fun <T> findBy(clazz: Class<T>, columnArray: Array<String>, valueArray: Array<String>, columns: String = ""): List<T> {

        if (columnArray.size != valueArray.size) {
            throw IllegalArgumentException("the columnArray and valueArray params length must same size!")
        }
        val operation = Operation.build(clazz)
        if (columns != "") operation.setColumn(columns)

        IntStream.range(0, columnArray.size).forEach { index ->
            operation.equals(columnArray[index], valueArray[index])
        }

        return find(operation)
    }

    @JvmOverloads
    fun <T> getBy(clazz: Class<T>, column: String, value: String, columns: String = ""): T? {

        return getBy(clazz, arrayOf(column), arrayOf(value), columns)
    }

    @JvmOverloads
    fun <T> getBy(clazz: Class<T>, columnArray: Array<String>, valueArray: Array<String>, columns: String = ""): T? {

        if (columnArray.size != valueArray.size) {
            throw IllegalArgumentException("the columnArray and valueArray params length must same size!")
        }
        val operation = Operation.build(clazz)
        if (columns != "") operation.setColumn(columns)

        IntStream.range(0, columnArray.size).forEach { index ->
            operation.equals(columnArray[index], valueArray[index])
        }
        return get(operation)
    }

    /**
     * 插入对象,属性为空则插入null值
     *
     * @param component 实体对象e
     */
    fun insert(component: Any) {

        val sql = sqlGenerator.generateInsertSql(component)
        executeSql(sql)
    }

    fun insertList(list: List<Any>) {

        for (t in list) {
            insert(t)
        }
    }

    fun <T> delete(operation: Operation<T>) {

        val sql = sqlGenerator.generateDeleteSql(TableInfo.getComponent(operation.componentClass), operation)
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

        val sql = sqlGenerator.generateUpdateSql(TableInfo.getComponent(operation.componentClass), operation)
        executeSql(sql, operation.getParams())
    }

    companion object {

        private val logger = Log.logger
    }
}
package github.cweijan.ultimate.core

import github.cweijan.ultimate.component.ComponentScan
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeConvert
import github.cweijan.ultimate.core.extra.ExtraData
import github.cweijan.ultimate.core.extra.ExtraDataService
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
    var sqlGenerator: SqlGenerator = GeneratorAdapter.getSqlGenerator(dbConfig.driver)

    init {
        if (dbConfig.develop) {
            HotSwapSupport.startHotSwapListener(dbConfig)
        }
        val extraData = ComponentInfo.init(ExtraData::class.java)
        dbConfig.scanPackage?.run { ComponentScan.scan(this.split(",")) }
        val dbInitialer = DBInitialer(dbConfig)
        dbInitialer.initalerTable()
        dbInitialer.createTable(extraData)
        Query.db = this
    }

    @JvmOverloads
    fun <T> executeSqlOf(sql: String, params: Array<Any>? = null, clazz: Class<T>): T? {

        return TypeConvert.resultSetToBean(sqlExecutor.executeSql(sql, params)!!, clazz)

    }

    @JvmOverloads
    fun <T> executeSqlOfList(sql: String, params: Array<Any>? = null, clazz: Class<T>): List<T> {

        return TypeConvert.resultSetToBeanList(sqlExecutor.executeSql(sql, params)!!, clazz)
    }

    @JvmOverloads
    fun executeSqlOfMap(sql: String, params: Array<Any>? = null): Map<String, Any>? {

        return TypeConvert.resultSetToMap(sqlExecutor.executeSql(sql, params)!!)
    }

    @JvmOverloads
    fun executeSqlOfMapList(sql: String, params: Array<Any>? = null): List<Map<String, Any>> {

        return TypeConvert.resultSetToMapList(sqlExecutor.executeSql(sql, params)!!)
    }

    fun executeSql(sql: String, params: Array<Any>? = null): ResultSet? {

        return sqlExecutor.executeSql(sql, params)

    }

    @JvmOverloads
    fun <T> getBySql(sql: String, params: Array<Any>? = null, clazz: Class<T>): T? {

        val resultSet = sqlExecutor.executeSql(sql, params)!!
        resultSet.last()
        val rowCount = resultSet.row
        resultSet.beforeFirst()
        if (rowCount > 1) {
            throw TooManyResultException("Expect 1 result,but fond $rowCount")
        } else {
            return TypeConvert.resultSetToBean(resultSet, clazz)
        }
    }

    fun <T> getCount(query: Query<T>): Int {

        val sql = sqlGenerator.generateCountSql(query)

        return getBySql(sql, query.consumeParams(), Int::class.java)!!
    }

    fun <T> getByQuery(query: Query<T>): T? {

        val sql = sqlGenerator.generateSelectSql(query)

        return getBySql(sql, query.consumeParams(), query.componentClass)
    }

    fun <T> getByPrimaryKey(clazz: Class<T>, value: Any): T? {

        return getByQuery(Query.of(clazz).eq(TableInfo.getComponent(clazz).primaryKey!!, value))
    }

    fun deleteByPrimaryKey(clazz: Class<*>, value: Any) {
        Query.of(clazz).eq(TableInfo.getComponent(clazz).primaryKey!!, value).executeDelete();
    }

    fun <T> find(query: Query<T>): List<T> {

        val sql = sqlGenerator.generateSelectSql(query)

        val resultSet = sqlExecutor.executeSql(sql, query.consumeParams())

        return TypeConvert.resultSetToBeanList(resultSet!!, query.componentClass)
    }

    /**
     * 保存或更新附加对象
     */
    fun saveExtra(key: Any, extraObject: Any) {
        ExtraDataService.save(key,extraObject)
    }

    /**
     * 获取附加对象
     */
    fun <T> getExtra(key: Any, extraType: Class<T>): T? {

        return ExtraDataService.getExtraData(key,extraType)
    }

    /**
     * 设置附加对象过期时间
     */
    @JvmOverloads
    fun expireExtra(key: Any, extraType: Class<*>,minute:Int=0){
        ExtraDataService.expireExtraData(key,extraType,minute)
    }

    /**
     * 插入对象,只插入非空属性
     *
     * @param component 实体对象e
     */
    fun insert(component: Any) {

        val sqlObject = sqlGenerator.generateInsertSql(component)
        val executeSql = executeSql(sqlObject.sql,sqlObject.params.toTypedArray())
        if(executeSql?.next()==true){
            TableInfo.getComponent(component.javaClass).setPrimaryValue(component, executeSql.getInt(1))
        }

    }

    fun insertList(componentList: List<Any>) {

        for (t in componentList) {
            insert(t)
        }
    }

    fun insertOfUpdate(component: Any) {
        val componentInfo = TableInfo.getComponent(component.javaClass)
        if (componentInfo.getPrimaryValue(component) == null) {
            insert(component)
        } else {
            update(component)
        }
    }

    fun <T> batchDelete(query: Query<T>, privateKeyList: List<Any>) {
        privateKeyList.forEach { privateKey -> query.eq(query.component.primaryKey!!, privateKey).executeDelete() }
    }

    fun <T> batchDelete(query: Query<T>, privateKeys: Array<Any>) {
        privateKeys.forEach { privateKey -> query.eq(query.component.primaryKey!!, privateKey).executeDelete() }
    }

    fun <T> delete(query: Query<T>) {

        val sql = sqlGenerator.generateDeleteSql(query)
        executeSql(sql, query.consumeParams())
    }

    fun update(component: Any) {

        try {
            val sqlObject = sqlGenerator.generateUpdateSqlByObject(component)
            executeSql(sqlObject.sql,sqlObject.params.toTypedArray())
        } catch (e: IllegalAccessException) {
            Log.error(e.message, e)
        }

    }

    fun <T> update(query: Query<T>) {

        val sql = sqlGenerator.generateUpdateSqlByObject(query)
        executeSql(sql, query.consumeParams())
    }

}
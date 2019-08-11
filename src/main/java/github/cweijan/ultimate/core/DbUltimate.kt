package github.cweijan.ultimate.core

import github.cweijan.ultimate.convert.TypeConvert
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.dialect.DialectAdapter
import github.cweijan.ultimate.core.dialect.SqlDialect
import github.cweijan.ultimate.core.extra.ExtraDataService
import github.cweijan.ultimate.core.extra.GroupFunction
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.Log
import java.sql.ResultSet

/**
 * 核心Api,用于Crud操作
 */
class DbUltimate internal constructor(var dbConfig: DbConfig) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig)
    var sqlGenerator: SqlDialect = DialectAdapter.getSqlGenerator(dbConfig.getDatabaseType())

    @JvmOverloads
    fun <T> findBySql(sql: String, params: Array<Any>? = null, clazz: Class<T>): List<T> {

        return TypeConvert.resultSetToBeanList(sqlExecutor.executeSql(sql, params)!!, clazz)
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
//            throw TooManyResultException("Expect 1 result,but fond $rowCount")
            Log.getLogger().warn("TooManyResultWarn: Get expect 1 result,but fond $rowCount")
        }
        return TypeConvert.resultSetToBean(resultSet, clazz)
    }

    fun <T> getCount(query: Query<T>): Int {

        val sql = sqlGenerator.generateCountSql(query)

        val toInt = getBySql(sql, query.consumeParams(), GroupFunction::class.java)!!.count.toInt()
        dbConfig.tryCloseConnection()
        return toInt
    }

    fun <T> getByQuery(query: Query<T>): T? {

        val sql = sqlGenerator.generateSelectSql(query)

        val bySql = getBySql(sql, query.consumeParams(), query.componentClass)
        dbConfig.tryCloseConnection()
        return bySql
    }

    fun <T> getByPrimaryKey(clazz: Class<T>, value: Any?): T? {
        if(value==null)return null
        return getByQuery(Query.of(clazz).eq(TableInfo.getComponent(clazz).primaryKey!!, value))
    }

    fun deleteByPrimaryKey(clazz: Class<*>, value: Any) {
        Query.of(clazz).eq(TableInfo.getComponent(clazz).primaryKey!!, value).executeDelete();
        dbConfig.tryCloseConnection()
    }

    fun <T> find(query: Query<T>): List<T> {

        val sql = sqlGenerator.generateSelectSql(query)

        val resultSet = sqlExecutor.executeSql(sql, query.consumeParams())

        val resultSetToBeanList = TypeConvert.resultSetToBeanList(resultSet!!, query.componentClass)
        dbConfig.tryCloseConnection()
        return resultSetToBeanList
    }

    /**
     * 保存或更新附加对象
     */
    fun saveExtra(key: Any, extraObject: Any) {
        ExtraDataService.save(key, extraObject)
    }

    /**
     * 获取附加对象
     */
    fun <T> getExtra(key: Any, extraType: Class<T>): T? {

        return ExtraDataService.getExtraData(key, extraType)
    }

    /**
     * 插入对象,只插入非空属性
     *
     * @param component 实体对象e
     */
    fun insert(component: Any) {

        val sqlObject = sqlGenerator.generateInsertSql(component)
        val executeSql = executeSql(sqlObject.sql, sqlObject.params.toTypedArray())
        if (executeSql?.next() == true) {
            TableInfo.getComponent(component.javaClass).setPrimaryValue(component, executeSql.getInt(1))
        }
        dbConfig.tryCloseConnection()

    }

    /**
     * 插入对象,如果对象已经存在，则不插入
     *
     * @param component 实体对象
     */
    fun ignoreInsert(component: Any) {

        val primaryValue = TableInfo.getComponent(component::class.java).getPrimaryValue(component)
        val byPrimaryKey = getByPrimaryKey(component::class.java,primaryValue)
        if (byPrimaryKey != null) return
        val byData = Query.of(component::class.java).read(component).get()
        if (byData != null) return
        insert(component)
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
        dbConfig.tryCloseConnection()
    }

    fun <T> batchDelete(query: Query<T>, privateKeys: Array<Any>) {
        privateKeys.forEach { privateKey -> query.eq(query.component.primaryKey!!, privateKey).executeDelete() }
        dbConfig.tryCloseConnection()
    }

    fun <T> delete(query: Query<T>) {

        val sql = sqlGenerator.generateDeleteSql(query)
        executeSql(sql, query.consumeParams())
        dbConfig.tryCloseConnection()
    }

    fun update(component: Any) {

        updateBy(null,component)

    }

    fun updateBy(columnName:String?,component: Any) {

        try {
            val sqlObject = sqlGenerator.generateUpdateSqlByObject(component,columnName)
            executeSql(sqlObject.sql, sqlObject.params.toTypedArray())
            dbConfig.tryCloseConnection()
        } catch (e: IllegalAccessException) {
            Log.error(e.message, e)
        }

    }

    fun <T> update(query: Query<T>) {

        val sql = sqlGenerator.generateUpdateSqlByQuery(query)
        executeSql(sql, query.consumeParams())
        dbConfig.tryCloseConnection()
    }

}
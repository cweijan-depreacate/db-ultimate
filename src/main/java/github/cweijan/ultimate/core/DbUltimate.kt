package github.cweijan.ultimate.core

import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.convert.TypeConvert
import github.cweijan.ultimate.core.component.ComponentScan
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.dialect.DialectAdapter
import github.cweijan.ultimate.core.dialect.SqlDialect
import github.cweijan.ultimate.core.extra.ExtraDataService
import github.cweijan.ultimate.core.result.ResultInfo
import github.cweijan.ultimate.db.HikariDataSourceAdapter
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.springboot.util.ServiceMap
import github.cweijan.ultimate.util.Log
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * 核心Api,用于Crud操作
 */
class DbUltimate private constructor(dbConfig: DbConfig, val dataSource: DataSource) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig, dataSource)
    var sqlGenerator: SqlDialect = DialectAdapter.getSqlGenerator(dbConfig.getDatabaseType())

    @JvmOverloads
    fun <T> findBySql(sql: String, params: Array<Any>? = null, clazz: Class<T>): List<T> {

        val beanList = sqlExecutor.executeSql(sql, params) { resultSet, _ -> TypeConvert.resultSetToBeanList(resultSet, clazz) }!!
        beanList.forEach { bean ->
            handlerRelation(bean)
        }
        return beanList
    }

    fun <T> executeSql(sql: String, clazz: Class<T>, vararg params: Any?): List<T>? {

        return sqlExecutor.executeSql(sql, params) { resultset, _ -> TypeConvert.resultSetToBeanList(resultset, clazz) }

    }

    fun <T> executeSql(sql: String, vararg params: Any?): ResultInfo? {

        return sqlExecutor.executeSql(sql, params) { _, resultInfo -> resultInfo }

    }

    @JvmOverloads
    fun <T> getBySql(sql: String, params: Array<Any>? = null, clazz: Class<T>): T? {

        val resultSetToBeanList = sqlExecutor.executeSql(sql, params) { resultSet, _ -> TypeConvert.resultSetToBeanList(resultSet, clazz) }!!
        if (resultSetToBeanList.isEmpty()) return null;
        if (resultSetToBeanList.size > 1) {
            Log.getLogger().warn("TooManyResultWarn: Get expect 1 result,but fond ${resultSetToBeanList.size}")
        }
        val bean = resultSetToBeanList[0]

        handlerRelation(bean)
        return bean
    }

    /**
     * 一对多赋值
     */
    private fun handlerRelation(bean: Any?) {
        bean ?: return
        if (TypeAdapter.isAdapterType(bean.javaClass)) return
        val component = TableInfo.getComponent(bean.javaClass)
        //一对多赋值
        if (component.oneToManyLazy.isInitialized()) {
            component.oneToManyList.forEach { oneToManyInfo ->
                oneToManyInfo.oneTomanyField.set(bean, ServiceMap.get(oneToManyInfo.relationClass)
                        .query.eq(oneToManyInfo.relationColumn, component.getValueByFieldName(bean, component.primaryField!!.name))
                        .where(oneToManyInfo.where)
                        .list())
            }
        }
        // 一对一赋值
        if (component.oneToOneLazy.isInitialized()) {
            component.oneToOneList.forEach { oneToOneInfo ->
                oneToOneInfo.oneToOneField.set(bean, ServiceMap.get(oneToOneInfo.relationClass)
                        .getBy(oneToOneInfo.relationColumn, component.getValueByFieldName(bean, component.primaryField!!.name)))
            }
        }
    }

    fun <T> getCount(query: Query<T>): Int {

        val sql = sqlGenerator.generateCountSql(query)

        return getBySql(sql, query.queryCondition.consumeParams(), Int::class.java)!!
    }

    fun <T> getByQuery(query: Query<T>): T? {

        val sql = sqlGenerator.generateSelectSql(query)

        return getBySql(sql, query.queryCondition.consumeParams(), query.componentClass)
    }

    fun <T> getByPrimaryKey(clazz: Class<T>, value: Any?): T? {
        if (value == null) return null
        return getByQuery(Query.of(clazz).eq(TableInfo.getComponent(clazz).primaryKey!!, value))
    }

    /**
     * 根据主键进行删除
     */
    fun deleteByPrimaryKey(clazz: Class<*>, value: Any): Int? {
        return this.delete(Query.of(clazz).eq(TableInfo.getComponent(clazz).primaryKey!!, value))
    }

    /**
     * 根据主键数组进行批量删除
     */
    fun deleteByPrimaryKeyList(clazz: Class<*>, value: Array<Any>): Int? {
        return this.delete(Query.of(clazz).`in`(TableInfo.getComponent(clazz).primaryKey!!, value.toMutableList()))
    }

    /**
     * 根据主键list进行批量删除
     */
    fun deleteByPrimaryKeyList(clazz: Class<*>, value: List<Any>) {
        this.delete(Query.of(clazz).`in`(TableInfo.getComponent(clazz).primaryKey!!, value))
    }

    /**
     *  使用query对象进行查询
     */
    fun <T> find(query: Query<T>): List<T> {
        val sql = sqlGenerator.generateSelectSql(query)
        return findBySql(sql, query.queryCondition.consumeParams(), query.componentClass)
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
    fun insert(component: Any): Int? {

        val sqlObject = sqlGenerator.generateInsertSql(component)
        return sqlExecutor.executeSql(sqlObject.sql, sqlObject.params.toTypedArray()) { _: ResultSet?, resultInfo: ResultInfo ->
            if (resultInfo.generateKey != null)
                TableInfo.getComponent(component.javaClass).setPrimaryValue(component, resultInfo.generateKey)
            resultInfo.updateLine
        }

    }

    /**
     * 插入对象,如果对象已经存在，则不插入
     *
     * @param component 实体对象
     */
    fun ignoreInsert(component: Any): Int? {

        val primaryValue = TableInfo.getComponent(component::class.java).getPrimaryValue(component)
        val byPrimaryKey = getByPrimaryKey(component::class.java, primaryValue)
        if (byPrimaryKey != null) return 0
        val byData = Query.of(component::class.java).read(component).get()
        if (byData != null) return 0
        return insert(component)
    }


    /**
     * 批量插入
     */
    fun insertList(componentList: List<Any>): Int {

        var updateLine: Int = 0
        for (t in componentList) {
            insert(t)?.let { updateLine += it; }
        }
        return updateLine;
    }

    /**
     * 如果主键为空,则进行insert, 不为空进行update
     */
    fun insertOfUpdate(component: Any): Int? {
        val componentInfo = TableInfo.getComponent(component.javaClass)
        return if (componentInfo.getPrimaryValue(component) == null) {
            insert(component)
        } else {
            update(component)
        }
    }

    /**
     * 使用query对象进行删除
     */
    fun <T> delete(query: Query<T>): Int? {

        val sql = sqlGenerator.generateDeleteSql(query)
        return sqlExecutor.executeSql(sql, query.queryCondition.consumeParams()) { _, info -> info.updateLine }
    }

    fun update(component: Any): Int? {

        return updateBy(null, component)
    }

    fun updateBy(columnName: String?, component: Any): Int? {

        val sqlObject = sqlGenerator.generateUpdateSqlByObject(component, columnName)
        return sqlExecutor.executeSql(sqlObject.sql, sqlObject.params.toTypedArray()) { _, info -> info.updateLine }

    }

    fun <T> update(query: Query<T>): Int? {

        val sql = sqlGenerator.generateUpdateSqlByQuery(query)
        return sqlExecutor.executeSql(sql, query.queryCondition.consumeParams()) { _, info -> info.updateLine }
    }

    companion object {
        /**
         * 初始化Db-Ultimate
         */
        @JvmStatic
        fun init(dbConfig: DbConfig, dataSource: DataSource): DbUltimate {
            val dbUltimate = DbUltimate(dbConfig, dataSource)
            Query.db = dbUltimate
            TableInfo.enableDevelopMode(dbConfig.develop)
            dbConfig.scanPackage?.run { ComponentScan.scan(this.split(",")) }
            DBInitialer(dbConfig, dbUltimate.dataSource).initializeTable()
            return dbUltimate
        }

        /**
         * 初始化Db-Ultimate
         */
        @JvmStatic
        fun init(dbConfig: DbConfig): DbUltimate {
            return init(dbConfig, HikariDataSourceAdapter(dbConfig).dataSource)
        }
    }


}
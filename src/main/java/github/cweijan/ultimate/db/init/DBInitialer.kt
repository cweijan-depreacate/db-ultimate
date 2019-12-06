package github.cweijan.ultimate.db.init

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.annotation.Table
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.core.extra.ExtraData
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.generator.TableAutoMode
import github.cweijan.ultimate.db.init.generator.TableInitSqlGenerator
import github.cweijan.ultimate.db.init.generator.TableStruct
import github.cweijan.ultimate.db.init.generator.impl.mysql.MysqlTableStruct
import org.springframework.jdbc.datasource.DataSourceUtils
import github.cweijan.ultimate.util.Log
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.stream.Collectors
import javax.sql.DataSource

/**
 * 用于创建实体对应的不存在的数据表
 */
class DBInitialer(private val dbConfig: DbConfig, private val dataSource: DataSource) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig, dataSource)
    private var connection: Connection = DataSourceUtils.getConnection(dataSource)
    private var initSqlGenetator: TableInitSqlGenerator = GeneratorAdapter.getInitGenerator(dbConfig.databaseType)

    private fun getConnection(): Connection {
        if (connection.isClosed) connection = DataSourceUtils.getConnection(dataSource)
        return connection
    }

    /**
     * 创建Bean所对应的表
     */
    fun initializeTable() {

        val extraData = ComponentInfo.init(ExtraData::class.java)
        createTable(extraData)

        val excludeList = listOf(MysqlTableStruct::class.java, ExtraData::class.java)
        val component = TableInfo.componentList.stream().filter { componentInfo ->
            !excludeList.contains(componentInfo.componentClass) && componentInfo.componentClass.getAnnotation(Table::class.java) != null
        }.collect(Collectors.toList())
        when (dbConfig.tableMode) {
            TableAutoMode.init -> {
                component.forEach { componentInfo -> recreateTable(componentInfo) }
            }
            TableAutoMode.update -> {
                initSqlGenetator.initStruct()
                component.forEach { componentInfo -> updateTable(componentInfo) }
            }
            else -> return
        }

    }

    fun recreateTable(componentInfo: ComponentInfo?) {
        if (componentInfo == null || tableExists(componentInfo.tableName)) return
        if (tableExists(componentInfo.tableName)) {
            initSqlGenetator.dropTable(componentInfo.tableName)?.let { sqlExecutor.executeSql(it) { _, _ -> } }
        }
        createTable(componentInfo)
    }


    fun createTable(componentInfo: ComponentInfo?) {

        if (componentInfo == null || tableExists(componentInfo.tableName)) return

        if (componentInfo.nonExistsColumn()) {
            Log.debug("${componentInfo.componentClass.name} dont have any columns, skip init table ")
            return
        }

        val sql = initSqlGenetator.createTable(componentInfo)

        try {
            sql ?: return
            sqlExecutor.executeSql(sql){ _, _ -> }
        } catch (e: Exception) {
            Log.error("Create table ${componentInfo.tableName} error!", e)
            return
        }
        Log.info("Auto create component table ${componentInfo.tableName}")
    }

    /**
     * 检测表是否存在
     *
     * @param tableName 表名
     */
    fun tableExists(tableName: String): Boolean {

        try {
            return getConnection().metaData.getTables(null, null, tableName, null).next()
        } catch (e: SQLException) {
            Log.error(e.message, e)
        }

        return true
    }

    /**
     * 更新表结构
     */
    private fun updateTable(componentInfo: ComponentInfo?) {

        if (componentInfo == null) return
        if (!tableExists(componentInfo.tableName)) {
            createTable(componentInfo)
            return
        }

        if (componentInfo.nonExistsColumn()) {
            Log.debug("${componentInfo.componentClass.name} dont have any columns, skip init table ")
            return
        }
        val updateSqlList = HashSet<String>()
        val structList = TableStruct.getTableStruct(dbConfig.getDatabaseType(), getConnection().catalog, componentInfo.tableName)
        structList.forEach { struct ->
            val tempColumnInfo = componentInfo.getColumnInfoByColumnName(struct.columnName)
            if (tempColumnInfo == null) {
                updateSqlList.add("ALTER TABLE ${componentInfo.tableName} DROP COLUMN ${struct.columnName};")
            }
        }

        TypeAdapter.getAllField(componentInfo.componentClass).let { fields ->

            fields.forEachIndexed { _, field ->

                if (componentInfo.isExcludeField(field) || !TypeAdapter.isAdapterType(field.type)) {
                    if (field.getAnnotation(Blob::class.java) == null) return@forEachIndexed
                }

                field.isAccessible = true

                val columnInfo = componentInfo.getColumnInfoByFieldName(field.name)!!
                //生成column
                val columnType = initSqlGenetator.getColumnTypeByField(field, columnInfo.length)
                val columnDefination = initSqlGenetator.getColumnDefination(field, componentInfo)

                //如果列不存在,新增
                if (TableStruct.columnNotExists(structList, columnInfo.columnName)) {
                    updateSqlList.add("ALTER TABLE ${componentInfo.tableName} ADD COLUMN $columnDefination;")
                    return@forEachIndexed
                }

                structList.forEach { struct ->
                    if (struct.columnName == columnInfo.columnName) {
                        if (struct.columnIsChanged(columnInfo, columnType)) {
                            updateSqlList.add("ALTER TABLE ${componentInfo.tableName} MODIFY $columnDefination;")
                            return@forEachIndexed
                        }
                    }
                }

            }
        }

        try {
            updateSqlList.forEach { sqlExecutor.executeSql(it){ _, _ -> } }
        } catch (e: Exception) {
            Log.error("Update table ${componentInfo.tableName} error!", e)
            return
        }
        if (updateSqlList.size > 0) {
            Log.info("Update component table ${componentInfo.tableName} success!")
        }

    }

}

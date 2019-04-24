package github.cweijan.ultimate.db.init

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.generator.GeneratorAdapter
import github.cweijan.ultimate.generator.TableInitSqlGenetator
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import org.springframework.transaction.annotation.Transactional
import java.sql.Connection
import java.sql.SQLException

/**
 * 用于创建实体对应的不存在的数据表
 */
class DBInitialer(private val dbConfig: DbConfig) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig)
    private var connection: Connection = dbConfig.getConnection()
    private var initSqlGenetator: TableInitSqlGenetator = GeneratorAdapter.getSqlGenerator(dbConfig.driver) as TableInitSqlGenetator

    /**
     * 创建Bean所对应的表
     */
    fun initalerTable() {

        if (dbConfig.createNonexistsTable) {
            TableInfo.componentList.stream().forEach { componentInfo ->
                createTable(componentInfo)
            }
        }

    }

    fun createTable(componentInfo: ComponentInfo?) {

        if (connection.isClosed) connection = dbConfig.getConnection()

        if (componentInfo == null || tableExists(componentInfo.tableName)) return

        if (componentInfo.nonExistsColumn()) {
            Log.debug("${componentInfo.componentClass.name} dont have any columns, skip create table ")
            return
        }

        var sql = "create table ${componentInfo.tableName}("
        val uniques = ArrayList<String>()

        TypeAdapter.getAllField(componentInfo.componentClass).let { fields ->
            fields.forEachIndexed { index, field ->
                if (componentInfo.isTableExcludeField(field)) {
                    return@forEachIndexed
                }
                field.isAccessible = true
                val columnInfo = componentInfo.getColumnInfoByFieldName(field.name)!!
                var columnDefination = "${columnInfo.columnName} ${initSqlGenetator.getColumnTypeByField(field, columnInfo.length)}"
                //生成主键或者非空片段
                columnDefination += when {
                    componentInfo.primaryKey == field.name -> " PRIMARY KEY "
                    columnInfo.nullable -> ""
                    else -> " NOT NULL "
                }
                if (field.name == componentInfo.primaryKey && columnInfo.autoIncrement) {
                    columnDefination += initSqlGenetator.generateAutoIncrementSqlFragment()
                }
                //生成默认值片段
                columnDefination += when {
                    columnInfo.nullable -> ""
                    field.name == componentInfo.primaryKey -> ""
                    else -> initSqlGenetator.generateDefaultSqlFragment(
                            if (StringUtils.isNotEmpty(columnInfo.defaultValue)) {
                                TypeAdapter.convertToSqlValue(componentInfo.componentClass, field.name, columnInfo.defaultValue!!)
                            } else {
                                TypeAdapter.getDefaultValue(field.type.name)
                            }
                    )
                }
                //生成注释片段
                columnInfo.comment?.let {
                    columnDefination += initSqlGenetator.generateCommentSqlFragment(it)
                }
                if (index != fields.size - 1) {
                    columnDefination += ","
                }
                //生成唯一索引补丁
                if (columnInfo.unique) {
                    uniques.add(initSqlGenetator.generateUniqueSqlFragment(componentInfo.tableName, columnInfo.columnName, columnDefination)!!)
                }

                //拼接sql
                sql += columnDefination
            }
        }

        uniques.forEach { uniqueSql -> sql += ",$uniqueSql" }
        sql += " );"

        try {
            sqlExecutor.executeSql(sql)
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
            return connection.metaData.getTables(null, null, tableName, null).next()
        } catch (e: SQLException) {
            Log.error(e.message, e)
        }

        return true
    }

}

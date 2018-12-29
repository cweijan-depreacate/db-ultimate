package github.cweijan.ultimate.db.init

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.DbUtils
import github.cweijan.ultimate.util.Log

import java.lang.reflect.Field
import java.sql.SQLException
import java.util.*

/**
 * 用于创建实体对应的不存在的数据表
 */
class DBInitialer(private val dbConfig: DbConfig) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig)

    companion object {
        private val logger = Log.logger
    }

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
        if (componentInfo==null || tableExists(componentInfo.tableName)) return

        if(componentInfo.nonExistsColumn()){
            logger.debug("${componentInfo.componentClass.name} dont have any columns, skip create table ")
            return
        }

        var sql = "create table ${componentInfo.tableName}("

        for (field in componentInfo.componentClass.declaredFields) {

            if (componentInfo.isExcludeField(field)) {
                continue
            }
            field.isAccessible = true
            sql += "`${componentInfo.getColumnNameByFieldName(field.name)}` ${getFieldType(field)} NOT NULL "
            sql += if (field.name == componentInfo.primaryKey) {
                " AUTO_INCREMENT "
            } else {
                " DEFAULT ${TypeAdapter.getDefaultValue(field.type.name)} "
            }
            sql += ","
        }

        if (componentInfo.primaryKey != null) sql += "primary key(`${componentInfo.primaryKey}`)"
        sql += " );"

        try {
            sqlExecutor.executeSql(sql)
        } catch (e: Exception) {
            logger.error("create table ${componentInfo.tableName} fail!")
            return
        }
        logger.info("auto create component table ${componentInfo.tableName}")
    }

    /**
     * 检测表是否存在
     *
     * @param tableName 表名
     */
    fun tableExists(tableName: String): Boolean {

        val connection = dbConfig.openConnection()

        try {
            return connection.metaData.getTables(null, null, tableName, null).next()
        } catch (e: SQLException) {
            logger.error(e.message, e)
        } finally {
            DbUtils.closeConnection(connection)
        }

        return true
    }

    private fun getFieldType(field: Field): String? {

        val fieldType = field.type
        if (fieldType == String::class.java) {
            return "varchar(100)"
        }

        if (fieldType == Int::class.java || fieldType == Integer::class.java) {
            return "int"
        }

        if (fieldType == Double::class.java) {
            return "double"
        }

        if (fieldType == Long::class.java || fieldType.name == "long") {
            return "int"
        }

        if (fieldType == Float::class.java) {
            return "float"
        }

        if (fieldType == Date::class.java) {
            return "datetime"
        }

        return if (fieldType.isPrimitive) {
            fieldType.name
        } else "varchar(100)"

    }

}

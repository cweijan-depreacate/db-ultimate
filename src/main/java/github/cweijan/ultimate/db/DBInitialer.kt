package github.cweijan.ultimate.db

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.DbUtils
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils

import java.lang.reflect.Field
import java.security.InvalidParameterException
import java.sql.Connection
import java.sql.SQLException
import java.util.ArrayList
import java.util.Objects
import java.util.Optional

/**
 * 用于创建实体对应的不存在的数据表
 */
class DBInitialer(private val dbConfig: DbConfig) {

    companion object {
        private val logger = Log.logger
    }

    /**
     * 创建Bean所对应的表
     */
    fun initalerTable() {

        if (dbConfig.isCreateNonexistsTable) {
            TableInfo.componentList.stream().filter { componentInfo -> !tableExists(componentInfo.tableName) }.forEach { componentInfo ->
                createTable(componentInfo)
            }
        }

    }

    fun createTable(componentInfo: ComponentInfo) {

        createTable(componentInfo.componentClass, componentInfo.tableName, componentInfo.primaryKey, dbConfig.openConnection())
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

    private fun <T> createTable(clazz: Class<T>, tableName: String, primaryKey: String?, connection: Connection) {

        if (primaryKey == "") {
            throw InvalidParameterException("class ${clazz.name} primary key must exists !")
        }
        DbUtils.checkConnectionAlive(connection)

        var sql = "create table $tableName("

        for (field in clazz.declaredFields) {

            field.isAccessible = true
            sql += "`${field.name}` ${getFieldType(field)} NOT NULL DEFAULT '' "
            if (field.name == primaryKey) {
                sql += " AUTO_INCREMENT"
            }
            sql += ","
        }

        if (primaryKey != "") sql += "primary key(`$primaryKey`)"
        sql += " );"

        SqlExecutor.executeSql(sql, null, dbConfig.openConnection())
    }

    private fun getFieldType(field: Field): String? {

        val fieldType = field.type
        if (fieldType == String::class.java) {
            return "varchar(100)"
        }

        if (fieldType == Int::class.java) {
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

        return if (fieldType.isPrimitive) {
            fieldType.name
        } else null

    }

}

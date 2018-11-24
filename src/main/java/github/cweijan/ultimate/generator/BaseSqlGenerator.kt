package github.cweijan.ultimate.generator

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.Operation
import github.cweijan.ultimate.exception.PrimaryValueNotSetException
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import java.lang.RuntimeException

abstract class BaseSqlGenerator : SqlGenerator {

    override fun generateInsertSql(component: Any, selective: Boolean): String {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        var values = ""
        val fields = componentInfo.componentClass.declaredFields
        var columns = componentInfo.notPrimaryColumns
        var selectiveColumns = ""
        for (field in fields) {
            try {
                field.isAccessible = true
                val fieldValue: Any? = field.get(component)
                if (selective && fieldValue == null || componentInfo.isExcludeField(field)) {
                    continue
                }

                if (selective) {
                    selectiveColumns += "${componentInfo.getColumnNameByFieldName(field.name)},"
                }

                //主键值不为空,说明不用自增主键
                if (fieldValue != null && componentInfo.isPrimaryField(field)) {
                    columns = componentInfo.allColumns
                }

                values += "${TypeAdapter.convertFieldValue(field.type.name, fieldValue)},"
            } catch (e: IllegalAccessException) {
                Log.logger.error(e.message, e)
            }

        }
        if (selective && selectiveColumns.lastIndexOf(",") != -1) {
            columns = selectiveColumns.substring(0, columns.lastIndexOf(","))
        }
        if (values.lastIndexOf(",") != -1) {
            values = values.substring(0, values.lastIndexOf(","))
        }

        return "insert into " + componentInfo.tableName + "(" + columns + ") values(" + values + ");"
    }

    @Throws(IllegalAccessException::class)
    override fun generateUpdateSql(component: Any): String {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        val primaryValue = componentInfo.getPrimaryValue(component)
        primaryValue ?: throw PrimaryValueNotSetException("primary value must set!")
        var sql = "UPDATE ${componentInfo.tableName} a set "

        val fields = component.javaClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            if(componentInfo.isExcludeField(field))continue
            val fieldValue: Any? = field.get(component)
            sql += "${field.name}=${TypeAdapter.convertFieldValue(field.type.name, fieldValue)},"
        }

        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }

        return "$sql where ${componentInfo.primaryKey}='$primaryValue'"
    }

    override fun generateDeleteSql(componentInfo: ComponentInfo, operation: Operation): String {

        return "DELETE FROM ${componentInfo.tableName} ${generateOperationSql(operation)}"
    }

    override fun generateCountSql(componentInfo: ComponentInfo, operation: Operation): String {

        return "select count(*) count from ${componentInfo.tableName} ${generateOperationSql(operation)}"
    }

    override fun generateUpdateSql(componentInfo: ComponentInfo, operation: Operation): String {

        var sql = "UPDATE ${componentInfo.tableName} a set "

        operation.updateList!!.forEach { key, value -> sql += "$key='$value'," }
        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }
        return "$sql ${generateOperationSql(operation)}"
    }

    override fun generateSelectSql(componentInfo: ComponentInfo, operation: Operation): String {

        return "select " + operation.getColumn() + " from " + componentInfo.tableName + generateOperationSql(operation)+generatePaginationSql(operation)
    }

    private fun generateOperationSql(operation: Operation): String {

        val AND = "and"
        val OR = "or"
        var sql = ""

        sql += generateOperationSql0(operation.equalsOperation, "=", AND, operation)
        sql += generateOperationSql0(operation.notEqualsOperation, "!=", AND, operation)
        sql += generateOperationSql0(operation.searchOperation, "like", AND, operation)
        sql += generateOperationSql0(operation.orEqualsOperation, "=", OR, operation)

        if (sql.startsWith(AND)) {
            sql = sql.replaceFirst(AND.toRegex(), "")
            sql = " where$sql"
        }
        if (sql.startsWith(OR)) {
            sql = sql.replaceFirst(OR.toRegex(), "")
            sql = " where$sql"
        }

        if (StringUtils.isNotEmpty(operation.orderBy)) {
            sql += " order by ${operation.orderBy}"
        }

        return sql
    }

    private fun generateOperationSql0(operationMap: Map<String, List<String>>?, condition: String, separator: String, operation: Operation): String {

        val sql = StringBuilder()

        operationMap?.forEach { key, operations ->
            operations.forEach { value ->
                sql.append("$separator $key $condition ? ")
                operation.addParam(value)
            }
        }

        return sql.toString()
    }

    abstract fun generatePaginationSql(operation: Operation):String?

}

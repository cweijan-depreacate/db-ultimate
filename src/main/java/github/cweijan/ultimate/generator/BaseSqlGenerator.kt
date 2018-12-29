package github.cweijan.ultimate.generator

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.Operation
import github.cweijan.ultimate.exception.PrimaryValueNotSetException
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import org.fest.reflect.core.Reflection

//import org.fest.reflect.core.Reflection.*

abstract class BaseSqlGenerator : SqlGenerator {

    override fun generateInsertSql(component: Any): String {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        var columns = ""
        var values = ""
        val fields = componentInfo.componentClass.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true

                val fieldValue: Any? = Reflection.field(field.name).ofType(field.type).`in`(component).get()

                if (fieldValue == null || componentInfo.isExcludeField(field)) {
                    continue
                }
                columns += "${componentInfo.getColumnNameByFieldName(field.name)},"

                values += "${TypeAdapter.convertFieldValue(field.type.name, fieldValue)},"
            } catch (e: IllegalAccessException) {
                Log.logger.error(e.message, e)
            }

        }
        if (columns.lastIndexOf(",") != -1) {
            columns = columns.substring(0, columns.lastIndexOf(","))
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
            val fieldValue: Any? = Reflection.field(field.name).ofType(field.type).`in`(component).get()
            if (fieldValue == null || componentInfo.isExcludeField(field) || componentInfo.isPrimaryField(field)) {
                continue
            }
            sql += "${field.name}=${TypeAdapter.convertFieldValue(field.type.name, fieldValue)},"
        }

        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }

        return "$sql where ${componentInfo.primaryKey}='$primaryValue'"
    }

    override fun <T> generateDeleteSql(componentInfo: ComponentInfo, operation: Operation<T>): String {

        return "DELETE FROM ${componentInfo.tableName} ${generateOperationSql(operation)}"
    }

    override fun <T> generateCountSql(componentInfo: ComponentInfo, operation: Operation<T>): String {

        return "select count(*) count from ${componentInfo.tableName} ${generateOperationSql(operation)}"
    }

    override fun <T> generateUpdateSql(componentInfo: ComponentInfo, operation: Operation<T>): String {

        var sql = "UPDATE ${componentInfo.tableName} a set "

        operation.updateList!!.forEach { key, value ->
            sql += "$key=?,"
            operation.addParam(value)
        }
        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }
        return "$sql ${generateOperationSql(operation)}"
    }

    override fun <T> generateSelectSql(componentInfo: ComponentInfo, operation: Operation<T>): String {

        val column = operation.getColumn() ?: componentInfo.selectColumns

        val sql = "select $column from ${componentInfo.tableName + generateOperationSql(operation)}"
        return generatePaginationSql(sql, operation)
    }

    private fun <T> generateOperationSql(operation: Operation<T>): String {

        val and = "and"
        val or = "or"
        var sql = ""

        sql += operation.alias
        sql += generateJoinTablesSql(operation.joinTables)
        sql += generateOperationSql0(operation.equalsOperation, "=", and, operation)
        sql += generateOperationSql0(operation.notEqualsOperation, "!=", and, operation)
        sql += generateOperationSql0(operation.searchOperation, "like", and, operation)
        sql += generateOperationSql0(operation.orEqualsOperation, "=", or, operation)

        if (sql.startsWith(and)) {
            sql = sql.replaceFirst(and.toRegex(), "")
            sql = " where$sql"
        }
        if (sql.startsWith(or)) {
            sql = sql.replaceFirst(or.toRegex(), "")
            sql = " where$sql"
        }

        if (StringUtils.isNotEmpty(operation.orderBy)) {
            sql += " order by ${operation.orderBy}"
        }

        return sql
    }

    private fun generateJoinTablesSql(joinTables: MutableList<String>?): String {

        val sql = StringBuilder()

        joinTables?.forEach { joinTable ->
            sql.append(joinTable)
        }

        return sql.toString()
    }

    private fun <T> generateOperationSql0(operationMap: Map<String, List<String>>?, condition: String, separator: String, operation: Operation<T>): String {

        val sql = StringBuilder()

        operationMap?.forEach { key, operations ->
            operations.forEach { value ->
                sql.append("$separator $key $condition ? ")
                operation.addParam(value)
            }
        }

        return sql.toString()
    }

    abstract fun <T> generatePaginationSql(sql: String, operation: Operation<T>): String

}

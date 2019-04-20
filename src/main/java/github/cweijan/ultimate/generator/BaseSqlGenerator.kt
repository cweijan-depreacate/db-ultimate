package github.cweijan.ultimate.generator

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.exception.PrimaryValueNotSetException
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils

//import org.fest.reflect.core.Reflection.*

abstract class BaseSqlGenerator : SqlGenerator {

    override fun generateInsertSql(component: Any): String {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        var columns = ""
        var values = ""
        val fields = componentInfo.componentClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            if (componentInfo.isInsertExcludeField(field)) continue
            field.get(component)?.run {
                columns += "${componentInfo.getColumnNameByFieldName(field.name)},"
                values += "${TypeAdapter.convertToSqlValue(componentInfo.componentClass, field.name, this)},"
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
            if (componentInfo.isUpdateExcludeField(field) || componentInfo.getColumnInfoByFieldName(field.name).isAutoIncrement) {
                continue
            }
            field.get(component)?.run {
                sql += "${field.name}=${TypeAdapter.convertToSqlValue(component.javaClass, field.name, this)},"
            }
        }

        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }

        return "$sql where ${componentInfo.primaryKey}='$primaryValue'"
    }

    override fun <T> generateDeleteSql(componentInfo: ComponentInfo, query: Query<T>): String {

        return "DELETE FROM ${componentInfo.tableName} ${generateOperationSql(query)}"
    }

    override fun <T> generateCountSql(componentInfo: ComponentInfo, query: Query<T>): String {

        return "select count(*) count from ${componentInfo.tableName} ${generateOperationSql(query)}"
    }

    override fun <T> generateUpdateSql(componentInfo: ComponentInfo, query: Query<T>): String {

        var sql = "UPDATE ${componentInfo.tableName} a set "

        query.updateList!!.forEach { key, value ->
            sql += "$key=?,"
            query.addParam(value)
        }
        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }
        return "$sql ${generateOperationSql(query)}"
    }

    override fun <T> generateSelectSql(componentInfo: ComponentInfo, query: Query<T>): String {

        val column = query.getColumn() ?: componentInfo.selectColumns

        val sql = "select $column from ${componentInfo.tableName + generateOperationSql(query)}"
        return generatePaginationSql(sql, query)
    }

    private fun <T> generateOperationSql(query: Query<T>): String {

        val and = "and"
        val or = "or"
        var sql = ""

        query.component.autoJoinComponentList.let {
            it.forEach { autoJoinComponent -> query.join(autoJoinComponent) }
        }
        sql += generateJoinTablesSql(query.joinTables)
        sql += generateOperationSql0(query.equalsOperation, "=", and, query)
        sql += generateOperationSql0(query.notEqualsOperation, "!=", and, query)
        sql += generateOperationSql0(query.searchOperation, "search", and, query)
        sql += generateOperationSql0(query.orEqualsOperation, "=", or, query)

        if (sql.startsWith(and)) {
            sql = sql.replaceFirst(and.toRegex(), "")
            sql = " where$sql"
        }
        if (sql.startsWith(or)) {
            sql = sql.replaceFirst(or.toRegex(), "")
            sql = " where$sql"
        }

        if (StringUtils.isNotEmpty(query.orderBy)) {
            sql += " order by ${query.orderBy}"
        }

        return query.alias + sql
    }

    private fun generateJoinTablesSql(joinTables: MutableList<String>?): String {

        val sql = StringBuilder()

        joinTables?.forEach { joinTable ->
            sql.append(joinTable)
        }

        return sql.toString()
    }

    private fun <T> generateOperationSql0(operationMap: Map<String, List<String>>?, condition: String, separator: String, query: Query<T>): String {

        val sql = StringBuilder()

        operationMap?.forEach { key, operations ->
            operations.forEach { value ->
                sql.append("$separator $key $condition ? ")
                query.addParam(value)
            }
        }

        return sql.toString()
    }

    abstract fun <T> generatePaginationSql(sql: String, query: Query<T>): String

}

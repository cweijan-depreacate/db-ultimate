package github.cweijan.ultimate.core.dialect

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.annotation.CreateDate
import github.cweijan.ultimate.annotation.UpdateDate
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.query.QueryConjunct
import github.cweijan.ultimate.exception.PrimaryKeyNotExistsException
import github.cweijan.ultimate.exception.PrimaryValueNotSetException
import github.cweijan.ultimate.util.DateUtils
import github.cweijan.ultimate.util.Json
import github.cweijan.ultimate.util.ReflectUtils
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseSqlDialect : SqlDialect {

    override fun generateInsertSql(component: Any): SqlObject {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        var columns = ""
        var values = ""
        val params = ArrayList<Any>()
        for (field in TypeAdapter.getAllField(componentInfo.componentClass)) {
            field.isAccessible = true
            if (componentInfo.isExcludeField(field)) continue
            val fieldValue =ReflectUtils.getFieldValue(component,field)
            fieldValue?.run {
                columns += "${componentInfo.getColumnNameByFieldName(field.name)},"
                values += "?,"
                params.add(toAdapterValue(field, this)!!)
            }
            if (fieldValue == null) {
                field.getAnnotation(CreateDate::class.java)?.run {
                    columns += "${componentInfo.getColumnNameByFieldName(field.name)},"
                    values += "?,"
                    params.add(TypeAdapter.convertAdapter(componentInfo.componentClass, field.name, DateUtils.toDateString(Date(), this.value)))
                }
            }
        }
        if (columns.lastIndexOf(",") != -1) {
            columns = columns.substring(0, columns.lastIndexOf(","))
        }
        if (values.lastIndexOf(",") != -1) {
            values = values.substring(0, values.lastIndexOf(","))
        }

        return SqlObject("INSERT INTO " + componentInfo.tableName + "(" + columns + ") VALUES(" + values + ");", params)
    }

    private fun toAdapterValue(field: Field, fieldValue: Any): Any? {
        field.getAnnotation(Blob::class.java)?.let {return Json.toJson(fieldValue).toByteArray() }
        if (fieldValue is Collection<*>) {
            return Json.toJson(fieldValue)
        }
        if (fieldValue::class.java.isEnum) {
            return (fieldValue as Enum<*>).name
        }
        if(fieldValue.javaClass==Date::class.java){
            return java.sql.Date((fieldValue as Date).time)
        }
        return fieldValue
    }

    @Throws(IllegalAccessException::class)
    override fun generateUpdateSqlByObject(component: Any, byColumn: String?): SqlObject {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        val fieldName = byColumn ?: componentInfo.primaryField?.name
        ?: throw PrimaryKeyNotExistsException("invoke update must annotation table primary key!")
        val conditionFieldValue = componentInfo.getValueByFieldName(component, fieldName)
                ?: throw PrimaryValueNotSetException("update componnet must set primary value!")
        var sql = "UPDATE ${componentInfo.tableName} a set "
        val params = ArrayList<Any>();

        for (field in TypeAdapter.getAllField(component.javaClass)) {
            field.isAccessible = true
            if (componentInfo.isExcludeField(field) || field.name == fieldName) {
                continue
            }
            val fieldValue =ReflectUtils.getFieldValue(component,field)
            fieldValue?.run {
                sql += "${componentInfo.getColumnNameByFieldName(field.name)}=?,"
                params.add(toAdapterValue(field, this)!!)
            }
            if (fieldValue == null) {
                field.getAnnotation(UpdateDate::class.java)?.run {
                    sql += "${componentInfo.getColumnNameByFieldName(field.name)}=?,"
                    params.add(TypeAdapter.convertAdapter(componentInfo.componentClass, field.name, DateUtils.toDateString(Date(), this.value)))
                }
            }
        }

        if (sql.lastIndexOf(",") == -1) {
            throw RuntimeException("Cannot find any update relationClass!")
        } else {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }
        sql += " WHERE ${componentInfo.getColumnNameByFieldName(fieldName)}=?"
        params.add(conditionFieldValue)

        return SqlObject(sql, params)
    }

    override fun <T> generateDeleteSql(query: Query<T>): String {
        return "DELETE FROM ${query.component.tableName} ${generateOperationSql(query)}"
    }

    override fun <T> generateCountSql(query: Query<T>): String {
        return "select COUNT(*) count from ${query.component.tableName} ${generateOperationSql(query, true)}"
    }

    override fun <T> generateUpdateSqlByQuery(query: Query<T>): String {
        var sql = "UPDATE ${query.component.tableName} a set "

        if (!query.queryCondition.updateLazy.isInitialized()) throw RuntimeException("Not update column!")
        query.queryCondition.updateMap.keys.forEachIndexed { index, key ->
            sql += if (index == 0) "$key=?"
            else ",$key=?"
            query.queryCondition.addParam(query.queryCondition.updateMap[key])
        }
        return "$sql ${generateOperationSql(query)}"
    }

    override fun <T> generateSelectSql(query: Query<T>): String {

        val componentInfo = query.component

        val column = query.queryCondition.generateColumns() ?: componentInfo.selectColumns

        val sql = "select $column from ${componentInfo.tableName + generateOperationSql(query, true)}"
        return generatePaginationSql(sql, query)
    }

    private fun <T> generateOperationSql(query: Query<T>, useAlias: Boolean = false): String {

        var joinSql = ""
        var sql = ""

        if (query.component.joinLazy.isInitialized()) joinSql += generateJoinTablesSql(query.queryCondition.joinTables)

        query.queryCondition.whereSql?.let { sql+="${QueryConjunct.AND} $it " }

        sql+=query.queryCondition.andCondition.toString()
        sql+=query.queryCondition.orCondition.toString()
        sql=sql.trim()

        if (sql.startsWith(QueryConjunct.AND)) {
            sql = sql.replaceFirst(QueryConjunct.AND.toRegex(), "")
            sql = " WHERE$sql"
        }
        if (sql.startsWith(QueryConjunct.OR)) {
            sql = sql.replaceFirst(QueryConjunct.OR.toRegex(), "")
            sql = " WHERE$sql"
        }

        sql = joinSql + sql

        if (query.queryCondition.groupLazy.isInitialized()) query.queryCondition.groupByList.forEachIndexed { index, groupBy ->
            sql += if (index == 0) " GROUP BY $groupBy" else ",$groupBy"
        }
        if (query.queryCondition.havingLazy.isInitialized()) query.queryCondition.havingSqlList.forEachIndexed { index, havingSql ->
            if (index == 0) sql += " HAVING "
            sql += havingSql
        }

        if (query.queryCondition.orderByLazy.isInitialized()) query.queryCondition.orderByList.forEachIndexed { index, orderBy ->
            sql += if (index == 0) " ORDER BY $orderBy" else ",$orderBy"
        }

        return if (useAlias) query.queryCondition.alias + sql else sql
    }

    private fun generateJoinTablesSql(joinTables: MutableList<String>?): String {

        val sql = StringBuilder()

        joinTables?.forEach { joinTable ->
            sql.append(joinTable)
        }

        return sql.toString()
    }

    private fun <T> generateOperationSql0(operationMap: Map<String, List<Any>>?, condition: String, separator: String, query: Query<T>): String {

        val sql = StringBuilder()

        operationMap?.forEach { key, operations ->
            operations.forEach { value ->
                sql.append("$separator $key $condition ? ")
                query.queryCondition.addParam(value)
            }
        }

        return sql.toString()
    }

    abstract fun <T> generatePaginationSql(sql: String, query: Query<T>): String

}

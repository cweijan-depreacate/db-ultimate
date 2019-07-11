package github.cweijan.ultimate.core.dialect

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.exception.PrimaryValueNotSetException
import github.cweijan.ultimate.util.Json

abstract class BaseSqlDialect : SqlDialect {

    override fun generateInsertSql(component: Any): SqlObject {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        var columns = ""
        var values = ""
        val params = ArrayList<Any>();
        for (field in TypeAdapter.getAllField(componentInfo.componentClass)) {
            field.isAccessible = true
            if (componentInfo.isInsertExcludeField(field)) continue
            field.get(component)?.run {
                columns += "${componentInfo.getColumnNameByFieldName(field.name)},"
                values += "?,"
                field.getAnnotation(Blob::class.java)?.let { params.add(Json.toJson(this).toByteArray()); return@run }
                params.add(TypeAdapter.convertAdapter(componentInfo.componentClass, field.name, this))
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

    @Throws(IllegalAccessException::class)
    override fun generateUpdateSqlByObject(component: Any): SqlObject {

        val componentInfo = TableInfo.getComponent(component.javaClass)
        val primaryValue = componentInfo.getPrimaryValue(component)
        primaryValue ?: throw PrimaryValueNotSetException("primary value must set!")
        var sql = "UPDATE ${componentInfo.tableName} a set "
        val params = ArrayList<Any>();

        for (field in TypeAdapter.getAllField(component.javaClass)) {
            field.isAccessible = true
            if (componentInfo.isUpdateExcludeField(field)) {
                continue
            }
            field.get(component)?.run {
                sql += "${componentInfo.getColumnNameByFieldName(field.name)}=?,"
                field.getAnnotation(Blob::class.java)?.let { params.add(Json.toJson(this).toByteArray()); return@run }
                params.add(TypeAdapter.convertAdapter(componentInfo.componentClass, field.name, this))
            }
        }

        if (sql.lastIndexOf(",") == -1) {
            throw RuntimeException("Cannot find any update value!")
        } else {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }
        sql += " WHERE ${componentInfo.primaryKey}=?"
        params.add(primaryValue)

        return SqlObject(sql, params)
    }

    override fun <T> generateDeleteSql(query: Query<T>): String {
        return "DELETE FROM ${query.component.tableName} ${generateOperationSql(query)}"
    }

    override fun <T> generateCountSql(query: Query<T>): String {
        return "select COUNT(*) count from ${query.component.tableName} ${generateOperationSql(query, true)}"
    }

    override fun <T> generateUpdateSqlByObject(query: Query<T>): String {
        var sql = "UPDATE ${query.component.tableName} a set "

        if (!query.updateLazy.isInitialized()) throw RuntimeException("Not update column!")
        query.updateMap.keys.forEachIndexed { index, key ->
            sql += if (index == 0) "$key=?"
            else ",$key=?"
            query.addParam(query.updateMap[key])
        }
        return "$sql ${generateOperationSql(query)}"
    }

    override fun <T> generateSelectSql(query: Query<T>): String {

        val componentInfo = query.component
        if (query.page != null && query.pageSize != 0) {
            val start = if (query.page!! <= 0) 0 else (query.page!! - 1) * (query.pageSize ?: 100)
            query.offset(start)
        }

        val column = query.generateColumns() ?: query.getColumn() ?: componentInfo.selectColumns

        val sql = "select $column from ${componentInfo.tableName + generateOperationSql(query, true)}"
        return generatePaginationSql(sql, query)
    }

    private fun <T> generateOperationSql(query: Query<T>, useAlias: Boolean = false): String {

        val and = "AND"
        val or = "OR"
        var joinSql: String? = null
        var sql = ""

        if (query.component.autoJoinLazy.isInitialized()) query.component.autoJoinComponentList.let { it.forEach { autoJoinComponent -> query.join(autoJoinComponent) } }
        if (query.joinLazy.isInitialized()) joinSql = generateJoinTablesSql(query.joinTables)

        if (query.eqLazy.isInitialized()) sql += generateOperationSql0(query.equalsOperation, "=", and, query)
        if (query.orEqLazy.isInitialized()) sql += generateOperationSql0(query.orEqualsOperation, "=", or, query)

        if (query.notEqLazy.isInitialized()) sql += generateOperationSql0(query.notEqualsOperation, "!=", and, query)
        if (query.orNotEqLazy.isInitialized()) sql += generateOperationSql0(query.orNotEqualsOperation, "!=", or, query)

        if (query.greatEqLazy.isInitialized()) sql += generateOperationSql0(query.greatEqualsOperation, ">=", and, query)
        if (query.lessEqLazy.isInitialized()) sql += generateOperationSql0(query.lessEqualsOperation, "<=", and, query)

        if (query.searchLazy.isInitialized()) sql += generateOperationSql0(query.searchOperation, "LIKE", and, query)
        if (query.orSearchLazy.isInitialized()) sql += generateOperationSql0(query.orSearchOperation, "LIKE", or, query)

        if (query.isNullLazy.isInitialized()) query.isNullList.forEach { sql += "$and $it IS NULL " }
        if (query.isNotNullLazy.isInitialized()) query.isNotNullList.forEach { sql += "$and $it IS NOT NULL " }

        //生成in查询语句
        var inSql = StringBuilder()
        if (query.inLazy.isInitialized()) query.inOperation.forEach { (key, operations) ->
            inSql.append("$and $key in (")
            operations.forEach { value ->
                inSql.append(",?")
                query.addParam(value)
            }
            inSql = StringBuilder(inSql.replaceFirst(",".toRegex(), ""))
            inSql.append(")")
        }
        sql += inSql.toString()

        if (sql.startsWith(and)) {
            sql = sql.replaceFirst(and.toRegex(), "")
            sql = " WHERE$sql"
        }
        if (sql.startsWith(or)) {
            sql = sql.replaceFirst(or.toRegex(), "")
            sql = " WHERE$sql"
        }

        joinSql?.run { sql = this + sql }

        if (query.groupLazy.isInitialized()) query.groupByList.forEachIndexed { index, groupBy ->
            sql += if (index == 0) " GROUP BY $groupBy" else ",$groupBy"
        }
        if (query.havingLazy.isInitialized()) query.havingSqlList.forEachIndexed { index, havingSql ->
            if (index == 0) sql += " HAVING "
            sql += havingSql
        }

        query.orderByList.forEachIndexed { index, orderBy ->
            sql += if (index == 0) " ORDER BY $orderBy" else ",$orderBy"
        }

        return if (useAlias) query.alias + sql else sql
    }

    private fun generateJoinTablesSql(joinTableSqls: MutableList<String>?): String {

        val sql = StringBuilder()

        joinTableSqls?.forEach { joinTableSql ->
            sql.append(joinTableSql)
        }

        return sql.toString()
    }

    private fun <T> generateOperationSql0(operationMap: Map<String, List<Any>>?, condition: String, separator: String, query: Query<T>): String {

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

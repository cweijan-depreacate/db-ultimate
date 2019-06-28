package github.cweijan.ultimate.core.dialect

import github.cweijan.ultimate.core.Query

interface SqlDialect {

    fun generateInsertSql(component: Any): SqlObject

    @Throws(IllegalAccessException::class)
    fun generateUpdateSqlByObject(component: Any): SqlObject

    fun <T> generateUpdateSqlByObject(query: Query<T>): String

    fun <T> generateDeleteSql(query: Query<T>): String

    fun <T> generateSelectSql(query: Query<T>): String

    fun <T> generateCountSql(query: Query<T>): String

}

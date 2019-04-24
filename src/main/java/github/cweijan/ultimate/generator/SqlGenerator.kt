package github.cweijan.ultimate.generator

import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.core.Query
import java.lang.reflect.Field

interface SqlGenerator {

    fun generateInsertSql(component: Any): String

    @Throws(IllegalAccessException::class)
    fun generateUpdateSqlByObject(component: Any): String

    fun <T> generateUpdateSqlByObject(query: Query<T>): String

    fun <T> generateDeleteSql(query: Query<T>): String

    fun <T> generateSelectSql(query: Query<T>): String

    fun <T> generateCountSql(query: Query<T>): String

}

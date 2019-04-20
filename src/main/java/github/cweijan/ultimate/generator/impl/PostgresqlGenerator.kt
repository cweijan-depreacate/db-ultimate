package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.generator.BaseSqlGenerator

class PostgresqlGenerator : BaseSqlGenerator() {
    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.pageSize) return sql

        return "$sql pageSize ${query.pageSize} offset ${query.offset ?: 0}"
    }
}
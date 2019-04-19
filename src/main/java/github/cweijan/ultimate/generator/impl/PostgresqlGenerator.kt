package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.generator.BaseSqlGenerator

class PostgresqlGenerator : BaseSqlGenerator() {
    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.limit) return sql

        return "$sql limit ${query.limit} offset ${query.offset ?: 0}"
    }
}
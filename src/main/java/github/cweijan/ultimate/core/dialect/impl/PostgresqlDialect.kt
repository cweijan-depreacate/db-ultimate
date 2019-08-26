package github.cweijan.ultimate.core.dialect.impl

import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.dialect.BaseSqlDialect

class PostgresqlDialect : BaseSqlDialect() {

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.queryCondition.offset && null == query.queryCondition.pageSize) return sql

        return "$sql limit ${query.queryCondition.pageSize} offset ${query.queryCondition.offset ?: 0}"
    }

}
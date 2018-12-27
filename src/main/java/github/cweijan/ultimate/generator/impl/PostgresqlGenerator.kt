package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.core.Operation
import github.cweijan.ultimate.generator.BaseSqlGenerator

class PostgresqlGenerator : BaseSqlGenerator() {
    override fun <T> generatePaginationSql(sql: String, operation: Operation<T>): String {

        if (null == operation.start && null == operation.limit) return sql

        return "$sql limit ${operation.limit} offset ${operation.start ?: 0}"
    }
}
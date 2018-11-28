package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.core.Operation
import github.cweijan.ultimate.generator.BaseSqlGenerator

class MysqlGenerator : BaseSqlGenerator() {
    override fun <T> generatePaginationSql(operation: Operation<T>): String {

        if (null == operation.start && null == operation.limit) return ""

        return " limit ${operation.start ?: 0},${operation.limit}"

    }
}


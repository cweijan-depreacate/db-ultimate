package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.core.Operation
import github.cweijan.ultimate.generator.BaseSqlGenerator

class OracleGenerator : BaseSqlGenerator() {
    override fun <T> generatePaginationSql(sql: String, operation: Operation<T>): String {

        if (null == operation.start && null == operation.limit) return sql

        return if (operation.start != null) {
            "select * from ( select row_.*, rownum rn from ( $sql ) row_ where rownum < ${operation.start}) where rn >= ${operation.limit!! + operation.start!!}"
        } else {
            "select * from ( $sql ) where rownum < ${operation.limit}"
        }
    }
}
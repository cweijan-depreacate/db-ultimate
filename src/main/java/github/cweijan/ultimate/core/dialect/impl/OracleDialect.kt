package github.cweijan.ultimate.core.dialect.impl

import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.dialect.BaseSqlDialect

class OracleDialect : BaseSqlDialect() {

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.queryCondition.offset && null == query.queryCondition.pageSize) return sql

        return if (query.queryCondition.offset != null) {
            "select * from ( select row_.*, rownum rn from ( $sql ) row_ where rownum < ${query.queryCondition.offset}) where rn >= ${query.queryCondition.pageSize!! + query.queryCondition.offset!!}"
        } else {
            "select * from ( $sql ) where rownum < ${query.queryCondition.pageSize}"
        }
    }

}
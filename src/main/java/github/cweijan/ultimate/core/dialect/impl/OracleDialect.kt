package github.cweijan.ultimate.core.dialect.impl

import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.dialect.BaseSqlDialect

class OracleDialect : BaseSqlDialect() {

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.pageSize) return sql

        return if (query.offset != null) {
            "select * from ( select row_.*, rownum rn from ( $sql ) row_ where rownum < ${query.offset}) where rn >= ${query.pageSize!! + query.offset!!}"
        } else {
            "select * from ( $sql ) where rownum < ${query.pageSize}"
        }
    }

}
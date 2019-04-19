package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.generator.BaseSqlGenerator

class OracleGenerator : BaseSqlGenerator() {
    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.limit) return sql

        return if (query.offset != null) {
            "select * from ( select row_.*, rownum rn from ( $sql ) row_ where rownum < ${query.offset}) where rn >= ${query.limit!! + query.offset!!}"
        } else {
            "select * from ( $sql ) where rownum < ${query.limit}"
        }
    }
}
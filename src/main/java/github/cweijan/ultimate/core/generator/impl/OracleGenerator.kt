package github.cweijan.ultimate.core.generator.impl

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.generator.BaseSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class OracleGenerator : BaseSqlGenerator() {

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.pageSize) return sql

        return if (query.offset != null) {
            "select * from ( select row_.*, rownum rn from ( $sql ) row_ where rownum < ${query.offset}) where rn >= ${query.pageSize!! + query.offset!!}"
        } else {
            "select * from ( $sql ) where rownum < ${query.pageSize}"
        }
    }

}
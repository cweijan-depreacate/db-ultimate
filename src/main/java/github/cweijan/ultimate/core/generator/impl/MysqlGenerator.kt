package github.cweijan.ultimate.core.generator.impl

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.generator.BaseSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class MysqlGenerator : BaseSqlGenerator() {

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.pageSize) return sql

        return "$sql limit ${query.offset ?: 0},${query.pageSize}"

    }
}


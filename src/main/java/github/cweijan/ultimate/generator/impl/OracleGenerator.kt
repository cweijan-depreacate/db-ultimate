package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.generator.BaseSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class OracleGenerator : BaseSqlGenerator() {
    override fun generateUniqueSqlFragment(tableName: String, columnName: String, columnDefinition: String): String? {
        TODO("not implemented")
    }

    override fun generateAutoIncrementSqlFragment(tableName: String?, columnName: String?): String {
        TODO("Not Support Oracle Increment")
    }

    override fun generateDefaultSqlFragment(defaultValue: Any?): String {
        return " DEFAULT ($defaultValue) "
    }

    override fun getColumnTypeByField(field: Field, length: Int?): String {
        return when (field.type.name) {
            JavaType.String -> "VARCHAR2(${length ?: 100})"
            JavaType.Character, "char" -> "CHAR(${length ?: 1})"
            JavaType.Boolean, "boolean",JavaType.Float, "float", JavaType.Long, "long", JavaType.Double, "double",JavaType.Integer, "int" -> "NUMBER"
            Array<Byte>::class.java.name -> "RAW"
            Date::class.java.name, LocalDateTime::class.java.name, LocalDate::class.java.name, LocalTime::class.java.name -> "DATE"
            else -> "VARCHAR2(${length ?: 100})"
        }
    }

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.pageSize) return sql

        return if (query.offset != null) {
            "select * from ( select row_.*, rownum rn from ( $sql ) row_ where rownum < ${query.offset}) where rn >= ${query.pageSize!! + query.offset!!}"
        } else {
            "select * from ( $sql ) where rownum < ${query.pageSize}"
        }
    }
}
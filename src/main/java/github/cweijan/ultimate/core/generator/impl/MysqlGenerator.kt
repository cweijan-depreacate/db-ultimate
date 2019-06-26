package github.cweijan.ultimate.core.generator.impl

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.generator.BaseSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class MysqlGenerator : BaseSqlGenerator() {
    override fun generateUniqueSqlFragment(tableName: String, columnName: String, columnDefinition: String): String? {
        return "UNIQUE INDEX ($columnName)"
    }

    override fun generateAutoIncrementSqlFragment(tableName: String?, columnName: String?): String? {
        if (tableName != null || columnName != null) return null
        return " AUTO_INCREMENT "
    }

    override fun generateCommentSqlFragment(comment: String): String? {
        return " COMMENT '$comment' "
    }

    override fun generateDefaultSqlFragment(defaultValue: Any?): String {
        return if (defaultValue == null) "" else " DEFAULT $defaultValue "
    }

    override fun getColumnTypeByField(field: Field, length: Int?): String {
        if(field.type.isEnum)return "VARCHAR(${length ?: 50})"
        return when (field.type.name) {
            JavaType.String -> "VARCHAR(${length ?: 100})"
            JavaType.Character, "char" -> "char(${length ?: 1})"
            JavaType.Integer, "int" -> "int"
            JavaType.byteArray -> "BLOB"
            JavaType.Boolean, "boolean" -> "BIT"
            JavaType.Long, "long" -> "bigint"
            JavaType.Float, "float", JavaType.Double, "double" -> "DECIMAL(${length ?: 10},2)"
            Date::class.java.name, LocalDateTime::class.java.name -> "datetime"
            LocalDate::class.java.name -> "DATE"
            LocalTime::class.java.name -> "TIME"
            else -> "varchar(${length ?: 100})"
        }
    }

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.pageSize) return sql

        return "$sql limit ${query.offset ?: 0},${query.pageSize}"

    }
}


package github.cweijan.ultimate.generator.impl

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.generator.BaseSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class PostgresqlGenerator : BaseSqlGenerator() {
    override fun generateUniqueSqlFragment(tableName: String, columnName: String, columnDefinition: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateAutoIncrementSqlFragment(tableName: String?, columnName: String?): String? {
        if(tableName==null||columnName==null)return null
        return "\n"+"""CREATE SEQUENCE ${tableName}_${columnName}_seq
            INCREMENT 1
            START 1
            MINVALUE 1
            MAXVALUE 99999999
            CACHE 1;
        ALTER TABLE $tableName alter column $columnName set default nextval('${tableName}_${columnName}_seq');
        """
    }

    override fun generateDefaultSqlFragment(defaultValue: Any?): String {
        return " DEFAULT $defaultValue "
    }

    override fun getColumnTypeByField(field: Field, length: Int?): String {
        return when (field.type.name) {
            JavaType.String -> "varchar(${length ?: 100})"
            JavaType.Character, "char" -> "char(${length ?: 1})"
            JavaType.Integer, "int" -> "int"
            JavaType.Long, "long" -> "int8"
            JavaType.Float, "float" -> "float4"
            JavaType.Double, "double" -> "float8"
            JavaType.Boolean, "boolean" -> "bool"
            Array<Byte>::class.java.name -> "bytea"
            Date::class.java.name, LocalDateTime::class.java.name -> "timestamp"
            LocalDate::class.java.name -> "DATE"
            LocalTime::class.java.name -> "TIME"
            else -> "varchar(${length ?: 100})"
        }
    }

    override fun <T> generatePaginationSql(sql: String, query: Query<T>): String {

        if (null == query.offset && null == query.pageSize) return sql

        return "$sql limit ${query.pageSize} offset ${query.offset ?: 0}"
    }
}
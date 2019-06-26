package github.cweijan.ultimate.db.init.generator.impl

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.db.init.generator.BaseInitSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author cwj
 * @date 2019/6/25/025 14:44
 */
class OracleInit : BaseInitSqlGenerator() {

    override fun generateUniqueSqlFragment(tableName: String, columnName: String, columnDefinition: String): String? {
        return " CONSTRAINT ${columnName}_uk UNIQUE ($columnName) "
    }

    override fun generateAutoIncrementSqlFragment(tableName: String?, columnName: String?): String {
        TODO("Not Support Oracle Increment")
    }

    override fun generateDefaultSqlFragment(defaultValue: Any?): String {
        return " DEFAULT ($defaultValue) "
    }

    override fun getColumnTypeByField(field: Field, length: Int?): String {
        if (field.type.isEnum) return "VARCHAR2(${length ?: 50})"
        return when (field.type.name) {
            JavaType.String -> "VARCHAR2(${length ?: 100})"
            JavaType.Character, "char" -> "CHAR(${length ?: 1})"
            JavaType.Boolean, "boolean", JavaType.Float, "float", JavaType.Long, "long", JavaType.Double, "double", JavaType.Integer, "int" -> "NUMBER"
            JavaType.byteArray -> "RAW"
            Date::class.java.name, LocalDateTime::class.java.name, LocalDate::class.java.name, LocalTime::class.java.name -> "DATE"
            else -> "VARCHAR2(${length ?: 100})"
        }
    }

}

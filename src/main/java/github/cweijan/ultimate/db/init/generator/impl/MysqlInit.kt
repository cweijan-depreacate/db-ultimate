package github.cweijan.ultimate.db.init.generator.impl

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.db.init.generator.BaseInitSqlGenerator
import github.cweijan.ultimate.db.init.generator.struct.TableStruct

import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author cwj
 * @date 2019/6/25/025 14:43
 */
class MysqlInit : BaseInitSqlGenerator() {

    override fun initStruct() {
        ComponentInfo.init(TableStruct::class.java)
    }

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
        if (field.type.isEnum) return "VARCHAR(${length ?: 50})"
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

}

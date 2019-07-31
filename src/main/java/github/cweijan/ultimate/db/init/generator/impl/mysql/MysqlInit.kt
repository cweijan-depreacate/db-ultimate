package github.cweijan.ultimate.db.init.generator.impl.mysql

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.db.init.generator.TableInitSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author cwj
 * @date 2019/6/25/025 14:43
 */
class MysqlInit : TableInitSqlGenerator {

    override fun getColumnDefination(field: Field, componentInfo: ComponentInfo): String {
        field.isAccessible = true
        val columnInfo = componentInfo.getColumnInfoByFieldName(field.name)!!
        //生成column
        var columnDefination = "${columnInfo.columnName} ${getColumnTypeByField(field, columnInfo.length)}"
        //生成主键或者非空片段
        columnDefination += when {
            columnInfo.isPrimary && columnInfo.autoIncrement -> " PRIMARY KEY AUTO_INCREMENT "
            columnInfo.isPrimary -> " PRIMARY KEY "
            columnInfo.unique -> " UNIQUE "
            columnInfo.nullable -> ""
            else -> " NOT NULL "
        }
        //生成默认值片段
        columnDefination += when {
            columnInfo.unique || columnInfo.isPrimary || columnInfo.defaultValue==null -> ""
            else -> " DEFAULT " + if (TypeAdapter.CHARACTER_TYPE.contains(field.type.name)) {
                TypeAdapter.contentWrapper(columnInfo.defaultValue)
            } else {
                columnInfo.defaultValue
            }
        }

        //生成注释片段
        columnInfo.comment?.let { comment ->
            columnDefination += " COMMENT '$comment' "
        }
        return columnDefination
    }

    override fun initStruct() {
        ComponentInfo.init(MysqlTableStruct::class.java)
    }

    override fun getColumnTypeByField(field: Field, length: Int?): String {
        if (field.type.isEnum) return "VARCHAR(${length ?: 50})"
        field.getAnnotation(Blob::class.java)?.run { return "BLOB" }
        return when (field.type.name) {
            JavaType.String -> "VARCHAR(${length ?: 100})"
            JavaType.Character, "char" -> "char(${length ?: 1})"
            JavaType.Integer, "int" -> "int"
            JavaType.byteArray -> "BLOB"
            JavaType.ByteArray -> "BLOB"
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

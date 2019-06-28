package github.cweijan.ultimate.db.init.generator.impl.postgresql

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.db.init.generator.TableInitSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class PostgresqlInit : TableInitSqlGenerator {

    override fun getColumnDefination(field: Field, componentInfo: ComponentInfo): String {
        field.isAccessible = true
        val columnInfo = componentInfo.getColumnInfoByFieldName(field.name)!!
        //生成column
        val columnType = getColumnTypeByField(field, columnInfo.length)
        var columnDefination = "${columnInfo.columnName} $columnType"
        //生成主键或者非空片段
        when {
            columnInfo.isPrimary && columnInfo.autoIncrement -> {
                columnDefination = columnDefination.replace(columnType, "serial")
                columnDefination += " PRIMARY KEY "
            }
            columnInfo.isPrimary -> columnDefination += " PRIMARY KEY "
            columnInfo.unique -> columnDefination += " UNIQUE "
            columnInfo.nullable -> columnDefination += ""
            else -> columnDefination += " NOT NULL "
        }
        //生成默认值片段
        columnDefination += when {
            columnInfo.unique || columnInfo.isPrimary || columnInfo.defaultValue == null -> ""
            else -> " DEFAULT " + if (TypeAdapter.CHARACTER_TYPE.contains(field.type.name)) {
                TypeAdapter.contentWrapper(columnInfo.defaultValue)
            } else {
                columnInfo.defaultValue
            }
        }

        //注释:Postgresql不支持生成注释

        return columnDefination
    }

    override fun initStruct() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun generateAutoIncrementSqlFragment(tableName: String?, columnName: String?): String? {
        if (tableName == null || columnName == null) return null
        return "\n" + """CREATE SEQUENCE ${tableName}_${columnName}_seq
            INCREMENT 1
            START 1
            MINVALUE 1
            MAXVALUE 99999999
            CACHE 1;
        ALTER TABLE $tableName alter column $columnName set default nextval('${tableName}_${columnName}_seq');
        """
    }

    override fun getColumnTypeByField(field: Field, length: Int?): String {
        if (field.type.isEnum) return "VARCHAR(${length ?: 50})"
        return when (field.type.name) {
            JavaType.String -> "varchar(${length ?: 100})"
            JavaType.Character, "char" -> "char(${length ?: 1})"
            JavaType.Integer, "int" -> "int"
            JavaType.Long, "long" -> "int8"
            JavaType.Float, "float" -> "float4"
            JavaType.Double, "double" -> "float8"
            JavaType.Boolean, "boolean" -> "bool"
            JavaType.byteArray -> "bytea"
            Date::class.java.name, LocalDateTime::class.java.name -> "timestamp"
            LocalDate::class.java.name -> "DATE"
            LocalTime::class.java.name -> "TIME"
            else -> "varchar(${length ?: 100})"
        }
    }

}
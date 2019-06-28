package github.cweijan.ultimate.db.init.generator

import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.info.ComponentInfo
import java.lang.reflect.Field

interface TableInitSqlGenerator {

    fun initStruct()

    fun getColumnTypeByField(field: Field, length: Int?): String

    fun getColumnDefination(field: Field, componentInfo: ComponentInfo): String

    fun createTable(componentInfo: ComponentInfo): String? {

        var sql = "create table ${componentInfo.tableName}("

        TypeAdapter.getAllField(componentInfo.componentClass).let { fields ->
            fields.forEach { field ->
                if (componentInfo.isTableExcludeField(field) || !TypeAdapter.isAdapterType(field.type)) {
                    return@forEach
                }
                val columnDefination = getColumnDefination(field, componentInfo)
                //拼接sql
                sql += "$columnDefination,"
            }
        }

        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }
        sql += " );"

        return sql
    }


    fun dropTable(tableName: String?): String? {
        tableName ?: return null
        return "DROP TABLE $tableName;"
    }

}
package github.cweijan.ultimate.db.init.generator.impl.oracle

import github.cweijan.ultimate.convert.JavaType
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.db.init.generator.TableInitSqlGenerator
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author cwj
 * @date 2019/6/25/025 14:44
 */
class OracleInit : TableInitSqlGenerator {

    override fun getColumnDefination(field: Field, componentInfo: ComponentInfo): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initStruct() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

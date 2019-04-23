package github.cweijan.ultimate.generator

import java.lang.reflect.Field

interface TableInitSqlGenetator {

    fun getColumnTypeByField(field: Field, length: Int?): String

    fun generateDefaultSqlFragment(defaultValue:Any?):String

    fun generateAutoIncrementSqlFragment(tableName: String?=null,columnName:String?=null):String

    fun generateCommentSqlFragment(comment: String): String?

}
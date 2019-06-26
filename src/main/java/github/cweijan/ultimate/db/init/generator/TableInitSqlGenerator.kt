package github.cweijan.ultimate.db.init.generator

import java.lang.reflect.Field

interface TableInitSqlGenerator {

    fun getColumnTypeByField(field: Field, length: Int?): String

    fun generateDefaultSqlFragment(defaultValue:Any?):String

    fun generateAutoIncrementSqlFragment(tableName: String?=null,columnName:String?=null):String?

    fun generateCommentSqlFragment(comment: String): String?

    fun generateUniqueSqlFragment(tableName: String,columnName: String,columnDefinition: String): String?

}
package github.cweijan.ultimate.core.component.info

class ColumnInfo {

    lateinit var columnName: String
    lateinit var fieldType: Class<*>
    lateinit var excelHeader: String
    var length: Int? = null
    var nullable: Boolean = false
    var autoIncrement: Boolean = false
    var unique: Boolean=false
    var comment: String?=null
    var defaultValue: String?=null
    var dateFormat: String = "yyyy-MM-dd HH:mm:ss"
    var excludeInsert: Boolean = false
    var excludeUpdate: Boolean = false
    var excludeResult: Boolean = false
    var excludeTable: Boolean = false
}

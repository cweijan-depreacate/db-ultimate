package github.cweijan.ultimate.component.info

import github.cweijan.ultimate.annotation.Exclude

class ColumnInfo {

    lateinit var columnName: String
    lateinit var fieldType: Class<*>
    var length: Int? = null
    var isNullable: Boolean = false
    var isAutoIncrement: Boolean = false
    var dateFormat:String="yyyy-MM-dd HH:mm:ss"
    var excludeAnnotaionValue:Exclude?=null
    var excludeInsert: Boolean = false
    var excludeUpdate: Boolean = false
    var excludeResult: Boolean = false
    var excludeTable: Boolean = false
}

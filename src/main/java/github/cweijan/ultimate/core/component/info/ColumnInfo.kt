package github.cweijan.ultimate.core.component.info

import com.fasterxml.jackson.annotation.JsonFormat
import github.cweijan.ultimate.annotation.*
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.util.StringUtils
import java.lang.reflect.Field

class ColumnInfo {

    lateinit var field: Field
    lateinit var columnName: String
    lateinit var fieldType: Class<*>
    lateinit var excelHeader: String
    var length: Int? = null
    var nullable: Boolean = true
    var autoIncrement: Boolean = false
    var isBlob: Boolean = false
    var unique: Boolean = false
    var comment: String? = null
    var defaultValue: String? = null
    lateinit var dateFormat: String
    var exclude: Boolean = false
    var isPrimary: Boolean = false

    companion object {

        @JvmStatic
        fun init(componentInfo: ComponentInfo, field: Field) {

            field.isAccessible = true

            val columnInfo = ColumnInfo()
            columnInfo.fieldType = field.type
            columnInfo.field = field

            //生成exclude信息
            field.getAnnotation(Exclude::class.java)?.run {
                columnInfo.exclude = true
            }

            //生成日期格式化信息
            val jsonFormat = field.getAnnotation(JsonFormat::class.java)
            if(jsonFormat==null){
                if(TypeAdapter.DATE_TYPE.contains(field.type.name)){
                    columnInfo.dateFormat =TypeAdapter.getDefaultFormat(field.type)
                }
            }else{
                columnInfo.dateFormat = jsonFormat.pattern
            }

            //生成日期格式化信息
            field.getAnnotation(JsonFormat::class.java)?.run {
                columnInfo.isBlob = true
            }

            //生成column info
            val columnAnnotation = field.getAnnotation(Column::class.java)
            if (columnAnnotation != null) {
                columnAnnotation.run {
                    columnInfo.columnName = if (StringUtils.isNotEmpty(this.value)) this.value else field.name
                    columnInfo.comment = if (StringUtils.isNotEmpty(this.comment)) this.comment else null
                    columnInfo.defaultValue = if (StringUtils.isNotEmpty(this.defaultValue)) this.defaultValue else null
                    columnInfo.nullable = this.nullable
                    columnInfo.length = if (columnAnnotation.length != 0) this.length else null
                    columnInfo.unique = this.unique
                    columnInfo.excelHeader = if (this.excelHeader == "") columnInfo.columnName else this.excelHeader
                }
            } else {
                columnInfo.columnName = field.name
                columnInfo.excelHeader = field.name
            }
            componentInfo.excelHeaderFieldMap[columnInfo.excelHeader] = field

            columnInfo.columnName = TypeAdapter.convertHumpToUnderLine(columnInfo.columnName)!!

            //generate primary key column info
            val primaryAnnotation = field.getAnnotation(Primary::class.java)
            if (primaryAnnotation != null || (field.name == "id" && StringUtils.isEmpty(componentInfo.primaryKey))) {
                componentInfo.primaryKey = columnInfo.columnName
                componentInfo.primaryField = field
                columnInfo.isPrimary = true
                columnInfo.nullable = false
                primaryAnnotation?.run {
                    columnInfo.autoIncrement = this.autoIncrement
                    componentInfo.autoIncrement = this.autoIncrement
                    columnInfo.columnName = if (StringUtils.isNotEmpty(this.value)) this.value else field.name
                    columnInfo.comment = if (StringUtils.isNotEmpty(this.comment)) this.comment else null
                    columnInfo.length = if (primaryAnnotation.length != 0) this.length else null
                }

            }

            //generate foreign key column info
            field.getAnnotation(OneToOne::class.java)?.run {
                if(!Collection::class.java.isAssignableFrom(field.type)){
                    throw RuntimeException("OneToMany annotation only annotate Collection!")
                }

                val joinColumnName = TypeAdapter.convertHumpToUnderLine(relationColumn)!!
                componentInfo.oneToOneList.add(OneToOneInfo(field,joinColumnName,relationClass))
                columnInfo.exclude = true
            }
            field.getAnnotation(OneToMany::class.java)?.run {
                if(!Collection::class.java.isAssignableFrom(field.type)){
                    throw RuntimeException("OneToMany annotation only annotate Collection!")
                }
                val joinColumnName = TypeAdapter.convertHumpToUnderLine(relationColumn)!!
                componentInfo.oneToManyList.add(OneToManyInfo(field,joinColumnName,where,relationClass))
                columnInfo.exclude = true
            }
            componentInfo.fieldColumnInfoMap[field.name] = columnInfo
            componentInfo.columnInfoMap[columnInfo.columnName] = columnInfo

//            return columnInfo
        }

    }

}

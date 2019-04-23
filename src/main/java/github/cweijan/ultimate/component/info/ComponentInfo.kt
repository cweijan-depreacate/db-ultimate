package github.cweijan.ultimate.component.info

import github.cweijan.ultimate.annotation.*
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.exception.ForeignKeyNotSetException
import github.cweijan.ultimate.exception.PrimaryValueNotSetException
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import java.lang.reflect.Field

class ComponentInfo(var componentClass: Class<*>) {

    var primaryKey: String? = null

    private val primaryFieldName: String?
        get() {
            return primaryField?.name
        }

    private var primaryField: Field? = null

    lateinit var selectColumns: String

    lateinit var tableName: String

    var tableAlias: String? = null

    /**
     * 属性名与ColumnInfo的映射
     */
    private val fieldColumnInfoMap by lazy {
        return@lazy HashMap<String, ColumnInfo>()
    }

    /**
     * 列名与属性名的映射
     */
    private val columnFieldMap by lazy {
        return@lazy HashMap<String, String>()
    }

    /**
     * 外键映射
     */
    private val foreignKeyMap by lazy {
        return@lazy HashMap<Class<*>, ForeignKeyInfo>();
    }

    /**
     * 自动关联的外键列表
     */
    val autoJoinComponentList by lazy {
        return@lazy ArrayList<Class<*>>();
    }

    /**
     * 根据属性名找列名
     *
     * @param fieldName 列名
     * @return 对应的属性名
     */
    fun getColumnNameByFieldName(fieldName: String): String? {

        return fieldColumnInfoMap[fieldName]?.columnName
    }

    /**
     * 根据属性名找列的详细信息
     *
     * @param fieldName 列名
     * @return 对应的属性名
     */
    fun getColumnInfoByFieldName(fieldName: String): ColumnInfo? {

        return fieldColumnInfoMap[fieldName]
    }

    /**
     * 判断这个组件是否有属性
     */
    fun nonExistsColumn(): Boolean {
        return fieldColumnInfoMap.keys.size == 0
    }

    /**
     * 根据列名找属性名
     *
     * @param columnName 列名
     * @return 对应的属性名
     */
    fun getFieldNameByColumnName(columnName: String): String? {

        return columnFieldMap[columnName]
    }

    @Throws(IllegalAccessException::class)
    fun getPrimaryValue(component: Any): Any? {

        return primaryField!!.get(component)

    }

    fun getForeignKey(foreignClass: Class<*>): ForeignKeyInfo {
        if (!foreignKeyMap.contains(foreignClass))
            throw ForeignKeyNotSetException("${foreignClass.name} is not a valid foreign class")

        val foreignKeyInfo = foreignKeyMap[foreignClass]

        val joinKey = foreignKeyInfo!!.joinKey
        if (joinKey == "") {
            val component = TableInfo.getComponent(foreignClass)
            if (component.primaryKey == null || component.primaryKey == "") {
                throw PrimaryValueNotSetException("join component ${foreignClass.name} is not primary key found! ")
            } else {
                foreignKeyInfo.joinKey = component.primaryKey!!
            }
        }

        return foreignKeyInfo
    }

    fun isPrimaryField(field: Field?): Boolean {
        return null != field && field.name == primaryFieldName
    }

    fun isQueryExcludeField(field: Field?): Boolean {
        val columnInfo = fieldColumnInfoMap[field?.name]
        return null != columnInfo && columnInfo.excludeResult
    }

    fun isInsertExcludeField(field: Field?): Boolean {
        val columnInfo = fieldColumnInfoMap[field?.name]
        return null != columnInfo && columnInfo.excludeInsert
    }

    fun isUpdateExcludeField(field: Field?): Boolean {
        val columnInfo = fieldColumnInfoMap[field?.name]
        return null != columnInfo && columnInfo.excludeUpdate
    }

    fun isTableExcludeField(field: Field?): Boolean {
        val columnInfo = fieldColumnInfoMap[field?.name]
        return null != columnInfo && columnInfo.excludeTable
    }

    private fun putColumn(fieldName: String, columnInfo: ColumnInfo) {

        fieldColumnInfoMap[fieldName] = columnInfo
        columnFieldMap[columnInfo.columnName] = fieldName
    }

    companion object {

        /**
         * 生成component信息
         *
         * @param componentClass 实体类
         */
        fun init(componentClass: Class<*>, scanMode: Boolean = true): ComponentInfo? {

            if (TableInfo.isAlreadyInit(componentClass) && scanMode) return null
            val table = getComponentClass(componentClass) ?: return null
            var tableName = table.value
            if (tableName == "") {
                tableName = componentClass.simpleName.toLowerCase()
            }

            val componentInfo = ComponentInfo(componentClass)
            componentInfo.tableName = tableName
            componentInfo.selectColumns = table.selectColumns
            componentInfo.tableAlias = table.alias
            generateColumns(componentInfo, table.camelcaseToUnderLine)
            TableInfo.putComponent(componentClass, componentInfo)
            Log.debug("load component ${componentClass.name}, table is $tableName")
            return componentInfo
        }

        fun getComponentClass(componentClass: Class<*>): Table? {
            return if (componentClass.superclass != null) {
                getComponentClass(componentClass.superclass) ?: componentClass.getAnnotation(Table::class.java)
            } else {
                componentClass.getAnnotation(Table::class.java)
            }
        }

        /**
         * 生成component的列信息
         *
         * @param componentInfo component实例
         * @param camelcaseToUnderLine         是否将驼峰变量转为下划线列名
         */
        private fun generateColumns(componentInfo: ComponentInfo, camelcaseToUnderLine: Boolean = true) {

            var columnInfo: ColumnInfo

            for (field in TypeAdapter.getAllField(componentInfo.componentClass)) {
                field.isAccessible = true

                columnInfo = ColumnInfo()
                columnInfo.fieldType = field.type

                //生成exclude信息
                field.getAnnotation(Exclude::class.java)?.run {
                    columnInfo.excludeInsert = this.excludeInsert
                    columnInfo.excludeUpdate = this.excludeUpdate
                    columnInfo.excludeResult = this.excludeResult
                    columnInfo.excludeTable = this.excludeTable
                }
                field.getAnnotation(ExcludeResult::class.java)?.run {
                    columnInfo.excludeResult = this.value
                }

                //生成日期格式化信息
                field.getAnnotation(DateFormat::class.java)?.run {
                    columnInfo.dateFormat = this.value
                }

                //生成column info
                val columnAnnotation = field.getAnnotation(Column::class.java)
                if (columnAnnotation != null) {
                    columnAnnotation.run {
                        columnInfo.columnName = if (StringUtils.isNotEmpty(this.value)) this.value else field.name
                        columnInfo.comment = if (StringUtils.isNotEmpty(this.comment)) this.comment else null
                        columnInfo.defaultValue = if (StringUtils.isNotEmpty(this.defaultValue)) this.defaultValue else null
                        columnInfo.nullable = this.nullable
                        columnInfo.length = if (columnInfo.length != 0) this.length else null
                        columnInfo.unique=this.unique
                    }
                } else {
                    columnInfo.columnName = field.name
                }

                if (camelcaseToUnderLine) {
                    val regex = Regex("([a-z])([A-Z]+)")
                    val replacement = "$1_$2"
                    columnInfo.columnName = columnInfo.columnName.replace(regex, replacement).toLowerCase()
                }

                //generate primary key column info
                val primaryAnnotation = field.getAnnotation(Primary::class.java)
                if (primaryAnnotation != null || (field.name == "id" && StringUtils.isEmpty(componentInfo.primaryKey))) {
                    componentInfo.primaryKey = columnInfo.columnName
                    componentInfo.primaryField = field
                    primaryAnnotation.run {
                        columnInfo.autoIncrement = this?.autoIncrement ?: false
                        columnInfo.columnName = if (StringUtils.isNotEmpty(this.value)) this.value else field.name
                        columnInfo.comment = if (StringUtils.isNotEmpty(this.comment)) this.comment else null
                        columnInfo.length = if (columnInfo.length != 0) this.length else null
                    }

                }

                //generate foreign key column info
                field.getAnnotation(ForeignKey::class.java)?.run {
                    var joinColumnName = joinColumn
                    if (camelcaseToUnderLine) {
                        val regex = Regex("([a-z])([A-Z]+)")
                        val replacement = "$1_$2"
                        joinColumnName = joinColumn.replace(regex, replacement).toLowerCase()
                    }
                    if (autoJoin) {
                        componentInfo.autoJoinComponentList.add(value.java)
                    }
                    val foreignKeyInfo = ForeignKeyInfo(columnInfo.columnName, joinColumnName)
                    componentInfo.foreignKeyMap.put(value.java, foreignKeyInfo)
                }

                componentInfo.putColumn(field.name, columnInfo)
            }

        }
    }

}

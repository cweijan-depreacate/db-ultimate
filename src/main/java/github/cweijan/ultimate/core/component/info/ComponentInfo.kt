package github.cweijan.ultimate.core.component.info

import com.fasterxml.jackson.annotation.JsonFormat
import github.cweijan.ultimate.annotation.*
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.exception.ForeignKeyNotSetException
import github.cweijan.ultimate.exception.PrimaryValueNotSetException
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import java.lang.reflect.Field

class ComponentInfo(var componentClass: Class<*>) {

    var primaryKey: String? = null

    internal var primaryField: Field? = null

    lateinit var selectColumns: String

    lateinit var tableName: String

    internal var autoIncrement: Boolean = false

    var tableAlias: String? = null

    /**
     * 属性名与ColumnInfo的映射
     */
    val fieldColumnInfoMap by lazy {
        return@lazy HashMap<String, ColumnInfo>()
    }

    /**
     * 列名与属性名的映射
     */
    internal val columnInfoMap by lazy {
        return@lazy HashMap<String, ColumnInfo>()
    }

    /**
     * excel列名与field的映射
     */
    val excelHeaderFieldMap = HashMap<String, Field>()

    /**
     * 外键映射
     */
    internal val foreignKeyMap by lazy {
        return@lazy HashMap<Class<*>, ForeignKeyInfo>();
    }

    val autoJoinLazy = lazy { return@lazy ArrayList<Class<*>>(); }
    /**
     * 自动关联的外键列表
     */
    val autoJoinComponentList by autoJoinLazy

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
     * @param fieldName 属性名
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
     * 根据列名找ColumnInfo
     *
     * @param columnName 列名
     * @return columnInfo
     */
    fun getColumnInfoByColumnName(columnName: String): ColumnInfo? {

        return columnInfoMap[columnName]
    }

    @Throws(IllegalAccessException::class)
    fun getPrimaryValue(component: Any): Any? {

        return primaryField?.get(component)

    }

    @Throws(IllegalAccessException::class)
    fun setPrimaryValue(component: Any, primaryValue: Any) {

        if (autoIncrement && getPrimaryValue(component) == null) {
            primaryField?.set(component, primaryValue)
        }

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

    companion object {

        /**
         * 生成component信息
         *
         * @param componentClass 实体类
         */
        @JvmStatic
        fun init(componentClass: Class<*>, scanMode: Boolean = true): ComponentInfo {

            if (TableInfo.isAlreadyInit(componentClass) && scanMode) return TableInfo.getComponent(componentClass)
            val table = getComponentClass(componentClass)
            var tableName = table?.value?:""
            if (tableName == "") {
                tableName = TypeAdapter.convertHumpToUnderLine(componentClass.simpleName)!!
            }

            val componentInfo = ComponentInfo(componentClass)
            componentInfo.tableName = tableName
            componentInfo.selectColumns = table?.selectColumns?:"*"
            componentInfo.tableAlias = table?.alias
            generateColumns(componentInfo, table?.camelcaseToUnderLine?:true)
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
                columnInfo=ColumnInfo.init(componentInfo,field)
            }

        }
    }

}

package github.cweijan.ultimate.core.component.info

import github.cweijan.ultimate.annotation.Table
import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.excel.ExcludeExcel
import github.cweijan.ultimate.util.Log
import java.lang.reflect.Field

class ComponentInfo(var componentClass: Class<*>) {

    var primaryKey: String? = null

    internal var primaryField: Field? = null

    lateinit var selectColumns: String

    lateinit var tableName: String

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

    val joinLazy = lazy { return@lazy ArrayList<Class<*>>(); }
    /**
     * 关联的外键列表
     */
    val joinComponentList by joinLazy
    val oneToManyLazy = lazy { return@lazy ArrayList<OneToManyInfo>(); }
    /**
     * 关联的外键列表
     */
    val oneToManyList by oneToManyLazy
    val oneToOneLazy = lazy { return@lazy ArrayList<OneToOneInfo>(); }
    /**
     * 关联的外键列表
     */
    val oneToOneList by oneToOneLazy

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
    fun getColumnInfoByFieldName(fieldName: String?): ColumnInfo? {

        fieldName ?: return null

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

    fun getValueByFieldName(component: Any?, fieldName: String?): Any? {
        component ?: return null
        return fieldColumnInfoMap[fieldName]?.field?.get(component)
    }

    fun getPrimaryValue(component: Any): Any? {

        return primaryField?.get(component)
    }

    @Throws(IllegalAccessException::class)
    fun setPrimaryValue(component: Any, primaryValue: Any) {

        if (getPrimaryValue(component) == null) {
            primaryField?.set(component, primaryValue)
        }

    }

    fun<T> getExcelHeaderAndValues(dataList: List<T>): Pair<Array<ArrayList<Any?>>, List<String>> {
        val values = Array(dataList.size) { ArrayList<Any?>() }
        val headers = fieldColumnInfoMap.keys.filter { key ->
            val field = componentClass.getDeclaredField(key)
            field.getAnnotation(ExcludeExcel::class.java)?.run { return@filter false }
            field.isAccessible = true
            dataList.forEachIndexed { dataIndex, data ->
                values[dataIndex].add(TypeAdapter.convertAdapter(componentClass, field.name, field.get(data) ?: ""))
            }
            return@filter true
        }.map { key -> fieldColumnInfoMap[key]!!.excelHeader }
        return Pair(values, headers)
    }

    fun getExcelHeaders():List<String>{
        return fieldColumnInfoMap.keys.filter { key ->
            val field = componentClass.getDeclaredField(key)
            field.getAnnotation(ExcludeExcel::class.java)?.run { return@filter false }
            field.isAccessible = true

            return@filter true
        }.map { key -> fieldColumnInfoMap[key]!!.excelHeader }
    }

    fun isExcludeField(field: Field?): Boolean {
        val columnInfo = fieldColumnInfoMap[field?.name]
        return null != columnInfo && columnInfo.exclude
    }

    companion object {

        /**
         * 生成component信息
         *
         * @param componentClass 实体类
         * @param scanMode 是否扫描模式,扫描模式不会重新加载Componnet
         */
        @JvmStatic
        fun init(componentClass: Class<*>, scanMode: Boolean = true): ComponentInfo {

            if (TableInfo.isAlreadyInit(componentClass) && scanMode) return TableInfo.getComponent(componentClass)
            val table = getComponentClass(componentClass)
            var tableName = table?.value ?: ""
            if (tableName == "") {
                tableName = TypeAdapter.convertHumpToUnderLine(componentClass.simpleName)!!
            }

            val componentInfo = ComponentInfo(componentClass)
            componentInfo.tableName = tableName
            componentInfo.selectColumns ="*"
            componentInfo.tableAlias = table?.alias
            //生成列信息
            for (field in TypeAdapter.getAllField(componentInfo.componentClass)) {
                ColumnInfo.init(componentInfo, field)
            }
            TableInfo.putComponent(componentClass, componentInfo)
            if (scanMode)
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

    }

}

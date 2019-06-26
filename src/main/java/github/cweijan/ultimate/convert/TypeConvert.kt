package github.cweijan.ultimate.convert

import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.util.Log
import java.lang.reflect.Field
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.util.*
import kotlin.collections.HashMap

object TypeConvert {

    /**
     * 将resultSet转为java对象,根据列名与field进行映射
     *
     * @param resultSet 查询结果集
     * @param beanClass 要转换的实体类型
     * @return 返回转换完成的实体
     */
    fun <T> resultSetToBean(resultSet: ResultSet, beanClass: Class<T>, hadNext: Boolean = false): T? {

        if (!hadNext && !resultSet.next()) return null

        val columns = getColumns(resultSet)


        return toJavaBean(resultSet, beanClass, columns)
    }

    /**
     * 将resultSet转为java对象List
     *
     * @param resultSet 查询的结果集
     * @param beanClass 要转换的类型
     * @return 转换完成的实体列表
     */
    fun <T> resultSetToBeanList(resultSet: ResultSet, beanClass: Class<T>): List<T> {

        val beanList = ArrayList<T>()
        while (resultSet.next()) {
            beanList.add(resultSetToBean(resultSet, beanClass, true)!!)
        }

        return beanList
    }

    fun resultSetToMapList(resultSet: ResultSet): List<Map<String, Any>> {
        val list = ArrayList<Map<String, Any>>()
        while (resultSet.next()) {
            list.add(resultSetToMap(resultSet, true)!!)
        }
        return list
    }

    fun resultSetToMap(resultSet: ResultSet, hadNext: Boolean = false): Map<String, Any>? {

        if (!hadNext && !resultSet.next()) return null

        val md = resultSet.metaData
        val columns = md.columnCount
        val row = HashMap<String, Any>(columns)
        for (i in 1..columns) {
            row[md.getColumnLabel(i)] = resultSet.getObject(i)
        }
        return row;
    }

    /**
     * 将resultSet一列转为javaBean
     *
     * @param resultSet sql查询的结果
     * @param clazz     要转换成的javaBean类型
     * @param columns   Field对应的列名
     * @return 转换完成的实体类型
     */
    private fun <T> toJavaBean(resultSet: ResultSet, clazz: Class<T>, columns: HashMap<String, String>): T? {

        val beanInstance: T
        try {
            beanInstance = clazz.newInstance()
        } catch (e: Exception) {
            return null
        }
        if (columns.keys.isEmpty()) return beanInstance
        val component = TableInfo.getComponent(clazz)
        val objectMap = HashMap<Field, Class<*>>();

        // 为对象进行赋值
        for (field in TypeAdapter.getAllField(clazz)) {

            field.isAccessible = true
            val fieldName = field.name
            val fieldType = field.type.name
            val key = component.getColumnNameByFieldName(fieldName)?.toLowerCase()
            if (component.isQueryExcludeField(field) ||
                    (!columns.containsKey(key) && TypeAdapter.isAdapterType(field.type))) {
                continue
            }
            val columnName = columns[key]

            try {
                when {
                    TypeAdapter.isAdapterType(field.type) -> field.set(beanInstance, TypeAdapter.convertJavaObject(component.componentClass, field, try {
                        resultSet.getObject(columnName)
                    } catch (e: Exception) {
                        Log.error(e.message);null
                    }))
                    else -> objectMap[field] = Class.forName(fieldType)
                }
                columns.remove(key)
            } catch (e: Exception) {
                Log.error(e.message, e)
            }

        }
        objectMap.forEach { field, fieldClass -> field.set(beanInstance, toJavaBean(resultSet, fieldClass, columns)) }

        return beanInstance
    }

    /**
     * 获取resultSet字段Map key为字段,value为字段类型
     *
     * @param resultSet 查询的结果集
     */
    private fun getColumns(resultSet: ResultSet): HashMap<String, String> {

        val columns = HashMap<String, String>()
        val metaData: ResultSetMetaData = resultSet.metaData

        // 获取resultSet字段类型
        for (i in 1..metaData.columnCount) {
            columns[metaData.getColumnLabel(i).toLowerCase()] = metaData.getColumnLabel(i)
        }

        return columns
    }

}

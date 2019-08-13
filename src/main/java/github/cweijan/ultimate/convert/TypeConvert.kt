package github.cweijan.ultimate.convert

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.springboot.util.ServiceMap
import github.cweijan.ultimate.util.Log
import org.springframework.beans.BeanUtils
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
        val component = TableInfo.getComponent(beanClass)
        beanList.forEach { bean ->
            //一对多赋值
            if (component.oneToManyLazy.isInitialized()) {
                component.oneToManyList.forEach { oneToManyInfo ->
                    oneToManyInfo.oneTomanyField.set(bean, ServiceMap.get(oneToManyInfo.relationClass.javaObjectType)
                            .findBy(oneToManyInfo.relationColumn, component.getValueByFieldName(bean, component.primaryField!!.name)))
                }
            }
            // 一对一赋值
            if (component.oneToOneLazy.isInitialized()) {
                component.oneToOneList.forEach { oneToOneInfo ->
                    oneToOneInfo.oneToOneField.set(bean, ServiceMap.get(oneToOneInfo.relationClass.javaObjectType)
                            .getBy(oneToOneInfo.relationColumn, component.getValueByFieldName(bean, component.primaryField!!.name)))
                }
            }
        }

        return beanList
    }

    private fun resultSetToMap(resultSet: ResultSet): Map<out Any, Any?>? {

        val md = resultSet.metaData
        val columns = md.columnCount
        val row = HashMap<String, Any?>(columns)
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
            if (Map::class.java.isAssignableFrom(clazz)) {
                beanInstance = if (clazz.isInterface) BeanUtils.instantiateClass(HashMap::class.java) as T
                else BeanUtils.instantiateClass(clazz)
                resultSetToMap(resultSet)?.run {
                    (beanInstance as MutableMap<Any, Any?>).putAll(this)
                }
                return beanInstance
            } else {
                beanInstance = clazz.newInstance()
            }
        } catch (e: Exception) {
            Log.getLogger().error(e.message, e)
            return null
        }
        if (columns.keys.isEmpty()) return beanInstance
        val component = TableInfo.getComponent(clazz)

        // 为对象进行赋值
        for (field in TypeAdapter.getAllField(clazz)) {

            field.isAccessible = true
            val fieldName = field.name
            val key = component.getColumnNameByFieldName(fieldName)?.toLowerCase()
            if (component.isExcludeField(field) ||
                    (!columns.containsKey(key) && TypeAdapter.isAdapterType(field.type))) {
                continue
            }
            val columnName = columns[key]

            try {
                if (TypeAdapter.isAdapterType(field.type) || Collection::class.java.isAssignableFrom(field.type) || field.getAnnotation(Blob::class.java) != null) {
                    field.set(beanInstance, TypeAdapter.convertJavaObject(component.componentClass, field, try {
                        resultSet.getObject(columnName)
                    } catch (e: Exception) {
                        Log.error(e.message);null
                    }))
                }
            } catch (e: Exception) {
                Log.error(e.message, e)
            }

        }
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

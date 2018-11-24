package github.cweijan.ultimate.convert

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.util.*

object TypeConvert {

    private val logger = Log.logger

    /**
     * 将resultSet转为java对象,根据列名与field进行映射
     *
     * @param resultSet 查询结果集
     * @param beanClass 要转换的实体类型
     * @return 返回转换完成的实体
     */
    fun <T> resultSetToBean(resultSet: ResultSet, beanClass: Class<T>): T? {

        if (!resultSet.next()) {
            return null
        }

        val columns = getColumns(resultSet, beanClass)

        return toJavaBean(resultSet, beanClass, columns)
    }

    /**
     * 将resultSet一列转为javaBean
     *
     * @param resultSet sql查询的结果
     * @param clazz     要转换成的javaBean类型
     * @param columns   Field对应的列名
     * @return 转换完成的实体类型
     */
    private fun <T> toJavaBean(resultSet: ResultSet, clazz: Class<T>, columns: Map<String, String>): T {

        val fields = clazz.declaredFields
        val beanInstance = clazz.newInstance()

        // 为对象进行赋值
        for (field in fields) {

            field.isAccessible = true
            val fieldName = field.name
            val fieldType = field.type.name
            if (!columns.containsKey(fieldName) && TypeAdapter.isSimpleType(fieldType)) {
                continue
            }
            val columnName = columns[fieldName]

            try {
                if (TypeAdapter.isSimpleType(fieldType)) {
                    field.set(beanInstance, resultSet.getObject(columnName))
                } else {
                    val fieldClass = Class.forName(fieldType)
                    field.set(beanInstance, toJavaBean(resultSet, fieldClass, getColumns(resultSet, fieldClass)))
                }
            } catch (e: Exception) {
                logger.error(e.message, e)
            }

        }
        return beanInstance
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
        val columns = getColumns(resultSet, beanClass)

        while (resultSet.next()) {
            beanList.add(toJavaBean(resultSet, beanClass, columns))
        }

        return beanList
    }

    /**
     * 获取resultSet字段Map key为字段,value为字段类型
     *
     * @param resultSet 查询的结果集
     */
    private fun getColumns(resultSet: ResultSet, clazz: Class<*>): Map<String, String> {

        val columns = HashMap<String, String>()
        val component = TableInfo.getComponent(clazz)
        val metaData: ResultSetMetaData = resultSet.metaData

        // 获取resultSet字段类型
        for (i in 1..metaData.columnCount) {
            val fieldName = component.getFieldNameByColumnName(metaData.getColumnLabel(i)) ?: ""
            if (StringUtils.isEmpty(fieldName)) continue
            columns[fieldName] = metaData.getColumnLabel(i)
        }

        return columns
    }
}

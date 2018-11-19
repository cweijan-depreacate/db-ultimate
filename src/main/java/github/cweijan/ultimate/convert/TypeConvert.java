package github.cweijan.ultimate.convert;

import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.util.Log;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeConvert{

    private static Logger logger = Log.getLogger();

    /**
     将resultSet转为java对象,根据列名与field进行映射

     @param resultSet 查询结果集
     @param beanClass 要转换的实体类型
     @return 返回转换完成的实体
     */
    public static <T> T resultSetToBean(ResultSet resultSet, Class<T> beanClass){

        if(!next(resultSet)){
            return null;
        }

        Map<String, String> columns = getColumns(resultSet, beanClass);

        return toJavaBean(resultSet, beanClass, columns);
    }

    /**
     统一处理next异常,判断是否Result已到末尾

     @param resultSet sql查询的结果
     @return 是否可读取
     */
    private static boolean next(ResultSet resultSet){

        boolean result = false;

        try{
            result = resultSet.next();
        } catch(SQLException e){
            logger.error("access fail!", e);
        }

        return result;
    }

    /**
     将resultSet一列转为javaBean

     @param resultSet sql查询的结果
     @param clazz     要转换成的javaBean类型
     @param columns   Field对应的列名
     @return 转换完成的实体类型
     */
    private static <T> T toJavaBean(ResultSet resultSet, Class<T> clazz, Map<String, String> columns){

        Field[] fields = clazz.getDeclaredFields();
        T beanInstance;
        try{
            beanInstance = clazz.newInstance();
        } catch(InstantiationException | IllegalAccessException e){
            logger.error("this class have not default constructor!", e);
            return null;
        }

        // 为对象进行赋值
        for(Field field : fields){

            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldType = field.getType().getName();
            if(!columns.containsKey(fieldName) && TypeAdapter.isSimpleType(fieldType)){
                continue;
            }
            String columnName = columns.get(fieldName);

            try{
                if(TypeAdapter.isSimpleType(fieldType)){
                    field.set(beanInstance, resultSet.getObject(columnName));
                } else{
                    Class<?> fieldClass = Class.forName(fieldType);
                    field.set(beanInstance, toJavaBean(resultSet, fieldClass, columns));
                }
            } catch(Exception e){
                logger.error(e.getMessage(), e);
            }
        }
        return beanInstance;
    }

    /**
     将resultSet转为java对象List

     @param resultSet 查询的结果集
     @param beanClass 要转换的类型
     @return 转换完成的实体列表
     */
    public static <T> List<T> resultSetToBeanList(ResultSet resultSet, Class<T> beanClass){

        List<T> beanList = new ArrayList<>();

        Map<String, String> columns = getColumns(resultSet, beanClass);
        T bean;

        while(next(resultSet)){

            bean = toJavaBean(resultSet, beanClass, columns);

            beanList.add(bean);
        }

        return beanList;
    }

    /**
     获取resultSet字段Map key为字段,value为字段类型

     @param resultSet 查询的结果集
     */
    public static Map<String, String> getColumns(ResultSet resultSet, Class clazz){

        Map<String, String> columns = new HashMap<>();
        ComponentInfo component = TableInfo.getComponent(clazz);
        ResultSetMetaData metaData;
        String fieldName;

        // 获取resultSet字段类型
        try{
            metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            for(int i = 1; i <= count; i++){
                fieldName = component.getFieldNameByColumnName(metaData.getColumnLabel(i));
                columns.put(fieldName, component.getColumnNameByFieldName(fieldName));
            }
        } catch(SQLException e){
            logger.error("get result column info fail! \n " + e.getMessage(), e);
        }

        return columns;
    }
}

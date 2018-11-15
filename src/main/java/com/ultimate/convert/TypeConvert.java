package com.ultimate.convert;

import com.ultimate.component.TableInfo;
import com.ultimate.component.info.ColumnInfo;
import com.ultimate.component.info.ComponentInfo;
import com.ultimate.util.Log;
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

        Map<String, String> columns = getColumns(resultSet);

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
        T beanInstance = null;
        try{
            beanInstance = clazz.newInstance();
        } catch(InstantiationException e){
            logger.error("this class have not default constructor!", e);
        } catch(IllegalAccessException e){
            logger.error("this class not support instance!", e);
        }

        // 为对象进行赋值
        for(Field field : fields){

            field.setAccessible(true);
            String fieldName = field.getName();
            if(!columns.containsKey(fieldName)){
                continue;
            }
            String fieldType = columns.get(fieldName);

            try{
                System.out.println(fieldType);
                // TODO 这里需要增加其他类型的支持
                switch(fieldType){
                    case "INT":
                        field.set(beanInstance, resultSet.getInt(fieldName));
                        break;
                    case "LONG":
                        field.set(beanInstance, resultSet.getLong(fieldName));
                        break;
                    case "VARCHAR":
                        field.set(beanInstance, resultSet.getString(fieldName));
                        break;
                    case "FLOAT":
                    case "DECIMAL":
                        field.set(beanInstance, resultSet.getFloat(fieldName));
                        break;
                    case "DOUBLE":
                        field.set(beanInstance, resultSet.getDouble(fieldName));
                        break;
                }
            } catch(Exception e){
                logger.error("set fieldType error!", e);
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

        Map<String, String> columns = getColumns(resultSet);
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
    public static Map<String, String> getColumns(ResultSet resultSet){

        ResultSetMetaData metaData;
        Map<String, String> columns = null;
        String columnLabel;

        // 获取resultSet字段类型
        try{
            metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            columns = new HashMap<>();

            for(int i = 1; i <= count; i++){
                columnLabel = metaData.getColumnLabel(i);
                columns.put(columnLabel, metaData.getColumnTypeName(i));
            }
        } catch(SQLException e){
            logger.error(e.getMessage(), e);
        }

        return columns;
    }
}

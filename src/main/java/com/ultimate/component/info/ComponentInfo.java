package com.ultimate.component.info;

import com.ultimate.annotation.Column;
import com.ultimate.annotation.Exclude;
import com.ultimate.annotation.Primary;
import com.ultimate.annotation.Table;
import com.ultimate.component.TableInfo;
import com.ultimate.convert.TypeAdapter;
import com.ultimate.exception.ColumnNotExistsException;
import com.ultimate.util.Log;
import com.ultimate.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentInfo{

    private String primaryKey;

    private String tableName;

    private Class<?> componentClass;

    private List<String> columnList;

    private List<String> fieldNameList;

    private Map<String, ColumnInfo> fileColumnMap;

    public ColumnInfo getColumnInfoByFieldName(String fieldName){

        ColumnInfo columnInfo = fileColumnMap.get(fieldName);
        if(columnInfo==null){
               throw new ColumnNotExistsException("尝试获取列信息"+fieldName+"失败!");
        }

        return columnInfo;
    }

    private void putColumn(String fieldName, ColumnInfo columnInfo){

        if(fileColumnMap == null){
            fileColumnMap = new HashMap<>();
            columnList = new ArrayList<>();
            fieldNameList = new ArrayList<>();
        }

        fileColumnMap.put(fieldName, columnInfo);
        fieldNameList.add(fieldName);
        columnList.add(columnInfo.getJdbcName());

    }

    /**
     生成component信息

     @param componentClass 实体类
     */
    public static void init(Class<?> componentClass){

        Table table = componentClass.getAnnotation(Table.class);
        String tableName = table.value();
        if(StringUtils.isEmpty(table.value())){
            tableName = componentClass.getSimpleName().toLowerCase();
        }
        ComponentInfo componentInfo = new ComponentInfo();
        componentInfo.setComponentClass(componentClass);
        componentInfo.setTableName(tableName);
        generateColumns(componentInfo, componentClass);
        TableInfo.putComponent(componentClass, componentInfo);
        Log.getLogger().debug("load class " + componentClass.getName() + ", table is " + tableName);

    }

    /**
     生成component的列信息

     @param componentInfo component实例
     @param clazz         实体类
     */
    private static void generateColumns(ComponentInfo componentInfo, Class clazz){

        Field[] fields = clazz.getDeclaredFields();
        ColumnInfo columnInfo;

        for(Field field : fields){
            Exclude excludeAnnotation = field.getAnnotation(Exclude.class);
            if(excludeAnnotation != null){
                continue;
            }
            columnInfo = new ColumnInfo();
            String columnName;
            Column columnAnnotation = field.getAnnotation(Column.class);
            if(columnAnnotation != null && StringUtils.isNotEmpty(columnAnnotation.value())){
                columnName = columnAnnotation.value();
                if(columnAnnotation.nullable()){
                    columnInfo.setNullable(true);
                }
                if(columnAnnotation.length()!=0){
                    columnInfo.setLength(columnAnnotation.length());
                }
            } else{
                columnName = field.getName();
            }

            Primary primaryAnnotation = field.getAnnotation(Primary.class);
            if(primaryAnnotation != null){
                componentInfo.setPrimaryKey(columnName);
                if(primaryAnnotation.autoIncrement()){
                    columnInfo.setAutoIncrement(true);
                }
            }

            if(field.getName().equals("id") && StringUtils.isEmpty(componentInfo.getPrimaryKey())){
                componentInfo.setPrimaryKey(field.getName());
            }

            columnInfo.setFieldName(field.getName());
            columnInfo.setFieldType(field.getType());
            columnInfo.setJdbcName(columnName);
            columnInfo.setJdbcType(TypeAdapter.getJdbcType(field.getType()));
            columnInfo.setNumeric(TypeAdapter.checkNumericType(field.getType()));
            componentInfo.putColumn(field.getName(), columnInfo);
        }

    }

    @Override
    public String toString(){

        return "ComponentInfo{" + "primaryKey='" + primaryKey + '\'' + ", tableName='" + tableName + '\'' + ", columnList=" + columnList + '}';
    }

    public List<String> getFieldNameList(){

        return fieldNameList;
    }

    public Map<String, ColumnInfo> getFieldColumnMap(){

        return fileColumnMap;
    }

    /**
     @return 返回所有列信息, 用于查询
     */
    public String getAllColumns(){

        String allColumns = columnList.toString();

        return allColumns.substring(1, allColumns.length() - 1);
    }

    public String getTableName(){

        return tableName;
    }

    public void setTableName(String tableName){

        this.tableName = tableName;
    }

    public String getPrimaryKey(){

        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey){

        this.primaryKey = primaryKey;
    }

    public Class<?> getComponentClass(){

        return componentClass;
    }

    public void setComponentClass(Class componentClass){

        this.componentClass = componentClass;
    }

}

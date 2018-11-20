package github.cweijan.ultimate.component.info;

import github.cweijan.ultimate.annotation.Exclude;
import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.util.Log;
import github.cweijan.ultimate.util.StringUtils;
import github.cweijan.ultimate.annotation.Column;
import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;
import github.cweijan.ultimate.convert.TypeAdapter;

import java.lang.reflect.Field;
import java.util.*;

public class ComponentInfo{

    private String primaryKey;

    private String primaryFieldName;

    private Field primaryField;

    private String tableName;

    private Class<?> componentClass;

    /**
     所有列,用于insert语句
     */
    private List<String> columnList;

    /**
     不包含主键的所有列,用于Insert语句
     */
    private List<String> notPrimaryColumnList;

    /**
     exclude column list
     */
    private List<String> excludeColumnList;

    /**
     属性名与ColumnInfo的映射
     */
    private Map<String, ColumnInfo> fieldColumnInfoMap;

    /**
     列名与属性名的映射
     */
    private Map<String, String> columnFieldMap;

    public String getColumnNameByFieldName(String fieldName){

        ColumnInfo columnInfo = fieldColumnInfoMap.get(fieldName);
        if(columnInfo == null){
            return null;
        }

        return columnInfo.getColumnName();
    }

    public Object getPrimaryValue(Object component) throws IllegalAccessException{

        return primaryField.get(component);

    }

    /**
     根据列名找到属性名

     @param columnName 列名
     @return 对应的属性名
     */
    public String getFieldNameByColumnName(String columnName){

        return columnFieldMap.get(columnName);
    }

    private void putColumn(String fieldName, ColumnInfo columnInfo){

        if(fieldColumnInfoMap == null){
            fieldColumnInfoMap = new HashMap<>();
            columnFieldMap = new HashMap<>();
            columnList = new ArrayList<>();

        }

        fieldColumnInfoMap.put(fieldName, columnInfo);
        columnFieldMap.put(columnInfo.getColumnName(), fieldName);
        columnList.add(columnInfo.getColumnName());
    }

    public boolean isExcludeField(Field field){

        excludeColumnList = Optional.ofNullable(excludeColumnList).orElse(new ArrayList<>());

        return field != null && excludeColumnList.contains(field.getName());
    }

    /**
     生成component信息

     @param componentClass 实体类
     */
    public static void init(Class<?> componentClass){

        if(TableInfo.isAlreadyInit(componentClass)) return;

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
                componentInfo.excludeColumnList = Optional.ofNullable(componentInfo.excludeColumnList).orElse(new ArrayList<>());
                componentInfo.excludeColumnList.add(field.getName());
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
                if(columnAnnotation.length() != 0){
                    columnInfo.setLength(columnAnnotation.length());
                }
            } else{
                columnName = field.getName();
            }
field.setAccessible(true);
            Primary primaryAnnotation = field.getAnnotation(Primary.class);
            if(primaryAnnotation != null){
                componentInfo.setPrimaryKey(columnName);
                componentInfo.setPrimaryFieldName(field.getName());
                componentInfo.primaryField=field;
                if(primaryAnnotation.autoIncrement()){
                    columnInfo.setAutoIncrement(true);
                }
            } else if(field.getName().equals("id") && StringUtils.isEmpty(componentInfo.getPrimaryKey())){
                componentInfo.setPrimaryKey(field.getName());
                componentInfo.setPrimaryFieldName(field.getName());
                componentInfo.primaryField=field;
            } else{
                componentInfo.notPrimaryColumnList = new ArrayList<>();
                componentInfo.notPrimaryColumnList.add(columnName);
            }

            columnInfo.setColumnName(columnName);
            columnInfo.setNumeric(TypeAdapter.checkNumericType(field.getType()));
            componentInfo.putColumn(field.getName(), columnInfo);
        }

    }

    @Override
    public String toString(){

        return "ComponentInfo{" + "primaryKey='" + primaryKey + '\'' + ", tableName='" + tableName + '\'' + ", columnList=" + columnList + '}';
    }

    public boolean isPrimaryField(Field field){
        if(null==field)return false;

        return Objects.equals(field.getName(),primaryFieldName);
    }

    /**
     @return 返回所有列信息, 用于insert语句
     */
    public String getAllColumns(){

        String allColumns = columnList.toString();
        return allColumns.substring(1, allColumns.length() - 1);
    }

    public String getPrimaryFieldName(){

        return primaryFieldName;
    }

    public void setPrimaryFieldName(String primaryFieldName){

        this.primaryFieldName = primaryFieldName;
    }

    /**
     @return 返回不包含主键的所有列信息, 用于insert语句
     */
    public String getNotPrimaryColumns(){

        String notPrimaryColumns = notPrimaryColumnList.toString();
        return notPrimaryColumns.substring(1, notPrimaryColumns.length() - 1);
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

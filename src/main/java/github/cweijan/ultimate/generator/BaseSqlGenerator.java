package github.cweijan.ultimate.generator;

import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.convert.TypeAdapter;
import github.cweijan.ultimate.util.DateUtils;
import github.cweijan.ultimate.util.Log;
import github.cweijan.ultimate.util.StringUtils;
import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.core.Operation;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public abstract class BaseSqlGenerator implements SqlGenerator{

    @Override
    public String generateInsertSql(Object component, boolean selective){

        ComponentInfo componentInfo = TableInfo.getComponent(component.getClass());
        StringBuilder values = new StringBuilder();
        Field[] fields = componentInfo.getComponentClass().getDeclaredFields();
        String columns = componentInfo.getNotPrimaryColumns();
        String values2 = "";
        StringBuilder columnsBuild = new StringBuilder();
        for(Field field : fields){
            try{
                field.setAccessible(true);
                Object fieldValue = field.get(component);
                if(selective && fieldValue == null || componentInfo.isExcludeField(field)){
                    continue;
                }
                if(fieldValue != null && componentInfo.isPrimaryField(field)){
                    columns = componentInfo.getAllColumns();
                }
                if(selective){
                    columnsBuild.append(componentInfo.getColumnNameByFieldName(field.getName()));
                }
                if(String.class.equals(field.getType()) || field.getType().getName().equals("chat") || Character.class.equals(field.getType())){
                    if(fieldValue == null) fieldValue = "";
                    values.append("'").append(fieldValue).append("'");
                } else if(Date.class.equals(field.getType())){
                    if(fieldValue == null) fieldValue = "";
                    String date = DateUtils.formatDate((Date) fieldValue);
                    values.append("'").append(date).append("'");
                } else if(TypeAdapter.isSimpleType(field.getType().getName())){
                    if(fieldValue == null) fieldValue = 0;
                    values.append(fieldValue);
                } else{
                    values.append(fieldValue);
                }
                values.append(",");
                columnsBuild.append(",");
            } catch(IllegalAccessException e){
                Log.getLogger().error(e.getMessage(), e);
            }
        }
        if(selective){
            columns = columnsBuild.toString();
            if(columns.lastIndexOf(",") != -1){
                columns = columns.substring(0, columns.lastIndexOf(","));
            }
        }
        if(values.lastIndexOf(",") != -1){
            values2 = values.substring(0, values.lastIndexOf(","));
            Log.getLogger().info(values2);
        }

        return "insert into " + componentInfo.getTableName() + "(" + columns + ") values(" + values2 + ");";
    }

    @Override
    public String generateUpdateSql(Object component) throws IllegalAccessException{

        ComponentInfo componentInfo = TableInfo.getComponent(component.getClass());
        Object primaryValue = componentInfo.getPrimaryValue(component);
        StringBuilder sql = new StringBuilder("UPDATE " + componentInfo.getTableName() + " a set ");

        Field[] fields = component.getClass().getDeclaredFields();
        for(Field field : fields){
            try{
                field.setAccessible(true);
                Object fieldValue = field.get(component);
                sql.append(field.getName() + "=");
                if(TypeAdapter.isCharacterType(field.getType().getName())){
                    if(fieldValue == null) fieldValue = "";
                    sql.append("'").append(fieldValue).append("'");
                } else if(TypeAdapter.isDateType(field.getType().getName())){
                    if(fieldValue == null) fieldValue = "";
                    String date = DateUtils.formatDate((Date) fieldValue);
                    sql.append("'").append(date).append("'");
                } else if(TypeAdapter.isSimpleType(field.getType().getName())){
                    if(fieldValue == null) fieldValue = 0;
                    sql.append(fieldValue);
                } else{
                    sql.append(fieldValue);
                }
                sql.append(",");
            } catch(IllegalAccessException e){
                Log.getLogger().error(e.getMessage(), e);
            }

        }
        sql.append(" where ").append(componentInfo.getPrimaryKey()).append("='").append(primaryValue).append("'");
        return null;
    }

    @Override
    public String generateDeleteSql(ComponentInfo componentInfo, Operation operation){

        String sql = "DELETE FROM " + componentInfo.getTableName();
        sql += generateOperationSql(operation);
        return sql;
    }

    @Override
    public String generateCountSql(ComponentInfo componentInfo, Operation operation){

        String sql = "select count(*) count from " + componentInfo.getTableName();
        return sql + generateOperationSql(operation);
    }

    @Override
    public String generateUpdateSql(ComponentInfo componentInfo, Operation operation){

        StringBuilder sql = new StringBuilder("UPDATE " + componentInfo.getTableName() + " a set ");

        operation.getUpdateList().forEach((key, value)->sql.append(key).append("='").append(value).append("',"));
        String updateSql;
        if(sql.lastIndexOf(",") != -1){
            updateSql = sql.substring(0, sql.lastIndexOf(","));
        } else{
            updateSql = sql.toString();
        }
        updateSql += generateOperationSql(operation);
        return updateSql;
    }

    @Override
    public String generateSelectSql(ComponentInfo componentInfo, Operation operation){

        return "select " + operation.getColumn() + " from " + componentInfo.getTableName() + generateOperationSql(operation);
    }

    private String generateOperationSql(Operation operation){

        String AND = "and";
        String OR = "or";
        String sql = "";

        sql += generateOperationSql0(operation.getEqualsOperation(), "=?", AND, operation);
        sql += generateOperationSql0(operation.getNotEqualsOperation(), "!=?", AND, operation);
        sql += generateOperationSql0(operation.getSearchOperation(), "like ?", AND, operation);
        sql += generateOperationSql0(operation.getOrEqualsOperation(), "=?", OR, operation);

        if(sql.startsWith(AND)){
            sql = sql.replaceFirst(AND, "");
            sql = " where" + sql;
        }
        if(sql.startsWith(OR)){
            sql = sql.replaceFirst(OR, "");
            sql = " where" + sql;
        }

        if(StringUtils.isNotEmpty(operation.getOrderBy())){
            sql += " order by " + operation.getOrderBy();
        }

        return sql;
    }

    private String generateOperationSql0(Map<String, List<String>> operationMap, String type, String separator, Operation operation){

        StringBuilder sql = new StringBuilder();

        if(operationMap != null){
            operationMap.forEach((key, operations)->operations.forEach(value->{
                sql.append(separator).append(" ");
                sql.append(key).append(" ");
                sql.append(type).append(" ");
                operation.addParam(value);
            }));
        }

        return sql.toString();
    }
}

package com.ultimate.generator;

import com.ultimate.component.TableInfo;
import com.ultimate.component.info.ComponentInfo;
import com.ultimate.core.Operation;
import com.ultimate.util.DateUtils;
import com.ultimate.util.Log;
import com.ultimate.util.StringUtils;

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
        String columns = componentInfo.getAllColumns();
        StringBuilder columnsBuild = new StringBuilder();
        IntStream.range(0, fields.length).forEach(index->{
            try{
                Field field = fields[index];
                field.setAccessible(true);
                Object fieldValue = field.get(component);
                if(selective && fieldValue == null){
                    return;
                }
                if(selective){
                    columnsBuild.append(componentInfo.getColumnNameByFieldName(field.getName()));
                }
                if(String.class.equals(field.getType()) || field.getType().getName().equals("chat") || Character.class.equals(field.getType())){
                    values.append("'").append(fieldValue).append("'");
                } else if(Date.class.equals(field.getType())){
                    String date = DateUtils.formatDate((Date) fieldValue);
                    values.append("'").append(date).append("'");
                } else{
                    values.append(fieldValue);
                }
                if(index != fields.length - 1){
                    values.append(",");
                    columnsBuild.append(",");
                }
            } catch(IllegalAccessException e){
                Log.getLogger().error(e.getMessage(), e);
            }
        });
        if(selective){
            columns = columnsBuild.toString();
        }

        return "insert into " + componentInfo.getTableName() + "(" + columns + ") values(" + values.toString() + ");";
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

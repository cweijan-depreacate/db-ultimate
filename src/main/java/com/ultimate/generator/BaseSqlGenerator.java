package com.ultimate.generator;

import com.ultimate.component.TableInfo;
import com.ultimate.component.info.ComponentInfo;
import com.ultimate.core.Condition;
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
    public String generateDeleteSql(ComponentInfo componentInfo, Condition condition){

        String sql = "DELETE FROM " + componentInfo.getTableName();
        sql += generateConditionSql(condition);
        return sql;
    }

    @Override
    public String generateCountSql(ComponentInfo componentInfo, Condition condition){

        String sql = "select count(*) count from " + componentInfo.getTableName();
        return sql + generateConditionSql(condition);
    }

    @Override
    public String generateUpdateSql(ComponentInfo componentInfo, Condition condition){

        StringBuilder sql = new StringBuilder("UPDATE " + componentInfo.getTableName() + " a set ");

        condition.getUpdateList().forEach((key, value)->sql.append(key).append("='").append(value).append("',"));
        String updateSql;
        if(sql.lastIndexOf(",") != -1){
            updateSql = sql.substring(0, sql.lastIndexOf(","));
        } else{
            updateSql = sql.toString();
        }
        updateSql += generateConditionSql(condition);
        return updateSql;
    }

    @Override
    public String generateSelectSql(ComponentInfo componentInfo, Condition condition){

        return "select " + condition.getColumn() + " from " + componentInfo.getTableName() + generateConditionSql(condition);
    }

    private String generateConditionSql(Condition condition){

        String AND = "and";
        String OR = "or";
        String sql = "";

        sql += generateConditionSql0(condition.getEqualsCondition(), "=?", AND, condition);
        sql += generateConditionSql0(condition.getNotEqualsCondition(), "!=?", AND, condition);
        sql += generateConditionSql0(condition.getSearchCondition(), "like ?", AND, condition);
        sql += generateConditionSql0(condition.getOrEqualsCondition(), "=?", OR, condition);

        if(sql.startsWith(AND)){
            sql = sql.replaceFirst(AND, "");
            sql = " where" + sql;
        }
        if(sql.startsWith(OR)){
            sql = sql.replaceFirst(OR, "");
            sql = " where" + sql;
        }

        if(StringUtils.isNotEmpty(condition.getOrderBy())){
            sql += " order by " + condition.getOrderBy();
        }

        return sql;
    }

    private String generateConditionSql0(Map<String, List<String>> conditionMap, String type, String separator, Condition condition){

        StringBuilder sql = new StringBuilder();

        if(conditionMap != null){
            conditionMap.forEach((key, conditions)->conditions.forEach(value->{
                sql.append(separator).append(" ");
                sql.append(key).append(" ");
                sql.append(type).append(" ");
                condition.addParam(value);
            }));
        }

        return sql.toString();
    }
}

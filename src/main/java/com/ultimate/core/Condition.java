package com.ultimate.core;

import com.ultimate.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Condition{

    private Map<String, List<String>> equalsMap;
    private Map<String, List<String>> orEqualsMap;
    private Map<String, List<String>> notEqualsMap;
    private Map<String, List<String>> searchMap;
    private List<String> joinTables;
    private List<String> params;
    private Map<String, String> updateMap;
    private String orderBy;
    private String column;
    private Integer start;
    private Integer limit;
    private boolean autoConvert;

    public Condition(){

        this(false);
    }

    /**
     @param autoConvert convertCamelToUnderScore
     */
    public Condition(boolean autoConvert){

        this.autoConvert = autoConvert;
    }

    public void addParam(String param){

        if(params==null){
            params=new ArrayList<>();
        }

        params.add(param);
    }

    public String[] getParams(){

        if(params==null)return null;

        return params.toArray(new String[0]);
    }

    public void join(String table, String alias, String onCondition){

        if(joinTables == null) joinTables = new ArrayList<>();

        String segment = "join " + table + " " + alias + " on " + onCondition;
        joinTables.add(segment);
    }

    private List<String> getConditionList(Map<String, List<String>> map, String key){

        List<String> params = map.get(key);
        if(params == null){
            params = new ArrayList<>();
        }
        return params;
    }

    private String convert(String column){

        if(autoConvert){
            String regex = "([a-z])([A-Z]+)";
            String replacement = "$1_$2";
            column = column.replaceAll(regex, replacement).toLowerCase();
        }

        return column;
    }

    public void update(String column, Object value){

        if(updateMap == null) updateMap = new HashMap<>();

        column = convert(column);
        updateMap.put(column, value.toString());
    }

    public void put(Map<String, List<String>> map, String column, Object value){

        column = convert(column);
        List<String> conditionList = getConditionList(map, column);
        conditionList.add(value.toString());
        map.put(column, conditionList);
    }

    public void notEquals(String column, Object value){

        if(notEqualsMap == null) notEqualsMap = new HashMap<>();

        put(notEqualsMap, column, value);
    }

    public void equals(String column, Object value){

        if(equalsMap == null) equalsMap = new HashMap<>();

        put(equalsMap, column, value);
    }

    public void search(String column, Object content){

        if(searchMap == null) searchMap = new HashMap<>();

        put(searchMap, column, "%"+content+"%");
    }

    public void orEquals(String column, Object value){

        if(orEqualsMap == null) orEqualsMap = new HashMap<>();

        put(orEqualsMap, column, value);
    }

    public void limit(Integer limit){

        this.limit = limit;
    }

    public void start(Integer start){

        this.start = start;
    }

    public void setColumn(String column){

        column = convert(column);
        this.column = column;
    }

    public void orderBy(String orderBy){

        this.orderBy = orderBy;
    }

    public Map<String, String> getUpdateList(){

        return updateMap;
    }

    public Map<String, List<String>> getNotEqualsCondition(){

        return notEqualsMap;
    }

    public List<String> getJoinTables(){

        return joinTables;
    }

    public Map<String, List<String>> getEqualsCondition(){

        return equalsMap;
    }

    public Map<String, List<String>> getOrEqualsCondition(){

        return orEqualsMap;
    }

    public boolean isAutoConvert(){

        return autoConvert;
    }

    public void setAutoConvert(boolean autoConvert){

        this.autoConvert = autoConvert;
    }

    public Integer getLimit(){

        return this.limit;
    }

    public Integer getStart(){

        return this.start;
    }

    public String getColumn(){

        if(StringUtils.isEmpty(this.column)){
            return "*";
        }

        return this.column;
    }

    public String getOrderBy(){

        return this.orderBy;
    }

    public Map<String, List<String>> getSearchCondition(){

        return searchMap;
    }
}

package com.ultimate.component.info;

import java.sql.JDBCType;

public class ColumnInfo{

    private String fieldName;
    private Class fieldType;
    private String jdbcName;
    private JDBCType jdbcType;
    private Integer length;
    private boolean isNumeric;
    private boolean nullable;
    private boolean autoIncrement;

    public boolean isNumeric(){

        return isNumeric;
    }

    public void setNumeric(boolean numeric){

        isNumeric = numeric;
    }

    public Integer getLength(){

        return length;
    }

    public void setLength(Integer length){

        this.length = length;
    }

    public String getJdbcName(){

        return jdbcName;
    }

    public void setJdbcName(String jdbcName){

        this.jdbcName = jdbcName;
    }

    public String getFieldName(){

        return fieldName;
    }

    public void setFieldName(String fieldName){

        this.fieldName = fieldName;
    }

    public Class getFieldType(){

        return fieldType;
    }

    public void setFieldType(Class fieldType){

        this.fieldType = fieldType;
    }

    public JDBCType getJdbcType(){

        return jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType){

        this.jdbcType = jdbcType;
    }

    public boolean isNullable(){

        return nullable;
    }

    public void setNullable(boolean nullable){

        this.nullable = nullable;
    }

    public boolean isAutoIncrement(){

        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement){

        this.autoIncrement = autoIncrement;
    }
}

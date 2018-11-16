package com.ultimate.component.info;

public class ColumnInfo{

    private String columnName;
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

    public String getColumnName(){

        return columnName;
    }

    public void setColumnName(String columnName){

        this.columnName = columnName;
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

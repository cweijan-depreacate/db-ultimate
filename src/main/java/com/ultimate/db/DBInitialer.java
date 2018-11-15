package com.ultimate.db;

import com.ultimate.component.info.ComponentInfo;
import com.ultimate.component.TableInfo;
import com.ultimate.db.config.DbConfig;
import com.ultimate.util.Log;
import com.ultimate.util.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;

public class DBInitialer{

    private static Logger logger = Log.getLogger();
    private DbConfig dbConfig;

    public DBInitialer(DbConfig dbConfig){

        this.dbConfig = dbConfig;
        createComponentTable();
    }

    /**
     创建Bean所对应的表
     */
    private void createComponentTable(){

        Objects.requireNonNull(dbConfig);
        Objects.requireNonNull(TableInfo.getComponentList());

        if(dbConfig.isCreateNonexistsTable()){
            TableInfo.getComponentList().stream().
                    filter(componentInfo->!tableExists(componentInfo.getTableName())).
                    forEach(this::createTable);
        }

    }

    public void createTable(ComponentInfo componentInfo){

        createTable(componentInfo.getComponentClass(), componentInfo.getTableName(), componentInfo.getPrimaryKey(), dbConfig.getConnection());
    }

    /**
     检测表是否存在

     @param tableName 表明
     */
    public boolean tableExists(String tableName){

        Connection connection = dbConfig.getConnection();

        try{
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getTables(null, null, tableName, null).next();
        } catch(SQLException e){
            logger.error(e.getMessage(), e);
        } finally{
            try{
                connection.close();
            } catch(SQLException e){
                logger.error(e.getMessage(), e);
            }
        }

        return true;
    }

    private <T> void createTable(Class<T> t, String tableName, String primaryKey, Connection connection){

        if(StringUtils.isEmpty(primaryKey)){
            throw new InvalidParameterException("class " + t.getName() + " primary key must exists !");
        }
        SqlExecutor.checkConnectionAlive(connection);

        String sql = "create table " + tableName + "(";

        for(Field field : t.getDeclaredFields()){

            field.setAccessible(true);
            sql += "`" + field.getName() + "` " + getFieldType(field) + " NOT NULL DEFAULT '' ";
            if(field.getName().equals(primaryKey)){
                sql += " AUTO_INCREMENT";
            }
            sql += ",";
        }

        sql += "primary key(`" + primaryKey + "`)  ";

        sql += ");";

        SqlExecutor.executeSql(sql,null, dbConfig.getConnection());
    }

    private String getFieldType(Field field){

        Class<?> fieldType = field.getType();
        if(fieldType == String.class){
            return "varchar(100)";
        }

        if(fieldType == Integer.class){
            return "int";
        }

        if(fieldType == Double.class){
            return "double";
        }

        if(fieldType == Long.class || fieldType.getName().equals("long")){
            return "int";
        }

        if(fieldType == Float.class){
            return "float";
        }

        if(fieldType.isPrimitive()){
            return fieldType.getName();
        }

        return null;
    }
}

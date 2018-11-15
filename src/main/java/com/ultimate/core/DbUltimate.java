package com.ultimate.core;

import com.ultimate.component.TableInfo;
import com.ultimate.convert.TypeConvert;
import com.ultimate.db.SqlExecutor;
import com.ultimate.db.config.DbConfig;
import com.ultimate.generator.GeneratorAdapter;
import com.ultimate.generator.SqlGenerator;
import com.ultimate.util.Log;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.util.List;

public class DbUltimate{

    private static Logger logger = Log.getLogger();
    private SqlExecutor sqlExecutor;
    private SqlGenerator sqlGenerator;

    public DbUltimate(DbConfig dbConfig){

        sqlExecutor = new SqlExecutor(dbConfig);
        sqlGenerator = new GeneratorAdapter(dbConfig).get();

    }

    public <T> T getBySql(String sql, Class<T> clazz){

        return getBySql(sql, null, clazz);
    }

    public <T> T getBySql(String sql, String[] params, Class<T> clazz){

        ResultSet resultSet = sqlExecutor.executeSql(sql, params);

        return TypeConvert.resultSetToBean(resultSet, clazz);
    }

    public <T> List<T> findBySql(String sql, Class<T> clazz){

        return findBySql(sql, null, clazz);
    }

    public <T> List<T> findBySql(String sql, String[] params, Class<T> clazz){

        ResultSet resultSet = sqlExecutor.executeSql(sql, params);
        return TypeConvert.resultSetToBeanList(resultSet, clazz);
    }

    public void executeSql(String sql){

        sqlExecutor.executeSql(sql);

    }

    public <T> T get(Condition condition, Class<T> clazz){

        String sql = sqlGenerator.generateSelectSql(TableInfo.getComponent(clazz), condition);
        sql += " limit 1";
        return getBySql(sql, condition.getParams(), clazz);
    }

    public <T> T getByPrimaryKey(Object primary, Class<T> clazz){

        Condition condition = new Condition();
        condition.equals("id", primary);

        return get(condition, clazz);
    }

    public <T> List<T> find(Condition condition, Class<T> clazz){

        String sql = sqlGenerator.generateSelectSql(TableInfo.getComponent(clazz), condition);
        return findBySql(sql, condition.getParams(), clazz);
    }

    public <T> void insert(T component){

        String sql = sqlGenerator.generateInsertSql(component);
        sqlExecutor.executeSql(sql);
    }

    public <T> void insertList(List<T> list){

        for(T t : list){
            insert(t);
        }
    }

    public void delete(Condition condition, Class clazz){

        String sql = sqlGenerator.generateDeleteSql(TableInfo.getComponent(clazz), condition);
        sqlExecutor.executeSql(sql);
    }

    public void update(Condition condition, Class clazz){

        String sql = sqlGenerator.generateUpdateSql(TableInfo.getComponent(clazz), condition);
        sqlExecutor.executeSql(sql);
    }
}
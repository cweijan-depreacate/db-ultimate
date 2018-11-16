package com.ultimate.core;

import com.ultimate.component.ComponentScan;
import com.ultimate.component.TableInfo;
import com.ultimate.convert.TypeConvert;
import com.ultimate.db.DBInitialer;
import com.ultimate.db.SqlExecutor;
import com.ultimate.db.config.DbConfig;
import com.ultimate.generator.GeneratorAdapter;
import com.ultimate.generator.SqlGenerator;
import com.ultimate.util.DbUtils;
import com.ultimate.util.Log;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class DbUltimate{

    private static Logger logger = Log.getLogger();
    private SqlExecutor sqlExecutor;
    private SqlGenerator sqlGenerator;
    private DbConfig dbConfig;

    public DbUltimate(DbConfig dbConfig){

        this.dbConfig = dbConfig;
        sqlExecutor = new SqlExecutor(dbConfig);
        sqlGenerator = new GeneratorAdapter(dbConfig).getGenerator();
        new ComponentScan().scan("com.ultimate");
        new DBInitialer(dbConfig).initalerTable();

    }

    public ResultSet executeSql(String sql){

        return sqlExecutor.executeSql(sql);

    }

    public ResultSet executeSql(String sql, Object[] params){

        return sqlExecutor.executeSql(sql, params);

    }

    public <T> T getBySql(String sql, Object[] params, Class<T> clazz){

        Connection connection = dbConfig.openConnection();
        ResultSet resultSet = SqlExecutor.executeSql(sql, params, connection);
        T bean = TypeConvert.resultSetToBean(resultSet, clazz);
        DbUtils.closeConnection(connection);
        return bean;
    }

    public <T> List<T> findBySql(String sql, Object[] params, Class<T> clazz){

        Connection connection = dbConfig.openConnection();
        ResultSet resultSet = SqlExecutor.executeSql(sql, params, connection);
        List<T> beanList = TypeConvert.resultSetToBeanList(resultSet, clazz);
        DbUtils.closeConnection(connection);
        return beanList;
    }

    public <T> List<T> findBySql(String sql, Class<T> clazz){

        return findBySql(sql, null, clazz);
    }

    public <T> T getBySql(String sql, Class<T> clazz){

        return getBySql(sql, null, clazz);
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

    /**
     插入对象,属性为空则插入null值

     @param component 实体对象e
     */
    public void insert(Object component){

        String sql = sqlGenerator.generateInsertSql(component, false);
        executeSql(sql);
    }

    /**
     插入对象,只插入不为空的属性

     @param component 实体对象
     */
    public void insertSelective(Object component){

        String sql = sqlGenerator.generateInsertSql(component, true);
        executeSql(sql);
    }

    public <T> void insertList(List<T> list){

        for(T t : list){
            insert(t);
        }
    }

    public void delete(Condition condition, Class clazz){

        String sql = sqlGenerator.generateDeleteSql(TableInfo.getComponent(clazz), condition);
        executeSql(sql, condition.getParams());
    }

    public void update(Condition condition, Class clazz){

        String sql = sqlGenerator.generateUpdateSql(TableInfo.getComponent(clazz), condition);
        executeSql(sql, condition.getParams());
    }
}
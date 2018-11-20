package github.cweijan.ultimate.core;

import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.db.SqlExecutor;
import github.cweijan.ultimate.util.DbUtils;
import github.cweijan.ultimate.util.Log;
import github.cweijan.ultimate.component.ComponentScan;
import github.cweijan.ultimate.convert.TypeConvert;
import github.cweijan.ultimate.db.DBInitialer;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.generator.GeneratorAdapter;
import github.cweijan.ultimate.generator.SqlGenerator;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 核心Api,用于Crud操作
 */
public class DbUltimate{

    private static Logger logger = Log.getLogger();
    private SqlExecutor sqlExecutor;
    private SqlGenerator sqlGenerator;
    private DbConfig dbConfig;

    public DbUltimate(DbConfig dbConfig){

        this.dbConfig = dbConfig;
        sqlExecutor = new SqlExecutor(dbConfig);
        sqlGenerator = new GeneratorAdapter(dbConfig).getGenerator();
        ComponentScan.scan(dbConfig.getScanPackage());
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

    public <T> T get(Operation operation, Class<T> clazz){

        String sql = sqlGenerator.generateSelectSql(TableInfo.getComponent(clazz), operation);
        sql += " limit 1";
        return getBySql(sql, operation.getParams(), clazz);
    }

    public <T> T getByPrimaryKey(Object primary, Class<T> clazz){

        Operation operation = new Operation();
        operation.equals("id", primary);

        return get(operation, clazz);
    }

    public <T> List<T> find(Operation operation, Class<T> clazz){

        String sql = sqlGenerator.generateSelectSql(TableInfo.getComponent(clazz), operation);
        return findBySql(sql, operation.getParams(), clazz);
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

    public void delete(Operation operation, Class clazz){

        String sql = sqlGenerator.generateDeleteSql(TableInfo.getComponent(clazz), operation);
        executeSql(sql, operation.getParams());
    }

    public void update(Object object){

        try{
            String sql = sqlGenerator.generateUpdateSql(object);
            executeSql(sql);
        } catch(IllegalAccessException e){
            logger.error(e.getMessage(), e);
        }

    }

    public void update(Operation operation, Class clazz){

        String sql = sqlGenerator.generateUpdateSql(TableInfo.getComponent(clazz), operation);
        executeSql(sql, operation.getParams());
    }
}
package com.ultimate.db;

import com.ultimate.db.config.DbConfig;
import com.ultimate.util.Log;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class SqlExecutor{

    private static Logger logger = Log.getLogger();
    private DbConfig dbConfig;

    public SqlExecutor(DbConfig dbConfig){

        this.dbConfig = dbConfig;
    }

    /**
     @param sql sql
     */
    public ResultSet executeSql(String sql){

        return executeSql(sql, null, dbConfig.getConnection());
    }

    /**
     @param sql    sql
     @param params 查询参数
     */
    public ResultSet executeSql(String sql, String[] params){

        return executeSql(sql, params, dbConfig.getConnection());
    }

    public static ResultSet executeSql(String sql, String[] params, Connection connection){

        AtomicReference<ResultSet> resultSet = new AtomicReference<>();

        transaction(sql, connection, ()->{
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(sql);
            if(params != null){
                IntStream.range(0, params.length).forEach(index->{
                    try{
                        preparedStatement.setString(index + 1, params[index]);
                    } catch(SQLException e){
                        logger.error(e.getMessage(), e);
                    }
                });
            }
            if(sql.trim().startsWith("select")){
                resultSet.set(preparedStatement.executeQuery());
            } else{
                preparedStatement.executeUpdate();
            }
            logger.debug("Execute SQL : " + sql);
            if(params != null){
                IntStream.range(0, params.length).forEach(index->{
                    logger.debug("param " + index + ":" + params[index]+" ");
                });
            }
        });

        return resultSet.get();
    }

    /**
     sql异常处理以及回滚

     @param sql        sql语句
     @param connection 数据库连接
     @param sqlWrapper sql执行过程
     */
    public static void transaction(String sql, Connection connection, SqlWrapper sqlWrapper){

        checkConnectionAlive(connection);
        // TODO 这里需要修改为事务管理器
        try{
            sqlWrapper.execute();
        } catch(SQLException e){
            logger.error("Execute SQL : `" + sql + "` fail!  \n" + e.getMessage(), e);
            try{
                if(!connection.getAutoCommit() && !sql.contains("select")){
                    connection.rollback();
                }
            } catch(SQLException e1){
                Log.getLogger().error(e1.getMessage(), e1);
            }
        } finally{
            //            try{
            //                connection.close();
            //            } catch(SQLException e){
            //                Log.getLogger().error(e.getMessage(), e);
            //            }
        }
    }

    public static void checkConnectionAlive(Connection connection){

        try{
            if(connection == null || connection.isClosed()){
                throw new IllegalArgumentException("connection is valid!");
            }
        } catch(SQLException e1){
            logger.error(e1.getMessage(), e1);
        }
    }
}

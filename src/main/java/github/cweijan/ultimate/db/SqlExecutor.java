package github.cweijan.ultimate.db;

import github.cweijan.ultimate.transaction.jdbc.JdbcTransaction;
import github.cweijan.ultimate.util.DbUtils;
import github.cweijan.ultimate.util.Log;
import github.cweijan.ultimate.db.config.DbConfig;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 用来执行Sql
 */
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

        return executeSql(sql, null, dbConfig.openConnection());
    }

    /**
     @param sql    sql
     @param params 查询参数
     */
    public ResultSet executeSql(String sql, Object[] params){

        return executeSql(sql, params, dbConfig.openConnection());
    }

    public static ResultSet executeSql(String sql, Object[] params, Connection connection){

        AtomicReference<ResultSet> resultSet = new AtomicReference<>();

        transaction(sql, connection, ()->{
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(sql);
            if(params != null){
                IntStream.range(0, params.length).forEach(index->{
                    try{
                        preparedStatement.setObject(index + 1, params[index]);
                    } catch(SQLException e){
                        logger.error(e.getMessage(), e);
                    }
                });
            }
            if(sql.trim().startsWith("select")){
                resultSet.set(preparedStatement.executeQuery());
            } else{
                preparedStatement.executeUpdate();
                DbUtils.closeConnection(connection);
            }
            if(!logger.isDebugEnabled())return;
            logger.debug("Execute SQL : " + sql);
            if(params != null ){
                IntStream.range(0, params.length).forEach(index->logger.debug(" param " + index + " : " + params[index] + " "));
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

        DbUtils.checkConnectionAlive(connection);
        JdbcTransaction jdbcTransaction = new JdbcTransaction(connection);
        try{
            sqlWrapper.execute();
        } catch(SQLException e){
            logger.error("Execute SQL : `" + sql + "` fail!  \n" + e.getMessage(), e);
            try{
                jdbcTransaction.rollback();
            } catch(SQLException e1){
                Log.getLogger().error(e1.getMessage(), e1);
            }
        }
    }


}

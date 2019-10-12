package github.cweijan.ultimate.db;

import github.cweijan.ultimate.core.result.PreparedStatementCallback;
import github.cweijan.ultimate.core.result.ResultInfo;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.util.Log;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.stream.IntStream;

/**
 * sql executor
 *
 * @author cweijan
 * @version 2019/9/5 11:45
 */
public class SqlExecutor {
    private final DbConfig dbConfig;
    private final DataSource dataSource;

    public SqlExecutor(DbConfig dbConfig, DataSource dataSource) {
        this.dbConfig = dbConfig;
        this.dataSource = dataSource;
    }

    @Nullable
    public final <T> T executeSql(@NotNull String sql, PreparedStatementCallback<T> preparedStatementCallback) throws SQLException {
        Intrinsics.checkParameterIsNotNull(sql, "sql");
        return this.executeSql(sql, null, preparedStatementCallback);
    }

    @Nullable
    public final <T> T executeSql(@NotNull String sql, @Nullable Object[] params, PreparedStatementCallback<T> preparedStatementCallback) throws SQLException {
        Intrinsics.checkParameterIsNotNull(sql, "sql");
        return this.executeSql(sql, params, preparedStatementCallback, dataSource.getConnection());
    }

    private <T> T executeSql(@NotNull String sql, final Object[] params, PreparedStatementCallback<T> preparedStatementCallback, Connection connection) throws SQLException {
        ResultSet resultSet;
        long startTime = System.currentTimeMillis();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if (params != null) {
            for (int index = 0; index < params.length; index++) {
                preparedStatement.setObject(index + 1, params[index]);
            }
        }
        ResultInfo resultInfo = new ResultInfo();
        try {
            if (sql.trim().toLowerCase().startsWith("select")) {
                resultSet = preparedStatement.executeQuery();
            } else {
                resultInfo.setUpdateLine(preparedStatement.executeUpdate());
                resultSet = preparedStatement.getGeneratedKeys();
                if(resultSet.next()){
                    resultInfo.setGenerateKey(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            Log.getLogger().error("Fail Execute SQL : " + sql + "   \n " + e.getMessage() + ' ');
            throw e;
        }

        if (this.dbConfig.getShowSql()) {
            Log.getLogger().info("Execute SQL : " + sql + ", cost time: " + (System.currentTimeMillis() - startTime));
            if (params != null && params.length != 0) {
                StringBuilder paramContent = new StringBuilder(" param count " + params.length + " : ");
                IntStream.range(0, params.length).forEach(index -> {
                    if (index == 0) paramContent.append(params[index]);
                    else paramContent.append(",").append(params[index]);
                });
                Log.getLogger().info(paramContent.toString());
            }
        }
        try {
            return preparedStatementCallback.handlerResultSet(resultSet,resultInfo);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException ex) {
                    Log.getLogger().trace("Could not close JDBC ResultSet", ex);
                }
                DataSourceUtils.releaseConnection(connection,dataSource);
            }
        }
    }

}

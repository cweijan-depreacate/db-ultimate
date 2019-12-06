package github.cweijan.ultimate.db;

import github.cweijan.ultimate.core.result.ResultInfo;
import github.cweijan.ultimate.core.result.StatementCallback;
import github.cweijan.ultimate.db.config.DbConfig;
import org.springframework.jdbc.datasource.DataSourceUtils;
import github.cweijan.ultimate.util.Log;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public final <T> T executeSql(@NotNull String sql, StatementCallback<T> statementCallback) throws SQLException {
        Intrinsics.checkParameterIsNotNull(sql, "sql");
        return this.executeSql(sql, null, statementCallback);
    }

    @Nullable
    public final <T> T executeSql(@NotNull String sql, @Nullable Object[] params, StatementCallback<T> statementCallback) throws SQLException {
        Intrinsics.checkParameterIsNotNull(sql, "sql");
        return this.executeSql(sql, params, statementCallback, DataSourceUtils.getConnection(dataSource));
    }

    private <T> T executeSql(@NotNull String sql, final Object[] params, StatementCallback<T> statementCallback, Connection connection) throws SQLException {
        ResultSet resultSet = null;
        sql = sql.trim();
        String sqlLowerCase = sql.toLowerCase();
        boolean isInsert = sqlLowerCase.startsWith("insert");
        long startTime = System.currentTimeMillis();
        PreparedStatement preparedStatement = null;
        if (params != null && params.length > 0) {
            if (isInsert) {
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement(sql);
            }
            for (int index = 0; index < params.length; index++) {
                preparedStatement.setObject(index + 1, params[index]);
            }
        }
        ResultInfo resultInfo = new ResultInfo();
        try {
            if (sqlLowerCase.startsWith("select")) {
                if (preparedStatement == null) {
                    resultSet = connection.createStatement().executeQuery(sql);
                } else {
                    resultSet = preparedStatement.executeQuery();
                }
            } else {
                if (preparedStatement == null) {
                    Statement statement = connection.createStatement();
                    resultInfo.setUpdateLine(statement.executeUpdate(sql));
                } else {
                    resultInfo.setUpdateLine(preparedStatement.executeUpdate());
                    if (isInsert) {
                        resultSet = preparedStatement.getGeneratedKeys();
                        if (resultSet.next()) {
                            try {
                                resultInfo.setGenerateKey(resultSet.getLong(1));
                            } catch (SQLException e) {
                                Log.getLogger().error(e.getMessage());
                            }
                        }
                    }
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
            return statementCallback.handlerResultSet(resultSet, resultInfo);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    Log.getLogger().trace("Could not close JDBC ResultSet", ex);
                }
            }
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}

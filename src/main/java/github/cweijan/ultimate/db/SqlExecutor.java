package github.cweijan.ultimate.db;

import github.cweijan.ultimate.core.tx.TransactionHelper;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.util.Log;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.stream.IntStream;

/**
 * sql executor
 * @author cweijan
 * @version 2019/9/5 11:45
 */
public class SqlExecutor {
    private final DbConfig dbConfig;
    private final TransactionHelper transactionHelper;

    public SqlExecutor(DbConfig dbConfig, TransactionHelper transactionHelper) {
        this.dbConfig = dbConfig;
        this.transactionHelper = transactionHelper;
    }

    @Nullable
    public final ResultSet executeSql(@NotNull String sql) throws SQLException {
        Intrinsics.checkParameterIsNotNull(sql, "sql");
        return this.executeSql(sql, null, this.transactionHelper.getConnection());
    }

    @Nullable
    public final ResultSet executeSql(@NotNull String sql, @Nullable Object[] params) throws SQLException {
        Intrinsics.checkParameterIsNotNull(sql, "sql");
        return this.executeSql(sql, params, this.transactionHelper.getConnection());
    }

    private ResultSet executeSql(@NotNull String sql, final Object[] params, Connection connection) throws SQLException {
        ResultSet resultSet;
        long startTime = System.currentTimeMillis();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if (params != null) {
            for (int index = 0; index < params.length; index++) {
                preparedStatement.setObject(index + 1, params[index]);
            }
        }

        try {
            if (sql.trim().toLowerCase().startsWith("select")) {
                resultSet = preparedStatement.executeQuery();
            } else {
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
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

        return resultSet;
    }

}

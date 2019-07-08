package github.cweijan.ultimate.db

import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.Log
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.stream.IntStream

/**
 * 用来执行Sql
 */
class SqlExecutor(private val dbConfig: DbConfig) {

    /**
     * @param sql    sql
     * @param params 查询参数
     */
    fun executeSql(sql: String, params: Array<Any>? = null): ResultSet? {

        return executeSql(sql, params, dbConfig.getConnection())
    }

    private fun executeSql(sql: String, params: Array<Any>?, connection: Connection): ResultSet? {

        val resultSet: ResultSet?

        val preparedStatement: PreparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        if (params != null) {
            IntStream.range(0, params.size).forEach { index ->
                preparedStatement.setObject(index + 1, params[index])
            }
        }
        try {
            resultSet = if (sql.trim { it <= ' ' }.toLowerCase().startsWith("select")) {
                preparedStatement.executeQuery()
            } else {
                preparedStatement.executeUpdate()
                preparedStatement.generatedKeys
            }
        } catch (e: Exception) {
            Log.getLogger().error("Fail Execute SQL : $sql   \n ${e.message} ")
            throw e
        }
        if (dbConfig.showSql) {
            Log.getLogger().info("Execute SQL : $sql")
            if (params != null) {
                var paramContent=" param count ${params.size} : "
                IntStream.range(0, params.size).forEach { index ->
                    paramContent+=",${params[index]}"
                }
                Log.getLogger().info(paramContent.replaceFirst(",",""));
            }
        }

        return resultSet
    }

}

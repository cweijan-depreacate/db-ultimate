package github.cweijan.ultimate.db

import github.cweijan.ultimate.transaction.jdbc.JdbcTransaction
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.db.config.DbConfig

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.stream.IntStream

/**
 * 用来执行Sql
 */
class SqlExecutor(private val dbConfig: DbConfig) {

    /**
     * @param sql    sql
     * @param params 查询参数
     */
    fun executeSql(sql: String, params: Array<String>? = null): ResultSet? {

        return executeSql(sql, params, dbConfig.openConnection())
    }

    private fun executeSql(sql: String, params: Array<String>?, connection: Connection): ResultSet? {

        var resultSet: ResultSet? = null

        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
        val transaction = JdbcTransaction(connection)
        if (params != null) {
            IntStream.range(0, params.size).forEach { index ->
                preparedStatement.setObject(index + 1, params[index])
            }
        }
        try {
            if (sql.trim { it <= ' ' }.startsWith("select")) {
                resultSet = preparedStatement.executeQuery()
            } else {
                preparedStatement.executeUpdate()
                transaction.commit()

            }
        } catch (e: Exception) {
            logger.error("Fail Execute SQL : $sql   \n ${e.message} ")
            transaction.rollback()
            throw e
        }finally {
            if(!sql.trim().startsWith("select")){
                transaction.close()
            }
        }
        if (dbConfig.showSql) {
            logger.info("Execute SQL : $sql")
            if (params != null) {
                IntStream.range(0, params.size).forEach { index -> logger.debug(" param ${index + 1} : ${params[index]} ") }
            }
        }

        return resultSet
    }

    companion object {

        private val logger = Log.logger

    }
}

package github.cweijan.ultimate.util

import java.sql.Connection
import java.sql.ResultSetMetaData
import java.sql.SQLException

object DbUtils {

    private val logger = Log.logger

    /**
     * Get table all column metadata
     */
    fun getTableMetaData(tableName: String, connection: Connection): ResultSetMetaData? {

        checkConnectionAlive(connection)
        try {
            val preparedStatement = connection.prepareStatement("select * from $tableName")
            preparedStatement.executeQuery()
            return preparedStatement.metaData
        } catch (e: Exception) {
            logger.error(e.message, e)
        }

        return null
    }

    /**
     * Check datbase connection is alive
     */
    fun checkConnectionAlive(connection: Connection?) {

        try {
            if (connection == null || connection.isClosed) {
                throw IllegalArgumentException("connection is valid!")
            }
        } catch (e: SQLException) {
            logger.error(e.message, e)
        }

    }

    /**
     * Close connection
     */
    fun closeConnection(connection: Connection?) {

        try {
            if (connection != null && !connection.isClosed) {
                connection.close()
                if (logger.isDebugEnabled) {
                    logger.debug("closed connection $connection")
                }
            }
        } catch (e: SQLException) {
            logger.error(e.message, e)
        }

    }

}

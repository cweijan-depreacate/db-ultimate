package github.cweijan.ultimate.util

import java.sql.Connection
import java.sql.ResultSetMetaData
import java.sql.SQLException

object DbUtils {

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
            Log.error(e.message, e)
        }

        return null
    }

    /**
     * Check datbase connection is alive
     */
    fun checkConnectionAlive(connection: Connection?) {

        try {
            if (connection == null || connection.isClosed) {
                throw java.lang.IllegalArgumentException("connection is valid!")
            }
        } catch (e: SQLException) {
            Log.error(e.message, e)
        }

    }

    /**
     * Close connection
     */
    fun closeConnection(connection: Connection?) {

        try {
            if (connection != null && !connection.isClosed) {
                connection.close()
                Log.debug("closed connection $connection")
            }
        } catch (e: SQLException) {
            Log.error(e.message, e)
        }

    }

}

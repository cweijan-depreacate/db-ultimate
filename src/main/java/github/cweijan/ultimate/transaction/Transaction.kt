package github.cweijan.ultimate.transaction

import java.sql.Connection
import java.sql.SQLException

interface Transaction {

    /**
     * Commit inner database connection.
     */
    @Throws(SQLException::class)
    fun commit()

    /**
     * Rollback inner database connection.
     */
    @Throws(SQLException::class)
    fun rollback()

    /**
     * Close inner database connection.
     */
    @Throws(SQLException::class)
    fun close()

}

package github.cweijan.ultimate.transaction.jdbc

import github.cweijan.ultimate.transaction.Transaction
import github.cweijan.ultimate.util.Log
import java.sql.Connection
import java.sql.SQLException

class JdbcTransaction(var connection: Connection? = null, private var autoCommit: Boolean? = true) : Transaction {

    @Throws(SQLException::class)
    override fun commit() {

        if (connection != null && !connection!!.autoCommit) {
            if (log.isDebugEnabled) {
                log.debug("Committing JDBC Connection [$connection]")
            }
            connection!!.commit()
        }
    }

    @Throws(SQLException::class)
    override fun rollback() {

        if (connection != null && !connection!!.autoCommit) {
            if (log.isDebugEnabled) {
                log.debug("Rolling back JDBC Connection [$connection]")
            }
            connection!!.rollback()
        }
    }

    @Throws(SQLException::class)
    override fun close() {

        if (connection != null) {
            if (log.isDebugEnabled) {
                log.debug("Closing JDBC Connection [$connection]")
            }
            connection!!.close()
        }
    }

    companion object {

        private val log = Log.logger
    }

}

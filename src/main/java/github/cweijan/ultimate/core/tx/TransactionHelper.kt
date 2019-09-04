package github.cweijan.ultimate.core.tx

import org.springframework.jdbc.datasource.DataSourceUtils
import java.sql.Connection
import javax.sql.DataSource

/**
 * @author cweijan
 * @version 2019/9/3 18:13
 */
class TransactionHelper(var dataSource: DataSource) {

    private val threadLocal = ThreadLocal<Connection?>()

    fun getConnection(): Connection {

        val currentConnection = threadLocal.get()
        return if (currentConnection == null || currentConnection.isClosed) {
            threadLocal.set(DataSourceUtils.doGetConnection(dataSource))
            threadLocal.get()!!
        } else if (DataSourceUtils.isConnectionTransactional(currentConnection, dataSource)) {
            DataSourceUtils.doGetConnection(dataSource)
        } else {
            DataSourceUtils.doCloseConnection(currentConnection, dataSource)
            threadLocal.set(DataSourceUtils.doGetConnection(dataSource))
            threadLocal.get()!!
        }

    }

    fun tryCloseConnection() {
        val currentConnection = threadLocal.get()
        if (currentConnection != null && !currentConnection.isClosed && !DataSourceUtils.isConnectionTransactional(currentConnection, dataSource)) {
            DataSourceUtils.doCloseConnection(currentConnection, dataSource)
        }
    }
}

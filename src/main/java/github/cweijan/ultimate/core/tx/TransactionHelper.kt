package github.cweijan.ultimate.core.tx

import github.cweijan.ultimate.util.Log
import org.springframework.jdbc.datasource.DataSourceUtils
import java.sql.Connection
import javax.sql.DataSource

/**
 * @author cweijan
 * @version 2019/9/3 18:13
 */
class TransactionHelper(private var dataSource: DataSource) {

    private val threadLocal = ThreadLocal<Connection?>()

    fun getConnection(): Connection {

        val connection = DataSourceUtils.doGetConnection(dataSource)
        threadLocal.set(connection)
        return connection
    }

    fun tryCloseConnection() {
        val currentConnection = threadLocal.get()
        if (currentConnection != null) {
            DataSourceUtils.releaseConnection(currentConnection, dataSource)
            threadLocal.set(null)
        }else{
            Log.getLogger().debug("No connection discovery, close fail!")
        }
    }
}

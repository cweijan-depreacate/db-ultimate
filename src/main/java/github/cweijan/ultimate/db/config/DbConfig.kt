package github.cweijan.ultimate.db.config

import github.cweijan.ultimate.db.HikariDataSourceAdapter
import github.cweijan.ultimate.util.Log
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.jdbc.datasource.DataSourceUtils
import java.sql.Connection
import javax.sql.DataSource

@ConfigurationProperties(prefix = "ultimate.jdbc")
class DbConfig {

    var createNonexistsTable: Boolean = false
    var username: String? = null
    var password: String? = null
    var driver: String? = null
    var url: String? = null
        set(value) {
            field = when {
                value?.indexOf("characterEncoding=utf-8") == -1 ->
                    if (value.indexOf("?") == -1) {
                        "$value?characterEncoding=utf-8"
                    } else {
                        "$value&characterEncoding=utf-8"
                    }
                else -> value
            }
        }
    var dataSource: DataSource? = null
        get() {
            if (field == null) {
                Log.debug("dataSource is null! init hikariDataSource")
                val dataSourceAdapter = HikariDataSourceAdapter(this)
                field = dataSourceAdapter.dataSource
            }
            return field
        }

    var maximumPoolSize = DefaultProperties.MAXIUM_POOL_SIZE
    var minimumIdle = DefaultProperties.MINIUM_IDEL_SIZE
    var showSql = DefaultProperties.SHOW_SQL
    var enable = DefaultProperties.ENABLE
    var develop = DefaultProperties.DEVELOP
    var scanPackage: String? = null
    private val threadLocal = ThreadLocal<Connection?>()

    fun getConnection(): Connection {

        val currentConnection = threadLocal.get()
        return if (currentConnection == null || currentConnection.isClosed) {
            threadLocal.set(DataSourceUtils.doGetConnection(dataSource!!))
            threadLocal.get()!!
        } else if (DataSourceUtils.isConnectionTransactional(currentConnection, dataSource)) {
            DataSourceUtils.doGetConnection(dataSource!!)
        } else {
            DataSourceUtils.doCloseConnection(currentConnection,dataSource)
            threadLocal.set(DataSourceUtils.doGetConnection(dataSource!!))
            threadLocal.get()!!
        }

    }

}

package github.cweijan.ultimate.db.config

import github.cweijan.ultimate.db.HikariDataSourceAdapter
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

@ConfigurationProperties(prefix = "ultimate.jdbc")
class DbConfig {

    var dataSource: DataSource? = null
    var isCreateNonexistsTable: Boolean = false

    var url: String? = null
        get() = field ?: url ?: DefaultProperties.JDBC_URL

    var username: String? = null
        get() = field ?: DefaultProperties.USERNAME
    var password: String? = null
        get() = field ?: DefaultProperties.PASSWORD
    var driver: String? = null
        get() = field ?: DefaultProperties.DEFAULT_DRIVER
    var maximumPoolSize: Int? = null
        get() = field ?: DefaultProperties.MAXIUM_POOL_SIZE
    var minimumIdle: Int? = null
        get() = field ?: DefaultProperties.MINIUM_IDEL_SIZE
    var scanPackage: String? = null

    companion object {

        val ULTIMATE_PREFIX = "ultimate.jdbc"
        private val logger = Log.logger
    }

    @JvmOverloads
    fun openConnection(autoCommit: Boolean = true): Connection {

        if (StringUtils.isEmpty(url)) {
            throw IllegalArgumentException("JDBC_URL must be not empty!")
        }

        if (dataSource == null) {
            if (logger.isDebugEnabled) {
                logger.debug("dataSource is null! init hikariDataSource")
            }
            val dataSourceAdapter = HikariDataSourceAdapter(this)
            dataSource = dataSourceAdapter.dataSource
        }

        val connection = dataSource!!.connection
        try {
            connection.autoCommit = autoCommit
        } catch (e: SQLException) {
            logger.error("getGenerator jdbc connection fail!", e)
        }

        return connection
    }

}

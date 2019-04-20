package github.cweijan.ultimate.db.config

import github.cweijan.ultimate.db.HikariDataSourceAdapter
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

@ConfigurationProperties(prefix = "core.jdbc")
class DbConfig(private var dataSource: DataSource? = null) {

    var createNonexistsTable: Boolean = false

    var url: String? = null
    var username: String? = null
    var password: String? = null
    var driver: String? = null

    var maximumPoolSize = DefaultProperties.MAXIUM_POOL_SIZE
    var minimumIdle = DefaultProperties.MINIUM_IDEL_SIZE
    var showSql = DefaultProperties.SHOW_SQL
    var enable = DefaultProperties.ENABLE
    var develop = DefaultProperties.DEVELOP
    var scanPackage: String? = null

    @JvmOverloads
    fun openConnection(autoCommit: Boolean = true): Connection {

        if (StringUtils.isEmpty(url)) {
            throw IllegalArgumentException("JDBC_URL must be not empty!")
        }

        if (dataSource == null) {
            if (showSql) {
                Log.info("dataSource is null! init hikariDataSource")
            }
            val dataSourceAdapter = HikariDataSourceAdapter(this)
            dataSource = dataSourceAdapter.dataSource
        }

        val connection = dataSource!!.connection
        try {
            connection.autoCommit = autoCommit
        } catch (e: SQLException) {
            Log.error("getGenerator jdbc connection fail!", e)
        }

        return connection
    }

}

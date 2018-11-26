package github.cweijan.ultimate.db

import github.cweijan.ultimate.db.config.DbConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

import javax.sql.DataSource

class HikariDataSourceAdapter(private val dbConfig: DbConfig) {

    private var shutdownHook: Thread? = null

    val dataSource: DataSource
        get() {

            val dataSource = HikariDataSource(buildConfig())
            shutdownHook = Thread(Runnable { dataSource.close() })
            Runtime.getRuntime().addShutdownHook(shutdownHook!!)

            return dataSource
        }

    private fun buildConfig(): HikariConfig {

        val config = HikariConfig()
        config.jdbcUrl = dbConfig.url
        config.username = dbConfig.username
        config.password = dbConfig.password
        config.driverClassName = dbConfig.driver
        config.minimumIdle = dbConfig.minimumIdle
        config.maximumPoolSize = dbConfig.maximumPoolSize

        return config
    }

    fun refreshDataSource(): DataSource {

        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook!!)
        }
        return dataSource
    }

}

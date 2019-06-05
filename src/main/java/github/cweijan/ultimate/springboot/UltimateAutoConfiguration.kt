package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.core.DbUltimate
import github.cweijan.ultimate.db.config.CacheConfig
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DbConfig::class,CacheConfig::class)
open class UltimateAutoConfiguration {

    @Autowired(required = false)
    private val dbConfig: DbConfig? = null
    @Autowired(required = false)
    private val dataSource: DataSource? = null

    @ConditionalOnMissingBean(DataSource::class)
    @Bean
    @Order(0)
    open fun createDataSource():DataSource?{

        if (null == dbConfig || !dbConfig.enable) {
            Log.debug("Db-core is disabled, skip..")
            return null
        }
        return dbConfig.dataSource!!
    }

    @Bean
    @Order(1)
    open fun createTransactionManager(): PlatformTransactionManager? {

        if (null == dbConfig || !dbConfig.enable) {
            Log.debug("Db-core is disabled, skip..")
            return null
        }

        return DataSourceTransactionManager(dataSource ?: dbConfig.dataSource!!)
    }

    @Bean
    @Order(2)
    open fun createUltimate(): DbUltimate? {

        if (null == dbConfig || !dbConfig.enable) {
            Log.debug("Db-ultimate is disabled, skip init ultimate.")
            return null
        } else if (null == dbConfig.driver) {
            Log.debug("Can't not find database type, skip init ultimate.")
            return null
        } else if (dataSource == null && (null == dbConfig.username || null == dbConfig.url)) {
            Log.error("Db config not set, skip init ultimate.")
            return null
        }

        dataSource?.let {
            Log.debug("use datasource init dbultimate..")
            dbConfig.dataSource = it
        }

        return DbUltimate(dbConfig)
    }

}

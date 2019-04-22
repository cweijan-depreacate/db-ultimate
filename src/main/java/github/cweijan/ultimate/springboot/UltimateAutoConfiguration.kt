package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.core.DbUltimate
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.transaction.Transaction
import github.cweijan.ultimate.util.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DbConfig::class)
open class UltimateAutoConfiguration {

    @Autowired
    private val dbConfig: DbConfig? = null
    @Autowired(required = false)
    private val dataSource: DataSource? = null

    @Bean("tx")
    open fun createTransactionManager():PlatformTransactionManager?{

        if (null == dbConfig || !dbConfig.enable) {
            Log.debug("Db-core is disabled, skip..")
            return null
        }

        return Transaction(dbConfig)
    }

    @Bean
    @ConditionalOnProperty("ultimate.jdbc.scanPackage")
    open fun createUltimate(): DbUltimate? {

        if (null == dbConfig || !dbConfig.enable) {
            Log.debug("Db-core is disabled, skip..")
            return null
        }

        if (dataSource != null) {
            Log.debug("use datasource init dbultimate..")
            dbConfig.dataSource=dataSource
            return DbUltimate(dbConfig)
        }

        if (null == dbConfig.username || null == dbConfig.password || null == dbConfig.driver || null == dbConfig.url) {
            Log.error("db config not set! skip init db-core!")
            return null
        }

        return DbUltimate(dbConfig)
    }

}

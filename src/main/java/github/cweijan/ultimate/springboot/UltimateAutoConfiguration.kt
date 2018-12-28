package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.core.DbUltimate
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DbConfig::class)
open class UltimateAutoConfiguration {

    @Autowired
    private val dbConfig: DbConfig? = null

    @Autowired
    @Bean
    @ConditionalOnBean(DataSource::class)
    open fun createUltimate(dataSource: DataSource): DbUltimate? {

        if (null != dbConfig && !dbConfig.enable) {
            Log.logger.debug("Db-ultimate is disabled, skip..")
            return null
        }

        return DbUltimate(DbConfig(dataSource))
    }

    @Bean
    @ConditionalOnMissingBean(DataSource::class)
    open fun createUltimate(): DbUltimate? {

        if (null == dbConfig || !dbConfig.enable) {
            Log.logger.debug("Db-ultimate is disabled, skip..")
            return null
        }

        if (null == dbConfig.username || null == dbConfig.password || null == dbConfig.driver || null == dbConfig.url) {
            Log.logger.error("db config not set! skip init db-ultimate!")
            return null
        }

        return DbUltimate(dbConfig)
    }

}
